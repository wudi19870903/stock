package stormstock.analysis;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.data.DataEngine;
import stormstock.data.DataEngineBase.StockBaseInfo;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLStock {
	
	public ANLStock()
	{
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
		eigenMap = new HashMap<String, Object>();
	}	 
	public ANLStock(String sid, StockBaseInfo scurBaseInfo)
	{
		id = sid;
		curBaseInfo = scurBaseInfo;
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
		eigenMap = new HashMap<String, Object>();
	}	 

	// 获得最后一天的收盘价
	public float GetLastPrice()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).close;
		else
			return 0.0f;
	}
	
	// 获得最后一天的日期
	public String GetLastDate()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).date;
		else
			return "0000-00-00";
	}
		
	// 均线计算，计算date日期前count天均线价格
	public float GetMA(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = ANLUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			sum = sum + cANLDayKData.close;
			sumcnt++;
			//ANLLog.outputConsole("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		value = sum/sumcnt;
		return value;
	}
	
	// 高值计算，计算date日期前count天最高价格
	public float GetHigh(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = ANLUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			if(cANLDayKData.high >= value)
			{
				value = cANLDayKData.high;
			}
			//ANLLog.outputConsole("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		return value;
	}
	
	// 低值计算，计算date日期前count天最低价格
	public float GetLow(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 10000.0f;
		int iE = ANLUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			if(cANLDayKData.low <= value)
			{
				value = cANLDayKData.low;
			}
			//ANLLog.outputConsole("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		return value;
	}
	
	public String id;
	public StockBaseInfo curBaseInfo;
	public List<ANLStockDayKData> historyData;
	public Map<String, Object> eigenMap;
}
