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

public class ETDropStable {
	
	/**
	 * 
	 * @author wudi
	 * 检查日内当前位置是否是下挫企稳点
	 */
	public static class ResultXiaCuoQiWen
	{
		public ResultXiaCuoQiWen()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public int iHigh;
		public float highPrice;
		public int iLow;
		public float lowPrice;
		public float dropRate;
		public float getLevel()
		{
			int timeSpan = iLow-iHigh;
			if(timeSpan>0)
			{
				return (highPrice-lowPrice)/timeSpan;
			}
			return 0;
		}
	}
	public static ResultXiaCuoQiWen checkXiaCuoQiWen_single(List<StockTime> list, int iCheck)
	{
		float param_checkDieFu = -0.02f; // 检查跌幅
		float param_checkMaxTimeSpan = 20; // 最大检查时间段
		float param_checkMaxHighLowTimeSpan = 10; // 高低点最大差距时间段
		
		ResultXiaCuoQiWen cResultXiaCuoQiWen = new ResultXiaCuoQiWen();
		
		// 检查临近x分钟
		int iCheckSpan = 5;
		int iCheckBegin = iCheck;
		int iCheckEnd = iCheck;
		while(true)
		{
			iCheckBegin = iCheckBegin - iCheckSpan;
			
			/*
			 *  时间段检查
			 */
			if(iCheckBegin<0 || iCheckEnd-iCheckBegin>param_checkMaxTimeSpan) 
			{
				break;
			}
			//BLog.output("TEST", "iCheckBegin %s iCheckEnd %s\n", list.get(iCheckBegin).time, list.get(iCheckEnd).time);

			int indexHigh = StockUtils.indexStockTimeHigh(list, iCheckBegin, iCheckEnd);
			float highPrice = list.get(indexHigh).price;
			int indexLow = StockUtils.indexStockTimeLow(list, iCheckBegin, iCheckEnd);
			float lowPrice = list.get(indexLow).price;
			//BLog.output("TEST", "    H-%.3f(%s) L-%.3f(%s)\n", highPrice, list.get(indexHigh).time, lowPrice, list.get(indexLow).time);
			
			/*
			 * 1.最低点在最高点后面
			 * 2.对低点-最高点 在x分钟内
			 */
			if(indexHigh < indexLow 
					&& indexLow - indexHigh < param_checkMaxHighLowTimeSpan)
			{
			}
			else
			{
				continue;
			}
			
			/*
			 * 最大跌幅在x点以上
			 */
			float MaxDropRate = (lowPrice-highPrice)/highPrice;
			if(MaxDropRate < param_checkDieFu)
			{
				cResultXiaCuoQiWen.dropRate = MaxDropRate;
			}
			else
			{
				continue;
			}
			
			/*
			 * 最低点产生后x分钟不创新低
			 */
			if(iCheckEnd - indexLow >= 2 && iCheckEnd - indexLow <= 5)
			{
				cResultXiaCuoQiWen.bCheck = true;
				cResultXiaCuoQiWen.highPrice = highPrice;
				cResultXiaCuoQiWen.lowPrice = lowPrice;
				cResultXiaCuoQiWen.iHigh = indexHigh;
				cResultXiaCuoQiWen.iLow = indexLow;
				break;
			}
			else
			{
				continue;
			}
		}
		
		// 检查完毕，没有找到
		return cResultXiaCuoQiWen;
	}
	
	// iCheck是二次下挫企稳
	public static ResultXiaCuoQiWen checkXiaCuoQiWen_2Times(List<StockTime> list, int iCheck)
	{
		ResultXiaCuoQiWen cResultXiaCuoQiWen = new ResultXiaCuoQiWen();
		
		ResultXiaCuoQiWen cResultXiaCuoQiWen_tmp = checkXiaCuoQiWen_single(list, iCheck);
		if (cResultXiaCuoQiWen_tmp.bCheck) // 首先满足本次就是下挫企稳点
		{
			int iCPCnt = 0;
			int iCheckOKLast = -1;
			int iBegin = 0;
			int iEnd = iCheck;
			for(int i=iBegin; i<=iEnd; i++)
			{
				ResultXiaCuoQiWen cResultXiaCuoQiWenBefore = checkXiaCuoQiWen_single(list, i);
				if(cResultXiaCuoQiWenBefore.bCheck)
				{
					iCheckOKLast = i;
					iCPCnt++;
					i=i+10;
				}
			}
			
			if(iCheckOKLast == iCheck && iCPCnt==2)
			{
				cResultXiaCuoQiWen.bCheck = true;
			}
		}
		
		return cResultXiaCuoQiWen;
	}
	
	/*
	 * ********************************************************************
	 * Test
	 * ********************************************************************
	 */
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300163";
		String date = "2016-12-13";
		ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, date, "09:30:00", "15:00:00");
		List<StockTime> list = cResultDayDetail.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockTimeListCurve.setCurve(list);
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockTime cCurStockTime = list.get(i);
			
			//if(cCurStockTime.time.equals("09:39:00"))
			{
				ResultXiaCuoQiWen cResultXiaCuoQiWen = ETDropStable.checkXiaCuoQiWen_2Times(list, i);
				if (cResultXiaCuoQiWen.bCheck)
				{
					BLog.output("TEST", "CheckPoint %s level:%.3f\n", cCurStockTime.time, cResultXiaCuoQiWen.getLevel());
					s_StockTimeListCurve.markCurveIndex(i, "x");
					i=i+5;
				}
			}
        } 

		s_StockTimeListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("EStockTimePriceDropStable.jpg");
}
