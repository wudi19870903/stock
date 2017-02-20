package stormstock.app.analysistest;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultStockTime;

public class TestStockTimeListCurve {
	public static void main(String[] args) {
		
		BLog.start();
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
		BLog.stop();
	}
}
