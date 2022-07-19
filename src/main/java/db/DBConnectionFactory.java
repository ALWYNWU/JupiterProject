package db;

import db.sql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";
	
	public static DBConnection getConnection(String db) {
		switch (db) {
		case "mysql": 
			return new MySQLConnection();
		case "mongodb":
//			return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Unexpected value: " + db);
		}
	}
	
	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}
	

}
