package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class RunTest {
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300163";
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData(stockID);
		List<StockDay> cStockDayList = cResultHistoryData.resultList;
		for(int i = 0; i < cStockDayList.size(); i++)  
        {  
			StockDay cStockDay = cStockDayList.get(i);  
			BLog.output("TEST", "date:%s open %.2f close %.2f\n", 
					cStockDay.date(), cStockDay.open(), cStockDay.close());
        } 
		
		BLog.output("TEST", "Main End\n");
	}
}
