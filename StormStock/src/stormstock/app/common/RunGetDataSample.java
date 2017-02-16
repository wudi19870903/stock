package stormstock.app.common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.ori.stockdata.DataEngine;

/*
 * 简单取得股票信息数据的方式
 */
public class RunGetDataSample {
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		// 股票全列表，输出所有股票id
		ResultAllStockID cResultAllStockID = cStockDataIF.getAllStockID();
		for(int i=0; i<cResultAllStockID.resultList.size();i++)
		{
			String stockId = cResultAllStockID.resultList.get(i);
			BLog.output("TEST", "%s\n", stockId);
		}
		
		// 输出一只股票所有日k数据
		String stockID = "600020";
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData(stockID);
		for(int j = 0; j < cResultHistoryData.resultList.size(); j++)  
        {  
			StockDay cStockDay = cResultHistoryData.resultList.get(j);  
			BLog.output("TEST", "date:%s open %.2f\n", cStockDay.date(), cStockDay.open());
            // 输出一天交易的详细数据
            if(j == cResultHistoryData.resultList.size()-1)
            {
            	ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, cStockDay.date(), "09:30:00", "15:00:00");
            	for(int k = 0; k < cResultDayDetail.resultList.size(); k++)  
            	{
            		BLog.output("TEST", "    %s %.2f\n", 
            				cResultDayDetail.resultList.get(k).time,
            				cResultDayDetail.resultList.get(k).price);
            	}
            }
        } 
		BLog.output("TEST", "Main End\n");
	}
}
