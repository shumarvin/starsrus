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
    private PreparedStatement prepstmt;
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
        }
        catch(Exception e)
        {
            //Handle errors for Class.forName
            e.printStackTrace();
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
            //close prepared statement
            if(prepstmt != null)
                prepstmt.close();
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
        }
    }
    /*
        Queries the database for the account associated with the username
        @param accountType the type of account (Customer or Manager)
        @param username,password the username and password
        @return Account object with all its fields initialized if found,
                default Account object if username/password incorrect
    */
    public Account queryAccount(int accountType, String username, String password)
    {
        connect();
        //create query
        String sql = "";
        try
        {
            //customer/manager login query
            if(accountType == 0)
                sql = "SELECT * FROM Customer WHERE username=? AND password=?";
            else
                sql = "SELECT * FROM Manager WHERE username=? AND password=?";

            //use prepared statement
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            prepstmt.setString(2, password);

            //execute query
            rs = prepstmt.executeQuery();

            //process query
            /*
                If rs has something, then that is a valid username-password.
                If it doesn't, then it was invalid.
            */
            if(rs.next())
            {
                return new Account(username, password, rs.getString("firstName"),
                rs.getString("lastName"),rs.getString("state"), rs.getString("phone"),
                rs.getString("email"),rs.getInt("taxid"));

            }
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
        finally
        {
            close();
        }
        return new Account();
    }
    /*
        Create account.
        @param accountType the type of account (Customer or Manager)
    */
    public int createAccount(int placeholder)
    {
        return 0;
    }
}
