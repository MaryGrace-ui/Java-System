package grace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	private static final String URL = "jdbc:mysql://localhost:3306/water_station";
	private static final String USER = "root";
	private static final String PSW = "";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PSW);
	}
}
