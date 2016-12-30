package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataWebStockAllList.StockItem;
import stormstock.ori.stockdata.DataWebStockDayK.DayKData;

public class StockDataProvider {
	// 获得所有股票id列表
	public static List<String> getAllStocks()
	{
		List<String> retList = new ArrayList<String>();
		
		List<StockItem> cStockList = DataEngine.getLocalAllStock();
		
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i).id;
			retList.add(stockId);
		}
		
		return retList;
	}
	
	public static Stock getStock(String id, String fromDate, String endDate)
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
		if(0 != ret || retList.size() == 0)
		{
			return null;
		}
			
		Stock cStock = new Stock();
		cStock.id = id;
		
		DataEngine.getStockBaseData(id, cStock.curBaseInfo);
		
		for(int i = 0; i < retList.size(); i++)  
        {  
			DayKData cDayKData = retList.get(i);  
			if(cDayKData.date.compareTo(fromDate) >= 0
					&& cDayKData.date.compareTo(endDate) <= 0)
			{
				StockDay cStockDay = new StockDay();
				cStockDay.ref_stock = cStock;
				cStockDay.date = cDayKData.date;
				cStockDay.open = cDayKData.open;
				cStockDay.close = cDayKData.close;
				cStockDay.low = cDayKData.low;
				cStockDay.high = cDayKData.high;
				cStockDay.volume = cDayKData.volume;
//		            System.out.println(cDayKData.date + "," 
//		            		+ cDayKData.open + "," + cDayKData.close); 
				cStock.historyData.add(cStockDay);
			}
        } 
		
		return cStock;
	}
	public static Stock getStock(String id, String endDate)
	{
		return getStock(id, "2000-01-01", endDate);
	}
	// 新数据加载
	public static Stock getStock(String id)
	{
		return getStock(id, "2100-01-01");
	}
}
