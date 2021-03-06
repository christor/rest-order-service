package org.christor.ordersystem.service;

import java.util.Optional;
import org.christor.ordersystem.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Provides access to the products. Since this is a RepositoryRestResource, it
 * automatically provides a very nice HATEOS searching and paging mechanism
 * according to the exposed methods here.
 * 
 * @author crued
 */
@RepositoryRestResource(path = "products")
public interface ProductsRestRepository extends PagingAndSortingRepository<Product, Long> {

    Optional<Product> findById(Long id);
    
    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);
    Page<Product> findByDescriptionContaining(@Param("desc") String desc, Pageable pageable);
    Page<Product> findByNameContainingAndPriceLessThan(@Param("name") String name, @Param("price") int price, Pageable pageable);
}
