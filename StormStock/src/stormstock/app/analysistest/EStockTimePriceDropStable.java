package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.tranengine_lite.ANLUtils;

public class EStockTimePriceDropStable {
	
	public float m_checkDieFu;
	
	public EStockTimePriceDropStable()
	{
		m_checkDieFu = -0.02f;
	}
	
	public static class ResultXiaCuoQiWen
	{
		public ResultXiaCuoQiWen()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public int iH;
		public int iL;
		public float xiacuo;
		public float low;
		public float qiwen;
	}
	public ResultXiaCuoQiWen checkXiaCuoQiWen_single(List<StockTime> list, int iCheck)
	{
		float param_checkDieFu = -0.02f; // m_checkDieFu
		
		ResultXiaCuoQiWen cResultXiaCuoQiWen = new ResultXiaCuoQiWen();
		
		// 检查临近x分钟
		int iCheckBegin = iCheck-20;
		int iCheckEnd = iCheck;
		if(iCheckBegin<0)
		{
			return cResultXiaCuoQiWen;
		}
		if(iCheckEnd - iCheckBegin < 10)
		{
			return cResultXiaCuoQiWen;
		}
		int indexHigh = StockUtils.indexStockTimeHigh(list, iCheckBegin, iCheckEnd);
		float highPrice = list.get(indexHigh).price;
		int indexLow = StockUtils.indexStockTimeLow(list, iCheckBegin, iCheckEnd);
		float lowPrice = list.get(indexLow).price;
		
		/*
		 * 1.最低点在最高点后面
		 * 2.对低点-最高点 在x分钟内
		 */
		if(indexHigh < indexLow 
				&& indexLow - indexHigh < 20)
		{
		}
		else
		{
			return cResultXiaCuoQiWen;
		}
		
		/*
		 * 最大跌幅在x点以上
		 */
		float MaxDropRate = (lowPrice-highPrice)/highPrice;
		if(MaxDropRate < param_checkDieFu)
		{
			cResultXiaCuoQiWen.xiacuo = MaxDropRate;
		}
		else
		{
			return cResultXiaCuoQiWen;
		}
		
		/*
		 * 最低点产生后x分钟不创新低
		 */
		if(iCheckEnd - indexLow >= 3)
		{
			cResultXiaCuoQiWen.bCheck = true;
			cResultXiaCuoQiWen.low = lowPrice;
			cResultXiaCuoQiWen.qiwen = list.get(iCheckEnd).price;
			cResultXiaCuoQiWen.iH = indexHigh;
			cResultXiaCuoQiWen.iL = indexLow;
			return cResultXiaCuoQiWen;
		}
		else
		{
			return cResultXiaCuoQiWen;
		}
		
	}
	
	
//	public boolean checkPoint(List<StockTime> list)
//	{
//		List<ResultXiaCuoQiWen> list_ResultXiaCuoQiWen = new ArrayList<ResultXiaCuoQiWen>();
//
//		// 从头开始查找下挫企稳点
//		int iOrigin = 0;
//		for(int i = 0; i < list.size(); i++)  
//        {  
//			StockTime cCurStockTime = list.get(i);
//			
//			ResultXiaCuoQiWen cResultXiaCuoQiWen = findXiaCuoQiWen(list, iOrigin, i);
//		
//			if(cResultXiaCuoQiWen.bCheck)
//			{
//
//				list_ResultXiaCuoQiWen.add(cResultXiaCuoQiWen);
//				
//				iOrigin = i;
//			}
//        } 
//		
//		int iFindCnt = list_ResultXiaCuoQiWen.size();
//		if(iFindCnt > 1)
//		{
//			ResultXiaCuoQiWen cResultXiaCuoQiWenFirst = list_ResultXiaCuoQiWen.get(0);
//			ResultXiaCuoQiWen cResultXiaCuoQiWenLast = list_ResultXiaCuoQiWen.get(iFindCnt-1);
//			
//			float check1 = (cResultXiaCuoQiWenLast.qiwen - cResultXiaCuoQiWenFirst.low)/cResultXiaCuoQiWenFirst.low;
//			if(check1 < -0.02)
//			{
//				s_StockTimeListCurve.markCurveIndex(cResultXiaCuoQiWenLast.index, "CP");
//				return true;
//			}
//				
//		}
//
//		return false;
//	}

	/*
	 * ********************************************************************
	 */
	
	public static void main(String[] args) {
		
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		String stockID = "300163";
		String date = "2017-01-16";
		ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, date, "09:30:00", "15:00:00");
		List<StockTime> list = cResultDayDetail.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockTimeListCurve.setCurve(list);
		
		EStockTimePriceDropStable cEStockTimePriceDropStable = new EStockTimePriceDropStable();
		cEStockTimePriceDropStable.m_checkDieFu = -0.03f;
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockTime cCurStockTime = list.get(i);
			
			ResultXiaCuoQiWen cResultXiaCuoQiWen = cEStockTimePriceDropStable.checkXiaCuoQiWen_single(list, i);
			if (cResultXiaCuoQiWen.bCheck)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockTime.time);
				s_StockTimeListCurve.markCurveIndex(i, "x");
				i=i+10;
			}
        } 

		s_StockTimeListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("EStockTimePriceDropStable.jpg");
}
