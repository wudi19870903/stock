package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BImageCurve;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultAllStockID;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranengine_lite.ANLUtils;

public class RunTest {
	public static int check(List<StockDay> list, int i)
	{
		if(i<10)return 0;
		
		StockDay cCurStockDay = list.get(i);

		int iCheckB = i-10;
		int iCheckE = i;
		if(iCheckB<0)return 0;
		
		int indexHigh = StockUtils.indexHigh(list, iCheckB, iCheckE);
		int indexLow = StockUtils.indexLow(list, iCheckB, iCheckE);
		if(indexLow!= i)return 0;
		
		if(indexHigh < indexLow)
		{
			StockDay cStockDayHigh = list.get(indexHigh);
			StockDay cStockDayLow = list.get(indexLow);
			
			float zhangdie = (cStockDayLow.close() - cStockDayHigh.close())/cStockDayHigh.close();
			
			BLog.output("TEST", "date:%s  %.3f\n", 
					cCurStockDay.date(), zhangdie);
			if(zhangdie <= -0.10)
			{
				markCurveIndex(indexLow, String.format("%.3f", zhangdie));
//				int iLowNext = indexLow +1;
//				StockDay cStockDayLowNext = list.get(iLowNext);
//				if(cStockDayLowNext.close() - cStockDayLow.close() > 0)
//					markCurveIndex(iLowNext, String.format("%.3f", zhangdie));
				
				return 20;
			}
		}
		
		return 0;
		

//		int iCheckE = i;
//		for(int iCheckB = i; iCheckB >= i-20 && iCheckB > 0; iCheckB--)
//		{
//			StockDay cStockDay = list.get(iCheckE);
//			
//			// @ 最高点与最低点在区间位置的判断
//			boolean bCheckHighLowIndex = false;
//			int indexHigh = StockUtils.indexHigh(list, iCheckB, iCheckE);
//			int indexLow = StockUtils.indexLow(list, iCheckB, iCheckE);
//			if(indexHigh>iCheckB && indexHigh<=(iCheckB+iCheckE)/2
//					&& indexLow > (iCheckB+iCheckE)/2 && indexLow < iCheckE)
//			{
//				bCheckHighLowIndex = true;
//			}
//			
//			if(bCheckHighLowIndex)
//			{
//				BLog.output("TEST", "date:%s open %.2f close %.2f\n", 
//						cStockDay.date(), cStockDay.open(), cStockDay.close());
//				markCurveIndex(i);
//			}
//		}
	}
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300165";
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2012-11-01", "2014-04-20");
		List<StockDay> cStockDayList = cResultHistoryData.resultList;
		setCurve(cStockDayList);
		for(int i = 0; i < cStockDayList.size(); i++)  
        {  
			int iPass = check(cStockDayList, i);
			i = i + iPass;
//			StockDay cStockDay = cStockDayList.get(i);  
//			BLog.output("TEST", "date:%s open %.2f close %.2f\n", 
//					cStockDay.date(), cStockDay.open(), cStockDay.close());
        } 
		
		GenerateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	
	/*
	 * ------------------------------------------------------------------
	 */
	public static void setCurve(List<StockDay> list)
	{
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cStockDay = list.get(i);
			
			CurvePoint cCurvePoint = new CurvePoint();
			cCurvePoint.m_x = i;
			cCurvePoint.m_y = cStockDay.close();
			sPoiList.add(cCurvePoint);
        }
	}
	public static void markCurveIndex(int index, String name)
	{
		for(int i = 0; i < sPoiList.size(); i++)  
        {  
			if (i == index)
			{
				sPoiList.get(i).m_marked = true;
				sPoiList.get(i).m_name = name;
			}
        }
	}
	public static void GenerateImage()
	{
		sImageCurve.writeLogicCurve(sPoiList, 1);
		sImageCurve.GenerateImage();
	}
	public static List<CurvePoint> sPoiList = new ArrayList<CurvePoint>();
	public static BImageCurve sImageCurve = new BImageCurve(1600,900,"RunTest.jpg");
}
