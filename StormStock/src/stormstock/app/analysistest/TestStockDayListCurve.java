package stormstock.app.analysistest;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class TestStockDayListCurve {
	public static void main(String[] args) {
		
		BLog.start();
		BLog.output("TEST", "TestStockDayListCurve Begin\n");
		
		StockDayListCurve cStockDayListCurve = new StockDayListCurve("TestStockDayListCurve.jpg");
		
		// 输出一只股票所有日k数据
		StockDataIF cStockDataIF = new StockDataIF();
		String stockID = "600020";
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData(stockID);
		
		cStockDayListCurve.setCurve(cResultHistoryData.resultList);
		cStockDayListCurve.markCurveIndex(100, "markname 1111111");
		cStockDayListCurve.markCurveIndex(200, "markname 22222222222222222");
		cStockDayListCurve.generateImage();
		
		BLog.output("TEST", "TestStockDayListCurve End\n");
		BLog.stop();
	}
}
