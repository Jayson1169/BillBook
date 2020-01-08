package persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	private static final String MYSQL_URL = "jdbc:mysql://106.54.209.139:3306/bill_book?" +
			"user=root&password=Hots";

	public static Connection getConnection()
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(MYSQL_URL);
	}
}
