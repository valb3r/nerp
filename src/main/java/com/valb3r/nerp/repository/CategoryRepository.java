package com.valb3r.nerp.repository;

import com.valb3r.nerp.domain.catalog.Category;
import com.valb3r.nerp.repository.mappings.CategoryPath;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    @RestResource(exported = false)
    @Query("MATCH path = (w:Warehouse)<-[:IN]-(s:Stock)-[:OF]->(p:Product)<-[:HAS]-(cs:Category) WHERE id(w) = $warehouseId " +
        "WITH DISTINCT cs " +
        "MATCH categories = (ce:Category)-[:PARENT*1..]->(cs:Category) " +
        "WITH DISTINCT categories\n" +
        "WITH " +
        "[" +
        "rel IN relationships(categories) " +
        "| {st: startNode(rel), en: endNode(rel)}" +
        "] AS paths " +
        "UNWIND paths AS result RETURN result.st AS start, result.en AS end")
    List<CategoryPath> findCategoryCatalogForWarehouse(@Param("warehouseId") long warehouseId);
}


