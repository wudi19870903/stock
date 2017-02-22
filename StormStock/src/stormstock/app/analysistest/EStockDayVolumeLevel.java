package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayVolumeLevel {
	
	/**
	 * 
	 * @author wudi
	 * 检查当前位置是否是量活跃点
	 */
	public enum VOLUMELEVEL
	{
		ACTIVITY,
		UNKNOWN,
		INVALID,
	}
	public VOLUMELEVEL checkVolumeLevel(List<StockDay> list)
	{
		int iLast = list.size() - 1;
		// 计算中期均量， 去掉最低5个和最高5个后的平均值
		float aveVol60 = 0.0f;
		{
			int iBegin = iLast - 40;
			int iEnd = iLast;
			if(iBegin < 0)
			{
				return VOLUMELEVEL.INVALID;
			}
			List<Float> volList = new ArrayList<Float>();
			for(int i= iBegin; i <= iEnd; i++)  
	        {  
				StockDay cCurStockDay = list.get(i);
				volList.add(cCurStockDay.volume());
	        }
			Collections.sort(volList);
			volList.remove(0);
			volList.remove(0);
			volList.remove(0);
			volList.remove(0);
			volList.remove(0);
			volList.remove(volList.size()-1);
			volList.remove(volList.size()-1);
			volList.remove(volList.size()-1);
			volList.remove(volList.size()-1);
			volList.remove(volList.size()-1);
			for(int i= 0; i < volList.size(); i++)  
	        {  
				aveVol60 = aveVol60 + volList.get(i);
	        }
			aveVol60 = aveVol60/volList.size();
		}
		
		
		// 计算近日均量 去掉最低1个和最高1个后的平均值
		float aveVol10 = 0.0f;
		{
			int iBegin = iLast - 5;
			int iEnd = iLast;
			if(iBegin < 0)
			{
				return VOLUMELEVEL.INVALID;
			}
			List<Float> volList = new ArrayList<Float>();
			for(int i= iBegin; i <= iEnd; i++)  
	        {  
				StockDay cCurStockDay = list.get(i);
				volList.add(cCurStockDay.volume());
	        }
			Collections.sort(volList);
			volList.remove(0);
			volList.remove(volList.size()-1);
			for(int i= 0; i < volList.size(); i++)  
	        {  
				aveVol10 = aveVol10 + volList.get(i);
	        }
			aveVol10 = aveVol10/volList.size();
		}
		
		// 近期放量，进入活跃期
		if(aveVol10/aveVol60 > 1.3f)
		{
			return VOLUMELEVEL.ACTIVITY;
		}
		return VOLUMELEVEL.UNKNOWN;
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
		
		String stockID = "300166"; // 300163 300165 000401
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2012-01-01", "2013-01-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d)\n", stockID, list.size());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayVolumeLevel cEStockDayVolumeLevel = new EStockDayVolumeLevel();
		
		//cRunSimpleStockDayListTest.checkLiangHuoYue(list, list.size()-1);
		
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);
			
			int iCheckBegin = i-60;
			if(iCheckBegin<0) iCheckBegin = 0;
			int iCheckEnd = i;
			
			String beginDate = list.get(iCheckBegin).date();
			String endDate = list.get(iCheckEnd).date();
			
			List<StockDay> subList = StockUtils.subStockDayData(list,beginDate,endDate);
			
			VOLUMELEVEL volLev = cEStockDayVolumeLevel.checkVolumeLevel(subList);
			if (volLev == VOLUMELEVEL.ACTIVITY)
			{
				BLog.output("TEST", "CheckPoint %s\n", endDate);
				s_StockDayListCurve.markCurveIndex(i, "CP");
				
				//i = i + 10;
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayVolumeLevel.jpg");
}
