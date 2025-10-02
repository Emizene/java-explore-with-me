package ru.practicum.service.category;

import ru.practicum.model.Category;
import ru.practicum.categoryDto.CategoryDto;
import ru.practicum.categoryDto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);

    Category getCategoryEntityById(Long catId);
}
