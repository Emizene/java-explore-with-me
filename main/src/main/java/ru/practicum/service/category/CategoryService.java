package ru.practicum.service.category;

import ru.practicum.CategoryDto;
import ru.practicum.NewCategoryDto;
import ru.practicum.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);

    Category getCategoryEntityById(Long catId);
}
