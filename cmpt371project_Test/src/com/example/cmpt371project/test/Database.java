package com.example.cmpt371project.test;

import java.sql.*;

/**
 * Config
 * This class configures the database connection so SQL queries can be ran on it.
 * 
 * @author Yang
 */
public class Database 
{

	/**
	 * Host server name
	 */
	private static String serverName = "cmpt371g2.usask.ca";
	
	/**
	 * Database username
	 */
	private static String userName = "DavidThai";
	
	/**
	 * Database name
	 */
	private static String dbName = "daviddatabase";
	
	/**
	 * Database password
	 */
	private static String password = "showmethemoney";
	
	/**
	 * Database port number
	 */
	private static int portNumber = 5432;
	
	/**
	 * Database type
	 */
	private static String dbms = "postgresql";
	
	private static Connection conn = null;
		
	/**
	 * Initializes the database
	 */
	public Database(){}
	
	/**
	 * Gets the database instance if there is one, if not it creates one
	 * @return Database
	 */
	public static Connection getInstance()
	{
		if (conn == null)
		{
			conn = createConnection();
		}
		
		return conn;
	}
	
	/**
	 * Called for closing the database connection
	 */
	public static void closeConnection()
	{
		try
		{
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.print("ConnectionClose failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the connection to the database
	 * @return a database connection if successful, or null if unsuccessful
	 */
	private static Connection createConnection() 
	{
		// checks for postgres driver
		try 
		{
 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("No Postgres driver.");
			return null;
		}
		
		// creates a null connection
		Connection connection = null;
 
		try 
		{
			// attempts to connecting to the database
			connection = DriverManager.getConnection("jdbc:"+ dbms +"://" + serverName + ":" + portNumber + "/"+ dbName, userName, password);
 
		} catch (SQLException e) {
 
			System.out.println("Failed connection");
			e.printStackTrace();
			return null;
 
		}
		
		return connection;
	}
	
	/**
	 * Runs a given sql query and returns the result set. used for selecting from the database
	 * @param query
	 * @return Result Set if success, null if failed
	 */
	public static ResultSet runGetFromDatabaseSQL(String query)
	{	
		// checks connection worked.
		if (conn == null)
		{
			return null;
		}
		
		//variable used for statements used in the database
		Statement stmt = null;
		
		try 
		{
			// setting the statement variable to the connection
			stmt = conn.createStatement();
			
			// How to execute the query
			ResultSet rs = stmt.executeQuery(query);
			return rs;

		} 
		catch (SQLException e)
		{
			System.out.print("Statment failed");
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Insert an item into the database
	 * @param query
	 */
	public static void modifyDatabase(String query)
	{
		// checks connection worked.
				if (conn != null)
				{
					//variable used for statements used in the database
					Statement stmt = null;
					
					try 
					{
						// setting the statement variable to the connection
						stmt = conn.createStatement();
						
						// How to execute the query
						stmt.execute(query);
					} 
					catch (SQLException e)
					{
						System.out.print("Statment failed");
						e.printStackTrace();
					}
				}
	}
	
	/**
	 * A test method and example of database connections
	 * @param argv
	*/
	public static void main(String[] argv)
	{
		
		Database.getInstance();
		ResultSet rs = Database.runGetFromDatabaseSQL("SELECT * FROM users;");
		
		try 
		{
			// setting the statement variable to the connection
			while (rs.next())
			{
				// will list out all items in for each attribute that was requested
				System.out.print(rs.getString("username") + "\n");
				
			}
		}
		catch (SQLException e) 
		{
			System.out.print("Statment failed");
			e.printStackTrace();
		}
	}
}
