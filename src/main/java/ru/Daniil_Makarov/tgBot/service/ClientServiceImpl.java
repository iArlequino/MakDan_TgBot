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

    @Override
    public Client createClient(Long externalId, String fullName, String phone, String address) {
        Client client = new Client();
        client.setExternalId(externalId);
        client.setFullName(fullName);
        client.setPhoneNumber(phone);
        client.setAddress(address);
        return clientRepository.save(client);
    }

    @Override
    public ClientOrder createOrder(Client client, double total) {
        ClientOrder order = new ClientOrder();
        order.setClient(client);
        order.setStatus(1);
        order.setTotal(total);
        return clientOrderRepository.save(order);
    }

    @Override
    public OrderProduct addProductToOrder(ClientOrder order, Product product, int count) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setClientOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setCountProduct(count);
        return orderProductRepository.save(orderProduct);
    }

    @Override
    public ClientOrder getOrCreateCart(Long externalId) {
        final Client client = findByExternalId(externalId);
        if (client == null) {
            return clientOrderRepository.findByClientIdAndStatus(
                createClient(externalId, "User " + externalId, "Unknown", "Unknown").getId(), 
                0
            ).orElseGet(() -> {
                ClientOrder cart = new ClientOrder();
                cart.setClient(client);
                cart.setStatus(0);
                cart.setTotal(0.0);
                return clientOrderRepository.save(cart);
            });
        }

        return clientOrderRepository.findByClientIdAndStatus(client.getId(), 0)
                .orElseGet(() -> {
                    ClientOrder cart = new ClientOrder();
                    cart.setClient(client);
                    cart.setStatus(0); // 0 = В корзине
                    cart.setTotal(0.0);
                    return clientOrderRepository.save(cart);
                });
    }

    @Override
    public void addToCart(ClientOrder cart, Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setClientOrder(cart);
        orderProduct.setProduct(product);
        orderProduct.setCountProduct(1);
        orderProductRepository.save(orderProduct);
        
        // Обновляем общую сумму
        double total = cart.getTotal() + product.getPrice();
        cart.setTotal(total);
        clientOrderRepository.save(cart);
    }

    @Override
    public void submitOrder(ClientOrder cart) {
        cart.setStatus(1); // 1 = Оформлен
        clientOrderRepository.save(cart);
    }

    @Override
    public List<Product> getCartProducts(ClientOrder cart) {
        return orderProductRepository.findDistinctProductsByClientOrder(cart);
    }
}
