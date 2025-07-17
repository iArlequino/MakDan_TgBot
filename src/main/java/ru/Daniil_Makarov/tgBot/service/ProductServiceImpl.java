package ru.Daniil_Makarov.tgBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.entity.Category;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.repository.ProductRepository;
import ru.Daniil_Makarov.tgBot.repository.OrderProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final ClientService clientService;

    public ProductServiceImpl(ProductRepository productRepository, OrderProductRepository orderProductRepository, ClientService clientService) {
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.clientService = clientService;
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> searchByNameAndCategory(String name, Long categoryId) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId);
    }

    @Override
    public List<Product> findPopularProducts(int limit) {
        return orderProductRepository.findAll().stream()
                .collect(Collectors.groupingBy(op -> op.getProduct(), Collectors.summingInt(op -> op.getCountProduct())))
                .entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Product addProductToCart(Long chatId, Long productId) {
        Product product = findById(productId);
        if (product == null) {
            return null;
        }
        ClientOrder cart = clientService.getOrCreateCart(chatId);
        clientService.addToCart(cart, product);
        return product;
    }

    @Override
    public String getCartText(Long chatId) {
        ClientOrder cart = clientService.getOrCreateCart(chatId);
        List<Product> products = clientService.getCartProducts(cart);

        if (products.isEmpty()) {
            return "Корзина пуста";
        }

        StringBuilder message = new StringBuilder("Ваш заказ:\n");
        for (Product product : products) {
            message.append(String.format("%s = %.2f руб.\n", product.getName(), product.getPrice()));
        }
        message.append(String.format("\nИтого: %.2f руб.", cart.getTotal()));
        return message.toString();
    }
}


