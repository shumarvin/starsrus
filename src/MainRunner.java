public class MainRunner
{
	private static UserInterface userInterface;
	
	public static void main(String[]  args)
	{
		userInterface = new UserInterface();
		userInterface.start();

		System.exit(0);
	}
}