package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static Stock getStock(String id, String endDate)
	{
		return getStock(id, "2000-01-01", endDate);
	}
	
	// 新数据加载
	public static Stock getStock(String id)
	{
		return getStock(id, "2100-01-01");
	}
	
	public static Stock getStock(String id, String fromDate, String endDate)
	{
		boolean bEnableCache = false;
		
		// cache check
		if(bEnableCache)
		{
			String endDateActual = endDate;
			String fromDateActual = fromDate;
			if(null == s_localLatestDate)
			{
				s_localLatestDate = DataEngine.getUpdatedStocksDate();
			}
			if(endDateActual.compareTo(s_localLatestDate) > 0)
			{
				endDateActual = s_localLatestDate;
			}
			if(fromDateActual.compareTo("2008-01-01") < 0)
			{
				fromDateActual = "2008-01-01";
			}
			if(s_stockCacheMap.containsKey(id))
			{
				Stock cStock = s_stockCacheMap.get(id);
				if(fromDateActual.compareTo("2008-01-01")>=0 && 
						endDateActual.compareTo(s_localLatestDate) <=0)
				{
					Stock cNewStock = cStock.subObj(fromDate, endDate);
					return cNewStock;
				}
				else
				{
					s_stockCacheMap.remove(id);
				}
			}
		}
		
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
		
		// cache
		if(bEnableCache)
		{
			s_stockCacheMap.put(id, cStock);
		}
		
		return cStock;
	}

	
	public static void clearStockCache()
	{
		s_localLatestDate = null;
		s_stockCacheMap.clear();
	}
	private static String s_localLatestDate = null;
	private static Map<String, Stock> s_stockCacheMap = new HashMap<String, Stock>();
}
