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
import org.neo4j.ogm.annotation.Required;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@NodeEntity
public class Category {

    public static final String HAS_CATEGORY = "HAS";
    public static final String PARENT_CATEGORY = "PARENT";

    @Id
    @GeneratedValue
    private Long id;

    @Required
    @NotBlank
    private String name;

    @Relationship(type = PARENT_CATEGORY, direction = Relationship.INCOMING)
    private Set<Category> parents;
}
