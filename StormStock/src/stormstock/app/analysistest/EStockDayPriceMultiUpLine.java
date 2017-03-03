package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EStockDayPriceDrop.ResultCheckPriceDrop;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayPriceMultiUpLine {
	
	public static class ResultCheckMultiUpLine
	{
		public ResultCheckMultiUpLine()
		{
			bCrossAllMA = false;
		}
		public boolean bCrossAllMA;
	}
	
	public static ResultCheckMultiUpLine checkMultiUpLine(List<StockDay> list, int iCheck)
	{
		ResultCheckMultiUpLine cResultCheckMultiUpLine = new ResultCheckMultiUpLine();
		
		int iBegin = iCheck-10;
		if(iBegin<0) 
			return cResultCheckMultiUpLine;
		
		int iEnd = iCheck;
		int iEndBefore1= iCheck-1;
		int iEndBefore2= iCheck-2;
		
		float fIncEnd = StockUtils.GetInreaseRatioRefOpen(list, iEnd);
		float fIncEndBefore1 = StockUtils.GetInreaseRatioRefOpen(list, iEndBefore1);
		float fIncEndBefore2 = StockUtils.GetInreaseRatioRefOpen(list, iEndBefore2);

		if(fIncEnd>=0 && fIncEndBefore1>=0 && fIncEndBefore2>=0)
		{
			
		}
		else
		{
			return cResultCheckMultiUpLine;
		}
		
		int iFirstUpLine = iEnd;
		for(int i=iEnd; i>iBegin; i--)
		{
			float fIncOpen = StockUtils.GetInreaseRatioRefOpen(list, i);
			if(fIncOpen >= 0)
			{
				iFirstUpLine = i;
			}
			else
			{
				break;
			}
		}
		StockDay cStockDayFirst = list.get(iFirstUpLine);
		StockDay cStockDayEnd = list.get(iEnd);
		// BLog.output("TEST", "%s %s\n", cStockDayFirst.date(), cStockDayEnd.date());
		
		float MA5_F = StockUtils.GetMA(list, 5, iFirstUpLine);
		float MA10_F = StockUtils.GetMA(list, 10, iFirstUpLine);
		float MA20_F = StockUtils.GetMA(list, 20, iFirstUpLine);
		float MA60_F = StockUtils.GetMA(list, 60, iFirstUpLine);
		
		float MA5_E = StockUtils.GetMA(list, 5, iEnd);
		float MA10_E = StockUtils.GetMA(list, 10, iEnd);
		float MA20_E = StockUtils.GetMA(list, 20, iEnd);
		float MA60_E = StockUtils.GetMA(list, 60, iEnd);
			
		if(cStockDayFirst.low() < MA5_F
				&& cStockDayFirst.low() < MA10_F
				&& cStockDayFirst.low() < MA20_F
				&& cStockDayFirst.low() < MA60_F
				)
		{
			
		}
		else
		{
			return cResultCheckMultiUpLine;
		}
		
		if(cStockDayEnd.close() > MA5_E
				&& cStockDayEnd.close() > MA10_E
				&& cStockDayEnd.close() > MA20_E
				&& cStockDayEnd.close() > MA60_E
				)
		{
			
		}
		else
		{
			return cResultCheckMultiUpLine;
		}
			
		
		cResultCheckMultiUpLine.bCrossAllMA = true;
		return cResultCheckMultiUpLine;
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
		
		String stockID = "300163"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2011-09-01", "2017-03-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			if(cCurStockDay.date().equals("2016-02-17"))
			{
				BThread.sleep(1);
			}
			
			ResultCheckMultiUpLine cResultCheckMultiUpLine = EStockDayPriceMultiUpLine.checkMultiUpLine(list, i);
			if (cResultCheckMultiUpLine.bCrossAllMA)
			{
				BLog.output("TEST", "### CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "D");
				//i=i+2;
			}

        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceMultiUpLine.jpg");

}
