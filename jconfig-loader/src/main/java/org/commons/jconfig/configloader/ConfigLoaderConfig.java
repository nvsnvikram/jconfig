package org.commons.jconfig.configloader;

import org.commons.jconfig.annotations.Config;
import org.commons.jconfig.annotations.ConfigGet;
import org.commons.jconfig.annotations.ConfigResource;
import org.commons.jconfig.annotations.ConfigSet;
import org.commons.jconfig.annotations.StringNotEmpty;
import org.commons.jconfig.annotations.TimeRange;
import org.commons.jconfig.datatype.TimeValue;
import org.commons.jconfig.datatype.ValueType;


/**
 * Configuration class for the configuration loader application
 */
@Config(description = "Config Loader Config Classs")
@ConfigResource(name = "config_loader.json")
public class ConfigLoaderConfig {

    private TimeValue configSyncInterval;

    /**
     * How often to check for configuration changes (in seconds)
     * 
     * @return TimeValue
     */
    @ConfigGet(description = "ConfigLoade synch interval", type = ValueType.Time, defaultValue = "15 s")
    public TimeValue getConfigSyncInterval() {
        return configSyncInterval;
    }

    @ConfigSet
    @TimeRange(min = "0 ms", max = "60 m")
    public void setConfigSyncInterval(final TimeValue timeValue) {
        configSyncInterval = timeValue;
    }

    private String configFileName;

    /**
     * path of configloader config file
     * 
     * @return
     */
    @ConfigGet(
            description = "AutoConf downloaded file name.",
            type = ValueType.String,
            defaultValue = "/etc/config_loader.json")
    public String getConfigFileName() {
        return configFileName;
    }

    @ConfigSet
    @StringNotEmpty
    public void setConfigFileName(final String name) {
        configFileName = name;
    }

    /**
     * Time interval to read MBeans for all the applications running on this
     * host.
     */
    private TimeValue jmxReadInterval;

    /**
     * How often to read MBean values for applications running (in seconds)
     * 
     * @return TimeValue
     */
    @ConfigGet(description = "JMX read interval", type = ValueType.Time, defaultValue = "60 s")
    public TimeValue getJmxReadInterval() {
        return jmxReadInterval;
    }

    @ConfigSet
    @TimeRange(min = "0 ms", max = "60 m")
    public void setJmxReadInterval(final TimeValue timeValue) {
        jmxReadInterval = timeValue;
    }

    private String jmxFileName;

    /**
     * Path of filename where Application JMX Mbeans will be written
     * 
     * @return
     */
    @ConfigGet(description = "Path of filename where Application JMX Mbeans will be written.", type = ValueType.String, defaultValue = "/home/y/logs/yjava_ymail_config_loader/jmx")
    public String getJmxFileName() {
        return jmxFileName;
    }

    @ConfigSet
    @StringNotEmpty
    public void setJmxFileName(final String name) {
        jmxFileName = name;
    }

    private String configServerURL;

    /**
     * Config server uri
     * 
     * @return Config server uri path
     */
    @ConfigGet(description = "config server url", type = ValueType.String, defaultValue = "localhost")
    public String getConfigServerURL() {
        return configServerURL;
    }

    @ConfigSet
    @StringNotEmpty
    public void setConfigServerURL(final String uri) {
        configServerURL = uri;
    }

    private TimeValue configServerReadInterval;

    /**
     * How often to check config server to read files
     * 
     * @return
     */
    @ConfigGet(description = "config server file check interval.", type = ValueType.Time, defaultValue = "5 m")
    public TimeValue getConfigServerReadInterval() {
        return configServerReadInterval;
    }

    @ConfigSet
    @TimeRange(min = "0 ms", max = "60 m")
    public void setConfigServerReadInterval(final TimeValue timeValue) {
        configServerReadInterval = timeValue;
    }

    /**
     * ConfigLoader max worker threads, used to push configs to all jvms running. Each jvm requires one thread and hence
     * this number should always be greater or equal to total number of jvm's running.
     */
    private Number maxWorkerThreads;

    @ConfigGet(description = "ConfigLoader max worker threads, used to push configs to all jvms running.",
            type = ValueType.Number, defaultValue = "20")
    public Number getMaxWorkerThreads() {
        return maxWorkerThreads;
    }

    @ConfigSet
    @StringNotEmpty
    public void setMaxWorkerThreads(final Number value) {
        maxWorkerThreads = value;
    }
}
