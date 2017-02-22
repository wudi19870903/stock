package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.app.analysistest.EStockDayPriceDrop.ResultCheckPriceDrop;
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
		EStockDayPriceDrop cEStockDayPriceDrop = new EStockDayPriceDrop();
		ResultCheckPriceDrop cResultCheckPriceDrop = cEStockDayPriceDrop.checkPriceDrop(list);
		if (cResultCheckPriceDrop.bCheck)
		{
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300166"; // 300163 300165 000401
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2012-01-01", "2013-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		RunSimpleStockDayListTest cRunSimpleStockDayListTest = new RunSimpleStockDayListTest();
		
		//cRunSimpleStockDayListTest.checkLiangHuoYue(list, list.size()-1);
		
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
			
			int iCheckBegin = i-60;
			if(iCheckBegin<0) iCheckBegin = 0;
			int iCheckEnd = i;
			
			String beginDate = list.get(iCheckBegin).date();
			String endDate = list.get(iCheckEnd).date();
			
			List<StockDay> subList = StockUtils.subStockDayData(list,beginDate,endDate);
			
			//1
			EStockDayPriceDrop cEStockDayPriceDrop = new EStockDayPriceDrop();
			ResultCheckPriceDrop cResultCheckPriceDrop = cEStockDayPriceDrop.checkPriceDrop(subList);
			if (cResultCheckPriceDrop.bCheck)
			{
				BLog.output("TEST", "CheckPoint %s\n", endDate);
				s_StockDayListCurve.markCurveIndex(i, "D");
				i=i+10;
			}
			
			//2
			EStockDayVolumeLevel cEStockDayVolumeLevel = new EStockDayVolumeLevel();
			VOLUMELEVEL volLev = cEStockDayVolumeLevel.checkVolumeLevel(subList);
			if (volLev == VOLUMELEVEL.ACTIVITY)
			{
				BLog.output("TEST", "CheckPoint %s\n", endDate);
				s_StockDayListCurve.markCurveIndex(i, "A");
				i = i + 10;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("RunSimpleStockDayListTest.jpg");
}
