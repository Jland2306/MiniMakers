package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

/**
 * Implements the functionality for JSON file-based persistence for Need
 * (To be implemented in later Sprints)
 * 
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the instance into other classes as needed
 * 
 * @author Lance Cordova
 */
@Component
public class CupboardFileDAO implements CupboardDAO {
    private static final Logger LOG = Logger.getLogger(CupboardFileDAO.class.getName());
    Map<String,Need> needs;   // Provides a local cache of the need objects
                                // so that we don't need to read from the file
                                // each time; Compared to HeroesAPI, we use String as the key
    private ObjectMapper objectMapper;  // Provides conversion between Need
                                        // objects and JSON text format written
                                        // to the file
    private String filename;    // Filename to read from and write to

    /**
     * Creates a Cupboard File Data Access Object
     * 
     * @param filename Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    public CupboardFileDAO(@Value("${cupboard.file:}") String filename,ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
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
     * Loads the cupboard contents from the configured JSON file.
     *
     * <p>If the file does not exist, it will be created as an empty JSON array.
     * If the file exists but is empty, it is treated as an empty list of needs.
     *
     * @return true if load succeeded
     * @throws IOException if the file cannot be read or parsed
     */
    private boolean load() throws IOException {

        needs = new TreeMap<>(); //init
        File f = new File(filename); // creates cupboard file to write needs in
        Need[] needArray = objectMapper.readValue(f, Need[].class);
        for (Need n : needArray) { // similar to HeroesAPI, but iterates through by name instead of id
            needs.put(key(n.getName()), n);
        }
        return true;
    }   

    // ensures unique name/key
    private String key(String name) {
        return name.trim().toLowerCase();
    }

    /**
     * Retrieves a need from the cupboard by name.
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
     * Creates a new need in the cupboard.
     *
     * <p>If a need with the same name already exists, no change is made.
     *
     * @param need the need to create
     * @return the created need when successful, or null if a need with the same name exists
     * @throws IOException if persistence fails
     */
    @Override
    public Need createNeed(Need need) throws IOException {
        synchronized(needs) {
            String k = key(need.getName());

            // puts the key and need in the map
            // if key already exists, return null 
            // Reference: https://www.geeksforgeeks.org/java/map-interface-in-java/ 
            Need prev = needs.putIfAbsent(k, need); 
            if (prev == null) { 
                save(); 
                return need;
            }  
            return null; // conflict (already exists)
        }
    }

    private void save() throws IOException {
        if (filename.isEmpty())
            return;

        // write the needs to the file for persistence
        objectMapper.writeValue(new File(filename), needs.values().toArray(new Need[0]));
    }

    /**
     * Deletes a need from the cupboard by name.
     *
     * @param name the name of the need to delete
     * @return true if the need existed and was deleted; false otherwise
     * @throws IOException if persistence fails
     */
    @Override
    public boolean deleteNeed(String name) throws IOException {
        synchronized(needs) { // helps to ensure it's thread-safe
            String k = key(name);
            if (needs.containsKey(k)) {
                needs.remove(k);
                save();
                return true;
            }
            else
                return false;
        }
    }

    /**
     * Searches for needs where the name contains the provided query string.
     *
     * @param query the search text
     * @return array of matching needs (empty if none match)
     */
    public Need[] searchNeeds(String query)  {
        Need[] allNeeds = getAllNeeds();
        List<Need> results = new ArrayList<>();

        for (Need n : allNeeds) {
            if (n.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(n);
            }
        }

        return results.toArray(new Need[0]);
    }


    /**
     * Updates an existing need in the cupboard.
     *
     * @param need the need with updated values
     * @return the updated need if found; otherwise null
     * @throws IOException if persistence fails
     */
    public Need updateNeed(Need need) throws IOException{
        Need[] allNeeds = getAllNeeds();
        for (Need n : allNeeds) {
            if (n.getName().equalsIgnoreCase(need.getName())) {
                n.setCost(need.getCost());
                n.setQuantity(need.getQuantity());
                n.setType(need.getType());
                save();
                return n;
            }
        }
        return null; // not found
    }

    /**
     * Returns all needs currently stored in the cupboard.
     *
     * @return an array of all needs (never null)
     */
    public Need[] getAllNeeds() {
        return needs.values().toArray(new Need[0]);

    }
}
    

