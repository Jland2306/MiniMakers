package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

// Unit test suite for the Need class.
// Verifies constructor, getters, setters, and toString behavior.
// @author Khushi Relan

@Tag("Model")
public class NeedTest {
    // Tests that the constructor correctly initializes all fields
    // and that getter methods return expected values   
    @Test
    public void testStandardCase() {
        // Setup
        String expectedName = "Some Need";
        double expectedCost = 12.34;
        int expectedQuantity = 100;
        String expectedType = "Some Type";
        // Invoke
        Need need = new Need(expectedName, expectedCost, expectedQuantity, expectedType);
        // Analyze
        assertEquals(expectedName, need.getName());
        assertEquals(expectedCost, need.getCost());
        assertEquals(expectedQuantity, need.getQuantity());
        assertEquals(expectedType, need.getType());
    }

    // Tests that the toString() method returns the correctly formatted string
    @Test
    public void testToString() {
        // Setup
        String expectedName = "Some Need";
        double expectedCost = 12.34;
        int expectedQuantity = 100;
        String expectedType = "Some Type";
        String expectedString = String.format("Need [name=%s, cost=%.2f, quantity=%d, type=%s]", expectedName, expectedCost, expectedQuantity, expectedType);
        // Invoke
        Need need = new Need(expectedName, expectedCost, expectedQuantity, expectedType);
        // Analyze
        assertEquals(expectedString, need.toString());
    }

    // Tests that setName correctly updates the name field
    @Test
    public void testSetName() {
        //Invoke
        Need need = new Need("Old", 10.0, 5, "Type");
        need.setName("New");
        //Analyze
        assertEquals("New", need.getName());
    }

    // Tests that setCost correctly updates the cost field
    @Test
    public void testSetCost() {
        //Invoke
        Need need = new Need("Need", 10.0, 5, "Type");
        need.setCost(20.5);
        //Analyze
        assertEquals(20.5, need.getCost());
    }
    
    // Tests that setQuantity correctly updates the quantity field
    @Test
    public void testSetQuantity() {
        //Invoke
        Need need = new Need("Need", 10.0, 5, "Type");
        need.setQuantity(50);
        //Analyze
        assertEquals(50, need.getQuantity());
    }

    // Tests that setType correctly updates the type field
    @Test
    public void testSetType() {
        //Invoke
        Need need = new Need("Need", 10.0, 5, "OldType");
        need.setType("NewType");
        //Analyze
        assertEquals("NewType", need.getType());
    }
}
