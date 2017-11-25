import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

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
			movieList = doc.getElementsByTagName("movie");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void printDocumentRoot()
	{
		 System.out.println("Root element:" + doc.getDocumentElement().getNodeName());
	}
}