package db.sql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;


public class MySQLTableCreation {
	
	public static void main(String args[]) {

		try {
			// This is java.sql.Connection. Not com.mysql.jdbc.Connection.
			Connection conn = null;
	
			// Step 1 Connect to MySQL.
			try {
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				
				// java 反射 通过运行时的值来创建class 
				// com.mysql.jdbc.Driver是路径 forname返回的是一个class
				//call这一句会调用内部的static initializer, static initializer会注册driver
				Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
				
				// 获得和数据库的连接
				conn = DriverManager.getConnection(MySQLDBUtil.URL);
				
			} catch (SQLException e) {
				e.printStackTrace();
			} 
	
	
			if (conn == null) {
				return;
			}
			
			// Step 2 Drop tables in case they exist.
			Statement stmt = conn.createStatement();
			
			stmt.execute("SET FOREIGN_KEY_CHECKS=0");
			
			String sql = "DROP TABLE IF EXISTS categories";
			
			// return: 有多少行被更新了
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			/*
				Step 3 Creat new table
			 	Primary key: Also a key that is unique for each record. 
			 	Cannot be NULL and used as a unique identifier.
			 */
			sql = "CREATE TABLE items ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "local_date VARCHAR(255),"
					+ "PRIMARY KEY (item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
//			// Test: insert data
			sql = "INSERT INTO users VALUES ("
					+ "'1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
//			
			System.out.println("Exceting query:" + sql);
			stmt.executeUpdate(sql);
//			
//			String sql2 = "INSERT IGNORE INTO items VALUES ("
//					+ "'G5vYZ96pBVNWc', 'test', '0.00', 'street101', '', '', '')";
////			
////			System.out.println("Exceting query:" + sql);
////			Statement stmt2 = conn.createStatement();
////			stmt2.execute("SET FOREIGN_KEY_CHECKS=0");
//			stmt.execute(sql2);
//			System.out.println("success!");
					

			System.out.println("Import is done successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

	


