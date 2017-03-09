package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.app.analysistest.EStockDayPriceDrop.ResultCheckPriceDrop;
import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

/**
 * 计算某日的参考价格波动阙值 TSD
 * 说明：去除异常量的 排名前20波动日的均值
 * 备注：至少60交易日才能计算出
 * @author wudi
 *
 */
public class EStockDayPriceWaveThreshold {

	public static float get(List<StockDay> list, int iCheck)
	{
		float fResultCheckPriceWave = 0.0f;
		
		// 检查区间确定
		int iBegin = iCheck-60;
		int iEnd = iCheck;
		if(iBegin<0)
		{
			iBegin = 0;
		}
		if(iEnd-iBegin<20)
		{
			return fResultCheckPriceWave;
		}
		
		//波动排序后去除过大的波动
		List<Float> waveList = new ArrayList<Float>();
		for(int i=iBegin; i<=iEnd; i++)
		{
			StockDay cStockDay = list.get(i);
			waveList.add(cStockDay.wave());
		}
		if(waveList.size()>30)
		{
		}
		else
		{
			return fResultCheckPriceWave;
		}
		Collections.sort(waveList);
		for(int i=0; i<5; i++)
		{
			waveList.remove(waveList.size()-1);
		}
		
		// 靠前20天振幅的均值
		float fSum = 0.0f;
		int cnt = 0;
		for(int i=waveList.size()-1; i>waveList.size()-20; i--)
		{
			fSum = fSum + waveList.get(i);
			cnt++;
		}
		float wave = fSum/cnt;
		
//		int iW = (int)(wave*1000)-3;
//		iW = iW / 10;
//		cResultCheckPriceWave.wave = iW/(float)100.f;

		fResultCheckPriceWave = wave;
		return fResultCheckPriceWave;
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
		
		String stockID = "002366"; // 300163 300165
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2016-09-01", "2017-03-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
	
			float fResultCheckPriceWave = EStockDayPriceWaveThreshold.get(list, i);
			BLog.output("TEST", "Check stockID(%s) refWave(%.3f)\n", cCurStockDay.date(), 
					fResultCheckPriceWave);
			
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayPriceWave.jpg");
}
