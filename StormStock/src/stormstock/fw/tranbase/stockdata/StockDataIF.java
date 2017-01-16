package stormstock.fw.tranbase.stockdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.account.AccountAccessor;
import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngineBase;
import stormstock.ori.stockdata.DataEngineBase.ResultStockBaseData;
import stormstock.ori.stockdata.DataEngineBase.ResultUpdatedStocksDate;
import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList;
import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;
import stormstock.ori.stockdata.DataEngine.ExKData;
import stormstock.ori.stockdata.DataEngine.ResultMinKDataOneDay;
import stormstock.ori.stockdata.CommonDef.*;

/*
 * 注意：取得的数据均为前复权价格
 */

public class StockDataIF {
	
	public StockDataIF()
	{
	}
	
	/*
	 * 获得某日期时间的账户访问器
	 * 可以获取账户信息
	 */
	public StockDataAccessor getStockDataAccessor(String date, String time)
	{
		return new StockDataAccessor(date, time, this);
	}
	
	/*
	 * 更新所有股票数据
	 * s_localLatestDate为缓存最新数据日期，此日期大于等于要更新日时，不做事情
	 * 真正数据更新执行后，所有缓存清空
	 */
	public boolean updateAllLocalStocks(String dateStr)
	{
		if(null == m_localLatestDate)
		{
			BLog.output("STOCKDATA","DataEngine.getUpdatedStocksDate\n");
			ResultUpdatedStocksDate cResultUpdatedStocksDate = DataEngine.getUpdatedStocksDate();
			if(0 == cResultUpdatedStocksDate.error)
			{
				m_localLatestDate = cResultUpdatedStocksDate.date;
			}
			else
			{
				BLog.error("STOCKDATA", "DataEngine.getUpdatedStocksDate error(%d) \n", cResultUpdatedStocksDate.error);
			}
		}
		
		if(m_localLatestDate.compareTo(dateStr) >= 0)
		{
			BLog.output("STOCKDATA", "update success! (current is newest, local: %s)\n", m_localLatestDate);
		}
		else
		{
			int iUpdateCnt = DataEngine.updateAllLocalStocks(dateStr);
			BLog.output("STOCKDATA", "update success to date: %s (count: %d)\n", m_localLatestDate, iUpdateCnt);
			clearAllCache();
		}
		
		return true;
	}
	/*
	 * 更新单只股票数据
	 * 此方法不改变s_localLatestDate
	 */
	public boolean updateLocalStocks(String stockID, String dateStr)
	{
		if(null == m_localLatestDate)
		{
			BLog.output("STOCKDATA","DataEngine.getUpdatedStocksDate\n");
			ResultUpdatedStocksDate cResultUpdatedStocksDate = DataEngine.getUpdatedStocksDate();
			if(0 == cResultUpdatedStocksDate.error)
			{
				m_localLatestDate = cResultUpdatedStocksDate.date;
			}
			else
			{
				BLog.error("STOCKDATA", "DataEngine.getUpdatedStocksDate error(%d) \n", cResultUpdatedStocksDate.error);
			}
		}
		
		if(m_localLatestDate.compareTo(dateStr) >= 0)
		{
			BLog.output("STOCKDATA", "update %s success! (current is newest, local: %s)\n",stockID, m_localLatestDate);
		}
		else
		{
			// 更新单只股票数据 不影响s_localLatestDate
			int iUpdateCnt = DataEngine.updateStock(stockID);
			BLog.output("STOCKDATA", "update %s success to date: %s (count: %d)\n", stockID, iUpdateCnt);
		}
		return true;
	}
	
	/*
	 * 获取所有股票Id列表
	 * 基于当前本地数据获取，不保证是最新（依赖于数据更新）
	 * 此接口带有数据缓存机制
	 */
	public List<String> getAllStockID()
	{
		if(null != m_cache_allStockID)
		{
			return m_cache_allStockID;
		}
		else
		{
			m_cache_allStockID = new ArrayList<String>();
			
			ResultAllStockList cResultAllStockList = DataEngine.getLocalAllStock();
			if(0 == cResultAllStockList.error)
			{
				for(int i=0; i<cResultAllStockList.resultList.size();i++)
				{
					String stockId = cResultAllStockList.resultList.get(i).id;
					m_cache_allStockID.add(stockId);
				}
			}
			else
			{
				BLog.error("STOCKDATA", "DataEngine.getLocalAllStock error(%d) \n", cResultAllStockList.error);
			}
			
			return m_cache_allStockID;
		}
	}

	/*
	 * 获取某只股票基本信息
	 * 不保证是最新（依赖于数据更新）
	 * 此接口带有数据缓存机制
	 */
	public StockInfo getLatestStockInfo(String id)
	{
		// 首次进行缓存
		if(null == m_cache_latestStockInfo || !m_cache_latestStockInfo.containsKey(id))
		{
			if(null == m_cache_latestStockInfo)
			{
				m_cache_latestStockInfo = new HashMap<String,StockInfo>();
			}
			
			StockInfo cStockInfo = new StockInfo();
			cStockInfo.ID = id;
			
			ResultStockBaseData cResultStockBaseData = DataEngine.getBaseInfo(id);
			
			if(0 == cResultStockBaseData.error)
			{
				cStockInfo.name = cResultStockBaseData.stockBaseInfo.name;
				cStockInfo.allMarketValue = cResultStockBaseData.stockBaseInfo.allMarketValue; 
				cStockInfo.circulatedMarketValue = cResultStockBaseData.stockBaseInfo.circulatedMarketValue; 
				cStockInfo.peRatio = cResultStockBaseData.stockBaseInfo.peRatio;
			}
			else
			{
				BLog.error("STOCKDATA", "DataEngine.getBaseInfo error(%d) \n", cResultStockBaseData.error);
			}
			
			m_cache_latestStockInfo.put(id, cStockInfo);
		}
			
		// 从缓存中取数据
		if(null != m_cache_latestStockInfo && m_cache_latestStockInfo.containsKey(id))
		{
			return m_cache_latestStockInfo.get(id);
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * 获取某只股票的历史日K数据
	 * 不保证是最新（依赖于数据更新）
	 * 此接口带有数据缓存机制
	 */
	public List<StockDay> getHistoryData(String stockID, String fromDate, String endDate)
	{
		// 首次进行历史数据缓存
		if(null == s_cache_stockDayData || !s_cache_stockDayData.containsKey(stockID))
		{
			if(null == s_cache_stockDayData)
			{
				s_cache_stockDayData = new HashMap<String,List<StockDay>>();
			}
			
			List<StockDay> historyData = new ArrayList<StockDay>();
			
			ResultDayKData cResultDayKData = DataEngine.getDayKDataQianFuQuan(stockID);
			
			if(0 == cResultDayKData.error && cResultDayKData.resultList.size() != 0)
			{
				for(int i = 0; i < cResultDayKData.resultList.size(); i++)  
		        {  
					DayKData cDayKData = cResultDayKData.resultList.get(i);  
	
					StockDay cStockDay = new StockDay();
					cStockDay.set(cDayKData.date, 
							cDayKData.open, cDayKData.close, cDayKData.low, cDayKData.high, cDayKData.volume);
					//System.out.println("historyData.add " + cDayKData.date + "," + cDayKData.open + "," + cDayKData.close); 
					historyData.add(cStockDay);
		        } 
			}
			else
			{
				BLog.error("STOCKDATA", "DataEngine.getDayKDataQianFuQuan error(%d) \n", cResultDayKData.error);
			}
			
//			BLog.output("TEST", "DataEngine getDayKDataQianFuQuan(%d)\n", retList.size());
//			BLog.output("TEST", "getHistoryData return! historyData(%d)\n", historyData.size());
			
			s_cache_stockDayData.put(stockID, historyData);
		}
		
		// 从缓存中取数据
		if(null != s_cache_stockDayData && s_cache_stockDayData.containsKey(stockID))
		{
			List<StockDay> cacheList = s_cache_stockDayData.get(stockID);
			return StockUtils.subStockDayData(cacheList, fromDate, endDate);
		}
		else
		{
			return null;
		}
	}
	
	public List<StockDay> getHistoryData(String id, String endDate)
	{
		return getHistoryData(id, "2000-01-01", endDate);
	}
	public List<StockDay> getHistoryData(String id)
	{
		return getHistoryData(id, "2000-01-01", "2100-01-01");
	}

	/*
	 * 获取某只股票某天某时间的细节数据
	 * 不保证是最新（依赖于数据更新）
	 * 此接口带有数据缓存机制
	 */
	public List<StockTime> getDayDetail(String id, String date, String beginTime, String endTime)
	{
		// 首次进行历史数据缓存
		String findKey = id + "_" + date;
		if(null == s_cache_stockTimeData || !s_cache_stockTimeData.containsKey(findKey))
		{
			if(null == s_cache_stockTimeData)
			{
				s_cache_stockTimeData = new HashMap<String,List<StockTime>>();
			}
			
			List<StockTime> detailDataList = new ArrayList<StockTime>();
			
			List<StockDay> historyData = getHistoryData(id, date, date);
			if(historyData.size()==1)
			{
				StockDay cStockDay = historyData.get(0);
				
				if(null != cStockDay && date.length() == "0000-00-00".length())
				{
					// load new detail data
					ResultMinKDataOneDay cResultMinKDataOneDay = DataEngine.get1MinKDataOneDay(id, date);
					
					if(0 == cResultMinKDataOneDay.error && cResultMinKDataOneDay.exKDataList.size() != 0)
					{
						// 由于可能是复权价位，需要重新计算相对价格
						float baseOpenPrice = cStockDay.open();
						float actruaFirstPrice = cResultMinKDataOneDay.exKDataList.get(0).open;
						for(int i = 0; i < cResultMinKDataOneDay.exKDataList.size(); i++)  
				        {  
							ExKData cExKData = cResultMinKDataOneDay.exKDataList.get(i);  
//				            System.out.println(cExKData.datetime + "," 
//				            		+ cExKData.open + "," + cExKData.close + "," 
//				            		+ cExKData.low + "," + cExKData.high + "," 
//				            		+ cExKData.volume);  
							
							float actrualprice = cExKData.close;
							float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
							float changedprice = baseOpenPrice + baseOpenPrice * changeper;
							
							// 添加上下午开盘点
							if(cExKData.getTime().compareTo("09:31:00") == 0
									|| cExKData.getTime().compareTo("13:01:00") == 0)
							{
								StockTime cStockDayDetail = new StockTime();
								cStockDayDetail.price = baseOpenPrice;
								String openTime = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(cExKData.getTime(), -1);
								cStockDayDetail.time = openTime;
								detailDataList.add(cStockDayDetail);
							}
							

							StockTime cStockDayDetail = new StockTime();
							cStockDayDetail.price = changedprice;
							cStockDayDetail.time = cExKData.getTime();
							detailDataList.add(cStockDayDetail);
				        } 
					}
				}
			}
			
			s_cache_stockTimeData.put(findKey, detailDataList);
		}
		
		// 从缓存中取数据
		if(null != s_cache_stockTimeData && s_cache_stockTimeData.containsKey(findKey))
		{
			List<StockTime> cacheList = s_cache_stockTimeData.get(findKey);
			return StockUtils.subStockTimeData(cacheList, beginTime, endTime);
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * 获取某只股票某天某时间的价格
	 * 数据缓存机制依赖于getDayDetail接口
	 */
	public boolean getStockTime(String id, String date, String time, StockTime out_cStockTime)
	{
		boolean bRealTime = false;
		String curDate = BUtilsDateTime.GetCurDateStr();
		String curTime = "";
		if(date.compareTo(curDate) == 0)
		{
			curTime = BUtilsDateTime.GetCurTimeStr();
			if(Math.abs(BUtilsDateTime.subTime(time,curTime)) < 10) // 离当前时间10秒内
			{
				bRealTime = true;
			}
		}
		
		if(bRealTime)
		{
			ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(id);
		
			if(0 == cResultRealTimeInfo.error)
			{
				out_cStockTime.time = curTime;
				out_cStockTime.price = cResultRealTimeInfo.realTimeInfo.curPrice;
				return true;
			}
		}
		else
		{
			boolean bDataFitting = false;
			
			if(bDataFitting)
			{
				// 基于日K线的拟合生成
				// 9点30（不含）之前，为前一天收盘价格
				// 09:30:00 - 13:00:00（不含） ，为上午开盘价格
				// 13:00:00 - 24:00:00 （含），为下午收盘价格
				if(time.compareTo("09:30:00") >= 0)
				{
					List<StockDay> cStockDayList = getHistoryData(id, date, date);
					if(cStockDayList.size() > 0)
					{
						StockDay cStockDay = cStockDayList.get(0);
						float open = cStockDay.open();
						float close = cStockDay.close();
						out_cStockTime.time = time;
						if(time.compareTo("09:30:00") >= 0 && time.compareTo("13:00:00") < 0)
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
			else
			{
				// 基于真实的历史数据
				List<StockTime> cStockTimeList = getDayDetail(id, date, "09:30:00", time);
				if(null!=cStockTimeList && cStockTimeList.size()>0)
				{
					StockTime cStockTime = cStockTimeList.get(cStockTimeList.size()-1);
					long subTimeMin = BUtilsDateTime.subTime(time, cStockTime.time);
					if(subTimeMin >=0 && subTimeMin<=120)
					{
						out_cStockTime.time = cStockTime.time;
						out_cStockTime.price = cStockTime.price;
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}
	
	// ******************************************************************************************
	
//	public Stock getStock(String id, String fromDate, String endDate)
//	{
//		boolean bEnableCache = false;
//		
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
//		
//		List<DayKData> retList = new ArrayList<DayKData>();
//		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
//		if(0 != ret || retList.size() == 0)
//		{
//			return null;
//		}
//			
//		Stock cStock = new Stock();
//		
//		StockInfo cStockInfo = getLatestStockInfo(id);
//		cStock.getCurLatestStockInfo().CopyFrom(cStockInfo);
//		
//		for(int i = 0; i < retList.size(); i++)  
//        {  
//			DayKData cDayKData = retList.get(i);  
//			if(cDayKData.date.compareTo(fromDate) >= 0
//					&& cDayKData.date.compareTo(endDate) <= 0)
//			{
//				StockDay cStockDay = new StockDay();
//				cStockDay.set(cDayKData.date, 
//						cDayKData.open, cDayKData.close, cDayKData.low, cDayKData.high, cDayKData.volume);
////		            System.out.println(cDayKData.date + "," 
////		            		+ cDayKData.open + "," + cDayKData.close); 
//				cStock.getCurStockDayData().add(cStockDay);
//			}
//        } 
//		
//		// cache
////		if(bEnableCache)
////		{
////			s_stockCacheMap.put(id, cStock);
////		}
//		
//		return cStock;
//	}

	
	// ***********************************************************************************
	
	public void clearAllCache()
	{
		m_localLatestDate = null;
		m_cache_allStockID = null;
		m_cache_latestStockInfo = null;
		s_cache_stockDayData = null;
		s_cache_stockTimeData = null;
	}
	
	// 当前总数据最新更新日期缓存
	private String m_localLatestDate = null;
	// 本地股票列表缓存
	private List<String> m_cache_allStockID = null;
	// 本地股票最新基本信息缓存
	private Map<String,StockInfo> m_cache_latestStockInfo = null;
	// 日K历史数据缓存
	// key:600001
	private Map<String,List<StockDay>> s_cache_stockDayData = null;
	// 日内分时缓存  
	// key:600001_2016-01-01
	private Map<String,List<StockTime>> s_cache_stockTimeData = null;
}
