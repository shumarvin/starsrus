public class MainRunner
{
	private static UserInterface userInterface;
	
	public static void main(String[]  args)
	{
		userInterface = new UserInterface();
		userInterface.showTitleScreen();

		System.exit(0);
	}
}