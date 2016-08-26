package stormstock.data;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine.ExKData;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class TestDataEngin {
	private static void test_getDayKDataQianFuQuan()
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan("000546", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DayKData cDayKData = retList.get(i);  
	            System.out.println(cDayKData.date + "," 
	            		+ cDayKData.open + "," + cDayKData.close);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
	private static void test_get5MinKDataOneDay()
	{
		List<ExKData> retList = new ArrayList<ExKData>();
		int ret = DataEngine.get5MinKDataOneDay("000920", "2016-08-25", retList);
		//int ret = get5MinKDataOneDay("600316", "2010-06-28", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				ExKData cExKData = retList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
	private static void test_get1MinKDataOneDay()
	{
		List<ExKData> retList = new ArrayList<ExKData>();
		int ret = DataEngine.get1MinKDataOneDay("000920", "2016-08-05", retList);
		//int ret = get5MinKDataOneDay("600316", "2010-06-28", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				ExKData cExKData = retList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
	private static void test_getLocalRandomStock()
	{
		List<StockItem> retList = DataEngine.getLocalRandomStock(3);
		for(int i = 0; i < retList.size(); i++)  
        {  
			StockItem cStockItem = retList.get(i);  
            System.out.println("cStockItem: id:" + cStockItem.id);  
        } 
	}
	public static void main(String[] args) {
		//test_getDayKDataQianFuQuan();
		//test_get5MinKDataOneDay();
		test_get1MinKDataOneDay();
		//test_getLocalRandomStock();
	}
}
