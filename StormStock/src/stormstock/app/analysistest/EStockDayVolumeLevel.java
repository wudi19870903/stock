package stormstock.app.analysistest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockUtils;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;

public class EStockDayVolumeLevel {
	
	/**
	 * 
	 * @author wudi
	 * 检查当前位置近期是否是量能活跃点
	 */
	public enum VOLUMELEVEL
	{
		ACTIVE, // 活跃的
		UNACTIVE, // 不活跃
		DEATH, // 死亡的
		UNKNOWN,
		INVALID,
	}
	public VOLUMELEVEL checkVolumeLevel(List<StockDay> list, int iCheck)
	{		
		// 计算中长期期均量， 去掉最低5个和最高5个后的平均值
		float aveVol60 = 0.0f;
		{
			int iBegin = iCheck - 60;
			int iEnd = iCheck;
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
		
		// 计算中期均量， 去掉最低5个和最高5个后的平均值
		float aveVol40 = 0.0f;
		{
			int iBegin = iCheck - 40;
			int iEnd = iCheck;
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
				aveVol40 = aveVol40 + volList.get(i);
	        }
			aveVol40 = aveVol40/volList.size();
		}
		
		
		// 计算近日均量 去掉最低1个和最高1个后的平均值
		float aveVol10 = 0.0f;
		{
			int iBegin = iCheck - 5;
			int iEnd = iCheck;
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
		
		// 死亡成交量判断
		StockDay cStockDay = list.get(iCheck);
		if(cStockDay.volume()/aveVol10 < 0.6
				&& cStockDay.volume()/aveVol40 < 0.6
				&& cStockDay.volume()/aveVol60 < 0.6)
		{
			return VOLUMELEVEL.DEATH;
		}
		
		// 活跃成交量判断
		if(aveVol10/aveVol40 > 1.2f)
		{
			return VOLUMELEVEL.ACTIVE;
		}
		
		if(aveVol10/aveVol60 < 0.7f)
		{
			return VOLUMELEVEL.UNACTIVE;
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

		String stockID = "300168"; // 300163 300165 000401
		ResultHistoryData cResultHistoryData = 
				cStockDataIF.getHistoryData(stockID, "2015-09-01", "2017-03-01");
		List<StockDay> list = cResultHistoryData.resultList;
		BLog.output("TEST", "Check stockID(%s) list size(%d) end(%s)\n", 
				stockID, list.size(), list.get(list.size()-1).date());
		
		s_StockDayListCurve.setCurve(list);
		
		EStockDayVolumeLevel cEStockDayVolumeLevel = new EStockDayVolumeLevel();
		
		for(int i = 0; i < list.size(); i++)  
        {  
			StockDay cCurStockDay = list.get(i);

			VOLUMELEVEL volLev = cEStockDayVolumeLevel.checkVolumeLevel(list, i);
			if (volLev == VOLUMELEVEL.ACTIVE)
			{
				BLog.output("TEST", "CheckPoint %s\n", cCurStockDay.date());
				s_StockDayListCurve.markCurveIndex(i, "A");
			}
        } 
		
		s_StockDayListCurve.generateImage();
		BLog.output("TEST", "Main End\n");
	}
	public static StockDayListCurve s_StockDayListCurve = new StockDayListCurve("EStockDayVolumeLevel.jpg");
}
