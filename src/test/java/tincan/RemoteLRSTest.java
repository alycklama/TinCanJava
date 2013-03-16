package tincan;

import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tincan.json.StringOfJSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Log
public class RemoteLRSTest {
    private static final Properties config = new Properties();

    @BeforeClass
    public static void setupOnce() {
        InputStream is = RemoteLRSTest.class.getResourceAsStream("/lrs.properties");
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testEndpoint() throws Exception {
        RemoteLRS obj = new RemoteLRS();
        Assert.assertNull(obj.getEndpoint());

        String strURL = "http://tincanapi.com/test/TinCanJava";
        obj.setEndpoint(strURL);
        Assert.assertEquals(strURL + "/", obj.getEndpoint().toString());

    }

    @Test(expected = MalformedURLException.class)
    public void testEndPointBadURL() throws MalformedURLException {
        RemoteLRS obj = new RemoteLRS();
        obj.setEndpoint("test");
    }

    @Test
    public void testVersion() throws Exception {
        RemoteLRS obj = new RemoteLRS();
        Assert.assertNull(obj.getVersion());

        obj.setVersion(TCAPIVersion.V095);
        Assert.assertEquals(TCAPIVersion.V095, obj.getVersion());
    }

    @Test
    public void testAuth() throws Exception {
        RemoteLRS obj = new RemoteLRS();
        Assert.assertNull(obj.getAuth());

        obj.setAuth("test");
        Assert.assertEquals("test", obj.getAuth());
    }

    @Test
    public void testUsername() throws Exception {
        RemoteLRS obj = new RemoteLRS();
        obj.setPassword("pass");

        Assert.assertNull(obj.getUsername());
        Assert.assertNull(obj.getAuth());

        obj.setUsername("test");
        Assert.assertEquals("test", obj.getUsername());
        Assert.assertEquals(obj.getAuth(), "Basic dGVzdDpwYXNz");
    }

    @Test
    public void testPassword() throws Exception {
        RemoteLRS obj = new RemoteLRS();
        obj.setUsername("user");

        Assert.assertNull(obj.getPassword());
        Assert.assertNull(obj.getAuth());

        obj.setPassword("test");
        Assert.assertEquals("test", obj.getPassword());
        Assert.assertEquals("Basic dXNlcjp0ZXN0", obj.getAuth());
    }

    @Test
    public void testExtended() throws Exception {
    }

    @Test
    public void testCalculateBasicAuth() throws Exception {
        RemoteLRS obj = new RemoteLRS();
        obj.setUsername("user");
        obj.setPassword("pass");
        Assert.assertEquals("Basic dXNlcjpwYXNz", obj.calculateBasicAuth());
    }

    @Test
    public void testSaveStatement() throws Exception {
        RemoteLRS obj = getLRS();

        Statement st = new Statement();
        st.stamp(); // triggers a PUT
        st.setActor(mockAgent());
        st.setVerb(mockVerbDisplay());
        st.setObject(mockActivity("testSaveStatement"));

        obj.saveStatement(st);
    }

    /*
     * Tests calling saveStatement without an ID which triggers a POST request
     */
    @Test
    public void testSaveStatementNoID() throws Exception {
        RemoteLRS obj = getLRS();

        Statement st = new Statement();
        st.setActor(mockAgent());
        st.setVerb(mockVerbDisplay());
        st.setObject(mockActivity("testSaveStatementNoID"));

        obj.saveStatement(st);
    }

    @Test
    public void testSaveStatements() throws Exception {
        RemoteLRS obj = getLRS();

        Statement[] sts = new Statement[2];

        Statement st0 = new Statement();
        st0.stamp();
        st0.setActor(mockAgent());
        st0.setVerb(mockVerbDisplay());
        st0.setObject(mockActivity("testSaveStatements1"));

        sts[0] = st0;

        Statement st1 = new Statement();
        st1.stamp();
        st1.setActor(mockAgent());
        st1.setVerb(mockVerbDisplay());
        st1.setObject(mockActivity("testSaveStatements2"));

        sts[1] = st1;

        obj.saveStatements(sts);
    }

    @Test
    public void testSaveStatementsNoIDs() throws Exception {
        RemoteLRS obj = getLRS();

        Statement[] sts = new Statement[2];

        Statement st0 = new Statement();
        st0.setActor(mockAgent());
        st0.setVerb(mockVerbDisplay());
        st0.setObject(mockActivity("testSaveStatementsNoIDs1"));

        sts[0] = st0;

        Statement st1 = new Statement();
        st1.setActor(mockAgent());
        st1.setVerb(mockVerbDisplay());
        st1.setObject(mockActivity("testSaveStatementsNoIDs2"));

        sts[1] = st1;

        obj.saveStatements(sts);
    }

    @Test
    public void testRetrieveStatement() throws Exception {
        RemoteLRS obj = getLRS();

        Statement result = obj.getStatement("5bd37f75-db5a-4486-b0fa-b7ec4d82c489");
        log.info("statement: " + result.toJSONPretty());
    }

    @Test
    public void testQueryStatements() throws Exception {
        RemoteLRS obj = getLRS();

        StatementsQuery query = new StatementsQuery();
        query.setSince(new DateTime("2013-03-13T14:17:42.610Z"));
        //query.setLimit(3);
        query.setActor(mockAgent());
        query.setObject(mockActivity("testSaveStatement"));

        StatementsResult result = obj.queryStatements(query);
        if (result != null) {
            log.info("statement count: " + result.getStatements().size());
            //for(Statement st : result.getStatements()) {
                //log.info("statement: " + st.toJSONPretty());
            //}
            log.info("result - more: " + result.getMoreURL());
        }
    }

    @Test
    public void testSaveState() throws Exception {
        RemoteLRS obj = getLRS();

        State state = new State("testSaveState", new StringOfJSON("{\"test\": \"Test\"}"));

        obj.saveState(state, mockActivity("testSaveState"), mockAgent(), null);
    }

    @Test
    public void testRetrieveState() throws Exception {
        RemoteLRS obj = getLRS();

        State state = new State("testRetrieveState", new StringOfJSON("{\"test\": \"Test\"}"));
        obj.saveState(state, mockActivity("testRetrieveState"), mockAgent(), null);

        State retrievedState = obj.retrieveState("testRetrieveState", mockActivity("testRetrieveState"), mockAgent(), null);
        log.info("state result: " + retrievedState.getId());
    }

    private RemoteLRS getLRS() {
        RemoteLRS obj = new RemoteLRS();
        try {
            obj.setEndpoint(config.getProperty("endpoint"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        obj.setVersion(TCAPIVersion.V095);
        obj.setUsername(config.getProperty("username"));
        obj.setPassword(config.getProperty("password"));

        return obj;
    }

    private Statement mockStatement() {
        Statement obj = new Statement();

        return obj;
    }

    private Agent mockAgent() {
        Agent obj = new Agent();
        obj.setMbox("mailto:tincanjava-test-tincan@tincanapi.com");

        return obj;
    }

    private Verb mockVerb() {
        Verb obj = new Verb();
        try {
            obj.setId(new URL("http://adlnet.gov/expapi/verbs/attempted"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private Verb mockVerbDisplay() {
        Verb obj = mockVerb();
        LanguageMap display = new LanguageMap();
        display.put("und", obj.getId().toString());
        display.put("en-US", "attempted");

        obj.setDisplay(display);

        return obj;
    }

    private Activity mockActivity(String suffix) {
        Activity obj = new Activity();
        try {
            obj.setId(new URL ("http://tincanapi.com/TinCanJava/Test/RemoteLRSTest_mockActivity/" + suffix));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
