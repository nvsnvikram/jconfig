package org.commons.jconfig.loader.adapters;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.codehaus.jackson.JsonNode;
import org.commons.jconfig.config.ConfigException;
import org.commons.jconfig.config.ConfigLoaderAdapterID;
import org.commons.jconfig.configloader.ConfigLoaderConfig;
import org.commons.jconfig.datatype.TimeValue;
import org.commons.jconfig.internal.ConfigAdapterJson;
import org.commons.jconfig.loader.adapters.AutoConf;
import org.commons.jconfig.loader.adapters.LsgAdapter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class LsgAdapterTest {

    private ConfigLoaderConfig config = null;
    private AutoConf autoConf = null;

    @BeforeTest
    public void BeforeTest() throws IOException {
        config = new ConfigLoaderConfig();
        // AutoConf test file
        String filename = new File(".").getCanonicalPath() + "/src/test/resources/autoconf.json";
        config.setConfigFileName(filename);
        config.setConfigSyncInterval(TimeValue.parse("60m"));

        autoConf = new AutoConf(config);
    }

    @Test
    public void testLSGAdapter() throws IOException, ConfigException {
        LsgAdapter adapter = new LsgAdapter(autoConf);

        Assert.assertEquals(adapter.getUri(), ConfigLoaderAdapterID.LSG_AUTOCONF.getUri());

        //Get the autConf adapted Json for lsg config
        JsonNode root = adapter.getModuleNode("Imap", "lsgclient");

        //Validate that we have a "Sets" array node
        JsonNode Sets = root.get(ConfigAdapterJson.CONST.SETS.toString());
        Assert.assertTrue(Sets.isArray());

        // Verify that we get values as expected
        // "Sets": [
        // { "key": [ "323" ], "keyList": { "lightsaberYCA": "xyz.mail.acl.yca.lsg-prod", "lightsaberServer": "ls323.mail.vip.mud.xyz.com:4080" } },
        JsonNode key = Sets.get(1).path(ConfigAdapterJson.CONST.KEY.toString());
        Assert.assertTrue(key.isArray());
        Assert.assertEquals(key.get(0).getTextValue(), "323");

        JsonNode keylist = Sets.get(1).path(ConfigAdapterJson.CONST.KEY_LIST.toString());
        Assert.assertEquals(keylist.get("lightsaberYCA").getTextValue(), "xyz.mail.acl.yca.lsg-prod");
        Assert.assertEquals(keylist.get("lightsaberServer").getTextValue(), "ls323.mail.vip.mud.xyz.com:4080");
    }

    @Test
    public void testLSGAdapterMissingLsgConfig() throws ConfigException, IOException {
        // AutoConf test file
        String filename = new File(".").getCanonicalPath() + "/src/test/resources/autoconf_nolsgclient.json";
        config.setConfigFileName(filename);

        AutoConf autoConfLocal = new AutoConf(config);
        LsgAdapter adapter = new LsgAdapter(autoConfLocal);

        //Get the autConf adapted Json for lsg config
        JsonNode root = adapter.getModuleNode("Imap", "lsgclient");

        Assert.assertNull(root);
    }

    @Test
    public void testLSGAdapterMissingAutoConfFile() throws IOException, ConfigException {
        // AutoConf test file
        ConfigLoaderConfig config = new ConfigLoaderConfig();
        // AutoConf test file
        String filename = new File(".").getCanonicalPath() + "/src/test/resources/autoconf.json";
        config.setConfigFileName(filename);
        config.setConfigSyncInterval(TimeValue.parse("60m"));

        AutoConf autoConfLocal = new AutoConf(config);
        LsgAdapter adapter = new LsgAdapter(autoConfLocal);

        //Get the autConf adapted Json for lsg config
        JsonNode root = adapter.getModuleNode("Imap", "lsgclient");
        
        //TODO: confirm with lafa
        Assert.assertEquals(root.toString(), "{\"_Sets_\":[{\"key\":[\"FIXME\"],\"keyList\":{\"lightsaberYCA\":\"\",\"lightsaberServer\":\"NONE\"}},{\"key\":[\"323\"],\"keyList\":{\"lightsaberYCA\":\"xyz.mail.acl.yca.lsg-prod\",\"lightsaberServer\":\"ls323.mail.vip.mud.xyz.com:4080\"}},{\"key\":[\"318\"],\"keyList\":{\"lightsaberYCA\":\"xyz.mail.acl.yca.lsg-prod\",\"lightsaberServer\":\"ls318.mail.vip.mud.xyz.com:4080\"}},{\"key\":[\"1200\"],\"keyList\":{\"lightsaberYCA\":\"xyz.mail.acl.yca.lsg-prod\",\"lightsaberServer\":\"ls100.mail.vip.ne1.xyz.com:4080\"}}],\"_Sets_Type_\":\"FARM\"}");
    }
}