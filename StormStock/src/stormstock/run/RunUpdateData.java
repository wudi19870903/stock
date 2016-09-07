package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockPool;
import stormstock.data.DataEngineBase;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;
import stormstock.data.DataWebStockRealTimeInfo;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class RunUpdateData {
	public static Formatter fmt = new Formatter(System.out);
	public static void main(String[] args) {
		int iRetupdateStock = 0;
		// 更新指数k
		String ShangZhiId = "999999";
		String ShangZhiName = "上证指数";
		iRetupdateStock = DataEngineBase.updateStock(ShangZhiId);
		ANLStock cANLStock = ANLStockPool.getANLStock(ShangZhiId);
		String newestDate = "";
		if(cANLStock.historyData.size()>0)
		{
			newestDate = cANLStock.historyData.get(cANLStock.historyData.size()-1).date;
		}
		if(iRetupdateStock >= 0)
		{

			fmt.format("update success: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, iRetupdateStock, newestDate);
		}
		else
		{
			fmt.format("update ERROR: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, iRetupdateStock, newestDate);
		}
		
		// 更新所有k
		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = DataWebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            iRetupdateStock = DataEngineBase.updateStock(cStockItem.id);
	            cANLStock = ANLStockPool.getANLStock(cStockItem.id);
	            if(cANLStock != null && cANLStock.historyData.size()>0)
	    		{
	    			newestDate = cANLStock.historyData.get(cANLStock.historyData.size()-1).date;
	    		}
	            if( iRetupdateStock >= 0)
	            {
	            	fmt.format("update success: %s (%s) item:%d date:%s\n", cStockItem.id, cStockItem.name, iRetupdateStock, newestDate);
	            }
	            else
	            {
	            	fmt.format("update ERROR: %s (%s) item:%d date:%s\n", cStockItem.id, cStockItem.name, iRetupdateStock, newestDate);
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
