package com.ufund.api.ufundapi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UfundApiApplicationTests {

	@Test
	void contextLoads() {
	}

	// Added for 100% coverage
	// Essentially just tests that the main method runs without throwing an exception
	@Test
    public void testMain() {
        assertDoesNotThrow(() -> UfundApiApplication.main(new String[] {}));
    }

}
