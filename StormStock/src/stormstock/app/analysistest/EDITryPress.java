package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EDIVirtualUpLine.EDIVirtualUpLineResult;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EDITryPress {
	
	public static class EDITryPressResult
	{
		public EDITryPressResult()
		{
			bCheck = false;
		}
		boolean bCheck;
		int iTry60;
		int iTry20;
	}
	
	public static EDITryPressResult get(List<StockDay> list, int iCheck)
	{
		EDITryPressResult cEDITryPressResult = new EDITryPressResult();
		
		// 测试70交易日
		int iBegin = iCheck-70;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return cEDITryPressResult;
		}
				
		int iE60 = 0;
		int iE20 = 0;
		for(int i=iBegin; i<=iEnd; i++)
		{
			StockDay cTmpStockDay = list.get(i);
			
			float fTmpHigh = cTmpStockDay.high();
			float fTmpEntityHigh = cTmpStockDay.entityHigh();
			float fTmpEntityMid = cTmpStockDay.midle();
			float fTmpEntityLow = cTmpStockDay.entityLow();
			float fTmpLow = cTmpStockDay.low();

			float fMA60 = StockUtils.GetMA(list, 60, i);
			float fMA20 = StockUtils.GetMA(list, 20, i);
			
			if(fTmpHigh >= fMA60 && fTmpEntityMid < fMA60)
			{
				//BLog.output("TEST", "X %s \n", cTmpStockDay.date());
				iE60++;
			}
			
			if(fTmpHigh >= fMA20 && fTmpEntityMid < fMA20)
			{
				//BLog.output("TEST", "Y %s \n", cTmpStockDay.date());
				iE20++;
			}
		} // 试探压力要有波顶端
		//BLog.output("TEST", "iE60 %d iE20 %d \n", iE60, iE20);
		if(iE60+iE20 > 10)
		{
		}
		else
		{
			return cEDITryPressResult;
		}
		
		cEDITryPressResult.bCheck = true;
		cEDITryPressResult.iTry60 = iE60;
		cEDITryPressResult.iTry20 = iE20;
		return cEDITryPressResult;
		// 试探压力要有波顶端
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
		
		String stockID = "300217"; // 300217 300227 300163 300165 00
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-01-01", "2017-03-01");
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

				EDITryPressResult cEDITryPressResult = EDITryPress.get(list, i);
				if(cEDITryPressResult.bCheck)
				{
					BLog.output("TEST", "iE60 %d iE20 %d \n", cEDITryPressResult.iTry60, cEDITryPressResult.iTry20);
				}
			}
			

        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EDITryPress.jpg");
}
