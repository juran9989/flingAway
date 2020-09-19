package com.fling.config.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@MapperScan(basePackages = "com.fling.repo", sqlSessionFactoryRef = "embeddedSqlSessionFactory")
public class MyBatisEmbedded {

    @Bean
    public SqlSessionFactory embeddedSqlSessionFactory(@Qualifier("embedDatasource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        configureSqlSessionFactory(sessionFactoryBean, dataSource);
        return sessionFactoryBean.getObject();
    }

    public void configureSqlSessionFactory(SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource) throws IOException {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setDataSource(dataSource);
        String typeAliasesPackage = "com.fling.dto";
        sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        String configLocation = "classpath:mybatis/mybatis-config.xml";
        sessionFactoryBean.setConfigLocation(pathResolver.getResource(configLocation));
        String mapperLocation = "classpath:mybatis/mapper/**/*.xml";
        sessionFactoryBean.setMapperLocations(pathResolver.getResources(mapperLocation));
        sessionFactoryBean.setVfs(SpringBootVFS.class);
    }


}
