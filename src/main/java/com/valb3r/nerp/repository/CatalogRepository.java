package com.valb3r.nerp.repository;

import com.valb3r.nerp.domain.catalog.Catalog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface CatalogRepository extends PagingAndSortingRepository<Catalog, Long> {
}
