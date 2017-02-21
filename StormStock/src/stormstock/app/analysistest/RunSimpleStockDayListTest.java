package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.app.analysistest.EStockDayVolumeLevel.VOLUMELEVEL;
import stormstock.fw.base.BImageCurve;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.tranbase.com.IEigenStock;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class RunSimpleStockDayListTest {
	
	public boolean checkPoint(List<StockDay> list)
	{
		EStockDayVolumeLevel cEStockDayVolumeLevel = new EStockDayVolumeLevel();
		VOLUMELEVEL volLev = cEStockDayVolumeLevel.checkVolumeLevel(list, list.size()-1);
		if (volLev == VOLUMELEVEL.ACTIVITY)
		{
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300163";
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-01-01", "2017-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());

		s_StockDayListCurve.setCurve(list);
		
		RunSimpleStockDayListTest cRunSimpleStockDayListTest = new RunSimpleStockDayListTest();
		
//		if(true) // 整个list调用一次
//		{
//			boolean bCheckPoint = cRunSimpleStockDayListTest.checkPoint(list);
//			BLog.output("TEST", "CheckPoint %b\n", bCheckPoint);
//		}
		
		if(true) // 模拟真实 sublist
		{
			String beginDate = list.get(0).date();
			for(int i = 0; i < list.size(); i++)  
	        {  
				StockDay cCurStockDay = list.get(i);
				String endDate = cCurStockDay.date();
				List<StockDay> subList = StockUtils.subStockDayData(list,beginDate,endDate);
				
				boolean bCheckPoint = cRunSimpleStockDayListTest.checkPoint(subList);
				if (bCheckPoint)
				{
					BLog.output("TEST", "CheckPoint %s\n", endDate);
					s_StockDayListCurve.markCurveIndex(i, "CP");
				}
	        } 
		}

		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("RunSimpleStockDayListTest.jpg");
}
