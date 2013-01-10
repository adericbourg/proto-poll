package services;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.Ebean;

/**
 * Test superclass.<br/>
 * Credits to Matthieu Guillemin for configuration.<br/>
 * To be able to run tests from your IDE, add
 * <code>-javaagent:/path/to/ebean/ebean-2.7.3-agent.jar</code> to your VM
 * options.
 * 
 * @see <a
 *      href="http://blog.matthieuguillermin.fr/2012/03/unit-testing-tricks-for-play-2-0-and-ebean/">Unit
 *      testing tricks for Play 2.0 and Ebean</a>
 * 
 * @author alban
 * 
 */
public abstract class ProtoPollTest {

	private static FakeApplication app;
	private static String createDdl = "";
	private static String dropDdl = "";

	@BeforeClass
	public static void startApp() throws IOException {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(getApplication());

		// Reading the evolution file
		String evolutionContent = FileUtils.readFileToString(getApplication()
				.getWrappedApplication().getFile(
						"conf/evolutions/default/1.sql"));

		// Splitting the String to get Create & Drop DDL

		String[] splittedEvolutionContent = evolutionContent
				.split("# --- !Ups");
		String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
		createDdl = upsDowns[0];
		dropDdl = upsDowns[1];

	}

	@Before
	public void createCleanDb() {
		Ebean.execute(Ebean.createCallableSql(dropDdl));
		Ebean.execute(Ebean.createCallableSql(createDdl));
	}

	@AfterClass
	public static void stopApp() {
		Helpers.stop(app);
	}

	protected static final FakeApplication getApplication() {
		return app;
	}
}
