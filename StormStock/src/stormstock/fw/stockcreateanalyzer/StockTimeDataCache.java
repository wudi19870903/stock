package stormstock.fw.stockcreateanalyzer;

import java.util.List;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StockTimeDataCache {
	
	public static void addStockTime(String id, String date, StockTime cStockTime)
	{
		if(date.compareTo(s_curDate) != 0)
		{
			clearCache();
		}
			
		if(!s_cacheStockTimeMap.containsKey(id))
		{
			s_cacheStockTimeMap.put(id, new ArrayList<StockTime>());
		}
		
		s_cacheStockTimeMap.get(id).add(cStockTime);
		s_curDate = date;
	}
	public static List<StockTime> getStockTimeList(String id, String date)
	{
		if(date.compareTo(s_curDate) == 0)
		{
			if(s_cacheStockTimeMap.containsKey(id))
			{
				return s_cacheStockTimeMap.get(id);
			}
			else
			{
				return new ArrayList<StockTime>();
			}
		}
		else
		{
			return new ArrayList<StockTime>();
		}
	}
	public static void clearCache()
	{
		BLog.output("CREATE", "    StockTimeDataCache.clearCache \n");
		s_cacheStockTimeMap.clear();
	}
	
	private static String s_curDateDefault = "0000-00-00";
	private static String s_curDate = s_curDateDefault;
	private static Map<String, List<StockTime>> s_cacheStockTimeMap = 
		new HashMap<String, List<StockTime>>();
}
