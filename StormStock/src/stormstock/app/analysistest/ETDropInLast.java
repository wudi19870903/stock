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
 * 检查日内分时是否满足尾盘急跌
 * @author wudi
 */

public class ETDropInLast {
	
	public static class ETDropInLastResult
	{
		public ETDropInLastResult()
		{
			bCheck = false;
		}
		boolean bCheck;
	}
	
	public static ETDropInLastResult get(List<StockTime> list, int iCheck)
	{
		ETDropInLastResult cETDropInLastResult = new ETDropInLastResult();
		
		// 全天震荡检查
		int iEnd = iCheck;
		int iHigh = StockUtils.indexStockTimeHigh(list, 0, iEnd);
		int iLow = StockUtils.indexStockTimeLow(list, 0, iEnd);
		StockTime cHigh = list.get(iHigh);
		StockTime cLow = list.get(iLow);
		float fAllWave = (cHigh.price - cLow.price)/cLow.price;
//		BLog.output("TEST", "cHigh(%s %.3f)  cLow(%s %.3f) fWave(%.4f)\n", 
//				cHigh.time, cHigh.price,
//				cLow.time, cLow.price, fAllWave);
		
		// 后1P4数据
		int i1P4Begin = iEnd/4 * 3;
		int iHigh1P4 = StockUtils.indexStockTimeHigh(list, i1P4Begin, iEnd);
		int iLow1P4 = StockUtils.indexStockTimeLow(list, i1P4Begin, iEnd);
		StockTime cHigh1P4 = list.get(iHigh1P4);
		StockTime cLow1P4 = list.get(iLow1P4);
		float fAllWave1P4 = (cHigh1P4.price - cLow1P4.price)/cLow1P4.price;
//		BLog.output("TEST", "cHigh1P4(%s %.3f)  cLow1P4(%s %.3f) fWave(%.4f)\n", 
//				cHigh1P4.time, cHigh1P4.price,
//				cLow1P4.time, cLow1P4.price, fAllWave1P4);
		
		// @ 后期震动大
		if(fAllWave1P4 > fAllWave*0.5)
		{
			
		}
		else
		{
			return cETDropInLastResult;
		}
		
		
		// @ 最后价格在低位
		float fEndPrice = list.get(iEnd).price;
		float fLowLimit = cLow.price + (cHigh.price - cLow.price)*0.35f;
//		BLog.output("TEST", "fEndPrice(%.3f) fLowLimit(%.3f)\n", fEndPrice, fLowLimit);
		if(fEndPrice < fLowLimit)
		{
			
		}
		else
		{
			return cETDropInLastResult;
		}

		cETDropInLastResult.bCheck = true;
		return cETDropInLastResult;
	}
	
	/*
	 * ********************************************************************
	 * Test
	 * ********************************************************************
	 */
	public static void main(String[] args) {
		BLog.output("TEST", "Main Begin\n");
		StockDataIF cStockDataIF = new StockDataIF();
		
		String stockID = "300165";
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-01-01", "2017-03-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		// 日检查
		for(int iDayCheck = 0; iDayCheck < list.size(); iDayCheck++)  
        {  
			StockDay cCurStockDay = list.get(iDayCheck);
			float fResultCheckPriceWave = EStockDayPriceWaveThreshold.get(list, iDayCheck);
			//BLog.output("TEST", "date(%s) fResultCheckPriceWave(%.3f)\n", cCurStockDay.date(), fResultCheckPriceWave);

			float fDrop = (cCurStockDay.close() - cCurStockDay.open())/cCurStockDay.open();
			if(fDrop<-fResultCheckPriceWave*0.5)
			//if(cCurStockDay.date().equals("2016-08-11"))
			{
				ResultDayDetail cResultDayDetail = cStockDataIF.getDayDetail(stockID, cCurStockDay.date(), "09:30:00", "15:00:00");
				if(cResultDayDetail.error == 0)
				{
					List<StockTime> listTime = cResultDayDetail.resultList;
					//BLog.output("TEST", "date(%s) listTime size(%d)\n", cCurStockDay.date(), listTime.size());
					
					s_StockTimeListCurve.setCurve(listTime);
					
					ETDropInLastResult cETDropInLastResult = ETDropInLast.get(listTime, listTime.size()-1);
					if(cETDropInLastResult.bCheck)
					{
						BLog.output("TEST", "CheckPoint %s \n", cCurStockDay.date());
					}
					
					s_StockTimeListCurve.generateImage();
				}

			}
        }
		
		BLog.output("TEST", "Main End\n");
	}
	
	public static StockTimeListCurve s_StockTimeListCurve = new StockTimeListCurve("ETDropInLast.jpg");

}
