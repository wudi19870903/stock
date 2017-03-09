package stormstock.app.analysistest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 区间上影线检查
 * @author wudi
 *
 */

//Eigen Day Interval 特征，日K，区间
public class EDIVirtualUpLine {
	
	public static class EDIVirtualUpLineResult
	{
		public EDIVirtualUpLineResult()
		{
			bCheck = false;
		}
		boolean bCheck;
		int iVirtualUpLineSharpA;
		int iVirtualUpLineSharpB;
	}
	
	public static EDIVirtualUpLineResult get(float fPriceWaveTSD, List<StockDay> list, int iCheck)
	{
		EDIVirtualUpLineResult cEDIVirtualUpLineResult = new EDIVirtualUpLineResult();
		
		//BLog.output("TEST", "fPriceWaveTSD %.4f\n", fPriceWaveTSD);
		
		// 测试70交易日
		int iBegin = iCheck-70;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			return cEDIVirtualUpLineResult;
		}
		
		int iVirtualUpLineSharpA = 0;
		int iVirtualUpLineSharpB = 0;
		for(int i=iBegin; i<=iEnd; i++)
		{
			StockDay cTmpStockDay = list.get(i);
			if(cTmpStockDay.wave() < fPriceWaveTSD*0.3) // 整体振幅过小忽略
			{
				continue;
			}

			float fTmpHigh = cTmpStockDay.high();
			float fTmpEntityHigh = cTmpStockDay.entityHigh();
			float fTmpEntityLow = cTmpStockDay.entityLow();
			float fTmpLow = cTmpStockDay.low();
	
			// 上影线相对百分比计算
			float fUpLineRatio = (fTmpHigh-fTmpEntityHigh)/fTmpEntityHigh;
			if(fUpLineRatio > fPriceWaveTSD*0.5)
			{
				iVirtualUpLineSharpA++;
				//BLog.output("TEST", "SA %s %.4f\n", cTmpStockDay.date(),fUpLineRatio );
			}
			else if(fUpLineRatio > fPriceWaveTSD*0.3)
			{
				iVirtualUpLineSharpB++;
				//BLog.output("TEST", "SB %s %.4f\n", cTmpStockDay.date(),fUpLineRatio );
			}
		}
		// BLog.output("TEST", "SA %d SB %d\n", iVirtualUpLineSharpA, iVirtualUpLineSharpB);
		if(iVirtualUpLineSharpA>3 && iVirtualUpLineSharpB>5)
		{
			
		}
		else
		{
			return cEDIVirtualUpLineResult;
		}
		
		cEDIVirtualUpLineResult.bCheck = true;
		cEDIVirtualUpLineResult.iVirtualUpLineSharpA = iVirtualUpLineSharpA;
		cEDIVirtualUpLineResult.iVirtualUpLineSharpB = iVirtualUpLineSharpB;
		
		return cEDIVirtualUpLineResult;
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
		
		String stockID = "300217"; // 300217 300227 300163 300165 00
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-01-01", "2017-03-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
			//BLog.output("TEST", "cCurStockDay %s \n", cCurStockDay.date());
			float fPriceWaveTSD = EStockDayPriceWaveThreshold.get(list, i);
			
			if(cCurStockDay.date().equals("2016-09-06"))
			{
				BThread.sleep(1);

				EDIVirtualUpLineResult cEDIVirtualUpLineResult = EDIVirtualUpLine.get(fPriceWaveTSD, list, i);
				if(cEDIVirtualUpLineResult.bCheck)
				{
					BLog.output("TEST", "SA %d SB %d\n", 
							cEDIVirtualUpLineResult.iVirtualUpLineSharpA, cEDIVirtualUpLineResult.iVirtualUpLineSharpB);
				}
			}
			

        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EDIVirtualUpLine.jpg");
}
