package org.christor.ordersystem.service;

import org.christor.ordersystem.model.RestockOrder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "x-orders")
public interface RestockOrdersRestRepository extends PagingAndSortingRepository<RestockOrder, Long> {
}
