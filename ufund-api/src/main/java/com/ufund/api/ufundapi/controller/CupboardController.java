package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.logging.Level;


import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.model.Need;

/**
 * Handles the REST API requests for the Cupboard resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Lance Cordova and Suhani Aggarwal
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("cupboard")
public class CupboardController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private CupboardDAO cupboardDao;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDao The {@link CupboardDAO Cupboard Data Access Object} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public CupboardController(CupboardDAO cupboardDao) {
        this.cupboardDao = cupboardDao;
    }

    /**
     * Creates a {@linkplain Need need} with the provided need object
     * 
     * @param need - The {@link Need need} to create
     * 
     * @return ResponseEntity with created {@link Need need} object and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of CONFLICT if {@link Need need} object already exists<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("")
    public ResponseEntity<Need> createNeed(@RequestBody Need need) {
        LOG.info("POST /cupboard " + need);
        try {
            Need createdNeed = cupboardDao.createNeed(need);
            if (createdNeed != null)
                return new ResponseEntity<Need>(createdNeed,HttpStatus.CREATED);
            else
                return new ResponseEntity<>(HttpStatus.CONFLICT); // already exists
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for a {@link Need need} for the given name
     * 
     * @param name The name used to locate the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/{name}")
    public ResponseEntity<Need> getNeed(@PathVariable String name) {
        LOG.info("GET /cupboard/" + name);
        try {
            Need need = cupboardDao.getNeed(name);
            if (need != null)
                return new ResponseEntity<Need>(need,HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Need need} with the given name
     * 
     * @param name The name of the {@link Need need} to delete
     * 
     * @return ResponseEntity HTTP status of OK if deleted<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Need> deleteNeed(@PathVariable String name) {
        LOG.info("DELETE /cupboard/" + name);
        try {
            boolean deleteRes = cupboardDao.deleteNeed(name);
            if (deleteRes)
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Updates a {@linkplain Need need} with the provided need object
     * <p>
     * The need is located by its name, and its cost, quantity, and type
     * are updated to match the provided need object.
     * 
     * @param need The {@link Need need} object containing updated fields
     * 
     * @return ResponseEntity with updated {@link Need need} object and HTTP status of OK if found and updated<br>
     * ResponseEntity with HTTP status of NOT_FOUND if {@link Need need} was not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("")
    public ResponseEntity<Need> updateNeed(@RequestBody Need need) {
        LOG.info("PUT /cupboard " + need);
        try {
            Need updatedNeed = cupboardDao.updateNeed(need);
            if (updatedNeed != null)
                return new ResponseEntity<Need>(updatedNeed,HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // not found
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    /**
     * Responds to the GET request to search for {@link Need needs} by partial name match
     * <p>
     * Returns all needs whose name contains the given query string (case-insensitive).
     * 
     * @param query The partial name string used to search for matching {@link Need needs}
     * 
     * @return ResponseEntity with an array of matching {@link Need needs} and HTTP status 
     * of OK if matches are found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if no matches are found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/search")
    public ResponseEntity<Need[]> searchNeeds(@RequestParam String query) {
        LOG.info("GET /cupboard/search?query=" + query);
        try {
            Need[] results = cupboardDao.searchNeeds(query); // search in DAO
            if (results != null && results.length > 0) {
                return new ResponseEntity<>(results, HttpStatus.OK);
            } else {
                // no matches found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } 
        catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
    * Responds to the GET request for all {@link Need needs} in the cupboard
    * <p>
    * Returns a list of all needs currently stored in the cupboard,
    * including each need's name, cost, quantity, and type.
    * 
    * @return ResponseEntity with an array of {@link Need needs} and 
    * HTTP status of OK<br>
    * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
    */

    @GetMapping("")
    public ResponseEntity<Need[]> getAllNeeds() {
        LOG.info("GET /cupboard");
        try {
            Need[] needs = cupboardDao.getAllNeeds();
            return new ResponseEntity<>(needs, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  
        }
    }


}








    
    
    

