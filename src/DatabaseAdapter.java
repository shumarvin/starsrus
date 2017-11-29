//class that interfaces with the database
import java.sql.*;
import java.util.HashMap;

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
                if (accountType == 0) {
                  return new Account(username, password, rs.getString("firstName"),
                  rs.getString("lastName"),rs.getString("state"), rs.getString("phone"),
                  rs.getString("email"),rs.getInt("taxid"));
                }else{
                  return new Account(username, password, null, null, null, null, null, -1);
                }
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
        @param accountType the type of account (0 -> Customer or 1 -> Manager)
    */
    public int createAccount(int accountType, String username, String password,
    String firstName, String lastName, String state, String phone, String email, int taxid)
    {
        String updateSql = "";
        int returnVal = 0;
        if (accountType == 0) { // Create customer account

        } else {
            //Everything other than username and password is null, taxid = -1
            System.out.println("Create account in DatabaseAdapter");
            updateSql = "INSERT INTO Manager (username, password)"
              + "VALUES (?,?);";
        }

        try {
            connect();
            prepstmt = conn.prepareStatement(updateSql);
            if (accountType == 0) {
              // placeholder for create customer account prepstmts
            } else {
              prepstmt.setString(1, username);
              prepstmt.setString(2, password);
            }
            prepstmt.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
            returnVal = -1;
        } finally {
            close();
        }
        return returnVal;
    }
    /*
        Updates the account's marketaccount with the specified amount and
        records the transaction
        @param account the account to update
        @param depositAmount the amount to deposit
        @param updateType whether we want to deposit(0) or withdraw(1)
        @return true if successful, false otherwise
    */
    public boolean updateMarketAccount(Account account, float updateAmount, int updateType)
    {
        String updateSql = "";
        String insertSqlTransaction = "";
        String insertSQLMarketTransaction = "";
        String username = account.getUsername();
        float currentBalance = getMarketAccountBalance(account);
        Date date = getCurrentDate();

        //check to make sure account balance doesn't go below 0 for withdraws
        if(updateType != 0 && currentBalance - updateAmount < 0)
        {
            System.out.println("Error! Not enough money to withdraw!");
            return false;
        }

        try
        {
            connect();

            /*
                Use SQL transaction to add/subtract money from Market Account
                and record transaction
            */
            conn.setAutoCommit(false);

            //deposit
            if(updateType == 0)
            {
                updateSql = "UPDATE MarketAccount M, OwnsMarket O, Customer C "
                            + "SET M.mbalance= M.mbalance + ? WHERE C.username=? AND O.m_aid = M.m_aid;";
                insertSqlTransaction = "INSERT INTO Transactions (transDate, marketIn) "
                            + "VALUES (?,?); ";

                insertSQLMarketTransaction = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                            + "SELECT M.m_aid, LAST_INSERT_ID() "
                                            + "FROM MarketAccount M, OwnsMarket O, Customer C "
                                            + "WHERE C.username=? AND O.m_aid = M.m_aid;";
            }
            //withdraw
            else
            {
                updateSql = "UPDATE MarketAccount M, OwnsMarket O, Customer C "
                            + "SET M.mbalance= M.mbalance - ? WHERE C.username=? AND O.m_aid = M.m_aid;";
                insertSqlTransaction = "INSERT INTO Transactions (transDate, marketOut) "   
                            + "VALUES (?,?); ";

                insertSQLMarketTransaction = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                            + "SELECT M.m_aid, LAST_INSERT_ID() "
                                            + "FROM MarketAccount M, OwnsMarket O, Customer C "
                                            + "WHERE C.username=? AND O.m_aid = M.m_aid;";
            }
            try
            {
                //first update market account balance
                prepstmt = conn.prepareStatement(updateSql);
                prepstmt.setFloat(1, updateAmount);
                prepstmt.setString(2,username);
                prepstmt.executeUpdate();

                //then record the transaction
                prepstmt = conn.prepareStatement(insertSqlTransaction);
                prepstmt.setDate(1, date);
                prepstmt.setFloat(2, updateAmount);
                prepstmt.executeUpdate();

                prepstmt = conn.prepareStatement(insertSQLMarketTransaction);
                prepstmt.setString(1,username);
                prepstmt.executeUpdate();

                conn.commit();
                conn.setAutoCommit(true);
            }
            catch(SQLException se)
            {
                se.printStackTrace();
                conn.rollback();
                return false;
            }


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
        String sql = "";
        String username = account.getUsername();
        float balance = 0;
        try
        {
            connect();

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
    /*
        Retrieves the current date from the database
        @return date the current date of tpye java.sql.Date
    */
    public Date getCurrentDate()
    {
        String sql = "";
        java.sql.Date date = null;
        try
        {
            connect();

            //sql query for date
            sql = "SELECT * FROM Date";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            //get date
            while(rs.next())
            {
                date = rs.getDate("currentDate");
            }
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return null;
        }
        finally
        {
            close();
        }
        return date;
    }
    /*
        Gets all the stocks and their prices
        @return stocks a Hashmap of all the 
        stock symbols mapped to their
        current prices
    */
    public HashMap<String,Float> getStocks()
    {
        String sql = "";
        HashMap<String,Float> stocks = new HashMap<String,Float>();

        try
        {
            connect();

            //sql query
            sql = "SELECT stocksymbol, currentprice "
                + "FROM Stock;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            //get results and store in stockSymbols
            while(rs.next())
            {
                stocks.put(rs.getString("stocksymbol"), rs.getFloat("currentprice"));
            }

        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return null;
        }
        finally
        {
            close();
        }
        return stocks;
    }
}
