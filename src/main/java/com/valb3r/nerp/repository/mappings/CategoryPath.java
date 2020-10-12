package com.valb3r.nerp.repository.mappings;

import com.valb3r.nerp.domain.catalog.Category;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@Data
@QueryResult
public class CategoryPath {

    private List<Category> path;
}
