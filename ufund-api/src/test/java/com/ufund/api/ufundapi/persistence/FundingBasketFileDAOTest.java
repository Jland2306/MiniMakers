package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

// Meant to test the persistence functionality of FundingBasketFileDAO
// @author Lance Cordova

@Tag("Persistence-tier")
public class FundingBasketFileDAOTest {
    private FundingBasketFileDAO fundingBasketFileDAO;
    private CupboardDAO mockCupboardDao; // cupboard for validation
    private Need[] testNeeds;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupFundingBasketFileDAO() throws IOException {
        objectMapper = new ObjectMapper();

        // replica array of Needs
        testNeeds = new Need[3];
        testNeeds[0] = new Need("Need 1", 12.34, 100, "Type 1");
        testNeeds[1] = new Need("Need 2", 56.78, 200, "Type 2");
        testNeeds[2] = new Need("Need 3", 90.12, 300, "Type 3");

        mockCupboardDao = mock(CupboardDAO.class);
        // mock cupboardDao to return name of needs in the testNeeds array
        when(mockCupboardDao.getNeed(testNeeds[0].getName())).thenReturn(testNeeds[0]);
        when(mockCupboardDao.getNeed(testNeeds[1].getName())).thenReturn(testNeeds[1]);
        when(mockCupboardDao.getNeed(testNeeds[2].getName())).thenReturn(testNeeds[2]);

        fundingBasketFileDAO = new FundingBasketFileDAO("", objectMapper, mockCupboardDao);
    }

    @Test
    public void testAddandGetNeed() throws IOException {
        // Invoke -- adds the need to the basket
        fundingBasketFileDAO.addNeed(testNeeds[0]);
        Need found = fundingBasketFileDAO.getNeed(testNeeds[0].getName());

        // Analyze
        assertNotNull(found);
        assertEquals(testNeeds[0].getQuantity(), found.getQuantity());
    }

    @Test
    public void testGetNeedNotFound() {
        // Invoke
        Need found = fundingBasketFileDAO.getNeed("Nonexistent Need");

        // Analyze
        assertEquals(null, found);
    }

    @Test
    public void testGetAllNeeds() throws IOException {
        // Setup
        fundingBasketFileDAO.addNeed(testNeeds[0]);
        fundingBasketFileDAO.addNeed(testNeeds[1]);
        fundingBasketFileDAO.addNeed(testNeeds[2]);

        // Invoke
        Need[] needs = assertDoesNotThrow(() -> fundingBasketFileDAO.getAllNeeds(),
                "Unexpected exception thrown");

        // Analyze
        assertNotNull(needs);
        assertEquals(3, needs.length);
    }

    @Test
    public void testGetAllNeedsEmpty() throws IOException {
        // Invoke
        Need[] needs = assertDoesNotThrow(() -> fundingBasketFileDAO.getAllNeeds(),
                "Unexpected exception thrown");

        // Analyze
        assertNotNull(needs);
        assertEquals(0, needs.length);
    }

    @Test
    public void testRemoveNeed() throws IOException {
        // Setup - add a need to the basket
        fundingBasketFileDAO.addNeed(testNeeds[0]);

        // Invoke
        boolean removeRes = fundingBasketFileDAO.removeNeed(testNeeds[0].getName());

        // Analyze
        assertEquals(true, removeRes);
        assertEquals(null, fundingBasketFileDAO.getNeed(testNeeds[0].getName()));
    }

    @Test
    public void testRemoveNeedNotFound() throws IOException {
        // Invoke
        boolean removeRes = fundingBasketFileDAO.removeNeed("NeedNotFound");

        // Analyze
        assertEquals(false, removeRes);
    }

    @Test
    public void testAddNeedCheckEmptyCase() throws IOException {
        // Setup - created need that's essentially nonexistent
        Need nullNeed = null; 

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(nullNeed);

        // Analyze
        assertEquals(false, checkRes);
    }

    @Test
    public void testAddNeedCheckNotInStock() throws IOException {
        // Setup - created need with 0 stock quantity
        Need outOfStockNeed = new Need("Out of Stock Need", 0.0, 0, "Type 4");

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(outOfStockNeed);

        // Analyze
        assertEquals(false, checkRes);
    }

    @Test
    public void testAddNeedCheckNullName() throws IOException {
        // Setup
        Need invalid = new Need(null, 1.0, 1, "Type");

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(invalid);

        // Analyze
        assertEquals(false, checkRes);
    }

    @Test
    public void testAddNeedCheckEmptyName() throws IOException {
        // Setup
        Need invalid = new Need("  ", 1.0, 1, "Type");

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(invalid);

        // Analyze
        assertEquals(false, checkRes);
    }

    @Test
    public void testAddNeedCheckCupboardMissing() throws IOException {
        // Setup - cupboard does not contain the need
        Need missing = new Need("Missing Need", 1.0, 1, "Type");
        when(mockCupboardDao.getNeed(missing.getName())).thenReturn(null);

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(missing);

        // Analyze
        assertEquals(false, checkRes);
    }

    @Test
    public void testAddNeedCheckNegativeQuantity() throws IOException {
        // Setup - invalid negative quantity
        Need invalidQty = new Need(testNeeds[0].getName(), 0.0, -5, "Type 4");

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(invalidQty);

        // Analyze
        assertEquals(false, checkRes);
    }

    @Test
    public void testAddNeedCheckOverQuantity() throws IOException {
        // Setup - created need request that's over the quantity in the cupboard
        Need replicaNeed = new Need(testNeeds[0].getName(), 0.0, testNeeds[0].getQuantity() + 1, "Type 4");
        when(mockCupboardDao.getNeed(replicaNeed.getName())).thenReturn(testNeeds[0]);

        // Invoke & Analyze
        assertThrows(IllegalArgumentException.class, () -> fundingBasketFileDAO.addNeedCheck(replicaNeed));
    }

    @Test
    public void testAddNeedCheckSuccess() throws IOException {
        // Setup - request is within cupboard quantity
        Need validNeed = new Need(testNeeds[0].getName(), 0.0, 1, "Type");
        when(mockCupboardDao.getNeed(validNeed.getName())).thenReturn(testNeeds[0]);

        // Invoke
        boolean checkRes = fundingBasketFileDAO.addNeedCheck(validNeed);

        // Analyze
        assertEquals(true, checkRes);
    }

    @Test
    public void testAddNeedNullCase() throws IOException {
        // Setup - cupboard does not have the need
        Need missing = new Need("Missing Need", 0.0, 1, "Type");
        when(mockCupboardDao.getNeed(missing.getName())).thenReturn(null);

        // Invoke
        Need result = fundingBasketFileDAO.addNeed(missing);

        // Analyze
        assertEquals(null, result);
    }

    @Test
    public void testAddNeedQuantityUpdate() throws IOException {
        // Setup - add the same need twice
        fundingBasketFileDAO.addNeed(testNeeds[0]);
        int initialQuantity = testNeeds[0].getQuantity();
        int additionalQuantity = 5;
        int expectedQuantity = initialQuantity + additionalQuantity;
        Need additional = new Need(testNeeds[0].getName(), testNeeds[0].getCost(), additionalQuantity, testNeeds[0].getType());

        // Invoke
        Need updated = fundingBasketFileDAO.addNeed(additional);

        // Analyze
        assertNotNull(updated);
        assertEquals(expectedQuantity, updated.getQuantity());
        // ensure the stored copy is also updated
        assertEquals(expectedQuantity, fundingBasketFileDAO.getNeed(testNeeds[0].getName()).getQuantity());
    }

    @Test
    public void testClearNeeds() throws IOException {
        // Setup - add needs to the basket
        fundingBasketFileDAO.addNeed(testNeeds[0]);
        fundingBasketFileDAO.addNeed(testNeeds[1]);

        // Invoke
        fundingBasketFileDAO.clear();

        // Analyze
        assertEquals(0, fundingBasketFileDAO.getAllNeeds().length);
        assertEquals(null, fundingBasketFileDAO.getNeed(testNeeds[0].getName()));
    }

    @Test
    public void testConstructorNullFilename() throws IOException {
        // Setup
        FundingBasketFileDAO dao = new FundingBasketFileDAO(null, objectMapper, mockCupboardDao);
        when(mockCupboardDao.getNeed(testNeeds[0].getName())).thenReturn(testNeeds[0]);

        // Invoke 
        Need result = dao.addNeed(testNeeds[0]);

        // Analyze
        assertNotNull(result);
        assertEquals(testNeeds[0].getName(), result.getName());
    }

    @TempDir
    Path tempDir;

    @Test
    public void testConstructorWithFileLoad() throws IOException {
        // Setup - basket.json is loaded in with 2 needs
        File basketFile = tempDir.resolve("basket.json").toFile();
        new ObjectMapper().writeValue(basketFile, new Need[]{ testNeeds[0], testNeeds[1] });

        // Invoke - constructor calls load() as a file is loaded in
        FundingBasketFileDAO dao = new FundingBasketFileDAO(
                basketFile.getAbsolutePath(), new ObjectMapper(), mockCupboardDao);

        // Analyze - checks for the 2 needs being loaded
        assertEquals(2, dao.getAllNeeds().length);
        assertNotNull(dao.getNeed(testNeeds[0].getName()));
        assertNotNull(dao.getNeed(testNeeds[1].getName()));
    }

    @Test
    public void testSavePersistsToFile() throws IOException {
        // Setup - start from an empty JSON file and writes a need to it
        File basketFile = tempDir.resolve("basket_save.json").toFile();
        new ObjectMapper().writeValue(basketFile, new Need[0]);

        FundingBasketFileDAO dao = new FundingBasketFileDAO(
                basketFile.getAbsolutePath(), new ObjectMapper(), mockCupboardDao);

        // Invoke - addNeed triggers save() internally
        dao.addNeed(testNeeds[0]);

        // Analyze - reload from same file and checks if the need persisted after adding to file
        FundingBasketFileDAO reloadInstance = new FundingBasketFileDAO(
                basketFile.getAbsolutePath(), new ObjectMapper(), mockCupboardDao);
        assertNotNull(reloadInstance.getNeed(testNeeds[0].getName()));
        assertEquals(testNeeds[0].getQuantity(), reloadInstance.getNeed(testNeeds[0].getName()).getQuantity());
    }

    @Test
    public void testSaveEmptyFilename() throws IOException {
        // Setup
        FundingBasketFileDAO dao = new FundingBasketFileDAO("", new ObjectMapper(), mockCupboardDao);

        // Invoke
        dao.addNeed(testNeeds[0]);

        // Analyz
        assertNotNull(dao.getNeed(testNeeds[0].getName()));
    }

    @Test
    public void testSaveNullFilename() throws IOException {
        // Setup
        FundingBasketFileDAO dao = new FundingBasketFileDAO(null, new ObjectMapper(), mockCupboardDao);

        // Invoke
        dao.addNeed(testNeeds[0]);

        // Analyz
        assertNotNull(dao.getNeed(testNeeds[0].getName()));
    }
}
