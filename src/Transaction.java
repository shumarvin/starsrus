import java.sql.Date;

public class Transaction{
  int transNum;
  Date transDate;
  float marketIn;
  float marketOut;
  float sharesIn;
  float sharesOut;
  String stocksymbol;
  float profit;

  // default constructor
  public Transaction(){
    transNum = -1;
    transDate = null;
    marketIn = -1;
    marketOut = -1;
    sharesIn = -1;
    sharesOut = -1;
    stocksymbol = "";
    profit = -1;
  }

  public Transaction(int tnum, Date date, float mIn, float mOut,
    float sIn, float sOut, String ss, float prof){
    transNum = tnum;
    transDate = date;
    marketIn = mIn;
    marketOut = mOut;
    sharesIn = sIn;
    sharesOut = sOut;
    stocksymbol = ss;
    profit = prof;
  }

  // Getters
  public int gettransNum(){
    return transNum;
  }
  public Date gettransDate(){
    return transDate;
  }
  public float getmarketIn(){
    return marketIn;
  }
  public float getmarketOut(){
    return marketOut;
  }
  public float getsharesIn(){
    return sharesIn;
  }
  public float getsharesOut(){
    return sharesOut;
  }
  public String getstocksymbol(){
    return stocksymbol;
  }
  public float getprofit(){
    return profit;
  }

  // Setters
  public void settransNum(int input){
    transNum = input;
  }
  public void settransDate(Date input){
    transDate = input;
  }
  public void setmarketIn(float input){
    marketIn = input;
  }
  public void setmarketOut(float input){
    marketOut = input;
  }
  public void setsharesIn(float input){
    sharesIn = input;
  }
  public void setsharesOut(float input){
    sharesOut = input;
  }
  public void setstocksymbol(String input){
    stocksymbol = input;
  }
  public void setprofit(float input){
    profit = input;
  }
}
