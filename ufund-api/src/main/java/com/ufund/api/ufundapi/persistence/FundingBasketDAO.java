package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import com.ufund.api.ufundapi.model.Need;

/**
 * Defines the interface for Funding Basket object persistence
 * 
 * @author Lance Cordova
 *
 * 
**/

public interface FundingBasketDAO {

    /**
     * Retrieves a {@linkplain Need need} with the given name
     * 
     * @param name The name of the {@link Need need} to get
     * 
     * @return a {@link Need need} object with the matching name
     * <br>
     * null if no {@link Need need} with a matching name is found
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need getNeed(String name) throws IOException;

    /**
     * Adds and saves a {@linkplain Need need} to the need basket
     * 
     * @param need {@linkplain Need need} object to be added and saved
     * <br>
     *
     * @return new {@link Need need} if successful, false otherwise 
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need addNeed(Need need) throws IOException;

    /**
     * Creates and saves a {@linkplain Need need}
     * 
     * @return current array of {@link Need needs} if successful, empty otherwise 
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need[] getAllNeeds();

    /**
     * Removes a {@linkplain Need need} from the funding basket
     * 
     * @return true if the {@link Need need} was deleted
     * <br>
     * false if need with the given name does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean removeNeed(String name) throws IOException;

    /**
     * Clears all items from the funding basket.
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    void clear() throws IOException;
}