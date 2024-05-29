package me.dev.onlinestoreapi.service;

import me.dev.onlinestoreapi.dto.CategoryDTO;
import me.dev.onlinestoreapi.model.Category;

import java.util.List;

public interface CategoryService {
    Category addCategory(CategoryDTO categoryDTO);

    Category getCategoryById(long categoryId);

    List<Category> getAllCategories();

    void updateCategory(long categoryId, CategoryDTO categoryDTO);

    void deleteCategory(long categoryId);
}
