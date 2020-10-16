package com.valb3r.nerp.controller;

import com.valb3r.nerp.domain.catalog.Product;
import com.valb3r.nerp.service.ProductByCategoryQueryBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.neo4j.ogm.session.Session;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-catalog")
public class ProductCatalog {

    private final Session session;
    private final ProductByCategoryQueryBuilder queryBuilder;

    @GetMapping
    public ProductPage categories(
        @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
        @RequestParam(value = "pageKey", defaultValue = "") String pageKey,
        @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
        @RequestParam(value = "order", defaultValue = "NONE") ProductByCategoryQueryBuilder.OrderBy orderBy
    ) {
        // Fetch 1 more product to check if the page is last
        int fetchPageSize = pageSize + 1;
        String query = queryBuilder.buildQuery(categories, fetchPageSize, orderBy);
        var resultSet = session.query(Product.class, query, orderBy.getDecodePageId().apply(pageKey));
        var products = StreamSupport.stream(resultSet.spliterator(), false).collect(Collectors.toList());

        if (products.size() == fetchPageSize && !products.isEmpty()) {
            String nextPageKey = orderBy.getEncodePageId().apply(products.get(pageSize - 1));
            return new ProductPage(products.subList(0, pageSize), nextPageKey, pageKey);
        }

        return new ProductPage(products, null, StringUtils.isEmpty(pageKey) ? null : pageKey);
    }

    @Data
    @AllArgsConstructor
    public static class ProductPage {

        private List<Product> products;
        private String nextPageKey;
        private String pageKey;
    }
}
