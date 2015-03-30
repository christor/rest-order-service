package org.christor.ordersystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Represents a product in the system.
 *
 * @author crued
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {

    public static final Product NO_SUCH_PRODUCT = Product.builder().stock(0L).build();

    @GeneratedValue
    @Id
    private Long id;

    private String name;
    private String description;
    private Long stock;
    private int price;

    @Transient
    private String href;

    /**
     * A helper function to build out a full URI for this entity in the REST
     * interface before we return this to the client.
     *
     * @param baseUri
     */
    public void updateUri(String baseUri) {
        if (this.href == null) {
            this.href = baseUri + "/products/" + getId();
        }
    }

}
