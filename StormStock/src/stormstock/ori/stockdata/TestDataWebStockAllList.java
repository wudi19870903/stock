package stormstock.ori.stockdata;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.CommonDef.StockSimpleItem;
import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList;

public class TestDataWebStockAllList {
	public static void main(String[] args) {

		ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
		if(0 == cResultAllStockList.error)
		{
			for(int i = 0; i < cResultAllStockList.resultList.size(); i++)  
	        {  
				StockSimpleItem cStockSimpleItem = cResultAllStockList.resultList.get(i);  
	            System.out.println(cStockSimpleItem.name + "," + cStockSimpleItem.id);  
	        } 
			System.out.println("count:" + cResultAllStockList.resultList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + cResultAllStockList.error);
		}
	}
}
