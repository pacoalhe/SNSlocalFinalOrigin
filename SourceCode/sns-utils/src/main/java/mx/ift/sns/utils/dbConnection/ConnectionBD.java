package mx.ift.sns.utils.dbConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConnectionBD {

	private static Connection connection = null;

	// Parameters for conexion SNS
	private final static String DB_ADDRESS_SERVER = "db.address.server";
	private final static String DB_INSTANCE = "db.instance";
	private final static String DB_USER_NAME = "db.user.name";
	private final static String DB_USER_PASSWORD = "db.user.password";
	private final static String DB_PORT = "db.port";
	private final static String DB_DRIVER = "db.driver";
	private final static String DELIMITATOR=":";

	private String addressServer;
	private String dbInstance;
	private String dbUserName;
	private String dbUserPassword;
	private String dbPort;
	private String dbDriver;

	/**
     * This method load the BD Connection parameters
     */
    private static Map<String, String> loadConfig() {
    	Map<String, String> paramCon=new HashMap<>();
    	try {
        	
        	Properties prop = new Properties();
    		String propFileName = "parameters.properties";
    		InputStream inputStream = ConnectionBD.class.getClassLoader().getResourceAsStream(propFileName);
    		prop.load(inputStream);
    		
    		paramCon.put(DB_ADDRESS_SERVER, prop.getProperty(DB_ADDRESS_SERVER));
    		paramCon.put(DB_INSTANCE, prop.getProperty(DB_INSTANCE));
    		paramCon.put(DB_USER_NAME, prop.getProperty(DB_USER_NAME));
    		paramCon.put(DB_USER_PASSWORD, prop.getProperty(DB_USER_PASSWORD));
    		paramCon.put(DB_PORT, prop.getProperty(DB_PORT));
    		//paramCon.put("dbDriver", prop.getProperty(DB_DRIVER));
    		
    		//load the db driver
    		Class.forName(prop.getProperty(DB_DRIVER));
    		System.out.println("loadConfig");
        }
        catch (Exception e) {
            errorHandler("Failed to load the paramet ",e);
        }
        return paramCon;
    }

	/**
	 * Method that loads the connection into the right property
	 * @return void
	 **/

	private static void loadConnection(Map<String, String> params) {
		try {
			System.out.println("loadConnection1");
			StringBuilder url=new StringBuilder();
			url.append(params.get(DB_ADDRESS_SERVER)).
				append(DELIMITATOR).
				append(params.get(DB_PORT)).
				append(DELIMITATOR).
				append(params.get(DB_INSTANCE));
			String user=params.get(DB_USER_NAME);
			String pass=params.get(DB_USER_PASSWORD);
			System.out.println(" "+url.toString()+" "+pass+" "+user);
			connection = DriverManager.getConnection(url.toString(), user, pass);
			System.out.println("loadConnection");
		} catch (SQLException e) {
			errorHandler("Failed to connect to the database ", e);
		}
	}
	
	/**
	 * Method that loads the connection into the right property
	 * @return void
	 **/

	private static Connection loadConnection(Map<String, String> params, Connection conn) {
		try {
			System.out.println("loadConnection");
			StringBuilder url=new StringBuilder();
			url.append(params.get(DB_ADDRESS_SERVER)).
				append(DELIMITATOR).
				append(params.get(DB_PORT)).
				append(DELIMITATOR).
				append(params.get(DB_INSTANCE));
			String user=params.get(DB_USER_NAME);
			String pass=params.get(DB_USER_PASSWORD);
			System.out.println(" "+url.toString()+" "+pass+" "+user);
			System.out.println("loadConnection");
			conn = DriverManager.getConnection(url.toString(), user, pass);
			
		} catch (SQLException e) {
			errorHandler("Failed to connect to the database ", e);
			conn=null;
		}
		return conn;
		
	}

	/**
	 * Method that shows the errors thrown by the singleton
	 * @param {String}
	 * Message
	 * @option {Exception} e
	 * @return void
	 **/

	private static void errorHandler(String message, Exception e) {
		System.out.println(message);
		if (e != null)
			System.out.println(e.getMessage());
	}


	/**
	 * Static method that returns the instance for the singleton
	 * 
	 * @return {Connection} connection
	 **/

	public static Connection getConnection() {
		try {
			if (connection == null) {
//				connection=null;
				System.out.println("getConnection");
				Map<String,String> params=loadConfig();
				loadConnection(params);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return connection;
	}
	
	/**
	 * Static method that returns the instance for the singleton
	 * 
	 * @return {Connection} connection
	 **/

	public static Connection getNewConnection() {
		Connection conn=null;
		try {	
				System.out.println("getConnection");
				Map<String,String> params=loadConfig();
				loadConnection(params, conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return conn;
	}

	/**
	 * Static method that close the connection to the database
	 * 
	 * @return void
	 **/

	public static void closeConnection() {
		if (connection == null) {
			errorHandler("No connection found", null);
		} else {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				errorHandler("Failed to close the connection", e);
			}
		}
	}

	public String getAddressServer() {
		return addressServer;
	}

	public void setAddressServer(String addressServer) {
		this.addressServer = addressServer;
	}

	public String getDbInstance() {
		return dbInstance;
	}

	public void setDbInstance(String dbInstance) {
		this.dbInstance = dbInstance;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbUserPassword() {
		return dbUserPassword;
	}

	public void setDbUserPassword(String dbUserPassword) {
		this.dbUserPassword = dbUserPassword;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	
	

}
