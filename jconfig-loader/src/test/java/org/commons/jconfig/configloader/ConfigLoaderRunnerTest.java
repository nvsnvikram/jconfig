package org.commons.jconfig.configloader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;
import mockit.Instantiation;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.commons.jconfig.config.ConfigContext;
import org.commons.jconfig.config.ConfigManager;
import org.commons.jconfig.config.ConfigContext.Entry;
import org.commons.jconfig.configloader.ConfigLoaderConfig;
import org.commons.jconfig.configloader.ConfigLoaderRunner;
import org.commons.jconfig.loader.test.integration.App1Config;
import org.commons.jconfig.loader.test.integration.App2Config;
import org.commons.jconfig.loader.test.integration.App3Config;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Unit test for testing communication between loader and manager. Both loader
 * and manager are running on same vm.
 * 
 * @author jaikit
 * 
 */
public class ConfigLoaderRunnerTest {
    private final JsonParser parser = new JsonParser();
    private static List<String> configFiles = new ArrayList<String>();

    @MockClass(realClass = HttpResponse.class)
    public static class MockHttpResponse {
        @Mock
        public StatusLine getStatusLine() {
            return Mockit.setUpMock(new MockStatusLine());
        }

        @Mock
        public HttpEntity getEntity() {
            return Mockit.setUpMock(new MockHttpEntity());
        }
    }

    @MockClass(realClass = StatusLine.class)
    public static class MockStatusLine {
        public static int statusCode = 200;

        /**
         * @param statusCode
         *            the statusCode to set
         */
        public static void setStatusCode(final int statusCode) {
            MockStatusLine.statusCode = statusCode;
        }

        @Mock
        public int getStatusCode() {
            return statusCode;
        }
    }

    @MockClass(realClass = DefaultHttpClient.class, instantiation = Instantiation.PerMockSetup)
    public static class MockHttpClient {
        @Mock
        public HttpResponse execute(final HttpUriRequest get) throws IOException {
            HttpResponse resp = Mockit.setUpMock(new MockHttpResponse());
            return resp;
        }
    }

    @MockClass(realClass = HttpEntity.class, instantiation = Instantiation.PerMockSetup)
    public static class MockHttpEntity {
        static int count = -1;

        @Mock
        public InputStream getContent() throws IllegalStateException {
            if (count == (configFiles.size() - 1)) {
                count = -1;
            }
            count++;

            String config = configFiles.get(count);
            return new ByteArrayInputStream(config.getBytes());
        }
    }

    @BeforeClass
    public void setUp() throws FileNotFoundException, IOException {
        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();

        obj.add("files", array);
        array.add(new JsonPrimitive("conf1.json"));
        array.add(new JsonPrimitive("conf2.json"));
        array.add(new JsonPrimitive("conf3.json"));
        array.add(new JsonPrimitive("conf4.json"));
        array.add(new JsonPrimitive("conf5.json"));

        configFiles.add(obj.toString());

        /*
         * Prepare list to hold configs. Configs will be returned from this list
         * instead of httpclient
         */
        InputStream instream = new FileInputStream(new File(".").getCanonicalPath()
                + "/src/test/resources/configmergertest.json");
        StringWriter writer = new StringWriter();
        IOUtils.copy(instream, writer, "UTF-8");
        String jsonString = writer.toString();
        JsonObject jsonConfig = (JsonObject) parser.parse(jsonString);

        for (Map.Entry<String, JsonElement> elem : jsonConfig.getAsJsonObject().entrySet()) {
            JsonObject module = new JsonObject();
            if (elem.getKey().equals("lsgclient")) {
                module.add(elem.getKey(), elem.getValue());
                configFiles.add(module.toString());
                continue;
            }
            JsonObject elem1 = new JsonObject();
            elem1.add(elem.getKey(), elem.getValue());
            module.add("Modules", elem1);
            configFiles.add(module.toString());
        }
    }

    @AfterMethod
    public void cleanup() {
        // Clear previous mocks; otherwise, they interfere.
        Mockit.tearDownMocks();
    }

    @BeforeMethod
    public void beforeTest() {
        Mockit.setUpMocks(new MockHttpClient());
    }

    @Test(groups = { "loader" }, enabled = false)
    public void runnerTest() {

        // Mockit.setUpMock(new MockConfigLoaderConfig());
        new StartRunner().start();
        ConfigManager.INSTANCE.setAppName("com.xyz.xyz.xmas.webservice.WsConfig");

        App1Config app1Config = ConfigManager.INSTANCE.getConfig(App1Config.class, new ConfigContext(new Entry("FARM",
                "1")));
        App2Config app2Config = ConfigManager.INSTANCE.getConfig(App2Config.class, new ConfigContext(new Entry("FARM",
                "1")));

        Assert.assertEquals(app1Config.getAttachmentServerHost(), "wrongnumber.corp.xyz.com");
        Assert.assertEquals(app1Config.getMaxNumberOfConnections().intValue(), 100);
        Assert.assertFalse(app1Config.getUseAttServer());

        ConfigContext context1 = new ConfigContext(new Entry("FARM", "318"));
        app2Config = ConfigManager.INSTANCE.getConfig(App2Config.class, context1);
        Assert.assertEquals(app2Config.getAttachmentServerHost(), "thumbp3-mud.thumb.mail.xyz.com");
        Assert.assertEquals(app2Config.getMaxNumberOfConnections().intValue(), 10);
        Assert.assertTrue(app2Config.getUseAttServer());

        ConfigContext context2 = new ConfigContext(new Entry("FARM", "421"));
        app2Config = ConfigManager.INSTANCE.getConfig(App2Config.class, context2);
        Assert.assertEquals(app2Config.getAttachmentServerHost(), "thumbp421-mud.thumb.mail.xyz.com");
        Assert.assertEquals(app2Config.getMaxNumberOfConnections().intValue(), 11);
        Assert.assertTrue(app2Config.getUseAttServer());

        ConfigContext context3 = new ConfigContext(new Entry("FARM", "000"));
        app2Config = ConfigManager.INSTANCE.getConfig(App2Config.class, context3);
        Assert.assertEquals(app2Config.getAttachmentServerHost(), "wrongnumber.corp.xyz.com");
        Assert.assertEquals(app2Config.getMaxNumberOfConnections().intValue(), 100);
        Assert.assertFalse(app2Config.getUseAttServer());

        App3Config app3Config = ConfigManager.INSTANCE.getConfig(App3Config.class, ConfigContext.EMPTY);
        Assert.assertEquals(app3Config.getSonoraServer(), "testserver.xyz.com");
        Assert.assertEquals(app3Config.getDefaultHost(), "defaulthost.xyz.com");

        LSGAdapterConfig config = ConfigManager.INSTANCE.getConfig(LSGAdapterConfig.class, new ConfigContext(new Entry(
                "FARM", "323")));
        Assert.assertEquals(config.getLsgHostName(), "ls323.mail.vip.mud.xyz.com:4080");

    }

    class StartRunner extends Thread {
        @Override
        public void run() {
            ConfigLoaderRunner loader = new ConfigLoaderRunner();
            try {
                Deencapsulation.setField(loader, "loaderConfigDirPath", new File(".").getCanonicalPath()
                        + "/src/test/resources/config_loader.json");
                loader.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed starting loader ");
            }
        }
    }

    @Test
    public void loadLoaderConfig() throws IOException {
        Mockit.tearDownMocks();
        ConfigLoaderRunner loader = new ConfigLoaderRunner();
        Deencapsulation.setField(loader, "loaderConfigDirPath", new File(".").getCanonicalPath()
                + "/src/test/resources/");
        Deencapsulation.invoke(loader, "loadLoaderConfig");
        ConfigLoaderConfig config = Deencapsulation.getField(loader, "config");
        Assert.assertEquals(config.getJmxFileName(), "/tmp/jmx");
        Assert.assertEquals(config.getConfigFileName(), "/tmp/xyz.json");
        Assert.assertEquals(config.getConfigServerURL(), "/xyz_confman_configs/");
        Assert.assertEquals(config.getJmxReadInterval().toSeconds(), 60);
    }
}
