package com.valb3r.nerp.domain.warehouse;

import com.valb3r.nerp.domain.catalog.Product;
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

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@NodeEntity
public class Stock {

    public static final String OF_PRODUCT = "OF";
    public static final String IN_WAREHOUSE = "IN";

    @Id
    @GeneratedValue
    private Long id;

    @Required
    @NotNull
    @Relationship(type = OF_PRODUCT, direction = Relationship.UNDIRECTED)
    private Product product;

    @Required
    @NotNull
    @Relationship(type = IN_WAREHOUSE)
    private Warehouse warehouse;

    private long balance;
}
