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

	
	@Value("${contentType.dir}")
	private String dir;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	@Override
	public String toString() {
		return "IndexDirEntity [dir=" + dir + "]";
	}
	
}