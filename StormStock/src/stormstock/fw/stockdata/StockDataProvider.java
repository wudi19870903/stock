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

/*
 * 注意：取得的数据均为前复权价格
 */

public class StockDataProvider {
	/*
	 * 获取所有股票Id列表
	 */
	public static List<String> getAllStockID()
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
	public static void cacheAllStockID(List<String> allStockID)
	{
		s_cache_allStockID = allStockID;
	}
	public static boolean isCachedAllStockID() 
	{
		return (s_cache_allStockID!=null)?true:false;
	}
	
	/*
	 * 获取某只股票基本信息
	 */
	public static StockInfo getLatestStockInfo(String id)
	{
		if(null != s_cache_latestStockInfo && s_cache_latestStockInfo.containsKey(id))
		{
			return s_cache_latestStockInfo.get(id);
		}
		StockInfo cStockInfo = new StockInfo();
		cStockInfo.ID = id;
		
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
	public static void cacheLatestStockInfo(StockInfo cStockInfo)
	{
		if(null == s_cache_latestStockInfo)
		{
			s_cache_latestStockInfo = new HashMap<String,StockInfo>();
		}
		s_cache_latestStockInfo.put(cStockInfo.ID, cStockInfo);
	}
	public static boolean isLatestStockInfo(String stockID) 
	{
		if(null == s_cache_latestStockInfo) return false;
		if(!s_cache_latestStockInfo.containsKey(stockID)) return false;
		return true;
	}
	
	/*
	 * 获取某只股票的历史日K数据
	 */
	public static List<StockDay> getHistoryData(String stockID, String fromDate, String endDate)
	{
		if(null != s_cache_stockDayData && s_cache_stockDayData.containsKey(stockID))
		{
			List<StockDay> cacheList = s_cache_stockDayData.get(stockID);
			return StockUtils.subStockDayData(cacheList, fromDate, endDate);
		}
		
		List<StockDay> historyData = new ArrayList<StockDay>();
		
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(stockID, retList);
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
	public static List<StockDay> getHistoryData(String id, String endDate)
	{
		return getHistoryData(id, "2000-01-01", endDate);
	}
	public static List<StockDay> getHistoryData(String id)
	{
		return getHistoryData(id, "2000-01-01", "2100-01-01");
	}
	public static void cacheHistoryData(String id, List<StockDay> cStockDayList)
	{
		if(null == s_cache_stockDayData)
		{
			s_cache_stockDayData = new HashMap<String,List<StockDay>>();
		}
		s_cache_stockDayData.put(id, cStockDayList);
	}
	public static boolean isCachedStockDayData(String stockID) 
	{
		if(null == s_cache_stockDayData) return false;
		if(!s_cache_stockDayData.containsKey(stockID)) return false;
		return true;
	}
	
	/*
	 * 获取某只股票某天某时间的细节数据
	 */
	public static List<StockTime> getDayDetail(String id, String date, String endTime)
	{
		List<StockTime> detailDataList = new ArrayList<StockTime>();
		
		List<StockDay> historyData = getHistoryData(id, date, date);
		if(historyData.size()==1)
		{
			StockDay cStockDay = historyData.get(0);
			
			if(null != cStockDay && date.length() == "0000-00-00".length())
			{
				// load new detail data
				List<ExKData> retList = new ArrayList<ExKData>();
				int ret = DataEngine.get1MinKDataOneDay(id, date, retList);
				if(0 == ret && retList.size() != 0)
				{
					// 由于可能是复权价位，需要重新计算相对价格
					float baseOpenPrice = cStockDay.open;
					float actruaFirstPrice = retList.get(0).open;
					for(int i = 0; i < retList.size(); i++)  
			        {  
						ExKData cExKData = retList.get(i);  
			            System.out.println(cExKData.datetime + "," 
			            		+ cExKData.open + "," + cExKData.close + "," 
			            		+ cExKData.low + "," + cExKData.high + "," 
			            		+ cExKData.volume);  
						
						float actrualprice = cExKData.close;
						float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
						float changedprice = baseOpenPrice + baseOpenPrice * changeper;
						
						if(cExKData.getTime().compareTo(endTime) <= 0) //只添加小于时间参数的
						{
							StockTime cStockDayDetail = new StockTime();
							cStockDayDetail.price = changedprice;
							cStockDayDetail.time = cExKData.getTime();
							detailDataList.add(cStockDayDetail);
						}
			        } 
				}
			}
		}
		
		return detailDataList;
	}
	
	
	// ******************************************************************************************
	
	
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
		
//		// cache check
//		if(bEnableCache)
//		{
//			String endDateActual = endDate;
//			String fromDateActual = fromDate;
//			if(null == s_localLatestDate)
//			{
//				s_localLatestDate = DataEngine.getUpdatedStocksDate();
//			}
//			if(endDateActual.compareTo(s_localLatestDate) > 0)
//			{
//				endDateActual = s_localLatestDate;
//			}
//			if(fromDateActual.compareTo("2008-01-01") < 0)
//			{
//				fromDateActual = "2008-01-01";
//			}
//			if(s_stockCacheMap.containsKey(id))
//			{
//				Stock cStock = s_stockCacheMap.get(id);
//				if(fromDateActual.compareTo("2008-01-01")>=0 && 
//						endDateActual.compareTo(s_localLatestDate) <=0)
//				{
//					Stock cNewStock = cStock.subObj(fromDate, endDate);
//					return cNewStock;
//				}
//				else
//				{
//					s_stockCacheMap.remove(id);
//				}
//			}
//		}
		
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
		if(0 != ret || retList.size() == 0)
		{
			return null;
		}
			
		Stock cStock = new Stock();
		
		StockInfo cStockInfo = getLatestStockInfo(id);
		cStock.getCurLatestStockInfo().CopyFrom(cStockInfo);
		
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
//		            System.out.println(cDayKData.date + "," 
//		            		+ cDayKData.open + "," + cDayKData.close); 
				cStock.getCurStockDayData().add(cStockDay);
			}
        } 
		
		// cache
//		if(bEnableCache)
//		{
//			s_stockCacheMap.put(id, cStock);
//		}
		
		return cStock;
	}

	
	// ***********************************************************************************
	
	public static void clearCache()
	{
	}
	
	private static String s_localLatestDate = null;
	private static List<String> s_cache_allStockID = null;
	private static Map<String,StockInfo> s_cache_latestStockInfo = null;
	private static Map<String,List<StockDay>> s_cache_stockDayData = null;
}
