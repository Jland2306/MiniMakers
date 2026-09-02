package com.ufund.api.ufundapi.controller;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.FundingBasketDAO;

/**
 * Test the Funding Basket Controller class
 * @author Lance Cordova
 */

@Tag("Controller")
public class FundingBasketControllerTest {
    private FundingBasketController fundingBasketController;
    private FundingBasketDAO mockBasketDao;
    private CupboardDAO mockCupboardDao;

    @BeforeEach
    public void setupFundingBasketController() {
        mockBasketDao = mock(FundingBasketDAO.class);
        mockCupboardDao = mock(CupboardDAO.class);
        fundingBasketController = new FundingBasketController(mockBasketDao, mockCupboardDao);
    }

     @Test
    public void testGetNeed() throws IOException {
        // Setup
        Need need = new Need("Some Need", 10.0, 1, "TYPE");
        when(mockBasketDao.getNeed("Some Need")).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.getNeed("Some Need");

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    public void testGetNeedNotFound() throws IOException {
        // Setup
        when(mockBasketDao.getNeed("Missing Need")).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.getNeed("Missing Need");

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetNeedHandleException() throws IOException {
        // Setup
        doThrow(new IOException()).when(mockBasketDao).getNeed("Error Need");

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.getNeed("Error Need");

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testAddNeed() throws IOException {
        // Setup - need created that exists in cupboard
        Need need = new Need("Some Need", 10.0, 1, "TYPE");
        when(mockBasketDao.addNeed(need)).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.addNeed(need);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(need.getName(), response.getBody().getName());
    }

    @Test
    public void testAddNeedNotFound() throws IOException {
        // Setup - need created that does not exist in cupboard
        Need need = new Need("Missing Need", 10.0, 1, "TYPE");
        when(mockBasketDao.addNeed(need)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.addNeed(need);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNeedTooHigh() throws IOException {
        // Setup - need created with quantity that exceeds cupboard stock
        Need need = new Need("Infinite", 10.0, 1000000000, "TYPE");
        doThrow(new IllegalArgumentException()).when(mockBasketDao).addNeed(need);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.addNeed(need);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testAddNeedHandleException() throws IOException {
        // Setup - simulate DAO IOException
        Need need = new Need("Error Need", 10.0, 1, "TYPE");
        doThrow(new IOException()).when(mockBasketDao).addNeed(need);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.addNeed(need);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testViewBasketMultipleNeeds() throws IOException {
        // Setup - mock basket DAO to return multiple needs in the basket
        Need n1 = new Need("A", 1.0, 1, "TYPE");
        Need n2 = new Need("B", 2.0, 2, "TYPE");
        Need[] expected = new Need[] { n1, n2 };
        when(mockBasketDao.getAllNeeds()).thenReturn(expected);

        // Invoke
        ResponseEntity<Need[]> response = fundingBasketController.getAllNeeds();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(expected, response.getBody());
    }

    @Test
    public void testViewBasketEmpty() throws IOException {
        // Setup - mock basket DAO to return empty array of needs in the basket
        when(mockBasketDao.getAllNeeds()).thenReturn(new Need[0]);

        // Invoke
        ResponseEntity<Need[]> response = fundingBasketController.getAllNeeds();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    @Test
    public void testViewBasketHandleException() throws IOException {
        // Setup - throw an exception from the DAO
        doThrow(new RuntimeException()).when(mockBasketDao).getAllNeeds();

        // Invoke
        ResponseEntity<Need[]> response = fundingBasketController.getAllNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testRemoveNeed() throws IOException {
        // Setup - removing a need that exists in the basket
        when(mockBasketDao.removeNeed("Some Need")).thenReturn(true);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.removeNeed("Some Need");

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedNotFound() throws IOException {
        // Setup - removing a need that does not exist in the basket
        when(mockBasketDao.removeNeed("NeedNotFound")).thenReturn(false);

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.removeNeed("NeedNotFound");

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveNeedHandleException() throws IOException {
        // Setup - simulate DAO IOException
        doThrow(new IOException()).when(mockBasketDao).removeNeed("SomeNeed");

        // Invoke
        ResponseEntity<Need> response = fundingBasketController.removeNeed("SomeNeed");

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCheckout() throws IOException {
        // Setup
        Need basketNeed = new Need("SomeNeed", 1.0, 2, "TYPE");
        Need cupboardNeed = new Need("SomeNeed", 1.0, 5, "TYPE");
        when(mockBasketDao.getAllNeeds()).thenReturn(new Need[] { basketNeed });
        when(mockCupboardDao.getNeed("SomeNeed")).thenReturn(cupboardNeed);

        // Invoke
        ResponseEntity<String> response = fundingBasketController.checkout();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // should update cupboard with remaining quantity 3 and clear basket
        assertEquals(3, cupboardNeed.getQuantity());
        // ensures these functions are called
        verify(mockCupboardDao).updateNeed(cupboardNeed);
        verify(mockBasketDao).clear();
    }

    @Test
    public void testCheckoutQuantityConflict() throws IOException {
        // Setup - basket requests more than cupboard has
        Need basketNeed = new Need("SomeNeed", 1.0, 10, "TYPE");
        Need cupboardNeed = new Need("SomeNeed", 1.0, 2, "TYPE");
        when(mockBasketDao.getAllNeeds()).thenReturn(new Need[] { basketNeed });
        when(mockCupboardDao.getNeed("SomeNeed")).thenReturn(cupboardNeed);

        // Invoke
        ResponseEntity<String> response = fundingBasketController.checkout();

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testCheckoutMissingStock() throws IOException {
        // Setup - basket has item but cupboard no longer has it
        Need basketNeed = new Need("Missing", 1.0, 1, "TYPE");
        when(mockBasketDao.getAllNeeds()).thenReturn(new Need[] { basketNeed });
        when(mockCupboardDao.getNeed("Missing")).thenReturn(null);

        // Invoke
        ResponseEntity<String> response = fundingBasketController.checkout();

        // Analyze
        assertEquals(HttpStatus.GONE, response.getStatusCode());
    }

    @Test
    public void testCheckoutHandleException() throws IOException {
        // Setup - throw IOException when retrieving cupboard data for a basket need
        Need basketNeed = new Need("Some Need", 1.0, 1, "TYPE");
        when(mockBasketDao.getAllNeeds()).thenReturn(new Need[] { basketNeed });
        doThrow(new IOException()).when(mockCupboardDao).getNeed("Some Need");

        // Invoke
        ResponseEntity<String> response = fundingBasketController.checkout();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCheckoutSuccessDelete() throws IOException {
        // Setup - cupboard quantity matches basket quantity
        Need basketNeed = new Need("SomeNeed", 1.0, 2, "TYPE");
        Need cupboardNeed = new Need("SomeNeed", 1.0, 2, "TYPE");
        when(mockBasketDao.getAllNeeds()).thenReturn(new Need[] { basketNeed });
        when(mockCupboardDao.getNeed("SomeNeed")).thenReturn(cupboardNeed);

        // Invoke
        ResponseEntity<String> response = fundingBasketController.checkout();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // ensures these functions are called
        verify(mockCupboardDao).deleteNeed("SomeNeed");
        verify(mockBasketDao).clear();
    }
}
