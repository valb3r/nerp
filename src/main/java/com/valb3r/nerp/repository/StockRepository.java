package com.valb3r.nerp.repository;

import com.valb3r.nerp.domain.warehouse.Stock;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface StockRepository extends PagingAndSortingRepository<Stock, Long> {
}
