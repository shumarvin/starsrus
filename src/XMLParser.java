import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import java.util.ArrayList;

/*
A class to parse the movie XML file
*/
public class XMLParser
{
	private File xmlFile;
	private DocumentBuilderFactory dbfactory;
	private DocumentBuilder dBuilder;
	private Document doc;
	private NodeList movieList;

	//constructor
	public XMLParser(String inputFile)
	{
		try
		{
			//instantiate all objects necessary to parse XML file
			xmlFile = new File(inputFile);
			dbfactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbfactory.newDocumentBuilder();

			//parse xml file
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			//get all movies in XML file
			movieList = doc.getElementsByTagName("MOVIE");


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
		Parses Movies.xml to get all movie names in the file
		@return names an ArrayList<String> of all movie names
	*/
	public ArrayList<String> getMovieNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		try
		{
			for(int i = 0; i < movieList.getLength(); i++)
			{
				Node node = movieList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					names.add(element.getElementsByTagName("TITLE").item(0).getTextContent());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return names;
	}
	/*
		Checks xml file to see if movie exists in the file, and prints out
		its info if it exists
		@param name the title of the movie
		@return true if found, false otherwise
	*/
	public boolean printMovie(String name)
	{
		for(int i = 0; i < movieList.getLength(); i++)
		{
			Node node = movieList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				if(element.getElementsByTagName("TITLE").item(0).getTextContent().equalsIgnoreCase(name))
				{
					printElement(element);
					return true;
				}
			}
		}
		return false;
	}
	//prints entire movie element
	private void printElement(Element element)
	{
		//print title
		System.out.println("Title: " + element.getElementsByTagName("TITLE").item(0)
						.getTextContent());

		//print production year
		System.out.println("Production Year : " + element.getElementsByTagName("PRODUCTIONYEAR").item(0)
						.getTextContent());

		//print reviews
		NodeList reviews = element.getElementsByTagName("REVIEWS");
		for(int i = 0; i < reviews.getLength(); i++)
		{
			Node node = reviews.item(i);
			if(node.getNodeType() == node.ELEMENT_NODE)
			{
				Element e = (Element) node;
				System.out.println("Review: " + e.getTextContent() + " by: " + e.getAttribute("AUTHOR"));
			}
		}

		//print organization rankings
		NodeList rankings = element.getElementsByTagName("RANKING");
		for(int i = 0; i < rankings.getLength(); i++)
		{
			Node node = rankings.item(i);
			if(node.getNodeType() == node.ELEMENT_NODE)
			{
				Element e = (Element) node;
				System.out.println("Ranking: " + e.getTextContent() + " by: " + e.getAttribute("ORGANIZATION"));
			}
		}
	}
	public void getTopMovies(String startYear, String endYear)
	{

	}
	private NodeList getTopMoviesInRange(String startYear, String endYear)
	{
		return null; 
	}
}
