package com.ufund.api.ufundapi.model;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

// Represents the Need entity in the application. 
// @author Lance Cordova

public class Need {
    private static final Logger LOG = Logger.getLogger(Need.class.getName());

    // Package private for tests
    static final String STRING_FORMAT = "Need [name=%s, cost=%.2f, quantity=%d, type=%s]";

    
    @JsonProperty("name") private String name;
    @JsonProperty("cost") private double cost; // BigDecimal might be better because of how it rounds? need further discussion
    @JsonProperty("quantity") private int quantity;
    @JsonProperty("type") private String type;
 
        /**
     * Create a hero with the given id and name
     * @param name The name of the need
     * @param cost The cost of the need
     * @param quantity The quantity of the need
     * @param type The type of the need
     * 
     * {@literal @}JsonProperty is used in serialization and deserialization
     * of the JSON object to the Java object in mapping the fields.  If a field
     * is not provided in the JSON object, the Java field gets the default Java
     * value, i.e. 0 for int
     */
    public Need(@JsonProperty("name") String name, @JsonProperty("cost") double cost, @JsonProperty("quantity") int quantity, @JsonProperty("type") String type) {
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.type = type;
    }

    /**
     * Sets the name of the need - necessary for JSON object to Java object deserialization
     * @param name The name of the need
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the cost of the need 
     * @param cost The cost of the need
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the quantity of the need 
     * @param quantity The quantity of the need
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the type of the need 
     * @param type The type of the need
     */
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(STRING_FORMAT,name,cost,quantity,type);
    }

    /**
     * Sets the name of the need
     * @param name The new name of the need
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the cost of the need
     * @param cost The new cost of the need
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Sets the quantity of the need
     * @param quantity The new quantity of the need
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the type of the need
     * @param type The new type of the need
     */
    public void setType(String type) {
        this.type = type;   
    }


}
