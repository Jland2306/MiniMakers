package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import com.ufund.api.ufundapi.model.Need;

/**
 * Defines the interface for Cupboard object persistence
 * 
 * @author Lance Cordova
 * 
 */
public interface CupboardDAO {

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
     * Creates and saves a {@linkplain Need need}
     * 
     * @param need {@linkplain Need need} object to be created and saved
     * <br>
     * The id of the need object is ignored and a new unique id is assigned
     *
     * @return new {@link Need need} if successful, false otherwise 
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need createNeed(Need need) throws IOException;

    /**
     * Deletes a {@linkplain Need need} with the given name
     * 
     * @param name The name of the {@link Need need}
     * 
     * @return true if the {@link Need need} was deleted
     * <br>
     * false if need with the given name does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean deleteNeed(String name) throws IOException;

    Need[] getAllNeeds();

    Need[] searchNeeds(String query);

    Need updateNeed(Need need) throws IOException;
}
