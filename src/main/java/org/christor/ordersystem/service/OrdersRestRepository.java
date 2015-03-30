package org.christor.ordersystem.service;

import org.christor.ordersystem.model.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "x-orders")
public interface OrdersRestRepository extends PagingAndSortingRepository<CustomerOrder, Long> {
    Page<CustomerOrder> findByName(String name, Pageable pageable);
}
