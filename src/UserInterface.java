import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.Console;

public class UserInterface
{
	private Scanner reader;                   //read in user input
	private Account account;                  //user's account
	private DatabaseAdapter dbAdapter;        //database adapter to interface with database
	private Console console;                  //console to read in password

	//constructor
	public UserInterface()
	{
		reader = new Scanner(System.in);
		dbAdapter = new DatabaseAdapter();
		console = System.console();
	}

	//starts program with title and login screen
	public void start()
	{
		System.out.println("");
		System.out.println("*************************");
		System.out.println("Welcome to Stars'R'Us!");
		System.out.println("*************************");

		//dbAdapter.connect();
		//dbAdapter.close();

		//start login process here
		showLoginChoices();
		try
		{
			System.out.print("Input: ");
			int choice = reader.nextInt();
			//handle invalid input
			while(choice < 1 || choice > 4)
			{
				System.out.println("Please choose one of the 4 options above.");
				showLoginChoices();
				choice = reader.nextInt();
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
		catch(InputMismatchException exception)
		{
			System.out.println("Error! Invalid Input!");
		}
	}

	//user interface for login
	private void showLoginChoices()
	{
		System.out.println();
		System.out.println("Please choose one of the following:");
		System.out.println("1. Log in (Customer)");
		System.out.println("2. Log in (Manager)");
		System.out.println("3. Create an Account");
		System.out.println("4. Quit");
		System.out.println();
	}
	//customer login user interface
	private void doCustomerLogin()
	{
		System.out.println("\n-------------------------");
		System.out.println("       Customer Login       ");
		System.out.println("-------------------------");

		//read in customer username and password
		System.out.print("Customer Username: ");
		reader.nextLine();
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
		
		System.out.println("Welcome " + account.getFirstName() + " " +
				account.getLastName() + "!");
		
	}
	//manager login user interface
	private void doManagerLogin()
	{
		System.out.println("\n-------------------------");
		System.out.println("      Manager Login      ");
		System.out.println("-------------------------");

		//read in manager username and password
		System.out.print("Manager Username: ");
		reader.nextLine();
		String managerUsername = reader.nextLine();
		char[] managerPassCharArr = console.readPassword("Manager Password: ");
		String managerPassword = new String(managerPassCharArr);
		//System.out.println("User is: " + managerUsername + "  " + " Pass is: " + managerPassword);
	}
	private void doCreateAccount()
	{
		System.out.println("create account");
	}
	private void quit()
	{
		System.out.println("Goodbye");
		System.exit(0);
	}
}
