import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.Console;
import java.util.*;
import java.text.NumberFormat;

public class UserInterface
{
	private Scanner reader;                   //read in user input
	private Account account;                  //user's account
	private DatabaseAdapter dbAdapter;        //database adapter to interface with database
	private Console console;                  //console to read in password
	private NumberFormat formatter;           //formatter for currency

	//constructor
	public UserInterface()
	{
		reader = new Scanner(System.in);
		dbAdapter = new DatabaseAdapter();
		console = System.console();
		formatter = NumberFormat.getCurrencyInstance();
	}

	//starts program with title and login screen
	public void start()
	{
		System.out.println("");
		System.out.println("*************************");
		System.out.println("Welcome to Stars'R'Us!");
		System.out.println("*************************");
		System.out.println("Current date: " + dbAdapter.getCurrentDate());


		//start login process here
		while(true)
		{
			System.out.println();
			System.out.println("Please choose one of the following:");
			System.out.println("1. Log in (Customer)");
			System.out.println("2. Log in (Manager)");
			System.out.println("3. Create an Account");
			System.out.println("4. Quit");
			System.out.println();
			System.out.print("Input: ");
			//make sure input is int
			if(!reader.hasNextInt())
			{
				System.out.println("Error! Invalid Input!");
				reader.nextLine();
				continue;
			}
			else
			{
				int choice = reader.nextInt();
				reader.nextLine();
				//handle invalid input
				if(choice < 1 || choice > 4)
				{
					System.out.println("Invalid input. Please choose one of the 4 options below.");
					continue;
				}

				//switch on choice
				switch(choice)
				{
					case 1: doCustomerLogin();
							break;
					case 2: doManagerLogin();
							break;
					case 3: doCreateAccount();
							break;
					default: quit();
				}
			}
		}
	}
	//customer login user interface
	private void doCustomerLogin()
	{
		System.out.println("\n-------------------------");
		System.out.println("     Customer Login      ");
		System.out.println("-------------------------");

		//read in customer username and password
		System.out.print("Customer Username: ");
		String customerUsername = reader.nextLine();
		char[] customerPassCharArr = console.readPassword("Customer Password: ");
		String customerPassword = new String(customerPassCharArr);

		//System.out.println("User is: " + customerUsername + " Pass is: " + customerPassword);

		//query database and see if it's valid
		account = dbAdapter.queryAccount(0, customerUsername, customerPassword);

		//if invalid username/password, have user try again
		while(account.getUsername() == "")
		{
			System.out.println("Invalid username or password. Please try again.");
			System.out.println();

			//read in customer username and password
			System.out.print("Customer Username: ");
			customerUsername = reader.nextLine();
			customerPassCharArr = console.readPassword("Customer Password: ");
			customerPassword = new String(customerPassCharArr);
			account = dbAdapter.queryAccount(0, customerUsername, customerPassword);
		}
		showTraderInterface();
	}
	//manager login user interface
	private void doManagerLogin()
	{
		System.out.println("\n-------------------------");
		System.out.println("      Manager Login      ");
		System.out.println("-------------------------");

		//read in manager username and password
		System.out.print("Manager Username: ");
		String managerUsername = reader.nextLine();
		char[] managerPassCharArr = console.readPassword("Manager Password: ");
		String managerPassword = new String(managerPassCharArr);
		//System.out.println("User is: " + managerUsername + "  " + " Pass is: " + managerPassword);

		//query database to see if valid account
		account = dbAdapter.queryAccount(1, managerUsername, managerPassword);

		//if invalid username/password, have user try again
		while(account.getUsername() == "")
		{
			System.out.println("Invalid username or password. Please try again.");
			System.out.println();

			//read in manager username and password
			System.out.print("Manager Username: ");
			managerUsername = reader.nextLine();
			managerPassCharArr = console.readPassword("Manager Password: ");
			managerPassword = new String(managerPassCharArr);
			account = dbAdapter.queryAccount(1, managerUsername, managerPassword);
		}
		showManagerInterface();
	}
	private void doCreateAccount()
	{
		System.out.println("\n-------------------------");
		System.out.println("      Create Account     ");
		System.out.println("-------------------------");

		//Ask for what account user wants to create
		System.out.println("Please choose account type:");
		System.out.println("1. Create (Customer) Account");
		System.out.println("2. Create (Manager)  Account");
		System.out.println();
		System.out.print("Input: ");
		int choice = reader.nextInt();
		reader.nextLine();
		if (choice == 1) {
			System.out.println("");
			System.out.println("--Creating Customer Account--");

			// Prompt user that he/she must deposit $1,000 into market account
			System.out.print("You must deposit $1,000 into market account. Is this ok? (y/n): ");
			String promptThousand = reader.nextLine();
			if (!promptThousand.equals("y")) {
				System.out.println("---Leaving customer account creation---");
				return;
			}

			System.out.print("Enter a username: ");
			String username = reader.nextLine();
			System.out.print("Enter a password: ");
			String password = reader.nextLine();
			System.out.print("Enter a firstName: ");
			String firstName = reader.nextLine();
			System.out.print("Enter a lastName: ");
			String lastName = reader.nextLine();
			System.out.print("Enter a state: ");
			String state = reader.nextLine();
			System.out.print("Enter a phone: ");
			String phone = reader.nextLine();
			System.out.print("Enter a email: ");
			String email = reader.nextLine();
			System.out.print("Enter a taxid: ");
			int taxid = reader.nextInt();
			reader.nextLine();
			int temp = dbAdapter.createAccount(0, username, password, firstName, lastName,
				state, phone, email, taxid);
			if (temp == 0) {
				System.out.println("");
				System.out.println("Customer account has been created for username " + username);
			} else {
				System.out.println("");
				System.out.println("Customer account creation has failed for username " + username);
			}
		} else {
			System.out.println("");
			System.out.println("--Creating Manager Account--");
			System.out.print("Enter a username: ");
			String username = reader.nextLine();
			System.out.print("Enter a password: ");
			String password = reader.nextLine();
			int temp = dbAdapter.createAccount(1, username, password, null, null, null, null, null, -1);
			if (temp == 0) {
				System.out.println("");
				System.out.println("Manager account has been created for username " + username);
			} else {
				System.out.println("");
				System.out.println("Manager account creation has failed for username " + username);
			}
		}
	}
	//manager user interface
	private void showManagerInterface()
	{
		System.out.println();
		System.out.println("Welcome " + account.getUsername() + "!");
		System.out.println();
		while(true){
			System.out.println("What would you like to do today?");
			System.out.println();
			System.out.println("1. Add interest");
			System.out.println("2. Generate Monthly Statement");
			System.out.println("3. List Active Customers");
			System.out.println("4. Generate Government Drug & Tax Evasion Report(DTER)");
			System.out.println("5. Generate Customer Report");
			System.out.println("6. Delete Transactions");
			System.out.println("7. Log out");
			System.out.println();
			System.out.print("Input: ");
			//check for non-int input
			if(!reader.hasNextInt())
			{
				System.out.println();
				System.out.println("Error! Invalid Input!");
				reader.nextLine();
				continue;
			}
			else
			{
				int choice = reader.nextInt();
				//handle invalid input
				if(choice < 1 || choice > 9)
				{
					System.out.println("Invalid input. Please choose one of the 7 options below.");
					continue;
				}
				//switch on choice
				switch(choice)
				{
					case 1: showAddInterest();
							break;
					case 2: showMonthlyStatement();
							break;
					case 3: showListActiveCustomers();
							break;
					case 4: showGovTaxReport();
							break;
					case 5: showCustomerReport();
							break;
					case 6: showDeleteTransactions();
							break;
					default: quit();
				}
			}
		}
	}
	private void showAddInterest()
	{
		System.out.println("Adding interest");
	}
	private void showMonthlyStatement()
	{
		System.out.println("Monthly Statement");
	}
	private void showListActiveCustomers()
	{
		System.out.println("List Active Customers");
	}
	private void showGovTaxReport()
	{
		System.out.println("Generate Government Tax Report");
	}
	private void showCustomerReport()
	{
		System.out.println("Generate Customer Report");
	}
	private void showDeleteTransactions()
	{
		System.out.println("Delete Transactions for new month");
	}

	//trader user interface
	private void showTraderInterface()
	{
		System.out.println();
		System.out.println("Welcome " + account.getFirstName() + " " +
				account.getLastName() + "!");
		System.out.println();


		/*
			Make trader interface a loop so that they can continue to use
			the program after making their first action and so that we don't
			have a gajillion method frames on the stack.
		*/
		while(true)
		{
			System.out.println("What would you like to do today?");
			System.out.println();
			System.out.println("1. Deposit into Market Account");
			System.out.println("2. Withdraw from Market Account");
			System.out.println("3. Buy Stocks");
			System.out.println("4. Sell Stocks");
			System.out.println("5. Show Market Account Balance");
			System.out.println("6. Show Stock Transaction History");
			System.out.println("7. List Current Stock Price");
			System.out.println("8. List Movie Information");
			System.out.println("9. Log out");
			System.out.println();
			System.out.print("Input: ");
			//check for non-int input
			if(!reader.hasNextInt())
			{
				System.out.println();
				System.out.println("Error! Invalid Input!");
				reader.nextLine();
				continue;
			}
			else
			{
				int choice = reader.nextInt();
				reader.nextLine();
				//handle invalid input
				if(choice < 1 || choice > 9)
				{
					System.out.println("Invalid input. Please choose one of the 9 options below.");
					continue;
				}

				//switch on choice
				switch(choice)
				{
					case 1: showDepositOrWithdraw(0);
							break;
					case 2: showDepositOrWithdraw(1);
							break;
					case 3: showBuy();
							break;
					case 4: showSell();
							break;
					case 5: showMarketBalance();
							break;
					case 6: showStockTransactions();
							break;
					case 7: showCurrentStockPrice();
							break;
					case 8: showMovieInfo();
							break;
					default: quit();
				}
			}
		}
	}
	//user interface for deposit and withdraw
	//@param updatetype 0 for deposit, 1 for withdraw
	private void showDepositOrWithdraw(int updateType)
	{
		System.out.println();
		while(true)
		{
			if(updateType == 0)
				System.out.println("How much would you like to deposit?");
			else
				System.out.println("How much would you like to withdraw?");
			System.out.println();
			System.out.print("Input: ");
			//check for non-double input
			if(!reader.hasNextDouble())
			{
				System.out.println("Invalid input. Please try again.");
				reader.next();
				continue;
			}
			else
			{
				//read in amount to deposit or withdraw
				float depositAmount = reader.nextFloat();
				reader.nextLine();

				//check for negative input
				if(depositAmount < 0)
				{
					System.out.println("Negative amounts are invalid. Try again");
					System.out.println();
					continue;
				}
				//confirm deposit or withdraw
				if(updateType == 0)
					System.out.println("You are depositing " + formatter.format(depositAmount) +
									". Is this the correct amount? (y/n)");
				else
					System.out.println("You are withdrawing " + formatter.format(depositAmount)
								 + ". Is this the correct amount? (y/n)");
				System.out.print("Input: ");
				String confirm = reader.next();
				if(confirm.equals("y"))
				{
					//update database

					//deposit
					if(updateType == 0)
					{
						if(dbAdapter.updateMarketAccount(account, depositAmount,0))
						{
							//dbAdapter.addMarketTransaction(account, depositAmount, 0);
							System.out.println("Deposit Successful!");
							System.out.println();
						}
						else
						{
							System.out.println("Error occurred. Please see above for details.");
							System.out.println();
						}
						break;
					}
					//withdraw
					else
					{
						if(dbAdapter.updateMarketAccount(account, depositAmount,1))
						{
							System.out.println("Withdraw Successful!");
							System.out.println();
						}
						else
						{
							System.out.println("Error occurred. Please see above for details.");
							System.out.println();
						}
						break;
					}
				}
				else
					continue;
			}
		}
	}
	//buy stocks user interface
	private void showBuy()
	{
		float numShares;
		while(true)
		{
			//show all stocks and their prices
			System.out.println("Which stocks would you like to buy?");
			System.out.println();
			System.out.println("StockSymbol      Price");
			HashMap<String,Float> stocks = dbAdapter.getStocks();
			Set<String> stockSymbols = stocks.keySet();
      for(String symbol: stockSymbols)
      {
					System.out.println(String.format("%11s", symbol)
								+ String.format("%11s", formatter.format(stocks.get(symbol))));
      }
      //read in which stock to buy
      System.out.println();
      System.out.print("Input(all caps): ");
      String stockToBuy = reader.nextLine();

      //loop so that user can re-input number of shares if
      //they didn't put in a float
      while(true)
      {
      	//read in number of shares to buy
        System.out.println("How many shares would you like to buy?");
        System.out.println();
        System.out.print("Input: ");

        //check for non-int input
        if(!reader.hasNextFloat())
					{
						System.out.println("Invalid input. Please try again.");
						reader.nextLine();
						continue;
					}
					else
					{
						numShares = reader.nextFloat();
	        	reader.nextLine();
	        	break;
					}
      }

	        //check to see if user input stockSymbol correctly
			if(dbAdapter.hasStock(stockToBuy))
			{
				if(dbAdapter.buyStock(account,stockToBuy, numShares, stocks.get(stockToBuy)))
				{
					System.out.println("Purchase Successful!");
					System.out.println();
				}
				else
				{
					System.out.println("Error occurred. See above for details.");
				}
				break;
			}
        	else
        	{
        		//print error and return to beginning of buy interface
        		System.out.println("Error! That stock doesn't exist!");
        		System.out.println();
        		continue;
        	}
    }
	}
	private void showSell()
	{
		float numShares;
		float buyingPrice;
		while(true)
		{
			//show all owned stocks and their prices
			System.out.println("Which stocks would you like to sell?");
			System.out.println();
			System.out.println(  "StockSymbol    SharesOwned    OrigBuyingPrice");
			ArrayList<OwnedStocks> stocks = dbAdapter.getOwnedStocks(account);
			for(OwnedStocks stock : stocks)
			{
				System.out.println(String.format("%11s", stock.getStocksymbol())
							+ String.format("%15s", stock.getSbalance())
							+ String.format("%19s", formatter.format(stock.getBuyprice())));
			}
			System.out.print("Input: ");
			String stocksymbol = reader.nextLine();
			while(true){
					System.out.println("How many would you like to sell?");
					System.out.print("Input: ");
					if(!reader.hasNextFloat())
					{
						System.out.println();
						System.out.println("Error! Invalid Input!");
						reader.nextLine();
						continue;
					}
					numShares = reader.nextFloat();
					reader.nextLine();
					break;
			}
			while(true){
					System.out.println("What was the original buying price?");
					System.out.print("Input: ");
					if(!reader.hasNextFloat())
					{
						System.out.println();
						System.out.println("Error! Invalid Input!");
						reader.nextLine();
						continue;
					}
					buyingPrice = reader.nextFloat();
					reader.nextLine();
					break;
			}
			if (dbAdapter.sellStock(account, stocksymbol, numShares, buyingPrice))
			{
					System.out.println("Shares have been sold!");
					break;
			}
    }
	}
	private void showMarketBalance()
	{
		float balance = dbAdapter.getMarketAccountBalance(account);
		if(balance != -1)
		{
			System.out.println("Your balance is: " + formatter.format(balance));
			System.out.println();
		}
		else
		{
			System.out.println("Error occurred. Please see above for details.");
			System.out.println();
		}
	}
	private void showStockTransactions()
	{
		System.out.println("show stock transactions");
		ArrayList<Transaction> tlist = dbAdapter.getTransactions(account);

		System.out.println();
		System.out.println("---Here are your stock transactions---");
		System.out.println("TransNum    TransDate    MarketIn    MarketOut"
		 + "    SharesIn    SharesOut    StockSymbol    Profit");
		for(Transaction trans : tlist)
		{
			System.out.println(String.format("%8s", trans.gettransNum())
						+ String.format("%13s", trans.gettransDate())
						+ String.format("%12s", formatter.format(trans.getmarketIn()))
						+ String.format("%13s", formatter.format(trans.getmarketOut()))
						+ String.format("%12s", trans.getsharesIn())
						+ String.format("%13s", trans.getsharesOut())
						+ String.format("%15s", trans.getstocksymbol())
						+ String.format("%10s", formatter.format(trans.getprofit()))
						);
		}
	}
	// NOTE TODO
	private void showCurrentStockPrice()
	{
		System.out.println("Which stock would you like to see the actor profile for?");
		System.out.println("StockSymbol      Price");
		HashMap<String,Float> stocks = dbAdapter.getStocks();
		Set<String> stockSymbols = stocks.keySet();
		for(String symbol: stockSymbols)
		{
				System.out.println(String.format("%11s", symbol)
							+ String.format("%11s", formatter.format(stocks.get(symbol))));
		}
		//read in stock to get actor profile for
		System.out.println();
		System.out.print("Input(all caps): ");
		String stockToGetAct = reader.nextLine();

		dbAdapter.getActorProfile(stockToGetAct);
		

	}
	//movie info user interface
	private void showMovieInfo()
	{
		while(true)
		{
			System.out.println("Which info would you like to see?");
			System.out.println();

			System.out.println("1. Detailed Movie Info");
			System.out.println("2. Top Movies");
			System.out.println("3. Movie Reviews");
			System.out.println("4. Go Back");

			System.out.print("Input: ");
			//check for non-int input
			if(!reader.hasNextInt())
			{
				System.out.println();
				System.out.println("Error! Invalid Input!");
				reader.nextLine();
				continue;
			}
			else
			{
				int choice = reader.nextInt();
				reader.nextLine();
				//handle invalid input
				if(choice < 1 || choice > 4)
				{
					System.out.println("Invalid input. Please choose one of the 9 options below.");
					continue;
				}

				switch(choice)
				{
					case 1: showDetailedMovieInfo();
							break;
					case 2: showTopMovies();
							break;
					case 3: showMovieReviews();
							break;
					default: break;
				}
				//go back to trader interface
				break;
			}
		}

	}
	//detailed movie info user interface
	private void showDetailedMovieInfo()
	{
		while(true)
		{
			//print out all movies so user can choose which one
			//to get info on
			System.out.println("Which movie would you like info on?");
			System.out.println();

			//get and print all movies
			ArrayList<String> movies = dbAdapter.getMovieNames();
			for(String s:movies)
			{
				System.out.println(s);
			}
			System.out.println();
			//get user input
			System.out.print("Input: ");
			String choice = reader.nextLine();
			System.out.println();
			//if invalid, prompt user to try again
			if(!dbAdapter.hasMovie(choice))
			{
				System.out.println("Invalid Movie. Please try again.");
				continue;
			}
			//print out movie info and go back to trader interface
			else
			{
				System.out.println(dbAdapter.getMovieInfo(choice));
				System.out.println();
				break;
			}
		}
	}
	//top movies user interface
	private void showTopMovies()
	{
		//have user specify time interval
		System.out.println("Please enter the time interval to search for");
		System.out.print("Start Year: ");
		String start = reader.nextLine();
		System.out.println();
		System.out.print("End Year:");
		String end = reader.nextLine();
		System.out.println();

		//get movies and print them out
		ArrayList<String> movies = dbAdapter.getTopMovies(start, end);
		if(movies.size() == 0)
		{
			System.out.println("Sorry, there were no top-rated movies in that time interval.");
			System.out.println();
		}
		else
		{
			for(String movie: movies)
				System.out.println(movie);
			System.out.println();
		}
	}
	//show movie reviews user interface
	private void showMovieReviews()
	{
		//print out all movies so user can choose which one
		//to get info on
		System.out.println("Which movie would you like to see the reviews for?");
		System.out.println();

		//get and print all movies
		ArrayList<String> movies = dbAdapter.getMovieNames();
		for(String s:movies)
		{
			System.out.println(s);
		}
		System.out.println();

		//get user input
		System.out.print("Input: ");
		String choice = reader.nextLine();
		System.out.println();

		//if invalid, prompt user to try again
		if(!dbAdapter.hasMovie(choice))
		{
			System.out.println("Invalid Movie. Please try again.");
		}
		//print out movie reviews and go back to trader interface
		else
		{
			HashMap<String,String> reviews =  dbAdapter.getMovieReviews(choice);

			//0 reviews message
			if(reviews.size() == 0)
			{
				System.out.println("No reviews currently available for that movie.");
				System.out.println();
			}
			else
			{
				Set<String> authors = reviews.keySet();
				for(String author: authors)
				{
					System.out.println("Review: " + reviews.get(author) + "    By: " + author);
				}
				System.out.println();
			}
		}
	}
	//shuts down system
	private void quit()
	{
		System.out.println("Goodbye");
		System.exit(0);
	}
}
