package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataWebStockAllList.StockItem;

public class TestDataWebStockAllList {
	public static void main(String[] args) {
		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = DataWebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id);  
	        } 
			System.out.println("count:" + retList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
