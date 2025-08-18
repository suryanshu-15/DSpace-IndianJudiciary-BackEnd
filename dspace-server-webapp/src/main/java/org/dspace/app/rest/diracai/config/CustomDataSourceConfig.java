package org.dspace.app.rest.diracai.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class CustomDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl("jdbc:postgresql://localhost:5432/dspace");
        ds.setUser("dspace");
        ds.setPassword("dspace");
        return ds;
    }
}
