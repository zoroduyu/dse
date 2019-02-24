package com.duy.dse.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author duyu
 */
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {

    private int port;
    
    private String[] ips;
    

    public String[] getIps() {
		return ips;
	}

	public void setIps(String[] ips) {
		this.ips = ips;
	}

	public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}