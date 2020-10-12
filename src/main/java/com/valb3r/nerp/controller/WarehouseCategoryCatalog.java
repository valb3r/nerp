package com.valb3r.nerp.controller;

import com.valb3r.nerp.repository.CategoryRepository;
import com.valb3r.nerp.repository.mappings.CategoryPath;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouse-catalog")
public class WarehouseCategoryCatalog {

    private final CategoryRepository categoryRepository;

    @GetMapping("/{warehouseId}")
    public List<CategoryPath> categories(@PathVariable long warehouseId, @RequestParam(value = "atLeastStock", defaultValue = "0") long atLeastStock) {
        return categoryRepository.findCategoryCatalogForWarehouse(warehouseId, atLeastStock);
    }
}
