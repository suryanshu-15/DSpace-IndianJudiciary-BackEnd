package org.dspace.app.rest.diracai.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "org.dspace.app.rest.diracai.Repository",
        entityManagerFactoryRef = "customEntityManagerFactory",
        transactionManagerRef = "customTransactionManager"
)
@EntityScan(basePackages = "org.dspace.content.Diracai")
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean customEntityManagerFactory(DataSource dataSource) {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
                new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter(),
                new HashMap<>(),
                null
        );

        return builder
                .dataSource(dataSource)
                .packages("org.dspace.content.Diracai")
                .persistenceUnit("custom")
                .build();
    }

    @Bean
    public JpaTransactionManager customTransactionManager(EntityManagerFactory customEntityManagerFactory) {
        return new JpaTransactionManager(customEntityManagerFactory);
    }
}
