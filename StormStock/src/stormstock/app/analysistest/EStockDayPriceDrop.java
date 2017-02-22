package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EStockDayVolumeLevel.VOLUMELEVEL;
import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayPriceDrop {

	public static class ResultCheckPriceDrop
	{
		public ResultCheckPriceDrop()
		{
			bCheck = false;
		}
		public boolean bCheck;
	}
	public ResultCheckPriceDrop checkPriceDrop(List<StockDay> list)
	{
		int iLast = list.size() - 1;
		
		ResultCheckPriceDrop cResultCheck = new ResultCheckPriceDrop();
		
		// 检查临近日
		int iBegin = iLast-5;
		int iEnd = iLast;
		if(iBegin<0)
		{
			return cResultCheck;
		}
		int indexHigh = StockUtils.indexHigh(list, iBegin, iEnd);
		float highPrice = list.get(indexHigh).high();
		int indexLow = StockUtils.indexLow(list, iBegin, iEnd);
		float lowPrice = list.get(indexLow).low();
		//BLog.output("TEST", " %d %d \n", indexHigh, indexLow);
		
		// 最低点在最高点后面 
		if(0 <= indexHigh && indexHigh < indexLow)
		{
		}
		else
		{
			return cResultCheck;
		}
		
		// 当前日为最低点
		if(indexLow == iLast)
		{
		}
		else
		{
			return cResultCheck;
		}
		
		// 最大跌幅
		float MaxDropRate = (lowPrice-highPrice)/highPrice;
		if(MaxDropRate < -0.10)
		{
		}
		else
		{
			return cResultCheck;
		}
		
		
		cResultCheck.bCheck = true;
		return cResultCheck;
	}
	
	/*
	 * ********************************************************************
	 * Test
	 * ********************************************************************
	 */
	public static void main(String[] args)
	{
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300166"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2012-01-01", "2013-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayPriceDrop cEStockDayPriceDrop = new EStockDayPriceDrop();
		
//		ResultCheckPriceDrop cResultCheckPriceDrop = cEStockDayPriceDrop.checkPriceDrop(list);
//		if(cResultCheckPriceDrop.bCheck)
//		{
//			BLog.output("TEST", "CheckPoint %s\n", list.get(list.size()-1).date());
//			s_StockDayListCurve.markCurveIndex(list.size()-1, "CP");
//		}
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
			
			int iCheckBegin = i-60;
			if(iCheckBegin<0) iCheckBegin = 0;
			int iCheckEnd = i;
			
			String beginDate = list.get(iCheckBegin).date();
			String endDate = list.get(iCheckEnd).date();
			
			List<StockDay> subList = StockUtils.subStockDayData(list,beginDate,endDate);
			
			ResultCheckPriceDrop cResultCheckPriceDrop = cEStockDayPriceDrop.checkPriceDrop(subList);
			if (cResultCheckPriceDrop.bCheck)
			{
				BLog.output("TEST", "CheckPoint %s\n", endDate);
				s_StockDayListCurve.markCurveIndex(i, "D");
				
				i = i + 10;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceDrop.jpg");
}
