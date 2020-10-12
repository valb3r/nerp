package com.valb3r.nerp.service;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.StatementBuilder;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.neo4j.cypherdsl.core.Cypher.node;

@Service
public class ProductByCategoryQueryBuilder {

    private static final Renderer cypherRenderer = Renderer.getDefaultRenderer();

    /*
     * MATCH (p:Product)<-[:HAS]-(cs:Category)<-[:PARENT*0..]-(ce:Category) WHERE id(ce) = 12927
     * WITH p AS prod
     * MATCH (p:Product)<-[:HAS]-(cs:Category)<-[:PARENT*0..]-(ce:Category) WHERE p=prod AND id(ce) = 12928
     * RETURN p LIMIT 10
     */
    // @Param("categories") Set<Long> categories, @Param("fromIdExclusive") long fromIdExclusive, @Param("pageSize") long pageSize
    public String buildQuery(List<Long> categories, long fromIdExclusive, long pageSize) {
        var product = node("Product").named("p");
        var categoryStart = node("Category").named("cs");
        var categoryEnd = node("Category").named("ce");
        var categoryEndId = categoryEnd.internalId();
        var productId = product.internalId();

        var basePattern = product.relationshipFrom(categoryStart, "HAS").relationshipFrom(categoryEnd, "PARENT").min(0);
        var baseStatement = Cypher.match(basePattern);

        StatementBuilder.BuildableStatement statement;
        var fromProductId = productId.gte(Cypher.literalOf(fromIdExclusive)); // Safe from injection as is long

        if (categories.isEmpty()) {
            statement = baseStatement.where(fromProductId).returning(product).limit(pageSize);
        } else {
            StatementBuilder.OrderableOngoingReadingAndWithWithoutWhere currentStmt = null;

            for (int pos = 0; pos < categories.size(); ++pos) {
                var category = categories.get(pos);
                if (0 == pos) {
                    currentStmt = baseStatement.where(
                        fromProductId.and(categoryEndId.isEqualTo(Cypher.literalOf(category)))  // Safe from injection as is long
                    ).with(product);
                } else  {
                    categoryStart = node("Category").named("cs" + pos);
                    categoryEnd = node("Category").named("ce" + pos);
                    basePattern = product.relationshipFrom(categoryStart, "HAS").relationshipFrom(categoryEnd, "PARENT").min(0);
                    currentStmt = currentStmt.match(basePattern).where(
                        categoryEnd.internalId().isEqualTo(Cypher.literalOf(category)) // Safe from injection as is long
                    ).with(product);
                }
            }
            statement = currentStmt.returning(product).limit(pageSize);
        }

        return cypherRenderer.render(statement.build());
    }
}
