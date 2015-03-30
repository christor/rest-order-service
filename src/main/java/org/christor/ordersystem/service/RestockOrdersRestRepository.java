package org.christor.ordersystem.service;

import org.christor.ordersystem.model.RestockOrder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Provides access to the RestockOrder entities. These represent an administrators
 * request to restock an item.
 * 
 * @author crued
 */
@RepositoryRestResource(path = "x-restock-orders")
public interface RestockOrdersRestRepository extends PagingAndSortingRepository<RestockOrder, Long> {
}
