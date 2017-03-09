package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EDITryPress.EDITryPressResult;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EDIAbnormity {
	
	public static class EDIAbnormityResult
	{
		public EDIAbnormityResult()
		{
			bCheck = false;
		}
		boolean bCheck;
		int iAbnUp;
		int iAbnDown;
	}
	
	public static EDIAbnormityResult get(float fPriceWaveTSD, List<StockDay> list, int iCheck)
	{
		EDIAbnormityResult cEDIAbnormityResult = new EDIAbnormityResult();
		
		int iBegin = iCheck-70;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return cEDIAbnormityResult;
		}
		
		int iAbnormityPriceUpInr = 0;
		int iAbnormityPriceDownInr = 0;
		for(int i=iBegin; i<=iEnd; i++)
		{
			StockDay cTmpStockDay = list.get(i);
			float fInrRatio = StockUtils.GetInreaseRatio(list, i);
			if(fInrRatio > fPriceWaveTSD*0.6)
			{
				iAbnormityPriceUpInr++;
				//BLog.output("TEST", "AbnormityPriceInr Up %s \n", cTmpStockDay.date());
			}
			if(fInrRatio < -fPriceWaveTSD*0.6)
			{
				iAbnormityPriceDownInr++;
				//BLog.output("TEST", "AbnormityPriceInr Down %s \n", cTmpStockDay.date());
			}
		}
		
		cEDIAbnormityResult.bCheck = true;
		cEDIAbnormityResult.iAbnUp = iAbnormityPriceUpInr;
		cEDIAbnormityResult.iAbnDown = iAbnormityPriceDownInr;
		return cEDIAbnormityResult;
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
			float fPriceWaveTSD = EStockDayPriceWaveThreshold.get(list, i);
			
			if(cCurStockDay.date().equals("2016-09-06"))
			{
				BThread.sleep(1);

				EDIAbnormityResult cEDIAbnormityResult = EDIAbnormity.get(fPriceWaveTSD, list, i);
				if(cEDIAbnormityResult.bCheck)
				{
					BLog.output("TEST", "iAbnormityPriceInr Up(%d) Down(%d) \n", 
							cEDIAbnormityResult.iAbnUp, cEDIAbnormityResult.iAbnDown);
				}
			}
			

        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EDITryPress.jpg");
}
