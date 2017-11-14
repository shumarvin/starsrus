import java.util.Scanner;
import java.util.InputMismatchException;

public class UserInterface
{
	private Scanner reader;                   //read in user input
	private int account;                      //keep track of which type of account user is using
	private DatabaseAdapter dbAdapter;        //database adapter to interface with database

	//constructor
	public UserInterface()
	{
		account = -1;
		reader = new Scanner(System.in);
		dbAdapter = new DatabaseAdapter();
		dbAdapter.connect();
	}

	//starts program with title and login screen
	public void start()
	{
		System.out.println("");
		System.out.println("*************************");
		System.out.println("Welcome to Stars'R'Us!");
		System.out.println("*************************");

		//start login process here 
		showLoginChoices();
		try
		{
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
				case 1: doBuyerLogin();
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

	private void showLoginChoices()
	{
		System.out.println();
		System.out.println("Please choose one of the following:");
		System.out.println("1. Log in (Buyer)");
		System.out.println("2. Log in (Manager)");
		System.out.println("3. Create an Account");
		System.out.println("4. Quit");
		System.out.println();
	}
	private void doBuyerLogin()
	{
		System.out.println("\n*************************");
		System.out.println("       Buyer Login       ");
		System.out.println("*************************");

	}
	private void doManagerLogin()
	{
		System.out.println("manager login");
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
