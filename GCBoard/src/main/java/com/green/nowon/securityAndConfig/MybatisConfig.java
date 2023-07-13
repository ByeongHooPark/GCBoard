package com.green.nowon.securityAndConfig;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class MybatisConfig {
	
	private final ApplicationContext ap;
	
	@Bean
	SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setConfiguration(mybatisConfiguration());
		//factoryBean.setMapperLocations(null); //xml파일위치 설정
		
		String locationPattern="classpath:mapper/**/*-mapper.xml";
		Resource[] mapperLocations = ap.getResources(locationPattern);
		factoryBean.setMapperLocations(mapperLocations);
		return factoryBean.getObject();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration") 
	org.apache.ibatis.session.Configuration mybatisConfiguration(){
		return new org.apache.ibatis.session.Configuration();
	}
	
	@Bean
	SqlSessionTemplate sqlSession(DataSource dataSource) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory(dataSource));
	}

}
