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
		ref_stock = c.ref_stock;
	}
	
	public String date;
	public float open;
	public float close;
	public float high;
	public float low;
	public float volume;
	
	public Stock ref_stock;
}
