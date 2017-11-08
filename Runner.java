import java.sql.*;


public class Runner
{
	// JDBC driver name and database URL
   	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   	static final String DB_URL = "jdbc:mysql://cs174a.engr.ucsb.edu/marvinshuDB";

	//  Database credentials
   	static final String USER = "marvinshu";
   	static final String PASS = "477";

	public static void main(String[] args)
	{
		Connection conn = null;
	   	Statement stmt = null;
	   	try{
	   		//Register JDBC driver
	      	Class.forName(JDBC_DRIVER);

	      	//Open a connection
	      	System.out.println("Connecting to a selected database...");
	      	conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      	System.out.println("Connected database successfully...");

	      	//STEP 4: Execute a query
	      	System.out.println("Creating table in given database...");
		    stmt = conn.createStatement();
		      
		    String sql = "CREATE TABLE Registration " +
		                   "(id INTEGER not NULL, " +
		                   " first VARCHAR(255), " + 
		                   " last VARCHAR(255), " + 
		                   " age INTEGER, " + 
		                   " PRIMARY KEY ( id ))"; 

		    stmt.executeUpdate(sql);
		    System.out.println("Created table in given database...");

		   	System.out.println("Inserting records into the table...");
	      	//stmt = conn.createStatement();
	      
	      	sql = "INSERT INTO Registration " +
	                   "VALUES (100, 'Zara', 'Ali', 18)";
	      	stmt.executeUpdate(sql);
	      	sql = "INSERT INTO Registration " +
	                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
	      	stmt.executeUpdate(sql);
	      	sql = "INSERT INTO Registration " +
	                   "VALUES (102, 'Zaid', 'Khan', 30)";
	      	stmt.executeUpdate(sql);
	      	sql = "INSERT INTO Registration " +
	                   "VALUES(103, 'Sumit', 'Mittal', 28)";
	      	stmt.executeUpdate(sql);
	      	System.out.println("Inserted records into the table...");
	      
	   	}catch(SQLException se){
      		//Handle errors for JDBC
      		se.printStackTrace();
	   	}catch(Exception e){
	      	//Handle errors for Class.forName
	      	e.printStackTrace();
	   	}finally{
	   		try{
	      	//finally block used to close resources
	      	if(stmt!=null)
            conn.close();
     	}catch(SQLException se){
      	}// do nothing
	      	try{
	         	if(conn!=null)
	            	conn.close();
	      }	catch(SQLException se){
	         	se.printStackTrace();
	      }//end finally try
	   	}//end try
	   System.out.println("Goodbye!");
	}
}