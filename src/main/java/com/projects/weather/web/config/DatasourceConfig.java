package com.projects.weather.web.config;

import io.github.cdimascio.dotenv.Dotenv;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("com.projects.weather")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class DatasourceConfig {

    private final Environment env;
    private static final Dotenv dotenv = Dotenv.load();

    public DatasourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("hibernate.driver_class"));
        dataSource.setUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USERNAME"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        var sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.projects.weather.model");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    private Properties hibernateProperties() {
        var properties = new Properties();
        properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
        properties.put("hibernate.highlight_sql", env.getRequiredProperty("hibernate.highlight_sql"));
        return properties;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        var transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    @Bean
    public SpringLiquibase liquibase() {
        var liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        liquibase.setShouldRun(true);
        return liquibase;
    }
}
