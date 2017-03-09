package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EDITryPress.EDITryPressResult;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 超跌反弹模型
 * @author wudi
 *
 * 应用于选择60日内跌幅最大企稳的股票
 * 持续持有20日，设定止盈止损，轮动运行。
 */

public class EStockComplexDYCheck {
	
	public static class ResultDYCheck
	{
		public ResultDYCheck()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public float po;
	}
	
	public static ResultDYCheck get(String stockId, List<StockDay> list, int iCheck)
	{
		ResultDYCheck cResultDYCheck = new ResultDYCheck();
		
		int iBegin = iCheck-60;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return cResultDYCheck;
		}
		
		StockDay cCurStockDay = list.get(iEnd);
		
		int iIndex60H = StockUtils.indexHigh(list, iBegin, iEnd);
		StockDay cStockDay60H = list.get(iIndex60H);
		int iIndex60L = StockUtils.indexLow(list, iBegin, iEnd);
		StockDay cStockDay60L = list.get(iIndex60L);
		
		BLog.output("TEST", "60 H(%s) L(%s)\n", cStockDay60H.date(), cStockDay60L.date());
		
		cResultDYCheck.bCheck = true;
		cResultDYCheck.po = 0;
		return cResultDYCheck;
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
		
		String stockID = "000151"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2010-09-01", "2014-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
			//BLog.output("TEST", "cCurStockDay %s \n", cCurStockDay.date());

			if(cCurStockDay.date().equals("2016-09-06"))
			{
				BThread.sleep(1);

				ResultDYCheck cResultDYCheck = EStockComplexDYCheck.get(stockID, list, i);
				if(cResultDYCheck.bCheck)
				{
					BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				}

			}
			

        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockComplexDYCheck.jpg");
}
