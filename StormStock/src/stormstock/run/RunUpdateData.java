package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stormstock.data.DataEngineBase;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;
import stormstock.data.DataWebStockRealTimeInfo;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class RunUpdateData {
	
	
	public static void main(String[] args) {

		// 更新指数k
		if(DataEngineBase.updateStock("999999") >= 0)
		{
			System.out.println("update success: 999999 （上证指数  ）");
		}
		else
		{
			System.out.println("update success: 999999 （上证指数  ） ERROR");
		}
		
		// 更新所有k
		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = DataWebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id); 
	            int iRetupdateStock = DataEngineBase.updateStock(cStockItem.id);
	            if( iRetupdateStock >= 0)
	            {
	            	System.out.println("update success: " +  cStockItem.id + " item:" + iRetupdateStock);
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
