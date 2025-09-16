package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.Category;
import ru.practicum.CategoryDto;
import ru.practicum.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toEntity(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        checkCategoryExists(catId);
        checkCategoryNotUsed(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = getCategoryEntityById(catId);
        category.setName(categoryDto.name());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto)
                .getContent();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = getCategoryEntityById(catId);
        return categoryMapper.toDto(category);
    }

    @Override
    public Category getCategoryEntityById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + catId));
    }

    private void checkCategoryExists(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category not found with id: " + catId);
        }
    }

    private void checkCategoryNotUsed(Long catId) {
        long eventsCount = eventRepository.countByCategoryId(catId);
        if (eventsCount > 0) {
            throw new ConflictException("Cannot delete category with associated events");
        }
    }
}