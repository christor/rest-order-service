package org.christor.ordersystem.model;

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
 * Represents an administrators request to restock a number of items.
 * 
 * @author crued
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestockOrder implements Serializable {

    @GeneratedValue
    @Id
    private Long id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date orderDate;

    @ManyToMany
    @RestResource(exported = false)
    private List<Product> products;

}
