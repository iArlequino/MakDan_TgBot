package ru.Daniil_Makarov.tgBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Daniil_Makarov.tgBot.entity.*;
import ru.Daniil_Makarov.tgBot.repository.*;

import java.util.List;

@SpringBootTest
public class FillingTests {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Test
    void fillData() {
        Category pizza = createCategory("Пицца", null);
        Category rolls = createCategory("Роллы", null);
        Category burgers = createCategory("Бургеры", null);
        Category drinks = createCategory("Напитки", null);

        Category classicRolls = createCategory("Классические роллы", rolls);
        Category bakedRolls = createCategory("Запеченные роллы", rolls);
        Category sweetRolls = createCategory("Сладкие роллы", rolls);
        Category sets = createCategory("Наборы", rolls);

        Category classicBurgers = createCategory("Классические бургеры", burgers);
        Category spicyBurgers = createCategory("Острые бургеры", burgers);

        Category soda = createCategory("Газированные напитки", drinks);
        Category energy = createCategory("Энергетические напитки", drinks);
        Category juice = createCategory("Соки", drinks);
        Category other = createCategory("Другие", drinks);

        createProduct("Маргарита", "Классическая итальянская пицца с томатами и моцареллой", 599.0, pizza);
        createProduct("Пепперони", "Острая пицца с колбасой пепперони", 649.0, pizza);
        createProduct("4 сыра", "Пицца с моцареллой, пармезаном, дор блю и чеддером", 699.0, pizza);

        createProduct("Филадельфия", "Классические роллы с лососем и сливочным сыром", 499.0, classicRolls);
        createProduct("Калифорния", "Роллы с крабовым мясом и авокадо", 450.0, classicRolls);
        createProduct("Унаги маки", "Роллы с копченым угрем", 420.0, classicRolls);

        createProduct("Хот ролл лосось", "Запеченные роллы с лососем под спайси соусом", 460.0, bakedRolls);
        createProduct("Хот ролл угорь", "Запеченные роллы с угрем под соусом унаги", 480.0, bakedRolls);
        createProduct("Хот ролл креветка", "Запеченные роллы с креветкой под сырным соусом", 440.0, bakedRolls);

        createProduct("Банановый ролл", "Сладкие роллы с бананом и шоколадом", 350.0, sweetRolls);
        createProduct("Клубничный ролл", "Сладкие роллы с клубникой и сливочным сыром", 380.0, sweetRolls);
        createProduct("Манговый ролл", "Сладкие роллы с манго и карамелью", 390.0, sweetRolls);

        createProduct("Сет №1", "Филадельфия, Калифорния, Унаги маки - 30 штук", 1299.0, sets);
        createProduct("Сет №2", "Хот роллы микс - 24 штуки", 1199.0, sets);
        createProduct("Сет №3", "Сладкий сет - все виды сладких роллов", 999.0, sets);

        createProduct("Чизбургер", "Классический бургер с говяжьей котлетой и сыром", 299.0, classicBurgers);
        createProduct("Гамбургер", "Классический бургер с говяжьей котлетой", 259.0, classicBurgers);
        createProduct("Биг бургер", "Двойной бургер с говядиной и беконом", 399.0, classicBurgers);

        createProduct("Мексиканский", "Острый бургер с халапеньо", 349.0, spicyBurgers);
        createProduct("Дьябло", "Острый бургер с перцем чили", 359.0, spicyBurgers);
        createProduct("Огненный", "Экстра острый бургер с соусом табаско", 369.0, spicyBurgers);

        createProduct("Кола", "Газированный напиток кола 0.5л", 89.0, soda);
        createProduct("Спрайт", "Газированный напиток спрайт 0.5л", 89.0, soda);
        createProduct("Фанта", "Газированный напиток фанта 0.5л", 89.0, soda);

        createProduct("Ред Булл", "Энергетический напиток 0.25л", 129.0, energy);
        createProduct("Монстр", "Энергетический напиток 0.45л", 159.0, energy);
        createProduct("Адреналин Раш", "Энергетический напиток 0.45л", 149.0, energy);

        createProduct("Апельсиновый", "Свежевыжатый апельсиновый сок 0.3л", 159.0, juice);
        createProduct("Яблочный", "Свежевыжатый яблочный сок 0.3л", 149.0, juice);
        createProduct("Морковный", "Свежевыжатый морковный сок 0.3л", 139.0, juice);

        createProduct("Чай зеленый", "Зеленый чай 0.4л", 79.0, other);
        createProduct("Чай черный", "Черный чай 0.4л", 79.0, other);
        createProduct("Кофе американо", "Классический американо 0.3л", 129.0, other);
        long categoriesCount = categoryRepository.count();
        long productsCount = productRepository.count();
        
        Assertions.assertEquals(14, categoriesCount);
        Assertions.assertEquals(33, productsCount);
    }

    private Category createCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String description, Double price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        return productRepository.save(product);
    }

    private Client createClient(Long externalId, String fullName, String phone, String address) {
        Client client = new Client();
        client.setExternalId(externalId);
        client.setFullName(fullName);
        client.setPhoneNumber(phone);
        client.setAddress(address);
        return clientRepository.save(client);
    }

    private ClientOrder createOrder(Client client, int status, double total) {
        ClientOrder order = new ClientOrder();
        order.setClient(client);
        order.setStatus(status);
        order.setTotal(total);
        return clientOrderRepository.save(order);
    }

    private OrderProduct createOrderProduct(ClientOrder order, Product product, int count) {
        OrderProduct op = new OrderProduct();
        op.setClientOrder(order);
        op.setProduct(product);
        op.setCountProduct(count);
        return orderProductRepository.save(op);
    }

    @Test
    void checkData() {
        System.out.println("Всего категорий: " + categoryRepository.count());
        System.out.println("\nСписок всех категорий:");
        categoryRepository.findAll().forEach(category -> 
            System.out.println(category.getName() + 
                (category.getParent() != null ? " (родитель: " + category.getParent().getName() + ")" : "")));
        System.out.println("\nВсего продуктов: " + productRepository.count());
        System.out.println("\nСписок всех продуктов:");
        productRepository.findAll().forEach(product -> 
            System.out.println(product.getName() + " (" + product.getCategory().getName() + ") - " + product.getPrice() + " руб."));
    }

    @Test
    void printCategoryTree() {
        categoryRepository.findAll().stream()
                .filter(c -> c.getParent() == null)
                .forEach(c -> printCategory(c, ""));
    }

    private void printCategory(Category category, String indent) {
        System.out.println(indent + "|- " + category.getName());
        categoryRepository.findAll().stream()
                .filter(c -> c.getParent() != null && category.getId().equals(c.getParent().getId()))
                .forEach(child -> printCategory(child, indent + "   "));
    }

    @Test
    void fillClientsAndOrders() {
        Client client1 = createClient(1001L, "Иван Петров", "+79990001122", "ул. Ленина, 1");
        Client client2 = createClient(1002L, "Роман Иванов", "+79990002233", "ул. Гагарина, 2");
        Client client3 = createClient(1003L, "Мария Смирнова", "+79990003344", "ул. Победы, 3");

        List<Product> products = productRepository.findAll();
        Product pizza = products.stream().filter(p -> p.getName().contains("Маргарита")).findFirst().orElse(null);
        Product roll = products.stream().filter(p -> p.getName().contains("Филадельфия")).findFirst().orElse(null);
        Product burger = products.stream().filter(p -> p.getName().contains("Чизбургер")).findFirst().orElse(null);

        ClientOrder order1 = createOrder(client1, 1, pizza.getPrice());
        ClientOrder order2 = createOrder(client2, 1, roll.getPrice());
        ClientOrder order3 = createOrder(client1, 2, pizza.getPrice() + burger.getPrice());
        ClientOrder order4 = createOrder(client3, 1, burger.getPrice());

        createOrderProduct(order1, pizza, 1);
        createOrderProduct(order2, roll, 2);
        createOrderProduct(order3, pizza, 1);
        createOrderProduct(order3, burger, 1);
        createOrderProduct(order4, burger, 2);

        Assertions.assertEquals(3, clientRepository.count());
        Assertions.assertEquals(4, clientOrderRepository.count());
        Assertions.assertEquals(6, orderProductRepository.count());
    }
}

