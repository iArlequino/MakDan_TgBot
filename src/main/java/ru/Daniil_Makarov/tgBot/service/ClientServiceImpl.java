package ru.Daniil_Makarov.tgBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Daniil_Makarov.tgBot.entity.Client;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.repository.ClientOrderRepository;
import ru.Daniil_Makarov.tgBot.repository.ClientRepository;
import ru.Daniil_Makarov.tgBot.repository.OrderProductRepository;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final OrderProductRepository orderProductRepository;

    public ClientServiceImpl(
            ClientRepository clientRepository,
            ClientOrderRepository clientOrderRepository,
            OrderProductRepository orderProductRepository) {
        this.clientRepository = clientRepository;
        this.clientOrderRepository = clientOrderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public List<ClientOrder> getOrdersByClientId(Long clientId) {
        return clientOrderRepository.findByClientId(clientId);
    }

    @Override
    public List<Product> getProductsByClientId(Long clientId) {
        List<ClientOrder> orders = getOrdersByClientId(clientId);
        return orderProductRepository.findDistinctProductsByClientOrderIn(orders);
    }

    @Override
    public List<Client> searchByName(String name) {
        return clientRepository.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    public Client findByExternalId(Long externalId) {
        return clientRepository.findByExternalId(externalId);
    }
}
