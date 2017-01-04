package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.List;

public class StockDetailDay {
	
	public StockDetailDay()
	{
		detailDataList = new ArrayList<StockDetailTime>();
	}
	
	public StockDetailDay subObj(String beginTime, String endTime)
	{
		StockDetailDay cNewStockDetailDay = new StockDetailDay();
		for(int i = 0; i < this.detailDataList.size(); i++)  
        {  
			StockDetailTime cStockDetailTime = this.detailDataList.get(i); 
			if(cStockDetailTime.time.compareTo(beginTime) >= 0 &&
					cStockDetailTime.time.compareTo(endTime) <= 0)
			{
				StockDetailTime cNewStockDetailTime = new StockDetailTime();
				cNewStockDetailTime.CopyFrom(cStockDetailTime);
				cNewStockDetailDay.detailDataList.add(cNewStockDetailTime);
			}
        }
		return cNewStockDetailDay;
	}

	public List<StockDetailTime> detailDataList;
}
