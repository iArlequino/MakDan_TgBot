package ru.Daniil_Makarov.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Daniil_Makarov.tgBot.model.ClientOrder;

@RepositoryRestResource(collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
}
