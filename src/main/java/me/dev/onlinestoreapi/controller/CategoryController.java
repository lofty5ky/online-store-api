package me.dev.onlinestoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.dto.CategoryDTO;
import me.dev.onlinestoreapi.model.Category;
import me.dev.onlinestoreapi.response.CategoryResponse;
import me.dev.onlinestoreapi.response.UpdateCategoryResponse;
import me.dev.onlinestoreapi.service.CategoryService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                        BindingResult result) {
        CategoryResponse categoryResponse = new CategoryResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            categoryResponse.setMessage("Creating a new product category fails");
            categoryResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category = categoryService.addCategory(categoryDTO);
        categoryResponse.setCategory(category);
        return ResponseEntity.ok(categoryResponse);
    }

    // Display all categories
    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam() int pageNo,
            @RequestParam() int pageSize) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Update existing category
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(@PathVariable("id") Long categoryId,
                                                                 @Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                .message("Updated category successfully")
                .build());
    }

    // Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                .message("Deleted category with id = " + categoryId)
                .build());
    }
}
