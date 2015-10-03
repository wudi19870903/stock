package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stormstock.data.DataEngineBase;
import stormstock.data.WebStockAllList;
import stormstock.data.WebStockAllList.StockItem;
import stormstock.data.WebStockDayK;
import stormstock.data.WebStockDayK.DayKData;
import stormstock.data.WebStockDividendPayout.DividendPayout;
import stormstock.data.WebStockRealTimeInfo;
import stormstock.data.WebStockRealTimeInfo.RealTimeInfo;

public class RunUpdate {
	
	
	public static void main(String[] args) {

		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = WebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id); 
	            if(0 == DataEngineBase.updateStock(cStockItem.id))
	            {
	            	System.out.println("update success: " +  cStockItem.id);
	            }
	            else
	            {
	            	System.out.println("update ERROR:" + cStockItem.id);
	            }
	            
	        } 
			System.out.println("count:" + retList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
