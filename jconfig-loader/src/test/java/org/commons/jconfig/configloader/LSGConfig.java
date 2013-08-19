package org.commons.jconfig.configloader;

public class LSGConfig implements LSGConfigMXBean {
   
    public void setlightsaberYCA(String yca) {
        this.yca = yca;
    }
    public String getlightsaberYCA() {
        return this.yca;
    }
    
    public void setlightsaberServer(String server) {
        this.serverName = server;
    }
    public String getlightsaberServer() {
        return this.serverName;
    }
    
    public String getConfigLoaderAdapter() {
        return "config:autoconf:lsg";
    }
    
    //Sets
    private String serverName;
    private String yca;
    
}