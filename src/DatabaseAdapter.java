//class that interfaces with the database
import java.sql.*;

public class DatabaseAdapter
{
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu/marvinshuDB";
    
    //  Database credentials
    private static final String USER = "marvinshu";
    private static final String PASS = "477";

    //connection, query, and result variables
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    //constructor
    public DatabaseAdapter()
    {
        Connection conn = null;
        Statement stmt = null;
        rs = null;
    }

    //connect to database, make sure to call close afterwards
    public void connect()
    {
        try
        {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            //System.out.println("Connecting to the database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //System.out.println("Connected database successfully...");
        }
        catch(SQLException se)
        {
            //Handle errors for JDBC
            se.printStackTrace();
            System.exit(0);
        }
        catch(Exception e)
        {
            //Handle errors for Class.forName
            e.printStackTrace();
            System.exit(0);
 		}
    }
  //close database connection
  public void close()
  {
    try
    {
        //close statement
        if(stmt != null)
            stmt.close();
    }
    catch(SQLException se)
    {
        se.printStackTrace();
    }
    try
    {
        //close connection
        if(conn!=null)
        {
            //System.out.println("Closing Connection...");
            conn.close();
            //System.out.println("Connection Closed.");
        }
    }
    catch(SQLException se)
    {
        se.printStackTrace();
        System.exit(0);
    }
  }
    
  public Account getAccount(int accountType, String username, String password)
  {
    connect();
    //create account that will be returned
    Account account = new Account();
    //create query
    String sql = "";
    try
    {
        stmt = conn.createStatement();
        //customer login
        if(accountType == 0)
            sql = "SELECT * FROM Customer WHERE username = " + username;
        else
            sql = "SELECT * FROM Manager WHERE username = " + username;

        //execute query
        rs = stmt.executeQuery(sql);

        //process query 
        if(rs.next())
        {
            account = new Account(username, password, rs.getString("name"), 
            rs.getString("state"), rs.getString("phone"), rs.getString("email"),
            rs.getInt("taxid"));
        }
    }
    catch(SQLException se)
    {
        se.printStackTrace();
        System.exit(0);
    }
    close();
    return account;
  }
}
