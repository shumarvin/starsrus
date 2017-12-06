//class that interfaces with the database
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;

public class DatabaseAdapter
{
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu/marvinshuDB";
    private static final String MOVIE_DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu/moviesDB";

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
        conn = null;
        stmt = null;
        prepstmt = null;
        rs = null;
    }

    /*
        Connect to either system database or movie database
        @param database which database to connect to (0 for system, 1 for movie)
    */
    private void connect(int database)
    {
        //if connection already established,
        //don't do anything
        if(conn != null)
        {
            return;
        }
        try
        {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            if(database == 0)
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            else
                conn = DriverManager.getConnection(MOVIE_DB_URL, USER, PASS);
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
    private void close()
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
                conn.close();
                conn = null;
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
        //create query
        String sql = "";
        Account account =  null;
        try
        {
            connect(0);

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
                if (accountType == 0) 
                {
                  account = new Account(username, password, rs.getString("firstName"),
                  rs.getString("lastName"),rs.getString("state"), rs.getString("phone"),
                  rs.getString("email"),rs.getInt("taxid"));
                }
                else
                {
                  account = new Account(username, password, null, null, null, null, null, -1);
                }
            }
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return account;
        }
        finally
        {
            close();
        }
        return account;
    }
    /*
        Gets the account associated with the username
        Precondition: username is valid
        @param username the username of the account
        @return Account the account associated with the username
    */
    public Account getAccount(String username)
    {
        String sql = "";
        Account account = null;
        try
        {
            connect(0);

            sql = "SELECT * FROM Customer WHERE username=?;";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            rs = prepstmt.executeQuery();

            while(rs.next())
            {
                account = new Account(username, rs.getString("password"), rs.getString("firstName"),
                  rs.getString("lastName"),rs.getString("state"), rs.getString("phone"),
                  rs.getString("email"),rs.getInt("taxid"));
            }
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return account;
        }
        finally
        {
            close();
        }
        return account;
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
            System.out.println("Create account in DatabaseAdapter");
            updateSql = "INSERT INTO Customer (username, password, firstName, "
              + "lastName, state, phone, email, taxid)"
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        } else {
            System.out.println("Create account in DatabaseAdapter");
            updateSql = "INSERT INTO Manager (username, password)"
              + "VALUES (?, ?);";
        }

        try {
            connect(0);
            prepstmt = conn.prepareStatement(updateSql);
            if (accountType == 0) {
              prepstmt.setString(1, username);
              prepstmt.setString(2, password);
              prepstmt.setString(3, firstName);
              prepstmt.setString(4, lastName);
              prepstmt.setString(5, state);
              prepstmt.setString(6, phone);
              prepstmt.setString(7, email);
              prepstmt.setInt(8, taxid);
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

        // returnVal == 0 if customer account has been successfully created
        // then update ownsStock and ownsMarket
        if (returnVal == 0 && accountType == 0) {
            String ownsStockSql = "";
            String ownsMarketSql = "";
            ownsStockSql = "INSERT INTO OwnsStock (username)"
                + "VALUES (?);";
            ownsMarketSql = "INSERT INTO OwnsMarket (username, mbalance)"
                + "VALUES (?, ?);";


            try {
                connect(0);

                // Create new entry in ownsStock, creating a new stock account
                prepstmt = conn.prepareStatement(ownsStockSql);
                prepstmt.setString(1, username);
                prepstmt.executeUpdate();

                // Create new entry in ownsMarket with $1,000 balance
                prepstmt = conn.prepareStatement(ownsMarketSql);
                prepstmt.setString(1, username);
                prepstmt.setFloat(2, 1000);
                prepstmt.executeUpdate();
            } catch(SQLException se) {
                se.printStackTrace();
                returnVal = -1;
            } finally {
                close();
            }

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
            connect(0);

            /*
                Use SQL transaction to add/subtract money from Market Account
                and record transaction
            */
            conn.setAutoCommit(false);

            //deposit
            if(updateType == 0)
            {
                updateSql = "UPDATE OwnsMarket O "
                            + "SET mbalance= mbalance + ? WHERE username=?;";
                insertSqlTransaction = "INSERT INTO Transactions (transDate, marketIn) "
                            + "VALUES (?,?); ";

                insertSQLMarketTransaction = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                            + "SELECT O.m_aid, LAST_INSERT_ID() "
                                            + "FROM OwnsMarket O "
                                            + "WHERE O.username=?;";
            }
            //withdraw
            else
            {
                updateSql = "UPDATE OwnsMarket O "
                            + "SET mbalance= mbalance - ? WHERE username=?;";
                insertSqlTransaction = "INSERT INTO Transactions (transDate, marketOut) "
                            + "VALUES (?,?); ";

                insertSQLMarketTransaction = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                            + "SELECT O.m_aid, LAST_INSERT_ID() "
                                            + "FROM OwnsMarket O "
                                            + "WHERE O.username=?;";
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
            connect(0);

            sql = "SELECT mbalance FROM OwnsMarket "
                + "WHERE username=?;";

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
            connect(0);

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
        Checks whether market is open or not
        @return true if open, false if closed
    */
    public boolean isMarketOpen()
    {
        String sql = "";
        boolean isMarketOpen = false;

        try
        {
            connect(0);

            sql = "SELECT Open FROM Date;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while(rs.next())
            {
                if(rs.getInt("Open") == 1)
                    isMarketOpen = true;
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
        return isMarketOpen;
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
            connect(0);

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
    /*
        Checks to see if a particular stock exists
        @param stocksymbol the stock to check
        @return true if it exists, false otherwise
    */
    public boolean hasStock(String stocksymbol)
    {
        String sql = "";
        try
        {
            connect(0);

            //sql query
            sql = "SELECT stocksymbol FROM Stock "
                + "WHERE stocksymbol = ?";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, stocksymbol);
            rs = prepstmt.executeQuery();

            //if rs is not empty, then stock exists
            if(rs.next())
                return true;
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
        return false;
    }
    /*
        Method for buying stocks
        @param account the trader that's buying
        @param stockSymbol the stock to buy
        @param numShares the number of shares to buy
        @param sharePrice the price of each share
        @return true if successful, false otherwise
    */
    public boolean buyStock(Account account, String stockSymbol, float numShares, float sharePrice)
    {
        String sql = "";
        String updateSql = "";
        String insertSqlTransaction = "";
        String insertSQLMarketTransaction = "";
        String updateSqlTransaction = "";
        String insertSQLStockTransaction = "";
        String sqlTracksStocks = "";
        String username = account.getUsername();
        float currentBalance = getMarketAccountBalance(account);
        Date date = getCurrentDate();
        float commission = 0;

        //check to make sure customer isn't buying
        //negative amount of shares
        if(numShares < 0)
        {
            System.out.println("Error! Cannot buy negative shares!");
            System.out.println();
            return false;
        }
        try
        {
            connect(0);

            //query database for buy commission
            sql = "SELECT amount FROM Commission WHERE transaction = 'buy';";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                commission = rs.getFloat("amount");
            }

            //calculate total price
            float totalPrice = sharePrice * numShares + commission;

            //check to make sure account balance doesn't go below 0 for withdraws
            if(currentBalance - totalPrice < 0)
            {
                System.out.println("Error! Not enough money to withdraw!");
                return false;
            }
            /*
                Use SQL transaction to withdraw money and buy stock
                at same time
            */
            conn.setAutoCommit(false);

            //withdraw total amount from trader's market account
            updateSql = "UPDATE OwnsMarket O "
                            + "SET mbalance= mbalance - ? WHERE username=?";
            insertSqlTransaction = "INSERT INTO Transactions (transDate, marketOut) "
                        + "VALUES (?,?); ";

            insertSQLMarketTransaction = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                        + "SELECT O.m_aid, LAST_INSERT_ID() "
                                        + "FROM OwnsMarket O "
                                        + "WHERE O.username=?;";

            //now record transaction
            updateSqlTransaction = "UPDATE Transactions SET sharesIn = ?, stockSymbol = ? "
                                + "WHERE transNum = LAST_INSERT_ID();";

            insertSQLStockTransaction = "INSERT INTO StockTransactions (s_aid,transNum) "
                                        + "SELECT S.s_aid, LAST_INSERT_ID()"
                                        + "FROM OwnsStock S "
                                        + "WHERE S.username=?;";

            //if customer has already bought the stock at the current sharprice,
            //increase the sbalance of that share
            if(hasBoughtStock(account, stockSymbol, sharePrice))
            {
                sqlTracksStocks = "UPDATE TracksStocks SET sbalance = sbalance + ? "
                                + "WHERE stocksymbol = ? AND s_aid IN (SELECT S.s_aid FROM (SELECT * FROM TracksStocks) AS T, OwnsStock S"
                                +" WHERE S.username = ? AND S.s_aid = T.s_aid)";
            }
            //otherwise, make a new entry
            else
            {
                sqlTracksStocks = "INSERT INTO TracksStocks (s_aid, stocksymbol, sbalance, buyprice) "
                                + "SELECT O.s_aid, ?, ?, ? "
                                + "FROM Customer C, OwnsStock O "
                                + "WHERE C.username = ? AND C.username = O.username";
            }

            try
            {
                //first update market account balance
                prepstmt = conn.prepareStatement(updateSql);
                prepstmt.setFloat(1, totalPrice);
                prepstmt.setString(2,username);
                prepstmt.executeUpdate();

                //then record the transaction
                prepstmt = conn.prepareStatement(insertSqlTransaction);
                prepstmt.setDate(1, date);
                prepstmt.setFloat(2, totalPrice);
                prepstmt.executeUpdate();

                prepstmt = conn.prepareStatement(insertSQLMarketTransaction);
                prepstmt.setString(1,username);
                prepstmt.executeUpdate();

                //add shares in and stock symbol to transaction
                prepstmt = conn.prepareStatement(updateSqlTransaction);
                prepstmt.setFloat(1, numShares);
                prepstmt.setString(2, stockSymbol);
                prepstmt.executeUpdate();

                //insert transaction into StockTransaction table
                prepstmt =  conn.prepareStatement(insertSQLStockTransaction);
                prepstmt.setString(1, username);
                prepstmt.executeUpdate();

                //record stock in TracksStocks
                if(hasBoughtStock(account, stockSymbol, sharePrice))
                {
                    prepstmt = conn.prepareStatement(sqlTracksStocks);
                    prepstmt.setFloat(1, numShares);
                    prepstmt.setString(2, stockSymbol);
                    prepstmt.setString(3, username);
                    prepstmt.executeUpdate();
                }
                else
                {
                    prepstmt = conn.prepareStatement(sqlTracksStocks);
                    prepstmt.setString(1, stockSymbol);
                    prepstmt.setFloat(2, numShares);
                    prepstmt.setFloat(3, sharePrice);
                    prepstmt.setString(4, username);
                    prepstmt.executeUpdate();
                }


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
        Method for selling stocks
        @param account the trader that's buying
        @param stockSymbol the stock to buy
        @param numShares the number of shares to buy
        @param sharePrice the price of each share
        @return true if successful, false otherwise
    */
    public boolean sellStock(Account account, String stockSymbol, float numShares, float sharePrice)
    {
        String sql = "";
        String username = account.getUsername();
        String takeStockSql = "";
        String updateSql = "";
        String insertSqlTransaction = "";
        String insertSQLMarketTransaction = "";
        String updateSqlTransaction = "";
        String insertSQLStockTransaction = "";
        float commission = -1;
        float currentprice = -1;
        float profit = 0;
        Date date = getCurrentDate();
        //ArrayList<OwnedStocks> stocks = dbAdapter.getOwnedStocks(account);
        try
        {
            connect(0);
            // Check if customer has said stocks to sell
            sql = "SELECT * FROM TracksStocks T, OwnsStock O "
                + "WHERE O.username=? AND O.s_aid=T.s_aid AND T.stocksymbol=? "
                + "AND T.buyprice=? AND T.sbalance>=?;";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            prepstmt.setString(2, stockSymbol);
            prepstmt.setFloat(3, sharePrice);
            prepstmt.setFloat(4, numShares);
            rs = prepstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Invalid stock");
                close();
                return false;
            }

            sql = "SELECT amount FROM Commission WHERE transaction = 'sell';";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                commission = rs.getFloat("amount");
            }

            sql = "SELECT currentprice FROM Stock WHERE stocksymbol = ?;";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, stockSymbol);
            rs = prepstmt.executeQuery();
            while(rs.next())
            {
                currentprice = rs.getFloat("currentprice");
            }
            float totalSellPrice = numShares * currentprice - commission;
            profit = numShares*(currentprice - sharePrice);


            conn.setAutoCommit(false);

            try
            {
                // Decrease sbalance in appropriate tuple in TracksStocks
                takeStockSql = "UPDATE TracksStocks SET sbalance = sbalance - ? "
                             + "WHERE (s_aid, stocksymbol, sbalance, buyprice) IN "
                             + "(SELECT T.s_aid, T.stocksymbol, T.sbalance, T.buyprice FROM (SELECT * FROM TracksStocks) AS T, OwnsStock O "
                             + "WHERE O.username=? AND O.s_aid=T.s_aid AND T.stocksymbol=? "
                             + "AND T.buyprice=? AND T.sbalance>=?);";

                //deposit total amount into trader's market account
                updateSql = "UPDATE OwnsMarket O "
                          + "SET mbalance= mbalance + ? WHERE username=?";
                insertSqlTransaction = "INSERT INTO Transactions (transDate, marketIn) "
                          + "VALUES (?,?); ";

                insertSQLMarketTransaction = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                           + "SELECT O.m_aid, LAST_INSERT_ID() "
                                           + "FROM OwnsMarket O "
                                           + "WHERE O.username=?;";

                //now record transaction
                updateSqlTransaction = "UPDATE Transactions SET sharesOut = ?, stockSymbol = ?, profit = ? "
                                     + "WHERE transNum = LAST_INSERT_ID();";

                insertSQLStockTransaction = "INSERT INTO StockTransactions (s_aid,transNum) "
                                            + "SELECT S.s_aid, LAST_INSERT_ID()"
                                            + "FROM OwnsStock S "
                                            + "WHERE S.username=?;";

                //first update stock balance
                prepstmt = conn.prepareStatement(takeStockSql);
                prepstmt.setFloat(1, numShares);
                prepstmt.setString(2, username);
                prepstmt.setString(3, stockSymbol);
                prepstmt.setFloat(4, sharePrice);
                prepstmt.setFloat(5, numShares);
                prepstmt.executeUpdate();


                //deposit total amount into trader's market balance
                prepstmt = conn.prepareStatement(updateSql);
                prepstmt.setFloat(1, totalSellPrice);
                prepstmt.setString(2, username);
                prepstmt.executeUpdate();

                //then record the transaction
                prepstmt = conn.prepareStatement(insertSqlTransaction);
                prepstmt.setDate(1, date);
                prepstmt.setFloat(2, totalSellPrice);
                prepstmt.executeUpdate();

                prepstmt = conn.prepareStatement(insertSQLMarketTransaction);
                prepstmt.setString(1,username);
                prepstmt.executeUpdate();

                //add shares out and stock symbol to transaction
                prepstmt = conn.prepareStatement(updateSqlTransaction);
                prepstmt.setFloat(1, numShares);
                prepstmt.setString(2, stockSymbol);
                prepstmt.setFloat(3, profit);
                prepstmt.executeUpdate();

                //insert transaction into StockTransaction table
                prepstmt = conn.prepareStatement(insertSQLStockTransaction);
                prepstmt.setString(1, username);
                prepstmt.executeUpdate();

                conn.commit();
                conn.setAutoCommit(true);

                if (getStocksBalance(account, stockSymbol, sharePrice) == 0)
                {
                    sql = "DELETE FROM TracksStocks "
                        + "WHERE (s_aid, stocksymbol, sbalance, buyprice) IN "
                             + "(SELECT T.s_aid, T.stocksymbol, T.sbalance, T.buyprice FROM (SELECT * FROM TracksStocks) AS T, OwnsStock O "
                             + "WHERE O.username=? AND O.s_aid=T.s_aid AND T.stocksymbol=? "
                             + "AND T.buyprice=? AND T.sbalance = 0);";
                    prepstmt = conn.prepareStatement(sql);
                    prepstmt.setString(1, username);
                    prepstmt.setString(2, stockSymbol);
                    prepstmt.setFloat(3, sharePrice);
                    prepstmt.executeUpdate();
                }
            }
            catch(SQLException se)
            {
                se.printStackTrace();
                return false;
            }

            return true;


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
    }

    private float getStocksBalance(Account account, String stocksymbol, float buyPrice)
    {
        String sql = "";
        String username = account.getUsername();
        float stocksBalance = -1;
        try
        {
            connect(0);

            sql = "SELECT * FROM TracksStocks T, OwnsStock S "
                + "WHERE S.username=? AND S.s_aid=T.s_aid AND T.stocksymbol=? "
                + "AND T.buyprice=?";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            prepstmt.setString(2, stocksymbol);
            prepstmt.setFloat(3, buyPrice);
            rs = prepstmt.executeQuery();
            while (rs.next())
            {
                stocksBalance = rs.getFloat("sbalance");
            }
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return -1;
        }
        return stocksBalance;
    }

    /*
        Method to get an account's owned stocks
        @param account the account
        @return stocks an ArrayList of stocks owned by that account
    */
    public ArrayList<OwnedStocks> getOwnedStocks(Account account)
    {
        String sql = "";
        String username = account.getUsername();
        ArrayList<OwnedStocks> stocks = new ArrayList<OwnedStocks>();
        try
        {
            connect(0);
            sql = "SELECT * FROM TracksStocks T, OwnsStock S "
                + "WHERE S.username=? AND S.s_aid=T.s_aid ";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            rs = prepstmt.executeQuery();
            while (rs.next())
            {
              stocks.add(new OwnedStocks(rs.getString("stocksymbol"), rs.getFloat("sbalance")
                  , rs.getFloat("buyprice")));
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
    /*
        Checks to see if trader has bought the stock at the specified share price
        @param Account the trader's account
        @param stockSymbol the stock
        @param sharePrice the stock's share price
        @return true if stock is found, false otherwise
    */
    private boolean hasBoughtStock(Account account, String stockSymbol, float sharePrice)
    {
        String sql = "";
        String username = account.getUsername();
        boolean hasStock = false;

        try
        {
            //sql quey
            sql = "SELECT * FROM TracksStocks T, OwnsStock O "
                + "WHERE O.username = ? AND O.s_aid = T.s_aid AND "
                + "T.stockSymbol = ? AND T.buyprice = ?;";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            prepstmt.setString(2, stockSymbol);
            prepstmt.setFloat(3, sharePrice);
            rs = prepstmt.executeQuery();

            if(rs.next())
                hasStock = true;
        }
        catch(SQLException se)
        {
            se.printStackTrace();
            return false;
        }
        return hasStock;
    }


    /*
        Method to get a trader's stock transactions.
        @return transactionsAList an Arraylist of all the
                transactions for that account
    */
    public ArrayList<Transaction> getTransactions(Account account)
    {
      String sql = "";
      String username = account.getUsername();
      int s_aid = -1;
      ArrayList<Transaction> transactionsAList = new ArrayList<Transaction>();
      try
      {
          connect(0);

          // First get s_aid using account's username.
          sql = "SELECT * FROM OwnsStock S WHERE S.username=?";
          prepstmt = conn.prepareStatement(sql);
          prepstmt.setString(1, username);
          rs = prepstmt.executeQuery();
          while (rs.next())
          {
              s_aid = rs.getInt("s_aid");
          }

          // Get all transactions that match each transNum in transNumAList
          sql = "SELECT * FROM Transactions T "
              + "WHERE T.transNum IN (SELECT S.transNum FROM StockTransactions S WHERE S.s_aid=?)";
          prepstmt = conn.prepareStatement(sql);
          prepstmt.setInt(1, s_aid);
          rs = prepstmt.executeQuery();
          while(rs.next())
          {
              transactionsAList.add(new Transaction(
                rs.getInt("transNum"),
                rs.getDate("transDate"),
                rs.getFloat("marketIn"),
                rs.getFloat("marketOut"),
                rs.getFloat("sharesIn"),
                rs.getFloat("sharesOut"),
                rs.getString("stocksymbol"),
                rs.getFloat("profit")));
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
      return transactionsAList;
    }
    // Method to get actor profile and movie contracts for that actor
    public ArrayList<String> getActorProfile(String stocksymbol)
    {
        String sql = "";
        float value = -1;
        ArrayList<String> output = new ArrayList<String>();
        try
        {
            connect(0);

            sql = "SELECT * FROM ActorProfile WHERE stocksymbol=?";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, stocksymbol);
            rs = prepstmt.executeQuery();
            // Should only return one actor profile IMPORTANT NOTE
            // First 3 entries are stocksymbol, actor name, and dob
            // Every subsequent 5 entries are movie contracts
            while(rs.next())
            {
              output.add(rs.getString("stocksymbol"));
              output.add(rs.getString("aname"));
              output.add(String.format("%s", rs.getDate("dob")));
            }

            sql = "SELECT * FROM MovieContract WHERE stocksymbol=?";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, stocksymbol);
            rs = prepstmt.executeQuery();
            while(rs.next())
            {
              output.add(rs.getString("movie_id"));
              output.add(rs.getString("mtitle"));
              output.add(rs.getString("role"));
              output.add(rs.getString("prodyear"));
              output.add(rs.getString("value"));
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
        return output;
    }


    /*
        Gets all movies names from the movie database
        @return movies all the movie names in an ArrayList
    */
    public ArrayList<String> getMovieNames()
    {
        String sql = "";
        ArrayList<String> movies = new ArrayList<String>();
        try
        {
            connect(1);

            //sql query
            sql = "SELECT title FROM Movies";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while(rs.next())
            {
                movies.add(rs.getString("title"));
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
        return movies;
    }
    /*
        Queries movie database to see if movie exists
        @param movieName the name of the movie
        @return true if found, false otherwise
    */
    public boolean hasMovie(String movieName)
    {
        String sql = "";
        try
        {
            connect(1);

            //sql query
            sql = "SELECT title FROM Movies WHERE title = ?;";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, movieName);
            rs = prepstmt.executeQuery();

            //if there is a row with that title, then
            //the movie exists
            if(rs.next())
                return true;
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
        return false;
    }
    /*
        Given a movie, returns the MovieInfo
        for that movie
        @param movieName the name of the movie
        @return movie the MovieInfo of that movie
    */
    public MovieInfo getMovieInfo(String movieName)
    {
        String sql = "";
        MovieInfo movie = new MovieInfo();

        try
        {
            connect(1);

            //first sql query to get info from Movies Table
            sql = "SELECT title, rating, production_year FROM Movies WHERE title = ?;";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, movieName);
            rs = prepstmt.executeQuery();

            while(rs.next())
            {
                //first get movie's name, rating, and prod year
                movie.setMovieName(rs.getString("title"));
                movie.setRating(rs.getFloat("rating"));
                movie.setProdYear(rs.getInt("production_year"));
            }

            //2nd query to get review info from Reviews table
            sql = "SELECT R.author, R.review FROM Reviews R, Movies M "
                + "WHERE M.title = ? AND M.id = R.movie_id;";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, movieName);
            rs = prepstmt.executeQuery();

            while(rs.next())
            {
                //add reviews
                movie.addReview(rs.getString("author"), rs.getString("review"));
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
        return movie;
    }
    /*
        Gets all the 5-rated movies that were produced in the timeframe
        @param startYear the beginning time frame
        @param endYear the ending time frame
        @return movies an ArrayList of all movie names that fit the
                above criteria
    */
    public ArrayList<String> getTopMovies(String startYear, String endYear)
    {
        String sql = "";
        ArrayList<String> movies = new ArrayList<String>();

        try
        {
            connect(1);

            //sql query
            sql = "SELECT M.title from Movies M "
                + "WHERE M.production_year > ? AND M. production_year < ? "
                        + "AND M.rating = 5;";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setInt(1, Integer.parseInt(startYear));
            prepstmt.setInt(2, Integer.parseInt(endYear));
            rs = prepstmt.executeQuery();

            while(rs.next())
            {
                //add to movies
                movies.add(rs.getString("title"));
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
        return movies;
    }
    /*
        Gets all the reviews for a given movie
        @param movieName the name of the movie
        @return reviews a HashMap mapping an author
                to his/her review
    */
    public HashMap<String,String> getMovieReviews(String movieName)
    {
        String sql = "";
        HashMap<String,String> reviews = new HashMap<String,String>();

        try
        {
            connect(1);

            //sql query
            sql = "SELECT R.author, R.review FROM Movies M, Reviews R "
                + "WHERE M.title = ? AND M.id = R.movie_id;";

            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, movieName);
            rs = prepstmt.executeQuery();

            while(rs.next())
            {
                //place each review into hashmap
                reviews.put(rs.getString("author"), rs.getString("review"));
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
        return reviews;
    }
    // Manager methods

    /*
        For a given user, gets all of that user's transactions
        for the current month
        @param username the user 
        @return transactionsList an ArrayList of Transactions for the
                user for the current month
    */
    public ArrayList<Transaction> getMonthTransactions(String username)
    {
        String currentMonthSql = "";
        String sql = "";
        int s_aid = -1;
        int m_aid = -1;
        int currentMonth = -1;
        Date currentDate = getCurrentDate();
        ArrayList<Transaction> transactionsList = new ArrayList<Transaction>();
        try
        {
            connect(0);
  
            //first, get current month
            currentMonthSql = "SELECT MONTH(?) AS currentMonth;";
            prepstmt = conn.prepareStatement(currentMonthSql);
            prepstmt.setDate(1, currentDate);
            rs = prepstmt.executeQuery();
            while(rs.next())
                currentMonth = rs.getInt("currentMonth");

            // Then, get s_aid using account's username.
            sql = "SELECT * FROM OwnsStock S WHERE S.username=?";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            rs = prepstmt.executeQuery();
            while (rs.next())
            {
                s_aid = rs.getInt("s_aid");
            }

            //Also get m_aid using account's username
            sql = "SELECT * FROM OwnsMarket M WHERE M.username=?";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1, username);
            rs = prepstmt.executeQuery();
            while (rs.next())
            {
                m_aid = rs.getInt("m_aid");
            }

            // Get all stock transactions that the user made in the current month
            sql = "SELECT * FROM Transactions T "
                + "WHERE T.transNum IN (SELECT S.transNum FROM StockTransactions S WHERE S.s_aid=? " + 
                        "AND MONTH(T.transDate) = ?);";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setInt(1, s_aid);
            prepstmt.setInt(2, currentMonth);
            rs = prepstmt.executeQuery();
            while(rs.next())
            {
              transactionsList.add(new Transaction(
                rs.getInt("transNum"),
                rs.getDate("transDate"),
                rs.getFloat("marketIn"),
                rs.getFloat("marketOut"),
                rs.getFloat("sharesIn"),
                rs.getFloat("sharesOut"),
                rs.getString("stocksymbol"),
                rs.getFloat("profit")));
            }

            //Get all market transactions that the user made in the current month while making sure
            //to ignore all those already included in stock transactions
            sql = "SELECT * FROM Transactions T "
                + "WHERE T.transNum IN (SELECT M.transNum FROM MarketTransactions M WHERE M.m_aid=? " + 
                        "AND MONTH(T.transDate) = ?) AND T.transNum NOT IN "
                                + "(SELECT T2.transNum FROM Transactions T2 WHERE T2.transNum IN "
                                    + "(SELECT S.transNum FROM StockTransactions S WHERE S.s_aid=? AND MONTH(T2.transDate) = ?));";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setInt(1, m_aid);
            prepstmt.setInt(2,currentMonth);
            prepstmt.setInt(3, s_aid);
            prepstmt.setInt(4, currentMonth);
            rs = prepstmt.executeQuery();
            while(rs.next())
            {
                transactionsList.add(new Transaction(
                rs.getInt("transNum"),
                rs.getDate("transDate"),
                rs.getFloat("marketIn"),
                rs.getFloat("marketOut"),
                rs.getFloat("sharesIn"),
                rs.getFloat("sharesOut"),
                rs.getString("stocksymbol"),
                rs.getFloat("profit")));
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
      return transactionsList;
    }
    /*
        Queries the database to find out if the
        customer with the specified username exists
        @return true if customer exists, false otherwise
    */
    public boolean hasCustomer(String username)
    {
        String sql = "";
        boolean hasCustomer = false;
        try
        {
            connect(0);

            //sql query
            sql = "SELECT username FROM Customer WHERE username = ?;";
            prepstmt = conn.prepareStatement(sql);
            prepstmt.setString(1,username);
            rs = prepstmt.executeQuery();

            //if there is a result, then username was valid
            if(rs.next())
                hasCustomer = true;
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
        return hasCustomer;
    }
    /*
        Gets all customers who have bought/traded at least 1000 shares
        @return activeCustomers an ArrayList of customers who fit the
                criteria above
    */
    public ArrayList<String> getAllActiveCustomers(){
      ArrayList<String> activeCustomers = new ArrayList<String>();
      String sql = "";
      try
      {
        connect(0);

        //sql query
        sql = "SELECT TempThree.username, TempThree.SumShares, C.firstName, C.lastName, C.email "
            + "FROM   (SELECT  TempTwo.username, TempTwo.SumShares "
            + "        FROM    (SELECT  Temp.username, SUM(Temp.sharesIn+Temp.sharesOut) AS SumShares "
            + "                 FROM    (SELECT Os.username, Os.s_aid, T.transNum, T.sharesIn, T.sharesOut "
            + "                          FROM OwnsStock AS Os "
            + "                          INNER JOIN StockTransactions AS St "
            + "                            ON Os.s_aid=St.s_aid "
            + "                          INNER JOIN Transactions AS T "
            + "                            ON T.transNum=St.transNum) AS Temp"
            + "                 GROUP BY Temp.username) AS TempTwo "
            + "        WHERE   TempTwo.SumShares >= 1000) AS TempThree "
            + "INNER JOIN Customer AS C "
            + "ON TempThree.username=C.username; ";
        prepstmt = conn.prepareStatement(sql);
        rs = prepstmt.executeQuery();
        while(rs.next())
        {
          // Every 5 entries in ArrayList is one Customer
          activeCustomers.add(rs.getString("username"));
          activeCustomers.add(rs.getString("SumShares"));
          activeCustomers.add(rs.getString("firstName"));
          activeCustomers.add(rs.getString("lastname"));
          activeCustomers.add(rs.getString("email"));
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
      return activeCustomers;
    }
    /*
        Gets all customers
        @return customerAccounts an ArrayList of all 
                customer accounts
    */
    public ArrayList<Account> getAllCustomers()
    {
          ArrayList<Account> customerAccounts = new ArrayList<Account>();
          String sql = "";
          try
          {
            connect(0);

            //sql query
            sql = "SELECT * FROM Customer;";
            prepstmt = conn.prepareStatement(sql);
            rs = prepstmt.executeQuery();
            while(rs.next())
            {
                Account currentAccount = new Account(rs.getString("username"), "", rs.getString("firstName"), rs.getString("lastName"), "", rs.getString("phone"),rs.getString("email"), -1);
                customerAccounts.add(currentAccount);
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
      return customerAccounts;
    }
    public ArrayList<String> CustomerReport(String username)
    {
      ArrayList<String> cReport = new ArrayList<String>();
      String sql = "";
      return null;
    }
    /*
        Adds interest to all the market accounts
        @return true if successful, false otherwise
    */
    public boolean addInterest()
    {
        String monthlyInterestSql = "";
        String customerProfitSql = "";
        String addInterestSql = "";
        String recordTransactionSql = "";
        String recordMarketTransactionSql = "";
        String resetRunningBalanceSql = "";
        float interestRate = -1;
        Date date = getCurrentDate();
        HashMap<Integer, Float> customerInterest = new HashMap<Integer, Float>();

        try
        {
            connect(0);

            //first get monthly interest rate
            monthlyInterestSql = "SELECT monthlyInterest FROM Interest;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(monthlyInterestSql);
            while(rs.next())
                interestRate = rs.getFloat("monthlyInterest");

            /*
                Generate a hashmap associating each customer with the amount
                he/she will make from the interest. This will be used to
                record the transaction
            */
            customerProfitSql = "SELECT m_aid, runningbalance FROM OwnsMarket;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(customerProfitSql);
            while(rs.next())
            {
                customerInterest.put(rs.getInt("m_aid"), rs.getFloat("runningbalance") * interestRate);

            }

            try
            {
                 //for the rest, do a SQL transaction
                conn.setAutoCommit(false);

                //add interest to each of the accounts
                addInterestSql = "UPDATE OwnsMarket SET mbalance = mbalance + (runningbalance) * ?";
                prepstmt = conn.prepareStatement(addInterestSql);
                prepstmt.setFloat(1, interestRate);
                prepstmt.executeUpdate();

                //record the transaction for each customer
                Iterator it = customerInterest.entrySet().iterator();
                while(it.hasNext())
                {
                    Map.Entry pair = (Map.Entry)it.next();
                    int m_aid = (Integer) pair.getKey();
                    float interest = (Float) pair.getValue();

                    //first insert into Transactions
                    recordTransactionSql = "INSERT INTO Transactions(transDate, marketIn, profit)"
                                        + "VALUES (?,?,?);";

                    //then insert into MarketTransactions
                    recordMarketTransactionSql = "INSERT INTO MarketTransactions (m_aid,transNum) "
                                        + "VALUES (?, LAST_INSERT_ID());";

                    //insert into transactions
                    prepstmt = conn.prepareStatement(recordTransactionSql);
                    prepstmt.setDate(1, date);
                    prepstmt.setFloat(2, interest);
                    prepstmt.setFloat(3, interest);
                    prepstmt.executeUpdate();

                    //insert into MarketTransactions
                    prepstmt = conn.prepareStatement(recordMarketTransactionSql);
                    prepstmt.setInt(1, m_aid);
                    prepstmt.executeUpdate();
                }

                //reset running balance
                resetRunningBalanceSql = "UPDATE OwnsMarket SET runningbalance = 0;";
                stmt = conn.createStatement();
                stmt.executeUpdate(resetRunningBalanceSql);

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
        Delete all transactions in the database
        @return true if successful, false otherwise
    */
    public boolean deleteTransactions()
    {
      String sql = "";
      try
      {
        connect(0);
        sql = "DELETE FROM Transactions;";
        prepstmt = conn.prepareStatement(sql);
        prepstmt.executeUpdate();

        sql = "ALTER TABLE Transactions AUTO_INCREMENT = 1;";
        prepstmt = conn.prepareStatement(sql);
        prepstmt.executeUpdate();
      }
      catch(SQLException se)
      {
        // Delete failed for some reason
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
        Opens the market for buying and selling
        @return true if successful, false otherwise
    */
    public boolean openMarket()
    {
        String openSql = "";
        String incrementSql = "";
        String incrementDate = "";
        Date date = getCurrentDate();

        try
        {
            connect(0);

            //use transaction to update date table
            conn.setAutoCommit(false);

            //first set Open attribute to 1
            openSql = "UPDATE Date SET Open = 1;";

            //then, increment date by one day
            incrementSql = "UPDATE Date SET currentDate = (SELECT DATE_ADD(?, INTERVAL 1 DAY));";

            try
            {
                //set open attribute to 1
                stmt = conn.createStatement();
                stmt.executeUpdate(openSql);

                //increment date by 1
                prepstmt = conn.prepareStatement(incrementSql);
                prepstmt.setObject(1, date);
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
        Closes the market so no buying/selling may occur
        Also adds each customer's current market balance to
        his/her running balance for the current month and
        updates each stock's closing price
        @return true if successful, false otherwise
    */
    public boolean closeMarket()
    {
        String closeSql = "";
        String addBalanceSql = "";
        String updateClosingPriceSql = "";
        try
        {
            connect(0);

            //use transaction to update both Date and OwnsMarket
            conn.setAutoCommit(false);

            //first, close the market
            closeSql = "UPDATE Date SET Open = 0;";

            //then, for each customer, add their current market balance
            //to their running balance for the month
            addBalanceSql = "UPDATE OwnsMarket SET runningbalance = runningbalance + mbalance;";

            //finally, set each stock's closing price
            updateClosingPriceSql = "UPDATE Stock SET closingprice = currentprice;";

            try
            {
                //close market
                stmt = conn.createStatement();
                stmt.executeUpdate(closeSql);

                //add running balance
                stmt = conn.createStatement();
                stmt.executeUpdate(addBalanceSql);

                stmt = conn.createStatement();
                stmt.executeUpdate(updateClosingPriceSql);
                
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
        Set date to the specified date
        Market will be open on the specified date
        @date date the new date to set to
        @return true if successuful, false otherwise
    */
    public boolean setDate(LocalDate date)
    {
        String timeIntervalSql = "";
        Date startDate = getCurrentDate();
        int timeInterval = 0;

        try
        {
            connect(0);

            //get time interval in between startDate and date
            timeIntervalSql = "SELECT datediff(?,?) AS 'interval';";
            prepstmt = conn.prepareStatement(timeIntervalSql);
            prepstmt.setObject(1, date);
            prepstmt.setDate(2, startDate);
            rs = prepstmt.executeQuery();
            while(rs.next())
                timeInterval = rs.getInt("interval");

            //if market is currently open, close it
            if(isMarketOpen())
                closeMarket();

            //open and close the market for each day up till the last one
            for(int i = 0; i < timeInterval -1; i++)
            {
                openMarket();
                closeMarket();
            }

            //open the market on the last day
            openMarket();
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
        Changes a stock's price to a new price
        @param stocksymbol the stock
        @param newprice the stock's new price
        @return true if successful, false otherwise
    */
    public boolean changeStockPrice(String stocksymbol, float newprice)
    {
      String sql = "";
      try
      {
        connect(0);
        sql = "UPDATE Stock SET currentprice=? WHERE stocksymbol=?;";
        prepstmt = conn.prepareStatement(sql);
        prepstmt.setFloat(1, newprice);
        prepstmt.setString(2, stocksymbol);
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
}
