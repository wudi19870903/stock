package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayPriceTrend {
	/**
	 * 
	 * @author wudi
	 * 检查当前位置月级别趋势
	 */
	public enum PRICETREND
	{
		UPWARD,
		DOWNWARD,
		UNKNOWN,
	}
	// 单一检查iCheck是否满足短期急跌
	private PRICETREND checkPriceTrend(List<StockDay> list, int iCheck)
	{
		PRICETREND ePriceTrend = PRICETREND.UNKNOWN;
		
		if(iCheck < 20)
		{
			return ePriceTrend;
		}
		
		float fCheckBegin = StockUtils.GetMA(list, 20, iCheck-10);
		float fCheckMid = StockUtils.GetMA(list, 20, iCheck-5);
		float fCheckEnd = StockUtils.GetMA(list, 20, iCheck);
		
		if(fCheckEnd > fCheckMid && fCheckMid > fCheckBegin)
		{
			ePriceTrend = PRICETREND.UPWARD;
		}
		
		
		return ePriceTrend;
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
		
		String stockID = "002601"; // 300163 300165 000401
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2015-09-01", "2017-06-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayPriceTrend cEStockDayPriceTrend = new EStockDayPriceTrend();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			PRICETREND eePriceTrend = cEStockDayPriceTrend.checkPriceTrend(list, i);
			if (eePriceTrend == PRICETREND.UPWARD)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "");
				
				//i=i+20;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceTrend.jpg");
}
