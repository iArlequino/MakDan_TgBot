package ru.Daniil_Makarov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.entity.Category;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
}
