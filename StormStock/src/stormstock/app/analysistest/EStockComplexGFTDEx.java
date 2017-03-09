package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EStockComplexDYCheck.ResultDYCheck;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockComplexGFTDEx {
	
	public static class ResultComplexGFTDEx
	{
		public ResultComplexGFTDEx()
		{
		}
	}
	
	public enum GFTDStatus
	{
		SELL_CHECK_START,
		BUY_CHECK_START
	}
	public static ResultComplexGFTDEx get(String stockId, List<StockDay> list, int iCheck)
	{
		ResultComplexGFTDEx cResultComplexGFTDEx = new ResultComplexGFTDEx();
		
		int iBegin = iCheck-120;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return cResultComplexGFTDEx;
		}
		
		int N1 = 5;
		int N2 = 3;
		int N3 = 5;
		
		
		GFTDStatus eGFTDStatus;
		
		int udi_sum = 0;
		int udi_last = 0;
		
		int signal_sum = 0;
		int signal_last_index = 0;
		
		for(int i = iBegin + N1; i<=iEnd; i++ )
		{
			StockDay cStockDay_T = list.get(i);
			StockDay cStockDay_Tn1 = list.get(i-N1);
			// init udi
			if(i == iBegin + N1)
			{
				if(cStockDay_T.close() > cStockDay_Tn1.close())  udi_last = 1;
				if(cStockDay_T.close() < cStockDay_Tn1.close())  udi_last = -1;
				if(cStockDay_T.close() == cStockDay_Tn1.close())  udi_last = 0;
			}
			else
			{
				int udi = 0;
				if(cStockDay_T.close() > cStockDay_Tn1.close())  udi = 1;
				if(cStockDay_T.close() < cStockDay_Tn1.close())  udi = -1;
				if(cStockDay_T.close() == cStockDay_Tn1.close())  udi = 0;
				
				if(udi==udi_last)
				{
					udi_sum = udi_sum + udi;
				}
				else
				{
					// udi变化了
					if(udi_sum == N2)
					{
						eGFTDStatus = GFTDStatus.SELL_CHECK_START; // 卖出启动形成
						udi_sum = 0;
						
						s_StockDayListCurve.clearMark(i);
						s_StockDayListCurve.markCurveIndex(i, "SellCheck");
						
						signal_last_index = i;
						continue;
					}
					if(udi_sum == -N2)
					{
						eGFTDStatus = GFTDStatus.BUY_CHECK_START; // 买入启动形成
						udi_sum = 0;
						
						s_StockDayListCurve.clearMark(i);
						s_StockDayListCurve.markCurveIndex(i, "BuyCheck");
						
						signal_last_index = i;
						continue;
					}
					
					// 信号检查
					if(eGFTDStatus == GFTDStatus.BUY_CHECK_START)
					{
						StockDay cStockDay_TB1 = list.get(i-1);
						StockDay cStockDay_TB2 = list.get(i-2);
						StockDay cStockDay_Last
						if(cStockDay_T.close() >= cStockDay_TB2.high()
								&& cStockDay_T.high() > cStockDay_TB1.high()
								&& cStockDay_T.high() >)
						{
							
						}
					}
					

				}
					
					
				// 保存udi
				udi_last = udi;
			}
		}

		return cResultComplexGFTDEx;
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
		
		String stockID = "000153"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2015-09-01", "2017-03-01");
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
			}
			
			ResultComplexGFTDEx cResultComplexGFTDEx = EStockComplexGFTDEx.get(stockID, list, i);
//			if(cResultDYCheck.bCheck)
//			{
//				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
//			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockComplexGFTDEx.jpg");
}
