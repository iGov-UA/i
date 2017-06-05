package org.igov.arm.controller;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import org.igov.arm.dao.ArmDao;
import org.igov.arm.dao.ArmDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan(basePackages = {"org.igov.arm"})
@ImportResource({"classpath*:context-arm-db.xml"})
public class ArmConfiguration {
	
	@Value("#{datasourceProps['datasource.url']}")
    private String jdbcUrl;
	
	@Value("#{datasourceProps['datasource.username']}")
    private String username;
	
	@Value("#{datasourceProps['datasource.password']}")
    private String password;
	@Value("#{datasourceProps['datasource.driverClass']}")
    private String driverClass;
	
	@Bean
	@Primary
	public javax.sql.DataSource getDataSource()
					throws PropertyVetoException, SQLException {
		 DriverManagerDataSource dataSource = new DriverManagerDataSource();
		 dataSource.setDriverClassName(driverClass);
	        dataSource.setUrl(jdbcUrl);
	        dataSource.setUsername(username);
	        dataSource.setPassword(password);
		return dataSource;
	}
	
	@Bean
    public ArmDao getArmDao() {
        try {
			return new ArmDaoImpl(getDataSource());
		} catch (PropertyVetoException|SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
}
