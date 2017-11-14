public class Account
{
	private String username;
	private String password;
	private String name;
	private String state;
	private String phone;
	private String email;
	private int taxid;

	//default constructor
	public Account()
	{
		username = "";
		password = "";
		name = "";
		state = "";
		phone = "";
		email = "";
		taxid = -1
	}
	//constructor
	public Account(String aUserName, String aPassword, String aName, 
		String aState, String aPhone, String aEmail, int aTaxid)
	{
		username = aUserName;
		password = aPassword;
		name = aName;
		state = aState;
		phone = aPhone;
		email = aEmail;
		taxid = aTaxid;
	}
}