package com.valb3r.nerp.repository;

import com.valb3r.nerp.domain.catalog.Product;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
}
