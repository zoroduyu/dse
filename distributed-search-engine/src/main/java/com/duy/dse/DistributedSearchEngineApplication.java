package com.duy.dse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.duy.dse.netty.NettyServerListener;
import com.netflix.discovery.shared.Application;

@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = { "com.duy.dse.dao" })
public class DistributedSearchEngineApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	private NettyServerListener nettyServerListener;

	public static void main(String[] args) {
		SpringApplication.run(DistributedSearchEngineApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	@Override
	public void run(String... args) throws Exception {
		nettyServerListener.start();
	}
}
