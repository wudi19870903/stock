package stormstock.app.analysistest;

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
	
	public static void findXiaCuoQiWen(List<StockTime> list, int index)
	{
		// 检查临近x分钟
		int iCheckBegin = index-30;
		int iCheckEnd= index;
		if(iCheckBegin<0) return;
		
		int indexHigh = StockUtils.indexStockTimeHigh(list, iCheckBegin, iCheckEnd);
		float highPrice = list.get(indexHigh).price;
		int indexLow = StockUtils.indexStockTimeLow(list, iCheckBegin, iCheckEnd);
		float lowPrice = list.get(indexLow).price;
		
		/*
		 * 1.最低点在最高点后面
		 * 2.对低点-最高点 在x分钟内
		 */
		boolean bHighLowPos = false;
		if(0 <= indexHigh && indexHigh < indexLow && indexLow - indexHigh < 20)
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
			if(MaxDropRate < -0.03f)
			{
				s_StockTimeListCurve.markCurveIndex(indexHigh, "H");
				s_StockTimeListCurve.markCurveIndex(indexLow, "L");
				bMaxDropRate = true;
			}
		}
		
		/*
		 * 最低点产生后5分钟不创新低
		 */
		boolean bQiWen = false;
		if(bMaxDropRate)
		{
			if(iCheckEnd - indexLow > 5)
			{
				s_StockTimeListCurve.markCurveIndex(iCheckEnd, "XXX");
				bQiWen = true;
			}
		}
		

		

	}
	
	public static class ResultCheckStockTimeList
	{
		public ResultCheckStockTimeList()
		{
			bCheck = false;
		}
		public boolean bCheck;
		public float data;
	}
	public static ResultCheckStockTimeList check(List<StockTime> list, int index)
	{
		ResultCheckStockTimeList cResultCheck = new ResultCheckStockTimeList();
		
		for(int i = 0; i < index; i++)
		{
			findXiaCuoQiWen(list, i);
		}
		
		return cResultCheck;
	}
	
	public static void main(String[] args) {
		
		BLog.output("TEST", "Main Begin\n");
		
		StockDataIF cStockDataIF = new StockDataIF();
		String stockID = "300163";
		String date = "2016-01-04";
		ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, date, "09:30:00", "15:00:00");
		List<StockTime> list = cResultDayDetail.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		
		s_StockTimeListCurve.setCurve(list);
		for(int i = 0; i < list.size(); i++)  
        {  
			StockTime cCurStockTime = list.get(i);
			
			ResultCheckStockTimeList cResultCheck = check(list, i);

			if(cResultCheck.bCheck)
			{
				BLog.output("TEST", "date:%s  %.3f\n", 
						cCurStockTime.time, cResultCheck.data);
			}
        } 
		
		s_StockTimeListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("RunSimpleStockTimeListTest.jpg");
}
