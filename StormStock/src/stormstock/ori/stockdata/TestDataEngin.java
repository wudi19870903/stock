package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.CommonDef.StockSimpleItem;
import stormstock.ori.stockdata.DataEngine.ExKData;
import stormstock.ori.stockdata.DataEngine.ResultMinKDataOneDay;
import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.CommonDef.*;

public class TestDataEngin {
	private static void test_getDayKDataQianFuQuan()
	{
		ResultDayKData cResultDayKData = DataEngine.getDayKDataQianFuQuan("000004");
		if(0 == cResultDayKData.error)
		{
			for(int i = 0; i < cResultDayKData.resultList.size(); i++)  
	        {  
				DayKData cDayKData = cResultDayKData.resultList.get(i);  
	            System.out.println(cDayKData.date + "," 
	            		+ cDayKData.open + "," + cDayKData.close);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultDayKData.error);
		}
	}
	private static void test_get5MinKDataOneDay()
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = DataEngine.get5MinKDataOneDay("000920", "2016-08-25");
		//int ret = get5MinKDataOneDay("600316", "2010-06-28", retList);
		if(0 == cResultMinKDataOneDay.error)
		{
			for(int i = 0; i < cResultMinKDataOneDay.exKDataList.size(); i++)  
	        {  
				ExKData cExKData = cResultMinKDataOneDay.exKDataList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultMinKDataOneDay.error);
		}
	}
	private static void test_get1MinKDataOneDay()
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = DataEngine.get1MinKDataOneDay("000004", "2016-10-12");
		//int ret = get5MinKDataOneDay("600316", "2010-06-28", retList);
		if(0 == cResultMinKDataOneDay.error)
		{
			for(int i = 0; i < cResultMinKDataOneDay.exKDataList.size(); i++)  
	        {  
				ExKData cExKData = cResultMinKDataOneDay.exKDataList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultMinKDataOneDay.error);
		}
	}
	private static void test_getLocalRandomStock()
	{
		List<StockSimpleItem> retList = DataEngine.getLocalRandomStock(3);
		for(int i = 0; i < retList.size(); i++)  
        {  
			StockSimpleItem cStockSimpleItem = retList.get(i);  
            System.out.println("cStockSimpleItem: id:" + cStockSimpleItem.id);  
        } 
	}
	public static void main(String[] args) {
		//test_getDayKDataQianFuQuan();
		//test_get5MinKDataOneDay();
		test_get1MinKDataOneDay();
		//test_getLocalRandomStock();
	}
}
