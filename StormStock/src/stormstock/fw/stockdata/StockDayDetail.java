package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.List;

public class StockDayDetail {
	
	public StockDayDetail()
	{
	}
	public void CopyFrom(StockDayDetail fromObj)
	{
		this.time = fromObj.time;
		this.price = fromObj.price;
	}
	
	public String time;
	
	public Float price;
}
