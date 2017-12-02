
public class OwnedStocks
{
  String stocksymbol;
  float sbalance;
  float buyprice;

  // default constructor
  public OwnedStocks()
  {
    stocksymbol = "";
    sbalance = -1;
    buyprice = -1;
  }

  public OwnedStocks(String ss, float sb, float bp)
  {
    stocksymbol = ss;
    sbalance = sb;
    buyprice = bp;
  }

  // Getters
  public String getStocksymbol()
  {
    return stocksymbol;
  }
  public float getSbalance()
  {
    return sbalance;
  }
  public float getBuyprice()
  {
    return buyprice;
  }
  // Setters
  public void setStocksymbol(String input)
  {
    stocksymbol = input;
  }
  public void setSbalance(float input)
  {
    sbalance = input;
  }
  public void setBuyprice(float input)
  {
    buyprice = input;
  }
}
