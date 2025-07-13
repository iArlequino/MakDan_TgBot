package ru.Daniil_Makarov.tgBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Daniil_Makarov.tgBot.entity.Client;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.entity.OrderProduct;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.repository.ClientOrderRepository;
import ru.Daniil_Makarov.tgBot.repository.ClientRepository;
import ru.Daniil_Makarov.tgBot.repository.OrderProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final OrderProductRepository orderProductRepository;

    public ClientServiceImpl(ClientRepository clientRepository, ClientOrderRepository clientOrderRepository, OrderProductRepository orderProductRepository) {
        this.clientRepository = clientRepository;
        this.clientOrderRepository = clientOrderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public List<ClientOrder> getOrdersByClientId(Long clientId) {
        return clientOrderRepository.findAll().stream()
                .filter(o -> o.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsByClientId(Long clientId) {
        List<ClientOrder> orders = getOrdersByClientId(clientId);
        return orderProductRepository.findAll().stream()
                .filter(op -> orders.stream().anyMatch(o -> o.getId().equals(op.getClientOrder().getId())))
                .map(OrderProduct::getProduct)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> searchByName(String name) {
        String lower = name.toLowerCase();
        return clientRepository.findAll().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
