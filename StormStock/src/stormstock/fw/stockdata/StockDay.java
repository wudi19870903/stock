package stormstock.fw.stockdata;

public class StockDay {

	public StockDay()
	{
	} 
	
	public void CopyFrom(StockDay c)
	{
		date = c.date;
		open = c.open;
		close = c.close;
		high = c.high;
		low = c.low;
		volume = c.volume;
	}
	
	public String date;
	
	public float open;
	public float close;
	public float high;
	public float low;
	public float volume;
}
