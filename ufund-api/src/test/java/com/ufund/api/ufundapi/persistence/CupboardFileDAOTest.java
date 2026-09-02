package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

// Meant to test the persistence functionality of CupboardFileDAO
// @author Lance Cordova

@Tag("Persistence-tier")
public class CupboardFileDAOTest {
    CupboardFileDAO cupboardFileDAO;
    Need[] testNeeds;
    ObjectMapper mockObjectMapper;

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * @throws IOException
     */
    @BeforeEach // essentially initializes a session with needs in the cupboard every time
    public void setupCupboardFileDAO() throws IOException {
        mockObjectMapper = new ObjectMapper();
        
        // replica array of Needs
        testNeeds = new Need[3];
        testNeeds[0] = new Need("Need 1", 12.34, 100, "Type 1");
        testNeeds[1] = new Need("Need 2", 56.78, 200, "Type 2");
        testNeeds[2] = new Need("Need 3", 90.12, 300, "Type 3");

        // it's just run without a file and in-memory instead
        cupboardFileDAO = new CupboardFileDAO("", mockObjectMapper); 
    }

    @Test
    public void testGetNeed() throws IOException {
        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]); //assuming it works

        // Invoke
        Need found = cupboardFileDAO.getNeed("Need 1");

        // Analyze
        assertNotNull(found);
        assertEquals(testNeeds[0].getName(), found.getName());
        assertEquals(testNeeds[0].getCost(), found.getCost(), 0.001);
        assertEquals(testNeeds[0].getQuantity(), found.getQuantity());
        assertEquals(testNeeds[0].getType(), found.getType());
    }

    @Test
    public void testCreateNeed() {
        // Setup
        Need need = new Need("Need 4", 123.45, 400, "Type 4");

        // Invoke
        Need result = assertDoesNotThrow(() -> cupboardFileDAO.createNeed(need),
                                "Unexpected exception thrown");

        // Analyze
        assertNotNull(result);
        Need actual = cupboardFileDAO.getNeed(need.getName()); // gets Need from Cupboard

        assertEquals(actual.getName(),need.getName());
        assertEquals(actual.getCost(),need.getCost(),0.001); // delta seems to be required for comparing doubles
        assertEquals(actual.getQuantity(),need.getQuantity());
        assertEquals(actual.getType(),need.getType());
    }

    @Test
    public void testDeleteNeed() throws IOException {
        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]); //assuming it works

        // Invoke
        boolean result = assertDoesNotThrow(() -> cupboardFileDAO.deleteNeed("Need 1"),
                            "Unexpected exception thrown");

        // Analyze
        assertEquals(result,true);
        // We check the internal tree map size against the length
        // of the test needs array - 1 (because of the delete)
        // Because needs attribute of CupboardFileDAO is package private
        // we can access it directly
        assertEquals(cupboardFileDAO.needs.size(), 0);
    }
    
    @Test
    public void testGetNeedNotFound() {
        // Invoke
        Need need = cupboardFileDAO.getNeed("Need 404 Not Found");

        // Analyze
        assertEquals(need,null);
    }

    @Test
    public void testCreateNeedConflict() throws IOException {

        // Setup
        Need need = new Need("Need 4", 123.45, 400, "Type 4");
        cupboardFileDAO.createNeed(need);

        // Invoke
        // we're expecting a null result
        Need result = assertDoesNotThrow(() -> cupboardFileDAO.createNeed(need),
                                                    "Duplicate needs throws and returns null");
        // Analyze
        assertEquals(result, null);
    }

    @Test
    public void testUpdateNeed() throws IOException {
        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]);
        Need updated = new Need(testNeeds[0].getName(), 99.99, 999, "Updated Type");

        // Invoke
        Need result = cupboardFileDAO.updateNeed(updated);

        // Analyze
        assertNotNull(result);
        assertEquals(updated.getCost(), result.getCost(), 0.001);
        assertEquals(updated.getQuantity(), result.getQuantity());
        assertEquals(updated.getType(), result.getType());

        // Test if the update is reflected in the cupboard
        Need stored = cupboardFileDAO.getNeed(testNeeds[0].getName());
        assertEquals(999, stored.getQuantity());
        assertEquals("Updated Type", stored.getType());
    }

    @Test
    public void testUpdateNeedIgnoreCase() throws IOException {
        // Setup - case for insensitive name matching on update
        cupboardFileDAO.createNeed(testNeeds[0]);
        Need updated = new Need(testNeeds[0].getName().toUpperCase(), 1.23, 321, "Case Sensitive?");

        // Invoke
        Need result = cupboardFileDAO.updateNeed(updated);

        // Analyze
        assertNotNull(result);
        assertEquals(1.23, result.getCost(), 0.001);
        assertEquals(321, result.getQuantity());
        assertEquals("Case Sensitive?", result.getType());

        // Ensure the stored entry is updated even though the case changed
        Need stored = cupboardFileDAO.getNeed(testNeeds[0].getName());
        assertEquals(321, stored.getQuantity());
        assertEquals("Case Sensitive?", stored.getType());
    }

    @Test
    public void testUpdateNeedSecondEntryMatch() throws IOException {
        // Setup - ensure loop iterates past the first entry before finding a match
        cupboardFileDAO.createNeed(testNeeds[0]);
        cupboardFileDAO.createNeed(testNeeds[1]);
        Need updated = new Need(testNeeds[1].getName(), 5.55, 555, "Second Entry Update");

        // Invoke
        Need result = cupboardFileDAO.updateNeed(updated);

        // Analyze
        assertNotNull(result);
        assertEquals(5.55, result.getCost(), 0.001);
        assertEquals(555, result.getQuantity());
        assertEquals("Second Entry Update", result.getType());

        // Validate that first entry was not changed
        Need first = cupboardFileDAO.getNeed(testNeeds[0].getName());
        assertEquals(testNeeds[0].getCost(), first.getCost(), 0.001);
    }

    @Test
    public void testUpdateNeedNotFound() throws IOException {
        // Setup
        Need updated = new Need("Nonexistent", 99.99, 999, "Updated Type");

        // Invoke
        Need result = cupboardFileDAO.updateNeed(updated);

        // Analyze
        assertEquals(null, result);
    }

    @Test
    public void testDeleteNeedNotFound() throws IOException {

        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]); //assuming it works
        cupboardFileDAO.createNeed(testNeeds[1]);

        // Invoke
        boolean result = assertDoesNotThrow(() -> cupboardFileDAO.deleteNeed("Need 404 Not Found"),
                                                "Unexpected exception thrown");

        // Analyze
        assertEquals(result,false);
        assertEquals(cupboardFileDAO.needs.size(), 2); // checks if nothing is deleted
    }

    @Test
    public void testGetAllNeeds() throws IOException {
        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]);
        cupboardFileDAO.createNeed(testNeeds[1]);
        cupboardFileDAO.createNeed(testNeeds[2]);

        // Invoke
        Need[] needs = assertDoesNotThrow(() -> cupboardFileDAO.getAllNeeds(),
                            "Unexpected exception thrown");

        // Analyze
        assertNotNull(needs);
        assertEquals(3, needs.length);
    }

    @Test
    public void testGetAllNeedsEmpty() throws IOException {
        // Invoke
        Need[] needs = assertDoesNotThrow(() -> cupboardFileDAO.getAllNeeds(),
                            "Unexpected exception thrown");

        // Analyze
        assertNotNull(needs);
        assertEquals(0, needs.length);
    }

    @Test
    public void testSearchNeedsFound() throws IOException {
        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]); // "Need 1"
        cupboardFileDAO.createNeed(testNeeds[1]); // "Need 2"
        cupboardFileDAO.createNeed(testNeeds[2]); // "Need 3"

        // Invoke
        Need[] results = assertDoesNotThrow(() -> cupboardFileDAO.searchNeeds("Need"),
                            "Unexpected exception thrown");

        // Analyze
        assertNotNull(results);
        assertEquals(3, results.length);
    }

    @Test
    public void testSearchNeedsNotFound() throws IOException {
        // Setup
        cupboardFileDAO.createNeed(testNeeds[0]);
        cupboardFileDAO.createNeed(testNeeds[1]);

        // Invoke
        Need[] results = assertDoesNotThrow(() -> cupboardFileDAO.searchNeeds("XYZ"),
                            "Unexpected exception thrown");

        // Analyze
        assertNotNull(results);
        assertEquals(0, results.length);
    }

    @Test
    public void testConstructorNullFilename() throws IOException {
        // Setup - DAO with null filename
        CupboardFileDAO dao = new CupboardFileDAO(null, mockObjectMapper);
        Need need = new Need("NullFilename", 1.0, 1, "Type");

        // Invoke
        Need result = assertDoesNotThrow(() -> dao.createNeed(need));

        // Analyze
        assertNotNull(result);
        assertEquals("NullFilename", result.getName());
    }

    @TempDir
    Path tempDir;

    @Test
    public void testConstructorFileLoad() throws IOException {
        // Setup - essentially copying a File to be used
        File cupboardFile = tempDir.resolve("cupboard.json").toFile();
        new ObjectMapper().writeValue(cupboardFile, testNeeds);

        // Invoke - constructor calls load() on cupboardFile
        CupboardFileDAO dao = new CupboardFileDAO(cupboardFile.getAbsolutePath(), new ObjectMapper());

        // Analyze - checks if it is all loaded correctly
        assertEquals(3, dao.getAllNeeds().length);
        assertNotNull(dao.getNeed(testNeeds[0].getName()));
    }

    @Test
    public void testSavePersistsToFile() throws IOException {
        // Setup - new file created and saved a need to
        File cupboardFile = tempDir.resolve("cupboard_save.json").toFile();
        new ObjectMapper().writeValue(cupboardFile, new Need[0]);

        CupboardFileDAO dao = new CupboardFileDAO(cupboardFile.getAbsolutePath(), new ObjectMapper());

        // Invoke - createNeed triggers save() internally
        dao.createNeed(testNeeds[0]);

        // Analyze - reload the file to check if need is persisted
        CupboardFileDAO reloadInstance = new CupboardFileDAO(cupboardFile.getAbsolutePath(), new ObjectMapper());
        assertNotNull(reloadInstance.getNeed(testNeeds[0].getName()));
        assertEquals(testNeeds[0].getQuantity(), reloadInstance.getNeed(testNeeds[0].getName()).getQuantity());
    }

    @Test
    public void testSaveEmptyFilename() throws IOException {
        // Setup 
        CupboardFileDAO dao = new CupboardFileDAO("", new ObjectMapper());
        
        // Invoke
        dao.createNeed(testNeeds[0]);

        // Analyze 
        assertNotNull(dao.getNeed(testNeeds[0].getName()));
    }
}

