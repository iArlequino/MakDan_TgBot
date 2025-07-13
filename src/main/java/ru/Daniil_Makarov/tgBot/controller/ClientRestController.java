package ru.Daniil_Makarov.tgBot.controller;

import org.springframework.web.bind.annotation.*;
import ru.Daniil_Makarov.tgBot.entity.Client;
import ru.Daniil_Makarov.tgBot.entity.ClientOrder;
import ru.Daniil_Makarov.tgBot.entity.Product;
import ru.Daniil_Makarov.tgBot.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/rest/clients")
public class ClientRestController {
    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/search")
    public List<Client> searchClients(@RequestParam String name) {
        return clientService.searchByName(name);
    }

    @GetMapping("/{id}/orders")
    public List<ClientOrder> getClientOrders(@PathVariable Long id) {
        return clientService.getOrdersByClientId(id);
    }

    @GetMapping("/{id}/products")
    public List<Product> getClientProducts(@PathVariable Long id) {
        return clientService.getProductsByClientId(id);
    }
}
