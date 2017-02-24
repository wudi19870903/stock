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
	 * ��鵱ǰλ�ý����Ƿ������ܻ�Ծ��
	 */
	public enum VOLUMELEVEL
	{
		ACTIVE, // ��Ծ��
		UNACTIVE, // ����Ծ
		DEATH, // ������
		UNKNOWN,
		INVALID,
	}
	public VOLUMELEVEL checkVolumeLevel(List<StockDay> list, int iCheck)
	{		
		// �����г����ھ����� ȥ�����5�������5�����ƽ��ֵ
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
		
		// �������ھ����� ȥ�����5�������5�����ƽ��ֵ
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
		
		
		// ������վ��� ȥ�����1�������1�����ƽ��ֵ
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
		
		// �����ɽ����ж�
		StockDay cStockDay = list.get(iCheck);
		if(cStockDay.volume()/aveVol10 < 0.6
				&& cStockDay.volume()/aveVol40 < 0.6
				&& cStockDay.volume()/aveVol60 < 0.6)
		{
			return VOLUMELEVEL.DEATH;
		}
		
		// ��Ծ�ɽ����ж�
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
