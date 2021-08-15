package se.nackademin.stringify.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for connecting to a database using Hikari
 */
@Configuration
public class DataSourceConfiguration {

    /**
     * Builds HikariDataSource with configuration
     * @return {@Code HikariDataSource.class}
     */
    @Bean
    @ConfigurationProperties("app.datasource")
    public HikariDataSource hikariDataSource() {
        return DataSourceBuilder.create().
                type(HikariDataSource.class)
                .build();
    }
}
