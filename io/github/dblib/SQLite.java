package io.github.dblib;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite 
{
	static Connection connection;
	static File file;
	static String dburl;
	static Statement statement;
	
	public static void setUrl(String url)
	{
		file = new File(url);
		dburl = "jdbc:sqlite:" + file.getPath();
	}
	
	public static void connect()
	{
		connection = null;
		
		try 
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			
			connection = DriverManager.getConnection(dburl);
			
			System.out.println("Database connected!");
			
			statement = connection.createStatement();
		} 
		catch (SQLException | IOException e) 
		{
			System.out.println("Database connection failed!");
			e.printStackTrace();
		}
	}
	
	public static void disconnect()
	{
		try 
		{
			if (connection != null)
			{
				connection.close();
				System.out.println("Database disconnected!");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Failed to disconnect from Database!");
			e.printStackTrace();
		}
	}
	
	private static void onUpdate(String sql)
	{
		try 
		{
			statement.execute(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	private static ResultSet onQuery(String sql)
	{
		try 
		{
			return statement.executeQuery(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Object getFromTable(String tableName, String objectName)
	{
		try 
		{
			ResultSet set = onQuery("SELECT " + objectName + " FROM " + tableName);
			
			if (set.next())
			{
				Object ret = set.getObject(1);
				return ret;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Object getFromTable(String tableName, String objectName, String search, Object index)
	{
		try 
		{
			ResultSet set = onQuery("SELECT " + objectName + " FROM " + tableName + " WHERE " + search + " = " + index);
			
			if (set.next())
			{
				Object ret = set.getObject(1);
				return ret;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void addTable(String tableName, Component... o)
	{
		/* create a for-loop that goes through every object in o.
		*  chech weather the object is a int, string, boolean etc.
		*  add the object to the table as the right value
		*/
		
		String columns = "";
		
		for (int i = 0; i < o.length; i++)
		{
			if (i == o.length - 1)
			{
				if (o[i].getType().equals(Integer.class)) columns += o[i].getName() + " INTEGER";
				if (o[i].getType().equals(String.class)) columns += o[i].getName() + " TEXT";
				if (o[i].getType().equals(Boolean.class)) columns += o[i].getName() + " BOOL";
			}
			else
			{
				if (o[i].getType().equals(Integer.class)) columns += o[i].getName() + " INTEGER, ";
				if (o[i].getType().equals(String.class)) columns += o[i].getName() + " TEXT, ";
				if (o[i].getType().equals(Boolean.class)) columns += o[i].getName() + " BOOL, ";
			}
		}
		
		onUpdate("CREATE TABLE IF NOT EXISTS " + tableName + "(" + columns + ")");
	}
	
	public static void add(String tableName, String[] columnNames, Object... values)
	{
		String row = "";
		String columns = "";
		
		for (int i = 0; i < columnNames.length; i++)
		{
			if (i == columnNames.length - 1)
			{
				columns += columnNames[i];
			}
			else
			{
				columns += columnNames[i] + ", ";
			}
		}
		
		for (int i = 0; i < values.length; i++)
		{
			if (i == values.length - 1)
			{
				row += values[i].toString();
			}
			else
			{
				row += values[i].toString() + ", ";
			}
		}
		
		onUpdate("INSERT INTO " + tableName + "(" + columns + ") VALUES(" + row + ")");
	}
	
	public static void example()
	{
		System.out.println(
				  "public static void main(String[] args)\r\n"
				+ "{\r\n"
				+ "    SQLite.setUrl(\"test.db\");\r\n"
				+ "    SQLite.connect();\r\n"
				+ "    SQLite.addTable(\"students\", new Component(\"id\", Integer.class), new Component(\"name\", String.class), new Component(\"age\", Integer.class));\r\n"
				+ "    \r\n"
				+ "    SQLite.add(\"students\", new String[]{\"id\", \"name\", \"age\"}, \"\\\"kilian\\\"\", 18);\r\n"
				+ "    SQLite.add(\"students\", new String[]{\"id\", \"name\", \"age\"}, \"\\\"anna\\\"\", 16);\r\n"
				+ "    \r\n"
				+ "    System.out.println(SQLite.getFromTable(\"students\", \"age\", \"id\", 2));"
				+ "\r\n"
				+ "    SQLite.disconnect();\r\n"
				+ "}");
	}
}