package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngine.ExKData;
import stormstock.ori.stockdata.DataEngineBase.StockBaseInfo;
import stormstock.ori.stockdata.DataWebStockAllList.StockItem;
import stormstock.ori.stockdata.DataWebStockDayK.DayKData;

public class StockDataProvider {
	/*
	 * ��ȡ���й�ƱId�б�
	 */
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
	/*
	 * ��ȡĳֻ��Ʊ������Ϣ
	 */
	public static StockInfo getLatestStockInfo(String id)
	{
		StockInfo cStockInfo = new StockInfo();
		
		StockBaseInfo cStockBaseInfo = new StockBaseInfo();
		if(0 == DataEngine.getStockBaseData(id, cStockBaseInfo))
		{
			cStockInfo.name = cStockBaseInfo.name;
			cStockInfo.price = cStockBaseInfo.price; 
			cStockInfo.allMarketValue = cStockBaseInfo.allMarketValue; 
			cStockInfo.circulatedMarketValue = cStockBaseInfo.circulatedMarketValue; 
			cStockInfo.peRatio = cStockBaseInfo.peRatio;
		}
		return cStockInfo;
	}
	/*
	 * ��ȡĳֻ��Ʊ����ʷ��K����
	 */
	public static List<StockDay> getHistoryData(String id, String fromDate, String endDate)
	{
		List<StockDay> historyData = new ArrayList<StockDay>();
		
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
		if(0 == ret && retList.size() != 0)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DayKData cDayKData = retList.get(i);  
				if(cDayKData.date.compareTo(fromDate) >= 0
						&& cDayKData.date.compareTo(endDate) <= 0)
				{
					StockDay cStockDay = new StockDay();
					cStockDay.date = cDayKData.date;
					cStockDay.open = cDayKData.open;
					cStockDay.close = cDayKData.close;
					cStockDay.low = cDayKData.low;
					cStockDay.high = cDayKData.high;
					cStockDay.volume = cDayKData.volume;
					// System.out.println(cDayKData.date + "," + cDayKData.open + "," + cDayKData.close); 
					historyData.add(cStockDay);
				}
	        } 
		}
		
		return historyData;
	}
	
	/*
	 * ��ȡĳֻ��Ʊĳ��ĳʱ���ϸ������
	 */
	public List<StockDayDetail> getDayDetail(String id, String date, String time)
	{

		List<StockDay> historyData = getHistoryData(id, date, date);
		if(historyData.size()==1)
		{
			StockDay cStockDay = historyData.get(0);
			
			if(null != cStockDay && date.length() == 6)
			{
				// load new detail data
				List<ExKData> retList = new ArrayList<ExKData>();
				int ret = DataEngine.get1MinKDataOneDay(id, date, retList);
				if(0 == ret && retList.size() != 0)
				{
					// ���ڿ����Ǹ�Ȩ��λ����Ҫ���¼�����Լ۸�
					float baseOpenPrice = cStockDay.open;
					float actruaFirstPrice = retList.get(0).open;
					for(int i = 0; i < retList.size(); i++)  
			        {  
						ExKData cExKData = retList.get(i);  
//			            System.out.println(cExKData.datetime + "," 
//			            		+ cExKData.open + "," + cExKData.close + "," 
//			            		+ cExKData.low + "," + cExKData.high + "," 
//			            		+ cExKData.volume);  
						
						float actrualprice = cExKData.close;
						float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
						float changedprice = baseOpenPrice + baseOpenPrice * changeper;
						
						StockDetailTime cStockDetailTime = new StockDetailTime();
						cStockDetailTime.price = changedprice;
						cStockDetailTime.time = cExKData.getTime();
						cStockDetailDay.detailDataList.add(cStockDetailTime);
			        } 
				}
			}
		}
		
		return cStockDetailDay;
	}
	
	
	// ******************************************************************************************
	
	
	public static Stock getStock(String id, String endDate)
	{
		return getStock(id, "2000-01-01", endDate);
	}
	
	// �����ݼ���
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
		
		StockInfo cStockInfo = getLatestStockInfo(id);
		cStock.latestInfo.CopyFrom(cStockInfo);
		
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
