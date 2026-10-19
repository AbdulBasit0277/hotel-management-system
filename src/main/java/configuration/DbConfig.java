
package configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {
    private static final String url = "jdbc:postgresql://localhost:5432/hotelDb";
    private static final String user = "postgres";
    private static final String password = "YOUR_PASSWORD";

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}



