package it.polimi.ingsw.am43.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Acts as a central utility provider for database connections.
 * It configures and maintains a high-performance HikariCP connection pooling infrastructure,
 * allowing thread-safe concurrent database access across the server application.
 */
public class DatabaseManager {

    private static HikariDataSource dataSource;

    /**
     * Initializes the central database connection pool using the provided configuration parameters.
     * Sets up connection URLs, user credentials, maximum pool sizing boundaries, and baseline idle counts.
     *
     * @param config The structural database credentials and connection path details provider.
     */
    public static void initialize(DatabaseConfig config) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);

        dataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Retrieves an active database connection from the initialized data source connection pool.
     *
     * @return An active, thread-allocated JDBC Connection structure.
     * @throws SQLException If the connection manager has not been initialized prior to lookup,
     * or a driver allocation error occurs.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Failed to initialize database manager.");
        }
        return dataSource.getConnection();
    }
}