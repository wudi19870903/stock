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

public class RunSimpleStockTimeListTest {
	
	public float m_checkDieFu;
	
	public RunSimpleStockTimeListTest()
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
		public int index;
		public float xiacuo;
		public float low;
		public float qiwen;
	}
	public ResultXiaCuoQiWen findXiaCuoQiWen(List<StockTime> list, int iOrigin, int iCheckEnd)
	{
		ResultXiaCuoQiWen cResultXiaCuoQiWen = new ResultXiaCuoQiWen();
		
		// 检查临近x分钟
		int iCheckBegin = iCheckEnd-30;
		if(iCheckBegin<iOrigin)iCheckBegin=iOrigin;
		if(iCheckEnd<iOrigin)iCheckEnd=iOrigin;
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
		boolean bHighLowPos = false;
		if(0 <= indexHigh 
				&& indexHigh < indexLow 
				&& indexLow - indexHigh < 30)
		{
			bHighLowPos = true;
		}
		
		/*
		 * 最大跌幅在x点以上
		 */
		boolean bMaxDropRate = false;
		if(bHighLowPos)
		{
			float MaxDropRate = (lowPrice-highPrice)/highPrice;
			if(MaxDropRate < m_checkDieFu)
			{
				bMaxDropRate = true;
				cResultXiaCuoQiWen.xiacuo = MaxDropRate;
			}
		}
		
		/*
		 * 最低点产生后x分钟不创新低
		 */
		boolean bQiWen = false;
		if(bMaxDropRate)
		{
			if(iCheckEnd - indexLow >= 3)
			{
				s_StockTimeListCurve.markCurveIndex(indexHigh, "H");
				s_StockTimeListCurve.markCurveIndex(indexLow, "L");
				s_StockTimeListCurve.markCurveIndex(iCheckEnd, "X");
				bQiWen = true;
				cResultXiaCuoQiWen.bCheck = true;
				cResultXiaCuoQiWen.index = iCheckEnd;
				cResultXiaCuoQiWen.low = lowPrice;
				cResultXiaCuoQiWen.qiwen = list.get(iCheckEnd).price;
			}
		}
		
		return cResultXiaCuoQiWen;
	}
	
	
	public boolean checkPoint(List<StockTime> list)
	{
		List<ResultXiaCuoQiWen> list_ResultXiaCuoQiWen = new ArrayList<ResultXiaCuoQiWen>();

		// 从头开始查找下挫企稳点
		int iOrigin = 0;
		for(int i = 0; i < list.size(); i++)  
        {  
			StockTime cCurStockTime = list.get(i);
			
			ResultXiaCuoQiWen cResultXiaCuoQiWen = findXiaCuoQiWen(list, iOrigin, i);
		
			if(cResultXiaCuoQiWen.bCheck)
			{

				list_ResultXiaCuoQiWen.add(cResultXiaCuoQiWen);
				
				iOrigin = i;
			}
        } 
		
		int iFindCnt = list_ResultXiaCuoQiWen.size();
		if(iFindCnt > 1)
		{
			ResultXiaCuoQiWen cResultXiaCuoQiWenFirst = list_ResultXiaCuoQiWen.get(0);
			ResultXiaCuoQiWen cResultXiaCuoQiWenLast = list_ResultXiaCuoQiWen.get(iFindCnt-1);
			
			float check1 = (cResultXiaCuoQiWenLast.qiwen - cResultXiaCuoQiWenFirst.low)/cResultXiaCuoQiWenFirst.low;
			if(check1 < -0.02)
			{
				s_StockTimeListCurve.markCurveIndex(cResultXiaCuoQiWenLast.index, "CP");
				return true;
			}
				
		}

		return false;
	}

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
		
		RunSimpleStockTimeListTest cRunSimpleStockTimeListTest = new RunSimpleStockTimeListTest();
		cRunSimpleStockTimeListTest.m_checkDieFu = -0.03f;
		
		if(false) // 整个list调用一次
		{
			boolean bCheckPoint = cRunSimpleStockTimeListTest.checkPoint(list);
			BLog.output("TEST", "CheckPoint %b\n", bCheckPoint);
		}
		
		if(true) // 模拟真实 sublist
		{
			String beginTime = list.get(0).time;
			for(int i = 0; i < list.size(); i++)  
	        {  
				StockTime cCurStockTime = list.get(i);
				String endTime = cCurStockTime.time;
				List<StockTime> subList = StockUtils.subStockTimeData(list,beginTime,endTime);
				
				boolean bCheckPoint = cRunSimpleStockTimeListTest.checkPoint(subList);
				if (bCheckPoint)
				{
					BLog.output("TEST", "CheckPoint %s\n", endTime);
					
//					i=i+10;
//					if(i >= list.size() -1)
//					{
//						i = list.size() -1;
//					}
//					beginTime = list.get(i).time;
					
					break;
				}
	        } 
		}

		s_StockTimeListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("RunSimpleStockTimeListTest.jpg");
}
