package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 中期回调偏离参数
 * 
 * 计算公式：
 *  中期最高价MH 最低价ML
 *  近期平均价 NE
 *  当前价C
 *  X = (C-MH) + (C-ML) + (C-NE)
 *  X = (C-MH)/MH + (C-ML)/ML + (C-NE)/NE
 */
public class EStockDayPriceMidleDeviateParam {
	
	public static float checkMidleDeviateParam(List<StockDay> list, int iCheck)
	{
		float fMidleDeviateParam = 0.0f;
		
		int iBegin = iCheck-60;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			iBegin = 0;
		}
		if(iEnd-iBegin < 10)
		{
			return fMidleDeviateParam;
		}
		
		int indexHigh =  StockUtils.indexHigh(list, iBegin, iEnd);
		int indexLow =  StockUtils.indexLow(list, iBegin, iEnd);
		float MH = list.get(indexHigh).midle();
		float ML = list.get(indexLow).midle();
		float NE = StockUtils.GetMA(list, 20, iCheck);
		float C = list.get(iCheck).midle();
		
		//BLog.output("TEST", "MH(%.3f) ML(%.3f) NE(%.3f) C(%.3f)\n", MH, ML, NE, C);
			
		float X = (C-MH) + (C-ML) + (C-NE);
		//float X = (C-MH)/MH + (C-ML)/ML + (C-NE)/NE;
		
		fMidleDeviateParam = X;
		return fMidleDeviateParam;
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
		
		String stockID = "300181"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-01-01", "2017-03-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			float fMidleDeviateParam = EStockDayPriceMidleDeviateParam.checkMidleDeviateParam(list, i);

			BLog.output("TEST", "%s %.3f\n", cCurStockDay.date(), fMidleDeviateParam);	
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceGap.jpg");
}
