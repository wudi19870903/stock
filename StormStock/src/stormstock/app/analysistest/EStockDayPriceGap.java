package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayPriceGap {
	
	public float checkPriceGap(List<StockDay> list, int iCheck)
	{
		float fPriceGap = 0.0f;
		
		// ºÏ≤È¡ŸΩ¸»’
		int iBegin = iCheck-1;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return fPriceGap;
		}
		
		StockDay cStockDayEnd = list.get(iEnd);
		StockDay cStockDayBegin = list.get(iBegin);
		
		if(cStockDayEnd.close() > cStockDayEnd.open())
		{
			if(cStockDayEnd.low() > cStockDayBegin.high())
			{
				fPriceGap = (cStockDayEnd.close() - cStockDayBegin.close())/cStockDayBegin.close();
			}
		}
		
		
		return fPriceGap;
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
		
		String stockID = "002474"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-01-01", "2017-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayPriceGap cEStockDayPriceGap = new EStockDayPriceGap();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			float fPriceGap = cEStockDayPriceGap.checkPriceGap(list, i);
			if (fPriceGap > 0.03)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "D");
				
				i=i+3;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceGap.jpg");
	
}
