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

   	//connect to database
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
         finally
   		{
            //finally block used to close resources
            try
            {
               if(conn!=null)
                  conn.close();
         	}
         	catch(SQLException se)
         	{
            	se.printStackTrace();
            	System.exit(0);
         	}
   		}
	  }
}