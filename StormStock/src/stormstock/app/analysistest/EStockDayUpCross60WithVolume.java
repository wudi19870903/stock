package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 
 * @author wudi
 * 很久都在60之下。然后带量上穿60
 */

public class EStockDayUpCross60WithVolume {
	
	public boolean checkUpCross60WithVolume(List<StockDay> list, int iCheck)
	{
		boolean bUpCross60WithVolume = false;
		
		// 检查临近日
		int iEndBefore = iCheck-1;
		int iEnd = iCheck;
		if(iEndBefore < 0)
		{
			return bUpCross60WithVolume;
		}
		StockDay cStockDayEnd = list.get(iEnd);
		StockDay cStockDayiEndBefore = list.get(iEndBefore);
		
		float M60 = StockUtils.GetMA(list, 60, iEnd);
		BLog.output("TEST", "(%s) %.3f\n", cStockDayEnd.date(), M60);
		
		if(cStockDayiEndBefore.close() < M60 
				&& cStockDayEnd.close() > 60)
		{
			bUpCross60WithVolume=true;
		}
		
		return bUpCross60WithVolume;
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
		
		String stockID = "600998"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2011-01-01", "2017-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayUpCross60WithVolume cEStockDayUpCross60WithVolume = new EStockDayUpCross60WithVolume();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			boolean bCheckUpCross60WithVolume = cEStockDayUpCross60WithVolume.checkUpCross60WithVolume(list, i);
			if (bCheckUpCross60WithVolume)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "D");
				i=i+30;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayUpCross60WithVolume.jpg");
}
