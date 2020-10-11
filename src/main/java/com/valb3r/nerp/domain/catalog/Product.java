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

import java.util.Set;

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

    private String name;

    @Relationship(type = IN_CATALOG, direction = Relationship.INCOMING)
    private Set<Catalog> catalogs;

    @Relationship(type = HAS_CATEGORY)
    private Set<Category> categories;
}
