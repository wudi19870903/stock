package stormstock.fw.stockdata;

public class StockDetailTime {
	public StockDetailTime()
	{
	}
	public void CopyFrom(StockDetailTime fromObj)
	{
		this.time = fromObj.time;
		this.price = fromObj.price;
	}
	public Float price;
	public String time;
}
