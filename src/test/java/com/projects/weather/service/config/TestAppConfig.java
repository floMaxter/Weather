package com.projects.weather.service.config;

import com.projects.weather.client.OpenWeatherClient;
import com.projects.weather.web.config.ConstraintProperties;
import com.projects.weather.web.config.SessionProperties;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.projects.weather.client",
        "com.projects.weather.service",
        "com.projects.weather.repository",
        "com.projects.weather.mapper",
        "com.projects.weather.security",
})
@PropertySource("classpath:application-test.properties")
public class TestAppConfig {

    private final Environment env;

    @Autowired
    public TestAppConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("hibernate.driver_class"));
        dataSource.setUrl(env.getRequiredProperty("hibernate.connection.url"));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        var sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.projects.weather");
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
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master-test.yaml");
        liquibase.setShouldRun(true);
        return liquibase;
    }

    @Bean
    public SessionProperties sessionProperties(@Value("${session.duration_seconds}") int durationSeconds,
                                               @Value("${session.cookie_name}") String cookieName,
                                               @Value("${session.cookie_path}") String cookiePath,
                                               @Value("${session.authorized_user_attribute}") String authorizedUserAttribute) {
        return SessionProperties.builder()
                .cookieName(cookieName)
                .durationSeconds(durationSeconds)
                .cookiePath(cookiePath)
                .authorizedUserAttribute(authorizedUserAttribute)
                .build();
    }

    @Bean
    public ConstraintProperties constraintProperties(@Value("${constraints.users.login}") String usersLoginConstraint) {
        return new ConstraintProperties(usersLoginConstraint);
    }


    @Bean
    public OpenWeatherClient openWeatherClient(RestClient restClient,
                                               @Value("${open_weather_api.weather.api_key}") String apiKey,
                                               @Value("${open_weather_api.weather.units_of_measurement}") String unitsOfMeasurement,
                                               @Value("${open_weather_api.location.search_limit}") int locationSearchLimit) {
        return new OpenWeatherClient(restClient, apiKey, unitsOfMeasurement, locationSearchLimit);
    }

    @Bean
    public RestClient restClient(@Value("${open_weather_api.weather.base_url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
