package ru.Daniil_Makarov.tgBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Daniil_Makarov.tgBot.entity.Category;
import ru.Daniil_Makarov.tgBot.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(parentId))
                .collect(Collectors.toList());
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> findSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    public List<Category> findRootCategories() {
        return categoryRepository.findByParentIsNull();
    }
}
