package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EStockDayPriceDrop.ResultCheckPriceDrop;
import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayPriceWave {
	
	public static class ResultCheckPriceWave
	{
		public ResultCheckPriceWave()
		{
			bCheck = false;
		}
		public float refWave()
		{
			int iYearWaveUnit = iYearWave/100 - 1;
			if(iYearWaveUnit<0) iYearWaveUnit = 0;
			float fRefWave = 0.01f + iYearWaveUnit*0.008f;
			return fRefWave;
		}
		public boolean bCheck;
		/*
		 * 2016年参考
		 * 微小市值（流通20亿以下）
		 *     603737 = 510
		 *     002805 = 591
		 *     300421 = 576
		 *     300165 = 768
		 *     002810 = 405
		 * @小市值（流通20-50亿）
		 *     300583 = 825
		 *     600589 = 534
		 *     002374 = 627
		 *     002629 = 846
		 * 中小市值（流通50-200亿）
		 *     600096云天化 = 435
		 *     600845报信软件 = 618
		 *     600485时代新蔡 = 462
		 *     000739普洛药业 = 354
		 *     000666纺机  = 450
		 *     000822山东海化 = 414
		 * @中市值（流通200-500亿）
		 *     TCL集团000100 = 453
		 *     通化东宝600867 = 228
		 *     600606绿地控股  = 414
		 *     600699均胜电子  = 507
		 *     000686东北证券 = 408
		 *     600160巨化股份 = 495
		 * 中大市值（流通500-1000亿）
		 *     601225陕西煤业  = 552
		 *     600029南方航空  = 378
		 *     600011华能国际  = 153
		 *     600518康美药业  = 243
		 *     600663陆家嘴  = 585
		 *     600031三一重工 = 300
		 * @大市值（流通1000-2500亿）
		 *     华泰证券601688 = 402
		 *     广汽集团601238 = 378
		 *     格力电器000651 = 648
		 *     美的电器000333 = 444
		 *     中国太保601601 = 189
		 * 超大市值（流通2500亿以上）
		 *     中国石油601857 = 108
		 *     中国银行601988 = 93
		 *     中国中车601766 = 306
		 *     浦发银行600000 = 156
		 */
		public int iYearWave;
	}
	
	private ResultCheckPriceWave checkPriceWave_single(List<StockDay> list, int iCheck)
	{
		ResultCheckPriceWave cResultCheckPriceWave = new ResultCheckPriceWave();
		
		// 检查区间确定
		int iBegin = iCheck-200;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			iBegin = 0;
		}
		if(iEnd-iBegin<20)
		{
			return cResultCheckPriceWave;
		}
		
		//判断波动日 年化
		int iCntWaveB8 = 0;
		int iCntWaveB5 = 0;
		int iCntWaveB3 = 0;
		int iCntWaveB1 = 0;
		for(int i=iBegin+1; i<=iEnd; i++)
		{
			StockDay cStockDayBefore1 = list.get(i-1);
			StockDay cStockDay = list.get(i);
			float curZhangDieRate = (cStockDay.close() - cStockDayBefore1.close())/cStockDayBefore1.close();
			//BLog.output("TEST", "%s %.4f\n", cStockDay.date(), curZhangDieRate);
			float waveAbs = Math.abs(curZhangDieRate);
			if(waveAbs>=0.08)
			{
				iCntWaveB8++;
			}
			else if(waveAbs>=0.05)
			{
				iCntWaveB5++;
			}
			else if(waveAbs>=0.03)
			{
				iCntWaveB3++;
			}
			else if(waveAbs>=0.01)
			{
				iCntWaveB1++;
			}
		}
		
		int iYWC = (iCntWaveB8*4 + iCntWaveB5*3 + iCntWaveB3*2 + iCntWaveB1*1)*3;
//		BLog.output("TEST", "B8(%d) B5(%d) B3(%d) B1(%d) iYWC=%d\n", 
//				iCntWaveB8, iCntWaveB5, iCntWaveB3, iCntWaveB1, iYWC);
		cResultCheckPriceWave.bCheck = true;
		cResultCheckPriceWave.iYearWave = iYWC;
				
		return cResultCheckPriceWave;
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
		
		String stockID = "002474"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2015-01-01", "2017-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayPriceWave cEStockDayPriceWave = new EStockDayPriceWave();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			ResultCheckPriceWave cResultCheckPriceWave = cEStockDayPriceWave.checkPriceWave_single(list, i);
			BLog.output("TEST", "Check stockID(%s) refWave(%.3f)\n", cCurStockDay.date(), cResultCheckPriceWave.refWave());
			
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceWave.jpg");
}
