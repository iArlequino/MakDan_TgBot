package ru.Daniil_Makarov.tgBot.service;

import ru.Daniil_Makarov.tgBot.entity.Client;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.entity.OrderProduct;

import java.util.List;

public interface ClientService {
    List<ClientOrder> getOrdersByClientId(Long clientId);
    List<Product> getProductsByClientId(Long clientId);
    List<Client> searchByName(String name);
    Client findByExternalId(Long externalId);
    ClientOrder createOrder(Client client, double total);
    OrderProduct addProductToOrder(ClientOrder order, Product product, int count);
    Client createClient(Long externalId, String fullName, String phone, String address);
    ClientOrder getOrCreateCart(Long externalId);
    void addToCart(ClientOrder cart, Product product);
    void submitOrder(ClientOrder cart);
    List<Product> getCartProducts(ClientOrder cart);
}
