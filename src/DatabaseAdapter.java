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
        prepstmt = null;
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

    /*
        Updates the account's marketaccount with the specified amount
        @param account the account to update
        @param depositAmount the amount to deposit
        @param updateType whether we want to deposit or withdraw
        @return true if successful, false otherwise
    */
    public boolean updateMarketAccount(Account account, float depositAmount, int updateType)
    {
        connect();
        String sql = "";
        String username = account.getUsername();
        try
        {
            //deposit
            if(updateType == 0)
                sql = "UPDATE MarketAccount M, OwnsMarket O, Customer C SET M.mbalance= M.mbalance + ? WHERE C.username=? AND O.m_aid = M.m_aid;";
            //withdraw
            else
                sql = "UPDATE MarketAccount M, OwnsMarket O, Customer C SET M.mbalance= M.mbalance - ? WHERE C.username=? AND O.m_aid = M.m_aid;";
            
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setFloat(1, depositAmount);
            prepstmt.setString(2,username);

            prepstmt.executeUpdate();
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return false;
        }
        finally
        {
            close();
        }
        return true;
    }
    /*
        Queries the database and retrives the market account balance for a 
        given account
        @param account the account to get the market balance of
        @return the market balance if found, -1 if error occurs
    */
    public float getMarketAccountBalance(Account account)
    {
        connect();
        String sql = "";
        String username = account.getUsername();
        float balance = 0;
        try
        {
            sql = "SELECT M.mbalance FROM MarketAccount M, OwnsMarket O, Customer C WHERE C.username=? AND O.m_aid = M.m_aid;";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);


            rs = prepstmt.executeQuery();
            //if rs is not null, then query was successful
            if(rs.next())
            {
                balance = rs.getFloat("mbalance");
            }
            else
                return -1;
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return -1;
        }
        finally
        {
            close();
        }
        return balance;
    }
}
