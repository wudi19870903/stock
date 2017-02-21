package stormstock.app.analysistest;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class TestCurve {
	
	public static void test_StockTimeListCurve()
	{
		BLog.output("TEST", "TestStockTimeListCurve Begin\n");
		
		StockTimeListCurve cStockTimeListCurve = new StockTimeListCurve("TestStockTimeListCurve.jpg");
		
		// 输出一只股票所有日k数据
		StockDataIF cStockDataIF = new StockDataIF();
		String stockID = "300163";
		String date = "2016-12-13";
		ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, date, "09:30:00", "15:00:00");
		
		cStockTimeListCurve.setCurve(cResultDayDetail.resultList);
		cStockTimeListCurve.markCurveIndex(50, "markname 1111111");
		cStockTimeListCurve.markCurveIndex(100, "markname 22222222222222222");
		cStockTimeListCurve.generateImage();
		
		BLog.output("TEST", "TestStockTimeListCurve End\n");
	}
	
	public static void test_StockDayListCurve()
	{
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
	}
	
	public static void main(String[] args) {
		BLog.start();
		test_StockTimeListCurve();
		test_StockDayListCurve();
		BLog.stop();
	}
}
