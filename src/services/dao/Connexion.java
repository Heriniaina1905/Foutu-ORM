package services.dao;

/**
 * @author Heriniaina-P11A-N48
 **/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Connexion {
    /// Fields
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public Connexion(String database, String server, int port, String dbname, String username, String pass) throws Exception {
        try {
        	String driver = null;
        	// step1 identify database 
        	switch(database) {
        		case "mysql":
        			driver = "com.mysql.jdbc.Driver";
        			break;
        		case "postgresql":
        			driver = "org.postgresql.Driver";
        			break; 
        		case "oracle":
        			driver = "oracle.jdbc.OracleDriver";
        			break;
        	}
        	
        	
            // step2 load the driver class
            Class.forName(driver);

            // step3 create the connection object
            this.setConnection(
                    DriverManager.getConnection("jdbc:"+database+"://"+server+":"+port+"/"+dbname+"", username, pass));
            // step4 create the statement object
            // TYPE_SCROLL_INSENSITIVE TO PREVENT DATA CHANGE FROM DATABASE
            //
            this.setStatement(
                    this.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
            System.out.println("Database Connected!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public Connexion(String dbname) throws Exception {
        try {
            // step1 load the driver class
            Class.forName("com.mysql.jdbc.Driver");

            // step2 create the connection object
            this.setConnection(
                    DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbname, "root", ""));
            // step3 create the statement object
            // TYPE_SCROLL_INSENSITIVE TO PREVENT DATA CHANGE FROM DATABASE
            //
            this.setStatement(
                    this.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
            System.out.println("Database Connected!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public Connexion() {
    	
    }

    public void clear() throws Exception {
        statement.close();
        connection.close();
        System.out.println("Database Disconnected!");
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return the statement
     */
    public Statement getStatement() {
        return this.statement;
    }

    /**
     * @param statement the statement to set
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    /**
     * @return the resultSet
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * @param resultSet the resultSet to set
     */
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }
}
