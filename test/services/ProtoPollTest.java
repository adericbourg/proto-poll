package services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import play.api.mvc.RequestHeader;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
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
	public void init() {
		initSessionRequestAndResponse();
		createCleanDb();
	}

	private void initSessionRequestAndResponse() {
		Request requestMock = mock(Request.class);
		RequestHeader requestHeaderMock = mock(RequestHeader.class);
		Http.Cookies cookiesMock = mock(Http.Cookies.class);
		when(requestMock.cookies()).thenReturn(cookiesMock);
		Context.current.set(new Context(-1L, requestHeaderMock, requestMock,
				new HashMap<String, String>(), new HashMap<String, String>(),
				new HashMap<String, Object>()));
	}

	private void createCleanDb() {
		Ebean.execute(Ebean.createCallableSql(dropDdl));
		Ebean.execute(Ebean.createCallableSql(createDdl));
	}

	@AfterClass
	public static void stopApp() {
		Helpers.stop(app);
	}

	protected static FakeApplication getApplication() {
		return app;
	}
}
