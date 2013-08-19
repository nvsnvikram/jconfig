package org.commons.jconfig.configloader;

/**
 * Test FilerGateConfig class 
 * 
 * @author aabed
 */
public class FilerGateConfig implements FilerGateConfigMXBean {

    public FilerGateConfig() {
        this.serverName = "error";
        this.appId = "error";
        this.serverPath = "error";
        this.timeout = "error";
        this.retries = "";
        this.serverPort = "";
     }
     
     public String  getFilerGateServerPort() {
        return this.serverPort;
     }
     public String getFilerGateDirPath() {
        return this.serverPath;
     }    
     public String getFilerGateTimeout() {
        return this.timeout;
     }
     public String getFilerGateRetries() {
        return this.retries;
     }
     public String getFilerGateServerName() {
         return this.serverName;
     }
     public String getFilerGateAppId() {
         return this.appId;
     }
     
     public synchronized void setFilerGateServerPort(String port) {
        this.serverPort = port;
     }
     public synchronized void setFilerGateDirPath(String path) {
        this.serverPath = path;
     }
     public synchronized void setFilerGateTimeout(String timeout) {
        this.timeout = timeout;
     }
     public synchronized void setFilerGateServerName(String name) {
         this.serverName = name;
     }
     public synchronized void setFilerGateAppId(String AppId) {
         this.appId = AppId;
     }
     public synchronized void setFilerGateRetries(String retries) {
        this.retries = retries;
     }
     
     //Leave this out by design for testing
     //public String getLoaderAdapter() {
     //    return "standard";
     //}
     
     //Sets
     private String serverName;
     private String appId;
     
     private String serverPort;
     private String serverPath;
     private String timeout;
     private String retries;
}