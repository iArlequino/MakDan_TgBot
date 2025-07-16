package ru.Daniil_Makarov.tgBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.repository.ProductRepository;
import ru.Daniil_Makarov.tgBot.repository.OrderProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    public ProductServiceImpl(ProductRepository productRepository, OrderProductRepository orderProductRepository) {
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByName(String name) {
        String lower = name.toLowerCase();
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByNameAndCategory(String name, Long categoryId) {
        String lower = name.toLowerCase();
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower) && p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
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
}
