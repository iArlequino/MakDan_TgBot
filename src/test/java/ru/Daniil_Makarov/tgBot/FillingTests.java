package ru.Daniil_Makarov.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.Daniil_Makarov.tgBot.repository.ClientRepository;
import ru.Daniil_Makarov.tgBot.model.Client;
import ru.Daniil_Makarov.tgBot.repository.CategoryRepository;
import ru.Daniil_Makarov.tgBot.repository.ProductRepository;
import ru.Daniil_Makarov.tgBot.model.Category;
import ru.Daniil_Makarov.tgBot.model.Product;
import org.springframework.test.annotation.Commit;

@SpringBootTest
class FillingTests {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void createClients() {
        saveClient(1L, "Иванов Иван Иванович", "Улица Ленина, дом 42, квартира 15, г. Новосибирск");
        saveClient(2L, "Петров Петр Петрович", "Улица Правды, дом 22, квартира 1, г. Красноярск");
        saveClient(3L, "Сидоров Сидр Сидорович", "Улица Кирова, дом 19, квартира 45, г. Майкоп");
    }

    private void saveClient(Long externalID, String fullName, String address) {
        Client client = new Client();
        client.setAddress(address);
        client.setExternalId(externalID);
        client.setFullName(fullName);
        clientRepository.save(client);
    }

    @Test
    @Commit
    void fillCategoriesAndProducts() {
        // Корневые категории
        Category pizza = saveCategory("Пицца", null);
        Category rolls = saveCategory("Роллы", null);
        Category burgers = saveCategory("Бургеры", null);
        Category drinks = saveCategory("Напитки", null);

        // Подкатегории роллов
        Category classicRolls = saveCategory("Классические роллы", rolls);
        Category bakedRolls = saveCategory("Запеченные роллы", rolls);
        Category sweetRolls = saveCategory("Сладкие роллы", rolls);
        Category sets = saveCategory("Наборы", rolls);

        // Подкатегории бургеров
        Category classicBurgers = saveCategory("Классические бургеры", burgers);
        Category spicyBurgers = saveCategory("Острые бургеры", burgers);

        // Подкатегории напитков
        Category soda = saveCategory("Газированные напитки", drinks);
        Category energy = saveCategory("Энергетические напитки", drinks);
        Category juice = saveCategory("Соки", drinks);
        Category otherDrinks = saveCategory("Другие", drinks);

        // Для каждой подкатегории создаём минимум 3 товара
        createProductsForCategory(classicRolls, "Классический ролл");
        createProductsForCategory(bakedRolls, "Запечённый ролл");
        createProductsForCategory(sweetRolls, "Сладкий ролл");
        createProductsForCategory(sets, "Набор роллов");
        createProductsForCategory(classicBurgers, "Классический бургер");
        createProductsForCategory(spicyBurgers, "Острый бургер");
        createProductsForCategory(soda, "Газировка");
        createProductsForCategory(energy, "Энергетик");
        createProductsForCategory(juice, "Сок");
        createProductsForCategory(otherDrinks, "Напиток");
        createProductsForCategory(pizza, "Пицца");
    }

    private Category saveCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    private void createProductsForCategory(Category category, String baseName) {
        for (int i = 1; i <= 3; i++) {
            Product product = new Product();
            product.setName(baseName + " " + i);
            product.setDescription("Описание для " + baseName + " " + i);
            product.setPrice(100.0 + i * 10);
            product.setCategory(category);
            productRepository.save(product);
        }
    }
}