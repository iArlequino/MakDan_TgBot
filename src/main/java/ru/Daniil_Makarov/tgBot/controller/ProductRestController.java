package ru.Daniil_Makarov.tgBot.controller;

import org.springframework.web.bind.annotation.*;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/rest/products")
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) Long categoryId) {
        if (name != null && categoryId != null) {
            return productService.searchByNameAndCategory(name, categoryId);
        } else if (name != null) {
            return productService.searchByName(name);
        } else if (categoryId != null) {
            return productService.findByCategoryId(categoryId);
        } else {
            return List.of();
        }
    }

    @GetMapping("/popular")
    public List<Product> getPopularProducts(@RequestParam int limit) {
        return productService.findPopularProducts(limit);
    }
}
