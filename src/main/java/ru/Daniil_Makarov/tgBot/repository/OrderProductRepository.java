package ru.Daniil_Makarov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Daniil_Makarov.tgBot.entity.OrderProduct;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.entity.Product;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query("SELECT DISTINCT op.product FROM OrderProduct op WHERE op.clientOrder IN :orders")
    List<Product> findDistinctProductsByClientOrderIn(List<ClientOrder> orders);
}
