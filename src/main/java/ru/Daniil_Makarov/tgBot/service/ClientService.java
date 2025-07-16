package ru.Daniil_Makarov.tgBot.service;

import ru.Daniil_Makarov.tgBot.entity.Client;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.entity.Product;

import java.util.List;

public interface ClientService {
    List<ClientOrder> getOrdersByClientId(Long clientId);
    List<Product> getProductsByClientId(Long clientId);
    List<Client> searchByName(String name);
    Client findByExternalId(Long externalId);
}
