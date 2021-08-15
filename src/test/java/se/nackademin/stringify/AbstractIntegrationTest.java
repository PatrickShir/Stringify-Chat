package se.nackademin.stringify;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;


public abstract class AbstractIntegrationTest {

    private static final PostgreSQLContainer postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer()
                .withDatabaseName("postgresDB")
                .withUsername("root")
                .withPassword("password");

        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.datasource.jdbc-url", postgreSQLContainer::getJdbcUrl);
        registry.add("app.datasource.username", postgreSQLContainer::getUsername);
        registry.add("app.datasource.password", postgreSQLContainer::getPassword);
    }

}
