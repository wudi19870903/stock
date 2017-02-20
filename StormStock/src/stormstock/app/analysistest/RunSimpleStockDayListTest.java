package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BImageCurve;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.tranbase.com.IEigenStock;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class RunSimpleStockDayListTest {

	public static class ResultCheckStockDayList
	{
		public ResultCheckStockDayList()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public float data;
	}
	public static ResultCheckStockDayList check(List<StockDay> list, int i)
	{
		ResultCheckStockDayList cResultCheck = new ResultCheckStockDayList();
		
		if(i<10)return cResultCheck;
		
		StockDay cCurStockDay = list.get(i);

		int iCheckB = i-20;
		int iCheckE = i;
		if(iCheckB<0)return cResultCheck;
		
		int indexHigh = StockUtils.indexHigh(list, iCheckB, iCheckE);
		int indexLow = StockUtils.indexLow(list, iCheckB, iCheckE);
		if(indexLow!= i)return cResultCheck;
		
		if(indexHigh < indexLow)
		{
			StockDay cStockDayHigh = list.get(indexHigh);
			StockDay cStockDayLow = list.get(indexLow);
			
			float zhangdie = (cStockDayLow.close() - cStockDayHigh.close())/cStockDayHigh.close();
			
			if(zhangdie <= -0.20)
			{
				String markName = String.format("%.3f", zhangdie);
				s_StockDayListCurve.markCurveIndex(indexLow, markName);
				
				cResultCheck.bCheck = true;
				cResultCheck.data = zhangdie;
				return cResultCheck;
			}
		}
		
		return cResultCheck;
	}
	
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300165";
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2011-01-01", "2012-01-01");
		List<StockDay> cStockDayList = cResultHistoryData.resultList;
		
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, cStockDayList.size());
		
		
		s_StockDayListCurve.setCurve(cStockDayList);
		for(int i = 0; i < cStockDayList.size(); i++)  
        {  
			StockDay cCurStockDay = cStockDayList.get(i);
			
			ResultCheckStockDayList cResultCheck = check(cStockDayList, i);

			if(cResultCheck.bCheck)
			{
				BLog.output("TEST", "date:%s  %.3f\n", 
						cCurStockDay.date(), cResultCheck.data);
				i = i + 20;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("RunSimpleStockDayListTest.jpg");
}
