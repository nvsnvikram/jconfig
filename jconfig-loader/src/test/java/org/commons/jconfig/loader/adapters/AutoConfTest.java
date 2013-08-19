package org.commons.jconfig.loader.adapters;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.commons.jconfig.config.ConfigException;
import org.commons.jconfig.configloader.ConfigLoaderConfig;
import org.commons.jconfig.datatype.TimeValue;
import org.commons.jconfig.loader.adapters.AutoConf;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@Test(singleThreaded=true)
public class AutoConfTest {
    private static final String GOOD_APP1 = "Modules";
    private static final String GOOD_MODULE1 = "SherpaConfig";
    private static final String GOOD_ATTRIB1 = "Port";

    private static final String GOOD_APP2 = "qmda";
    private static final String GOOD_MODULE2 = "FilerGateConfig";
    private static final String GOOD_ATTRIB2 = "_Sets_";

    private static final String BAD_APP = "foo";
    private static final String BAD_MODULE = "foo";
    private ConfigLoaderConfig config = null;
    private AutoConf autoConf = null;

    @BeforeMethod
    public void beforeMethod() throws IOException  {

        config = new ConfigLoaderConfig();
        // AutoConf test file
        String filename = new File(".").getCanonicalPath() + "/src/test/resources/autoconf.json";
        config.setConfigFileName(filename);
        config.setConfigSyncInterval(TimeValue.parse("1s"));
        // Check for file changes every 1 sec

        autoConf = new AutoConf(config);
    }

    @Test
    public void ztestMissingAutoConfFile() {
        //Set a conf file that does not exist
        String origFile = config.getConfigFileName();
        config.setConfigFileName(config.getConfigFileName() + ".bad");
        try {
            Assert.assertNull(autoConf.getApplication(GOOD_APP1));
            Assert.fail();
        } catch (ConfigException e) {
            //pass
        }
        config.setConfigFileName(origFile);
    }

    @Test
    public void testHasApplication() throws ConfigException {

        Assert.assertTrue(autoConf.hasApplication(GOOD_APP1));
        Assert.assertTrue(autoConf.hasApplication(GOOD_APP2));

        Assert.assertFalse(autoConf.hasApplication(BAD_APP));
    }

    @Test
    public void testHasModule() throws ConfigException {
        Assert.assertTrue(autoConf.hasModule(GOOD_APP1, GOOD_MODULE1));
        Assert.assertTrue(autoConf.hasModule(GOOD_APP2, GOOD_MODULE2));

        Assert.assertFalse(autoConf.hasModule(GOOD_APP1, BAD_MODULE));

        Assert.assertFalse(autoConf.hasModule(BAD_APP, GOOD_MODULE1));
    }

    @Test
    public void testGetApplication() throws ConfigException {

        Assert.assertNotNull(autoConf.getApplication(GOOD_APP1));
        Assert.assertNotNull(autoConf.getApplication(GOOD_APP2));

        Assert.assertNull(autoConf.getApplication(BAD_APP));
    }

    @Test
    public void testGetModule() throws ConfigException {

        Assert.assertNull(autoConf.getModule(GOOD_APP1, BAD_MODULE));
        Assert.assertNull(autoConf.getModule(BAD_APP, GOOD_MODULE1));

        //
        // Make sure we got a legit configuration node
        JsonNode node = autoConf.getModule(GOOD_APP2, GOOD_MODULE2);
        Assert.assertTrue(node.has(GOOD_ATTRIB2));
        Assert.assertTrue(node.get(GOOD_ATTRIB2).isArray());

        //
        // Make sure we got a legit configuration node
        node = autoConf.getModule(GOOD_APP1, GOOD_MODULE1);
        Assert.assertTrue(node.has(GOOD_ATTRIB1));
    }

    @Test
    public void testAutoConfFileChange() throws ConfigException, InterruptedException {

        JsonNode origNode = autoConf.getApplication(GOOD_APP1);

        //Modify the file
        File configFile = new File(config.getConfigFileName());
        configFile.setLastModified(configFile.lastModified() + 1000);

        //sleep for 2 seconds
        Thread.sleep(2000);
        Assert.assertTrue(origNode != autoConf.getApplication(GOOD_APP1));
    }
}
