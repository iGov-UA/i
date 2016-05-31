package org.igov.util.logging;

import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.PreparedStatementSpy;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Provieds better formatting for dumped sql.
 *
 * User: goodg_000
 * Date: 29.05.2016
 * Time: 23:06
 */
public class CustomPreparedStatementSpy extends PreparedStatementSpy {
    /**
     * Create a PreparedStatementSpy (JDBC 4 version) for logging activity of another PreparedStatement.
     *
     * @param sql                   SQL for the prepared statement that is being spied upon.
     * @param connectionSpy         ConnectionSpy that was called to produce this PreparedStatement.
     * @param realPreparedStatement The actual PreparedStatement that is being spied upon.
     */
    public CustomPreparedStatementSpy(String sql, ConnectionSpy connectionSpy,
                                      PreparedStatement realPreparedStatement) {
        super(sql, connectionSpy, realPreparedStatement);
    }

    @Override
    public void closeOnCompletion() throws SQLException {
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    protected String dumpedSql() {
        // removing new lines and multiple spaces.
        return super.dumpedSql().replaceAll("\n", "");
    }
}
