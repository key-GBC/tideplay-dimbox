package com.dimbox.product.controller;

import com.dimbox.common.result.Result;
import com.dimbox.product.dto.CategoryRequestDto;
import com.dimbox.product.dto.CategoryTreeNodeDto;
import com.dimbox.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<CategoryTreeNodeDto>> tree() {
        return Result.success(categoryService.tree());
    }

    @GetMapping("/admin-tree")
    public Result<List<CategoryTreeNodeDto>> adminTree() {
        return Result.success(categoryService.adminTree());
    }

    @PostMapping
    public Result<Long> add(@Valid @RequestBody CategoryRequestDto request) {
        return Result.success(categoryService.add(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Long id,
                               @Valid @RequestBody CategoryRequestDto request) {
        categoryService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return Result.success();
    }

}
