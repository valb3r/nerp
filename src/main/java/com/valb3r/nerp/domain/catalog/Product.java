package com.valb3r.nerp.domain.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.valb3r.nerp.domain.catalog.Category.HAS_CATEGORY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@NodeEntity
public class Product {

    public static final String IN_CATALOG = "IN";

    @Id
    @GeneratedValue
    private Long id;

    @Required
    @NotBlank
    private String name;

    // Neo4j does not support BigDecimal directly
    private long priceInCents;

    private String priceCurrency;

    @Required
    @NotEmpty
    @Relationship(type = IN_CATALOG, direction = Relationship.INCOMING)
    private List<Catalog> catalogs;

    @Relationship(type = HAS_CATEGORY, direction = Relationship.INCOMING)
    private List<Category> categories;
}
