package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockTime;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultDayDetail;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 检查日内分时满足当天高位运行
 * @author wudi
 *
 */
public class EStockTimePriceHighRun {
	
	public static boolean checkHighRun(String StockId, String date, List<StockTime> list)
	{
		boolean bResult = false;
		
		// 查找昨日收盘价
		float fBeforeClose = 0.0f;
		StockDataIF cStockDataIF = new StockDataIF();
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData(StockId);
		if(cResultHistoryData.error == 0)
		{
			int iBefore = StockUtils.indexDayKBeforeDate(cResultHistoryData.resultList, date, false);
			if(iBefore>=0)
			{
				StockDay cStockDay = cResultHistoryData.resultList.get(iBefore);
				fBeforeClose = cStockDay.close();
				//BLog.output("TEST", " BeforeDate %s fBeforeClose %.3f\n", cStockDay.date(), fBeforeClose);
			}
		}
		else
		{
			return bResult;
		}
		
		// 今日收盘价
		float fClose = 0.0f;
		int iBegin = 0;
		int iClose = list.size()-1;
		fClose = list.get(iClose).price;
		//BLog.output("TEST", "%s fClose %.3f\n", date, fClose);
		
		// 今日最高点
		int iHigh = StockUtils.indexStockTimeHigh(list, 0, list.size()-1);
		float fHigh = list.get(iHigh).price;
		//BLog.output("TEST", "%s fHigh %.3f\n", date, fHigh);
		
		// 今日收阳线
		if(fClose>fBeforeClose)
		{
		}
		else
		{
			return bResult;
		}
		

		
		// 当日全天价格大多以上在1/3增幅以上
		float checkSep13 = (fHigh - fBeforeClose)/3 + fBeforeClose;
		int iCheckCnt = 0;
		for(int i=0;i<list.size();i++)
		{
			float curPrice = list.get(i).price;
			if(curPrice>checkSep13)
			{
				iCheckCnt++;
			}
		}
		if(iCheckCnt > list.size()*0.66)
		{
		}
		else
		{
			return bResult;
		}
		//BLog.output("TEST", "%s checkSep13 %.3f iCheckCnt %d\n", date, checkSep13, iCheckCnt);
		// 当日下午价格全在在1/2增幅以上
		float checkSep12 = (fHigh - fBeforeClose)/2 + fBeforeClose;
		iCheckCnt = 0;
		for(int i=list.size()/2;i<list.size();i++)
		{
			float curPrice = list.get(i).price;
			if(curPrice<checkSep12)
			{
				iCheckCnt++;
			}
		}
		if(iCheckCnt==0)
		{
		}
		else
		{
			return bResult;
		}
		
		
		bResult = true;
		return bResult;
	}
	
	/*
	 * ********************************************************************
	 * Test
	 * ********************************************************************
	 */
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300217"; // 300163 300165 000401 600439
		String date = "2016-09-06";
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-09-01", "2017-03-01");
		List<StockDay> listDay = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, listDay.size());
		for(int iDayCheck = 0; iDayCheck < listDay.size(); iDayCheck++)  
        {  
			StockDay cCurStockDay = listDay.get(iDayCheck);
			//BLog.output("TEST", "date %s\n", cCurStockDay.date());
			if(cCurStockDay.date().equals(date))
			{
				
				/// 查看固定某日
				ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, date, "09:30:00", "15:00:00");
				List<StockTime> listTime = cResultDayDetail.resultList;
				
				
				s_StockTimeListCurve.setCurve(listTime);
				//////////////////////////////////////////////////////////////////////
				boolean bCheck = EStockTimePriceHighRun.checkHighRun(stockID, date, listTime);
				if(bCheck)
				{
					BLog.output("TEST", "CheckPoint %s\n", date);
				}
				
				//////////////////////////////////////////////////////////////////////
				s_StockTimeListCurve.generateImage();
				break;
			}
        }

		
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("EStockTimePriceHighRun.jpg");
}
