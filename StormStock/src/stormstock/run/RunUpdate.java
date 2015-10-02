package stormstock.run;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngineBase;
import stormstock.data.WebStockAllList;
import stormstock.data.WebStockAllList.StockItem;
import stormstock.data.WebStockDayK.DayKData;

public class RunUpdate {
	public static int updateStock(String id)
	{
		int retdownloadStockDayk =  DataEngineBase.downloadStockDayk(id);
		int retdownloadStockDividendPayout =  DataEngineBase.downloadStockDividendPayout(id);
		if(0 == retdownloadStockDayk 
				&& 0 == retdownloadStockDividendPayout)
		{
			return 0;
		}
		else
		{
			return -10;
		}
	}
	public static void main(String[] args) {
		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = WebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id); 
	            if(0 == updateStock(cStockItem.id))
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
