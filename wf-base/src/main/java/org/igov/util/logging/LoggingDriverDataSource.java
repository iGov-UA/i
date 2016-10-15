package org.igov.util.logging;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Wraps CustomConnectionSpy around created connections.
 *
 * User: goodg_000
 * Date: 29.05.2016
 * Time: 14:39
 */
public class LoggingDriverDataSource extends SimpleDriverDataSource {

    private Connection wrap(Connection connection) {
        if (connection instanceof CustomConnectionSpy) {
            return connection;
        }

        return new CustomConnectionSpy(connection);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrap(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrap(super.getConnection(username, password));
    }

    @Override
    protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
        return wrap(super.getConnectionFromDriver(username, password));
    }

    @Override
    protected Connection getConnectionFromDriver(Properties props) throws SQLException {
        return wrap(super.getConnectionFromDriver(props));
    }
}
