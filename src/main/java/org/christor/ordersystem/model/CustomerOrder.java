package org.christor.ordersystem.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Represents a customer's order in the system, including the related products
 * that were purchased.
 * 
 * @author crued
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder implements Serializable {

    @GeneratedValue
    @Id
    private Long id;

    private String name;
    private String address;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date orderDate;

    @ManyToMany
    @RestResource(exported = false)
    private List<Product> products;

}
