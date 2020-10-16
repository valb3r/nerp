package com.valb3r.nerp.service;

import com.valb3r.nerp.domain.catalog.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.SortItem;
import org.neo4j.cypherdsl.core.StatementBuilder;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.neo4j.cypherdsl.core.Cypher.node;
import static org.neo4j.cypherdsl.core.Cypher.sort;

@Service
public class ProductByCategoryQueryBuilder {

    private static final Renderer cypherRenderer = Renderer.getDefaultRenderer();

    private static final String FROM_ID = "fromId";
    private static final String PRICE_IN_CENTS = "priceInCents";
    private static final String FROM_PRICE = "fromPrice";

    static final Function<Node, Condition> BY_ID = n -> n.internalId().gt(Cypher.parameter(FROM_ID));
    static final Function<Node, SortItem[]> ORDER_ID = n -> new SortItem[] { sort(n.internalId()).ascending() };
    static final Function<Node, Condition> BY_PRICE_ASC = n -> n.property(PRICE_IN_CENTS).gt(Cypher.parameter(FROM_PRICE))
        .or(n.property(PRICE_IN_CENTS).isEqualTo(Cypher.parameter(FROM_PRICE)).and(n.internalId().gt(Cypher.parameter(FROM_ID))));
    static final Function<Node, SortItem[]> ORDER_PRICE_ASC = n -> new SortItem[] {sort(n.property(PRICE_IN_CENTS)).ascending(), sort(n.internalId())};

    static final Function<Node, Condition> BY_PRICE_DESC = n -> n.property(PRICE_IN_CENTS).lt(Cypher.parameter(FROM_PRICE))
        .or(n.property(PRICE_IN_CENTS).isEqualTo(Cypher.parameter(FROM_PRICE)).and(n.internalId().gt(Cypher.parameter(FROM_ID))));
    static final Function<Node, SortItem[]> ORDER_PRICE_DESC = n -> new SortItem[] {sort(n.property(PRICE_IN_CENTS)).descending(), sort(n.internalId())};

    static final Function<@NotNull Product, String> ENCODE_PRICE_PAGE_ID = p -> p.getPriceInCents() + "." + p.getId().toString();
    static final Function<@Nullable String, Map<String, ?>> DECODE_PRICE_PAGE_ID = s -> {
        if (StringUtils.isEmpty(s)) {
            return Map.of(FROM_PRICE, -1L, FROM_ID, -1L);
        }

        var split = s.split("\\.");
        return Map.of(FROM_PRICE, Long.valueOf(split[0]), FROM_ID, Long.valueOf(split[1]));
    };
    static final Function<String, Map<String, ?>> DECODE_PRICE_PAGE_ID_DESC = s -> {
        if (StringUtils.isEmpty(s)) {
            return Map.of(FROM_PRICE, Long.MAX_VALUE, FROM_ID, -1L);
        }

        var split = s.split("\\.");
        return Map.of(FROM_PRICE, Long.valueOf(split[0]), FROM_ID, Long.valueOf(split[1]));
    };

    // Cursor paging
    /*
     * NONE:
     * MATCH (p:Product)<-[:HAS]-(cs:Category)<-[:PARENT*0..]-(ce:Category) WHERE id(p) > fromIdExclusive[0] AND id(ce) = 12927
     * WITH p AS prod
     * MATCH (p:Product)<-[:HAS]-(cs:Category)<-[:PARENT*0..]-(ce:Category) WHERE p=prod AND id(ce) = 12928
     * RETURN p ORDER BY p.id LIMIT 10
     */
    /*
     * PRICE_ASC:
     * MATCH (p:Product)<-[:HAS]-(cs:Category)<-[:PARENT*0..]-(ce:Category) WHERE ((p.priceCents > fromIdExclusive[0]) OR (p.priceCents == fromIdExclusive[0] AND id(p) > fromIdExclusive[1]))  AND id(ce) = 12927
     * WITH p AS prod
     * MATCH (p:Product)<-[:HAS]-(cs:Category)<-[:PARENT*0..]-(ce:Category) WHERE p=prod AND id(ce) = 12928
     * RETURN p ORDER BY p.priceCents, p.id LIMIT 10
     */
    // @Param("categories") Set<Long> categories, @Param("fromIdExclusive") long fromIdExclusive, @Param("pageSize") long pageSize
    public String buildQuery(List<Long> categories, int pageSize, OrderBy orderBy) {
        var product = node("Product").named("p");
        var categoryStart = node("Category").named("cs");
        var categoryEnd = node("Category").named("ce");
        var categoryEndId = categoryEnd.internalId();

        var basePattern = product.relationshipFrom(categoryStart, "HAS").relationshipFrom(categoryEnd, "PARENT").min(0);
        var baseStatement = Cypher.match(basePattern);

        StatementBuilder.BuildableStatement statement;

        if (categories.isEmpty()) {
            statement = baseStatement
                .where(orderBy.getPageSkipClause().apply(product))
                .withDistinct(product)
                .returning(product).orderBy(orderBy.getOrderByClause().apply(product))
                .limit(pageSize);

            return cypherRenderer.render(statement.build());
        }

        StatementBuilder.OrderableOngoingReadingAndWithWithoutWhere currentStmt = null;

        for (int pos = 0; pos < categories.size(); ++pos) {
            var category = categories.get(pos);
            if (0 == pos) {
                currentStmt = baseStatement.where(
                    orderBy.getPageSkipClause().apply(product).and(categoryEndId.isEqualTo(Cypher.literalOf(category)))  // Safe from injection as is long
                ).with(product);

                continue;
            }

            categoryStart = node("Category").named("cs" + pos);
            categoryEnd = node("Category").named("ce" + pos);
            basePattern = product.relationshipFrom(categoryStart, "HAS").relationshipFrom(categoryEnd, "PARENT").min(0);
            currentStmt = currentStmt.match(basePattern).where(
                categoryEnd.internalId().isEqualTo(Cypher.literalOf(category)) // Safe from injection as is long
            ).with(product);
        }
        statement = currentStmt
            .withDistinct(product)
            .returning(product)
            .orderBy(orderBy.getOrderByClause().apply(product))
            .limit(pageSize);

        return cypherRenderer.render(statement.build());
    }

    @Getter
    @RequiredArgsConstructor
    public enum OrderBy {
        NONE(BY_ID, ORDER_ID, p -> p.getId().toString(), s -> Map.of(FROM_ID, StringUtils.isEmpty(s) ? -1L : Long.parseLong(s))),
        PRICE_ASC(BY_PRICE_ASC, ORDER_PRICE_ASC, ENCODE_PRICE_PAGE_ID, DECODE_PRICE_PAGE_ID),
        PRICE_DESC(BY_PRICE_DESC, ORDER_PRICE_DESC, ENCODE_PRICE_PAGE_ID, DECODE_PRICE_PAGE_ID_DESC);

        private final Function<Node, Condition> pageSkipClause;
        private final Function<Node, SortItem[]> orderByClause;
        private final Function<@NotNull Product, String> encodePageId;
        private final Function<@Nullable String, Map<String, ?>> decodePageId;
    }
}
