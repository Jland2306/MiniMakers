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

import java.util.logging.Level;


import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.FundingBasketDAO;

/**
 * Handles the REST API requests for the Funding Basket resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Lance Cordova
 */
@RestController
@RequestMapping("basket")
public class FundingBasketController {
    private static final Logger LOG = Logger.getLogger(FundingBasketController.class.getName());
    private FundingBasketDAO fundingBasketDao;
    private CupboardDAO cupboardDao;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param fundingBasketDao The {@link FundingBasketDAO Funding Basket Data Access Object} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public FundingBasketController(FundingBasketDAO fundingBasketDao, CupboardDAO cupboardDao) {
        this.fundingBasketDao = fundingBasketDao;
        this.cupboardDao = cupboardDao;
    }

    /**
     * Adds a {@linkplain Need need} to the basket.
     * 
     * @param need - The {@link Need need} to add
     * 
     * @return ResponseEntity with the added {@link Need need} and HTTP status of OK<br>
     * ResponseEntity with HTTP status of NOT_FOUND if the need does not exist in the cupboard<br>
     * ResponseEntity with HTTP status of CONFLICT if the requested quantity exceeds cupboard stock<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("")
    public ResponseEntity<Need> addNeed(@RequestBody Need need) {
        LOG.info("POST /basket " + need);
        try {
            Need added = fundingBasketDao.addNeed(need);
            if (added != null)
                return new ResponseEntity<Need>(added, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
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
        LOG.info("GET /basket/" + name);
        try {
            Need need = fundingBasketDao.getNeed(name);
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
    * Responds to the GET request for all {@link Need needs} in the funding basket
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
        LOG.info("GET /basket");
        try {
            Need[] needs = fundingBasketDao.getAllNeeds();
            return new ResponseEntity<>(needs, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  
        }
    }

    /**
     * Checkout the entire funding basket.
     * 
     * Checks applied to each {@link Need need} in the basket:
     * - If the cupboard item is missing, returns 410 (GONE)
     * - If the requested quantity exceeds cupboard stock, returns 409 (CONFLICT)
     * - Otherwise the cupboard quantity is decremented (and the item deleted from cupboard when it reaches 0)
     * 
     * The basket is emptied at the end.
     */
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout() {
        LOG.info("POST /basket/checkout");
        try {
            Need[] basketNeeds = fundingBasketDao.getAllNeeds();

            // iterative check 
            for (Need basketNeed : basketNeeds) {

                // Retrieves need info from cupboard to check against basketNeed
                Need cupboardNeed = cupboardDao.getNeed(basketNeed.getName());

                // Out of stock / Nonexistence check
                if (cupboardNeed == null) {
                    String msg = String.format("%s is out of stock", basketNeed.getName());
                    return new ResponseEntity<>(msg, HttpStatus.GONE);
                }

                // Quantity check
                if (cupboardNeed.getQuantity() < basketNeed.getQuantity()) {
                    String msg = String.format("%s has only %d in cupboard but %d was requested.",
                            basketNeed.getName(), cupboardNeed.getQuantity(), basketNeed.getQuantity());
                    return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
                }
            }

            // Apply changes
            for (Need basketNeed : basketNeeds) {

                Need cupboardNeed = cupboardDao.getNeed(basketNeed.getName());
                int remainAmt = cupboardNeed.getQuantity() - basketNeed.getQuantity();

                if (remainAmt <= 0) {
                    cupboardDao.deleteNeed(cupboardNeed.getName());
                } else {
                    cupboardNeed.setQuantity(remainAmt);
                    cupboardDao.updateNeed(cupboardNeed);
                }
            }

            // Clear basket once all changes are successful.
            fundingBasketDao.clear();

            return new ResponseEntity<>("Checkout successful", HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a {@linkplain Need need} with the given name
     * 
     * @param name The name of the {@link Need need} to delete
     * 
     * @return ResponseEntity HTTP status of OK if deleted<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Need> removeNeed(@PathVariable String name) {
        LOG.info("DELETE /basket/" + name);
        try {
            boolean deleteRes = fundingBasketDao.removeNeed(name);
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
}
