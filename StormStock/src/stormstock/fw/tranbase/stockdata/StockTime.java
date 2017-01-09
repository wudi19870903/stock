package stormstock.fw.tranbase.stockdata;

import java.util.ArrayList;
import java.util.List;

public class StockTime {
	
	public StockTime()
	{
	}
	
	public void CopyFrom(StockTime fromObj)
	{
		this.time = fromObj.time;
		this.price = fromObj.price;
	}
	
	public String time;
	
	public Float price;
}
