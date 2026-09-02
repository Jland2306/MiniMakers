package com.ufund.api.ufundapi.controller;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
/**
 * Test the Cupboard Controller class
 * Adapted from HeroesAPI
 * @author Jordan Landversicht
 */
@Tag("Controller")
public class CupboardControllerTest {
    private CupboardController cupboardController;
    private CupboardDAO mockCupboardDAO;

    /**
     * Before each test, create a new CupboardController object and inject
     * a mock Cupboard DAO
     */
    @BeforeEach
    public void setupCupboardController() {
        mockCupboardDAO = mock(CupboardDAO.class);
        cupboardController = new CupboardController(mockCupboardDAO);
    }

    @Test
    public void testGetNeed() throws IOException {  // getNeed may throw IOException
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");
        // When the same name is passed in, our mock Cupboard DAO will return the Need object
        when(mockCupboardDAO.getNeed(need.getName())).thenReturn(need);
        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(need.getName());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(need,response.getBody());
    }

    @Test
    public void testGetNeedFailed() throws IOException {  // getNeed may throw IOException
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");
        // the mockCupboard will return null simulating a need with an invalid name
        when(mockCupboardDAO.getNeed(need.getName())).thenReturn(null);
        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(need.getName());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals(null,response.getBody());
    }

    @Test
    public void testGetNeedHandleException() throws IOException {  // getNeed may throw IOException
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");

        // When getNeed is called on the Mock Cupboard DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeed(need.getName());

        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(need.getName());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testCreateNeed() throws IOException {  // createNeed may throw IOException
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");
        // when createNeed is called, return true simulating successful
        // creation and save
        when(mockCupboardDAO.createNeed(need)).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = cupboardController.createNeed(need);

        // Analyze
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(need,response.getBody());
    }

    @Test
    public void testCreateNeedFailed() throws IOException {  // createNeed may throw IOException
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");
        // when createNeed is called, return null simulating failed
        // creation and save
        when(mockCupboardDAO.createNeed(need)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = cupboardController.createNeed(need);

        // Analyze
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void testCreateNeedHandleException() throws IOException {  // createNeed may throw IOException
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");

        // When createNeed is called on the Mock Cupboard DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).createNeed(need);

        // Invoke
        ResponseEntity<Need> response = cupboardController.createNeed(need);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testGetAllNeeds() throws IOException {  // getAllNeeds may throw IOException
        // Setup
        Need firstNeed = new Need("Some Need", 12.34, 100, "Some Type");
        Need secondNeed = new Need("Another Need", 56.78, 200, "Another Type");
        Need[] expectedNeeds = new Need[]{firstNeed, secondNeed};

        // When the same name is passed in, our mock Cupboard DAO will return a list of needs
        when(mockCupboardDAO.getAllNeeds()).thenReturn(new Need[]{firstNeed, secondNeed});
        // Invoke
        ResponseEntity<Need[]> response = cupboardController.getAllNeeds();

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertArrayEquals(expectedNeeds,response.getBody()); // helps compare arrays of needs
    }

    @Test
    public void testGetAllNeedsHandleException() throws Exception {  // getAllNeeds may throw an exception
        // Invoke - When getAllNeeds is called on the Mock Cupboard DAO, throw a runtime exception
        doThrow(new RuntimeException()).when(mockCupboardDAO).getAllNeeds();
        ResponseEntity<Need[]> response = cupboardController.getAllNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testSearchNeeds() {
        // Setup
        Need firstNeed = new Need("Some Need", 12.34, 100, "Some Type");
        Need secondNeed = new Need("Another Need", 56.78, 200, "Another Type");
        Need[] expectedNeeds = new Need[]{firstNeed, secondNeed};

        // When the query is passed in, our mock Cupboard DAO will return matching needs
        when(mockCupboardDAO.searchNeeds("Some")).thenReturn(expectedNeeds);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds("Some");

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(expectedNeeds, response.getBody());
    }

    @Test
    public void testSearchNeedsNotFound() {
        // When no matches are found, dao returns an empty list
        when(mockCupboardDAO.searchNeeds("Nonexistent")).thenReturn(new Need[0]);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds("Nonexistent");

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSearchNeedsNullResults() {
        // Setup: When dao returns null, the controller should return NOT_FOUND
        when(mockCupboardDAO.searchNeeds("NullNeed")).thenReturn(null);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds("NullNeed");

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSearchNeedsHandleException() throws Exception {
        // Setup: Simulate throwing exception when searchNeeds is called on the mock DAO
        doThrow(new RuntimeException()).when(mockCupboardDAO).searchNeeds("Random Need");

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds("Random Need");

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateNeed() throws IOException {
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");

        // When updateNeed is called, return the updated need
        when(mockCupboardDAO.updateNeed(need)).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = cupboardController.updateNeed(need);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    public void testUpdateNeedNotFound() throws IOException {
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");

        // When updateNeed is called, return null to indicate not found
        when(mockCupboardDAO.updateNeed(need)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = cupboardController.updateNeed(need);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateNeedHandleException() throws IOException {
        // Setup
        Need need = new Need("Some Need", 12.34, 100, "Some Type");

        // When updateNeed is called on the mock, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).updateNeed(need);

        // Invoke
        ResponseEntity<Need> response = cupboardController.updateNeed(need);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testDeleteNeed() throws IOException {
        // When deleteNeed is called, return true to indicate success
        when(mockCupboardDAO.deleteNeed("Some Need")).thenReturn(true);

        // Invoke
        ResponseEntity<Need> response = cupboardController.deleteNeed("Some Need");

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteNeedNotFound() throws IOException {
        // When deleteNeed is called, return false to indicate not found
        when(mockCupboardDAO.deleteNeed("Some Need")).thenReturn(false);

        // Invoke
        ResponseEntity<Need> response = cupboardController.deleteNeed("Some Need");

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteNeedHandleException() throws IOException {
        // When deleteNeed is called on the mock, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).deleteNeed("Some Need");

        // Invoke
        ResponseEntity<Need> response = cupboardController.deleteNeed("Some Need");

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
