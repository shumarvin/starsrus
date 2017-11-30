import java.util.HashMap;
import java.util.Set;


/*
	A class to hold the movie info from
	the movie database
*/
public class MovieInfo
{
	String movieName;
	float rating;
	int productionYear;
	HashMap<String,String> reviews;

	//default constructor
	public MovieInfo()
	{
		movieName = "";
		rating = -1;
		productionYear = -1;
		reviews = new HashMap<String,String>();
	}

	//overridden toString method
	public String toString()
	{
		String info = "Title: " + movieName +
			"\nRating: " + rating + 
			"\nProduction Year: " + productionYear +"\n";

		Set<String> authors = reviews.keySet();
		for(String author:authors)
		{
			info += "Review: " + reviews.get(author) + "    By: " + author + "\n";
		}
		return info;

	}
	/*
		Getters
	*/
	public String getMovieName()
	{
		return movieName;
	}
	public float getRating()
	{
		return rating;
	}
	public int getProdYear()
	{
		return productionYear;
	}
	public HashMap<String,String> getReviews()
	{
		return reviews;
	}

	/*
		Setters
	*/
	public void setMovieName(String name)
	{
		movieName = name;
	}
	public void setRating(float aRating)
	{
		rating = aRating;
	}
	public void setProdYear(int year)
	{
		productionYear = year;
	}
	public void addReview(String author, String review)
	{
		reviews.put(author, review);
	}
}