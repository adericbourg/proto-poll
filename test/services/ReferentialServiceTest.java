package services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class ReferentialServiceTest extends ProtoPollTest {

	@Test
	public void testInstanciation() throws Exception {
		for (Constructor<?> constructor : ReferentialService.class
				.getDeclaredConstructors()) {
			constructor.setAccessible(true);
			try {
				constructor.newInstance();
			} catch (InvocationTargetException e) {
				assertTrue(e.getCause() instanceof AssertionError);
			}
		}
	}

	@Test
	public void testGetLanguages() {
		// TODO Make this test independent from configuration.
		assertNotNull(ReferentialService.getLanguages());
	}
}
