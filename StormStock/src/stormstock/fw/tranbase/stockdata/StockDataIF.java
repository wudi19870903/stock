package stormstock.fw.tranbase.stockdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngineBase;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo;
import stormstock.ori.stockdata.DataEngine.ExKData;
import stormstock.ori.stockdata.DataEngineBase.StockBaseInfo;
import stormstock.ori.stockdata.DataWebStockAllList.StockItem;
import stormstock.ori.stockdata.DataWebStockDayK.DayKData;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.RealTimeInfo;

/*
 * ע�⣺ȡ�õ����ݾ�Ϊǰ��Ȩ�۸�
 */

public class StockDataIF {
	
	public static boolean updateAllLocalStocks(String dateStr)
	{
		String updatedDate = DataEngine.getUpdatedStocksDate();
		if(updatedDate.compareTo(dateStr) >= 0)
		{
			BLog.output("STOCKDATA", "update success! (current is newest, local: %s)\n", updatedDate);
		}
		else
		{
			int iUpdateCnt = DataEngine.updateAllLocalStocks(dateStr);
			updatedDate = DataEngine.getUpdatedStocksDate();
			BLog.output("STOCKDATA", "update success to date: %s (count: %d)\n", updatedDate, iUpdateCnt);
		}
		
		return true;
	}
	
	public static boolean updateLocalStocks(String stockID, String dateStr)
	{
		String updatedDate = DataEngine.getUpdatedStocksDate();
		if(updatedDate.compareTo(dateStr) >= 0)
		{
			BLog.output("STOCKDATA", "update %s success! (current is newest, local: %s)\n",stockID, updatedDate);
		}
		else
		{
			int iUpdateCnt = DataEngine.updateStock(stockID);
			updatedDate = DataEngine.getUpdatedStocksDate();
			BLog.output("STOCKDATA", "update %s success to date: %s (count: %d)\n", stockID, updatedDate, iUpdateCnt);
		}
		
		return true;
	}
	
	/*
	 * ��ȡ���й�ƱId�б�
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
	 * ��ȡĳֻ��Ʊ������Ϣ
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
			//cStockInfo.price = cStockBaseInfo.price; 
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
	 * ��ȡĳֻ��Ʊ����ʷ��K����
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
					cStockDay.set(cDayKData.date, 
							cDayKData.open, cDayKData.close, cDayKData.low, cDayKData.high, cDayKData.volume);
					//System.out.println("historyData.add " + cDayKData.date + "," + cDayKData.open + "," + cDayKData.close); 
					historyData.add(cStockDay);
				}
	        } 
		}
//		BLog.output("TEST", "DataEngine getDayKDataQianFuQuan(%d)\n", retList.size());
//		BLog.output("TEST", "getHistoryData return! historyData(%d)\n", historyData.size());
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
	 * ��ȡĳֻ��Ʊĳ��ĳʱ��ļ۸�
	 */
	public static boolean getStockTime(String id, String date, String time, StockTime out_cStockTime)
	{
		boolean bRealTime = false;
		String curDate = BUtilsDateTime.GetCurDateStr();
		String curTime = "";
		if(date.compareTo(curDate) == 0)
		{
			curTime = BUtilsDateTime.GetCurTimeStr();
			if(Math.abs(BUtilsDateTime.subTime(time,curTime)) < 10) // �뵱ǰʱ��10����
			{
				bRealTime = true;
			}
		}
		
		if(bRealTime)
		{
			RealTimeInfo cRealTimeInfo = new RealTimeInfo();
			int ret = DataWebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
			if(0 == ret)
			{
				out_cStockTime.time = curTime;
				out_cStockTime.price = cRealTimeInfo.curPrice;
				return true;
			}
		}
		else
		{
			if(time.compareTo("09:30:00") >= 0)
			{
				List<StockDay> cStockDayList = getHistoryData(id, date, date);
				if(cStockDayList.size() > 0)
				{
					StockDay cStockDay = cStockDayList.get(0);
					float open = cStockDay.open();
					float close = cStockDay.close();
					out_cStockTime.time = time;
					if(time.compareTo("09:30:00") >= 0 && time.compareTo("13:00:00") <= 0)
					{
						out_cStockTime.price = open;
					}
					else if(time.compareTo("13:00:00") >= 0 && time.compareTo("24:00:00") <= 0)
					{
						out_cStockTime.price = close;
					}
					return true;
				}
			}
			else
			{
				String beforeDate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(date, -1);
				List<StockDay> cStockDayList = getHistoryData(id, beforeDate, beforeDate);
				if(cStockDayList.size() > 0)
				{
					StockDay cStockDay = cStockDayList.get(0);
					out_cStockTime.time = time;
					out_cStockTime.price = cStockDay.close();
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * ��ȡĳֻ��Ʊĳ��ĳʱ���ϸ������
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
					// ���ڿ����Ǹ�Ȩ��λ����Ҫ���¼�����Լ۸�
					float baseOpenPrice = cStockDay.open();
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
						
						if(cExKData.getTime().compareTo(endTime) <= 0) //ֻ����С��ʱ�������
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
	public static List<StockTime> getDayDetail(String id, String date)
	{
		return getDayDetail(id, date, "15:00:00");
	}
	public static void cacheDayDetail(String id, String date, List<StockTime> cStockTimeList)
	{
		if(null == s_cache_stockTimeData)
		{
			s_cache_stockTimeData = new HashMap<String, List<StockTime>>();
		}
		String key = id + "_" + date;
		s_cache_stockTimeData.put(key, cStockTimeList);
	}
	public static boolean isCachedDayDetailData(String id, String date) 
	{
		if(null == s_cache_stockTimeData) return false;
		String key = id + "_" + date;
		if(!s_cache_stockTimeData.containsKey(key)) return false;
		return true;
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
				cStockDay.set(cDayKData.date, 
						cDayKData.open, cDayKData.close, cDayKData.low, cDayKData.high, cDayKData.volume);
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
	private static Map<String, List<StockTime>> s_cache_stockTimeData = null;
}