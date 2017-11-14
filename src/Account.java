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
		taxid = -1;
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


	/*
		Getters
	*/
	public String getUsername()
	{
		return username;
	}
	public String getPassword()
	{
		return password;
	}
	public String getName()
	{
		return name;
	}
	public String getState()
	{
		return state;
	}
	public String getPhone()
	{
		return phone;
	}
	public String getEmail()
	{
		return email;
	}
	public int getTaxId()
	{
		return taxid;
	}
}