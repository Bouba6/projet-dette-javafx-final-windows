
package detteproject.core;

import java.sql.*;
import java.util.List;

public interface DataSource {

        public Connection setConnexion();

        public void closeConnexion();

        public int executeUpdate() throws SQLException;

        public ResultSet executeQuery() throws SQLException;

        public void innit(String sql);

        public String generateSql(Object entity, String action, String tableName, List<String> excludeFields,
                        String condition, String[] column);

        public void setFields(Object entity, PreparedStatement preparedStatement, List<String> excludeFields)
                        throws SQLException, IllegalAccessException, NoSuchFieldException;

}