package stormstock.app.analysistest;

import java.util.List;

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
		public float maxDropRate;
	}
	// 单一检查iCheck是否满足短期急跌
	private ResultCheckPriceDrop checkPriceDrop_single(List<StockDay> list, int iCheck)
	{
		ResultCheckPriceDrop cResultCheck = new ResultCheckPriceDrop();
		
		// 检查临近日
		int iBegin = iCheck-5;
		int iEnd = iCheck;
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
		if(indexLow == iEnd)
		{
		}
		else
		{
			return cResultCheck;
		}
		
//		// 当日为中阴线
//		StockDay cStockDay = list.get(iCheck);
//		float fx = (cStockDay.close() - cStockDay.open())/cStockDay.open();
//		if(fx<-0.01f)
//		{
//		}
//		else
//		{
//			return cResultCheck;
//		}
		
		// 最大跌幅
		float MaxDropRate = (lowPrice-highPrice)/highPrice;
		if(MaxDropRate < -0.10)
		{
			cResultCheck.maxDropRate = MaxDropRate;
		}
		else
		{
			return cResultCheck;
		}
		
		
		cResultCheck.bCheck = true;
		return cResultCheck;
	}
	// 附近检查iCheck是否满足短期急跌（排除连续性干扰）
	public ResultCheckPriceDrop checkPriceDrop(List<StockDay> list, int iCheck)
	{
		ResultCheckPriceDrop cResultCheckPriceDrop = new ResultCheckPriceDrop();
		
		ResultCheckPriceDrop cResultCheckPriceDropSingle = checkPriceDrop_single(list, iCheck);
		if(cResultCheckPriceDropSingle.bCheck) // 首先满足iCheck就是CheckPoint
		{
			int iCheckOKLast = -1;
			int iBegin = iCheck - 20;
			int iEnd = iCheck;
			for(int i=iBegin; i<=iEnd; i++)
			{
				ResultCheckPriceDrop cResultCheckPriceDropBefore = checkPriceDrop_single(list, i);
				if(cResultCheckPriceDropBefore.bCheck)
				{
					iCheckOKLast = i;
					cResultCheckPriceDrop.maxDropRate = cResultCheckPriceDropBefore.maxDropRate;
					i=i+10;
				}
			}
			
			if(iCheckOKLast == iCheck)
			{
				cResultCheckPriceDrop.bCheck = true;
			}
		}

		return cResultCheckPriceDrop;
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
		
		String stockID = "300165"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2011-01-01", "2013-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayPriceDrop cEStockDayPriceDrop = new EStockDayPriceDrop();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			ResultCheckPriceDrop cResultCheckPriceDrop = cEStockDayPriceDrop.checkPriceDrop(list, i);
			if (cResultCheckPriceDrop.bCheck)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "D"+cCurStockDay.date());
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceDrop.jpg");
}
