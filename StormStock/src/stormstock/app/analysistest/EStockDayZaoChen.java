package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayZaoChen {
	/*
	 * ********************************************************************
	 * Test
	 * ********************************************************************
	 */
	public static void main(String[] args)
	{
		BLog.output("TEST", "Main Begin\n");
		StockDataIF cStockDataIF = new StockDataIF();

		String stockID = "002601"; // 300163 300165 000401
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2015-09-01", "2017-06-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d) end(%s)\n", 
				stockID, list.size(), list.get(list.size()-1).date());
		
		s_StockDayListCurve.setCurve(list);
		
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
			
			if(i>3)
			{
				StockDay cStockDayLeft = list.get(i-2);
				float fInreaseRatioLeft = StockUtils.GetInreaseRatio(list, i-2);
				float fInreaseRatioRefOpenLeft = StockUtils.GetInreaseRatioRefOpen(list, i-2);
				
				StockDay cStockDayMid = list.get(i-1);
				float fInreaseRatioMid = StockUtils.GetInreaseRatio(list, i);
				float fInreaseRatioRefOpenMid = StockUtils.GetInreaseRatioRefOpen(list, i);
				
				StockDay StockDayRight = list.get(i);
				float fInreaseRatioRight = StockUtils.GetInreaseRatio(list, i);
				float fInreaseRatioRefOpenRight = StockUtils.GetInreaseRatioRefOpen(list, i);
				
				// Left为阴线
				if(fInreaseRatioLeft<-0.05f && fInreaseRatioRefOpenLeft<-0.05f)
				{
					
				}
				else
				{
					continue;
				}
				
				// Right为阳线线
				if(fInreaseRatioRight>0.03f && fInreaseRatioRefOpenRight>0.03f)
				{
					// 收复大部分
					float fTest = (StockDayRight.close() - cStockDayLeft.open())/cStockDayLeft.open();
					if(fTest>-0.01)
					{
						
					}
					else
					{
						continue;
					}
				}
				else
				{
					continue;
				}
				
				
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
			}

        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayZaoChen.jpg");
}
