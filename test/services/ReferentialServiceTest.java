package services;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ReferentialServiceTest extends ProtoPollTest {

	@Test
	public void testGetLanguages() {
		// TODO Make this test independent from configuration.
		assertNotNull(ReferentialService.getLanguages());
	}
}
