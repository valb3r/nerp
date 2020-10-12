package com.valb3r.nerp.repository;

import com.valb3r.nerp.domain.catalog.Category;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RepositoryRestResource
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    @Query("MATCH path = (w:Warehouse)<-[:IN]-(s:Stock)-[:OF]->(p:Product)<-[:HAS]-(cs:Category) WHERE id(w) = $warehouseId RETURN DISTINCT(cs)")
    Set<Category> findCategoryLeafsForWarehouse(@Param("warehouseId") long warehouseId);
}
