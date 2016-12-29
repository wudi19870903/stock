package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.List;

public class StockDayDetail {
	
	public static class DetailData
	{
		public Float price;
		public String time;
	}
	
	
	public StockDayDetail()
	{
		detailDataList = new ArrayList<DetailData>();
	}

	public List<DetailData> detailDataList;
}
