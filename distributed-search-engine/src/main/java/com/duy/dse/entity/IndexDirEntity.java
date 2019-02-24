package com.duy.dse.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件中address和resource对应的目录
 * @author duyu
 *
 */
@Component
public class IndexDirEntity {

	@Value("${contentType.addressDir}")
	private String addressDir;
	
	@Value("${contentType.resourceDir}")
	private String resourceDir;
	
	public String getAddressDir() {
		return addressDir;
	}

	public void setAddressDir(String addressDir) {
		this.addressDir = addressDir;
	}

	public String getResourceDir() {
		return resourceDir;
	}

	public void setResourceDir(String resourceDir) {
		this.resourceDir = resourceDir;
	}

	@Override
	public String toString() {
		return "IndexDirEntity [addressDir=" + addressDir + ", resourceDir=" + resourceDir + "]";
	}

	
}