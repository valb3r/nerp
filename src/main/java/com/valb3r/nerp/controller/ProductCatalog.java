package com.valb3r.nerp.controller;

import com.valb3r.nerp.domain.catalog.Product;
import com.valb3r.nerp.service.ProductByCategoryQueryBuilder;
import lombok.RequiredArgsConstructor;
import org.neo4j.ogm.session.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-catalog")
public class ProductCatalog {

    private final Session session;
    private final ProductByCategoryQueryBuilder queryBuilder;

    @GetMapping
    public Iterable<Product> categories(
        @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
        @RequestParam(value = "fromIdExclusive", defaultValue = "-1") long fromIdExclusive,
        @RequestParam(value = "pageSize", defaultValue = "20") long pageSize
    ) {
        String query = queryBuilder.buildQuery(categories, fromIdExclusive, pageSize);
        return session.query(Product.class, query, Collections.emptyMap());
    }
}
