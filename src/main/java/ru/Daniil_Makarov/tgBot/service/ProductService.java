package ru.Daniil_Makarov.tgBot.service;

import ru.Daniil_Makarov.tgBot.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> searchByName(String name);
    List<Product> searchByNameAndCategory(String name, Long categoryId);
    List<Product> findPopularProducts(int limit);
}
