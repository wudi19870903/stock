package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLDataProvider;
import stormstock.analysis.ANLLog;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;

/*
 * 简单取得股票信息数据的方式
 */
public class RunGetDataSample {
	public static void main(String[] args) {
		ANLLog.outputConsole("Main Begin\n\n");
		// 股票全列表，输出所有股票id
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLLog.outputConsole(stockId + "\n");
		}
		// 输出一只股票所有日k数据
		ANLStock cANLStock = ANLDataProvider.getANLStock("600020");
		for(int j = 0; j < cANLStock.historyData.size(); j++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
			ANLLog.outputConsole("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
            // 输出一天交易的详细数据
            if(j == cANLStock.historyData.size()-1)
            {
            	cANLDayKData.LoadDetail();
            	for(int k = 0; k < cANLDayKData.detailDataList.size(); k++)  
            	{
            		ANLLog.outputConsole("    %s %.2f\n", 
            				cANLDayKData.detailDataList.get(k).time,
            				cANLDayKData.detailDataList.get(k).price);
            	}
            }
        } 
		ANLLog.outputConsole("\n\nMain End");
	}
}
