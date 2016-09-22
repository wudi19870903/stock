package stormstock.analysis;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataEngineBase.StockBaseInfo;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLStock {
	static public Formatter fmt = new Formatter(System.out);
	public ANLStock()
	{
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
	}	 
	public ANLStock(String sid, StockBaseInfo scurBaseInfo)
	{
		id = sid;
		curBaseInfo = scurBaseInfo;
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
	}	 
	public String id;
	public StockBaseInfo curBaseInfo;
	public List<ANLStockDayKData> historyData;
	
	// 查找日期索引
	static public int indexDayKAfterDate(List<ANLStockDayKData> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) >= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	static public int indexDayKBeforeDate(List<ANLStockDayKData> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) <= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	public float GetLastPrice()
	{
		return historyData.get(historyData.size()-1).close;
	}
	public String GetLastDate()
	{
		return historyData.get(historyData.size()-1).date;
	}
		
	// 均线计算
	public float GetMA(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			sum = sum + cANLDayKData.close;
			sumcnt++;
			//fmt.format("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		value = sum/sumcnt;
		return value;
	}
	// 高值计算
	public float GetHigh(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			if(cANLDayKData.high >= value)
			{
				value = cANLDayKData.high;
			}
			//fmt.format("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		return value;
	}
	// 低值计算
	public float GetLow(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 10000.0f;
		int iE = indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			if(cANLDayKData.low <= value)
			{
				value = cANLDayKData.low;
			}
			//fmt.format("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		return value;
	}
}
