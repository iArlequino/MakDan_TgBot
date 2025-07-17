package ru.Daniil_Makarov.tgBot.service;

import ru.Daniil_Makarov.tgBot.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    List<Category> findByParentId(Long parentId);
    Category findById(Long id);
    List<Category> findRootCategories();
    List<Category> findSubCategories(Long parentId);
}
