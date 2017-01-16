package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList;
import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList.StockItem;

public class TestDataWebStockAllList {
	public static void main(String[] args) {

		ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
		if(0 == cResultAllStockList.error)
		{
			for(int i = 0; i < cResultAllStockList.resultList.size(); i++)  
	        {  
				StockItem cStockItem = cResultAllStockList.resultList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id);  
	        } 
			System.out.println("count:" + cResultAllStockList.resultList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + cResultAllStockList.error);
		}
	}
}
