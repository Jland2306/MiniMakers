package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

/**
 * Implements the functionality for JSON file-based persistence for Need
 * 
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the instance into other classes as needed
 * 
 * @author Lance Cordova
 */
@Component
public class FundingBasketFileDAO implements FundingBasketDAO {
    private static final Logger LOG = Logger.getLogger(FundingBasketFileDAO.class.getName());
    Map<String,Need> needs;   // Provides a local cache of the need objects
                                // so that we don't need to read from the file
                                // each time; Compared to HeroesAPI, we use String as the key
    private ObjectMapper objectMapper;  // Provides conversion between Need
                                        // objects and JSON text format written
                                        // to the file
    private String filename;    // Filename to read from and write to
    private CupboardDAO cupboardDao; // validaion that needs in cupboard exist before adding

    /**
     * Creates a Funding Basket File Data Access Object
     * 
     * @param filename Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    public FundingBasketFileDAO(@Value("${basket.file:}") String filename, 
            ObjectMapper objectMapper,
            CupboardDAO cupboardDao) throws IOException {
        this.objectMapper = objectMapper;
        this.cupboardDao = cupboardDao;
        if (filename == null) { // error handling
            this.filename = "";
        } else {
            this.filename = filename;
        }
        
        // If no filename is provided, skip loading from file
        if (!this.filename.isEmpty()) {
            load();  // load the needs from the file
        } else {
            needs = new TreeMap<>(); // created map instead of loading from file
        }
    }

    /**
     * Loads the current basket contents from the configured JSON file.
     *
     * <p>If the file does not exist, it is created as an empty JSON array. If the file
     * exists but is empty, it is treated as an empty list of needs.
     *
     * @return true if the load succeeded
     * @throws IOException if the file cannot be read or parsed
     */
    private boolean load() throws IOException {

        needs = new TreeMap<>(); //init
        File f = new File(filename); // creates basket file to write needs in
        Need[] needArray = objectMapper.readValue(f, Need[].class);
        for (Need n : needArray) { // similar to HeroesAPI, but iterates through by name instead of id
            needs.put(key(n.getName()), n);
        }
        return true;
    }   

    /**
     * Persists the current contents of the basket to the configured JSON file.
     * <p>
     * The file's parent directories are created if they do not already exist.
     *
     * @throws IOException if the file cannot be written
     */
    private void save() throws IOException {
        if (filename.isEmpty())
            return;

        // write the basket contents to the file for persistence
        objectMapper.writeValue(new File(filename), needs.values().toArray(new Need[0]));
    }

    /**
     * Converts a need name into a key used in internal storage.
     *
     * @param name the need name
     * @return a normalized key string
     */
    private String key(String name) {
        return name.trim().toLowerCase();
    }

    /**
     * Retrieves a need from the basket by name.
     *
     * @param name the name of the need
     * @return the need if found; otherwise null
     */

    @Override
    public Need getNeed(String name) {
        synchronized(needs) { 
            return needs.get(key(name));
        }
    }

    /**
     * Returns all needs currently stored in the funding basket.
     *
     * @return an array of needs currently in the basket (never null)
     */
    public Need[] getAllNeeds() {
        synchronized(needs) {
            return needs.values().toArray(new Need[0]);
        }
    }

    /**
     * Validates whether a need can be added to the basket.
     *
     * <p>Validation includes:
     * <ul>
     *   <li>need is non-null with a non-empty name</li>
     *   <li>requested quantity is greater than zero</li>
     *   <li>the need exists in the cupboard</li>
     *   <li>requested quantity is not greater than the cupboard stock</li>
     * </ul>
     *
     * @param need the need to validate
     * @return true if the need can be added, otherwise false
     * @throws IOException if there is an error querying the cupboard
     */
    public boolean addNeedCheck(Need need) throws IOException {

        // check if need exists here
        if (need == null || need.getName() == null || need.getName().trim().isEmpty()) {
            return false; // invalid input
        }

        // check if the need is in stock
        if (need.getQuantity() <= 0) {
            return false; // invalid quantity requested
        }

         // calls cupboardDao to check for the need
        Need cupboardNeed = cupboardDao.getNeed(need.getName());
        if (cupboardNeed == null) {
            return false; // does not exist in cupboard
        }

        // quantity check if need requested exceeds quantity in cupboard
        if (need.getQuantity() > cupboardNeed.getQuantity()) {
            throw new IllegalArgumentException("Quantity requested exceeds cupboard stock");
        }

        return true;
    }

    /**
     * Adds a need to the basket.
     *
     * <p>If the need already exists in the basket, the quantities are summed.
     * Changes are persisted to disk after modification.
     *
     * @param need the need to add
     * @return the need stored in the basket after the operation, or null if the request is invalid
     * @throws IOException if persistence fails
     */
    @Override
    public Need addNeed(Need need) throws IOException {

        if (!addNeedCheck(need)) {
            return null; // invalid input or need not found in cupboard
        }

        synchronized(needs) {
            String k = key(need.getName());

            // Get full data from cupboard
            Need cupboardNeed = cupboardDao.getNeed(need.getName());

            // create new need with correct data
            Need newNeed = new Need(
                cupboardNeed.getName(),
                cupboardNeed.getCost(),
                need.getQuantity(),         
                cupboardNeed.getType()
            );

            Need basketNeed = needs.get(k);

            if (basketNeed != null) {
                // modifies quantity if need already exists in the basket
                basketNeed.setQuantity(basketNeed.getQuantity() + newNeed.getQuantity());
                save();
                return basketNeed;
            } else {
                // adds the need to the basket if it doesn't already exist
                needs.put(k, newNeed);
                save();
                return newNeed;
            }
        }
    }

    /**
     * Removes a need from the basket by name.
     *
     * @param name the name of the need to remove
     * @return true if an item was removed; false if it was not present
     * @throws IOException if persistence fails
     */
    @Override
    public boolean removeNeed(String name) throws IOException {
        synchronized(needs) {
            String k = key(name);
            if (needs.containsKey(k)) {
                needs.remove(k);
                save();
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all needs from the basket and persists the empty state.
     *
     * @throws IOException if persistence fails
     */
    @Override
    public void clear() throws IOException {
        synchronized (needs) {
            needs.clear(); // removes all items from the basket
            save();
        }
    }
}
