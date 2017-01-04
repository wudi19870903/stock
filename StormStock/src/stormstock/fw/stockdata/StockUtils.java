package stormstock.fw.stockdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stormstock.fw.base.BLog;

public class StockUtils {
	// 查找日期索引，返回list中某日期（含）之后的第一天index索引
	static public int indexDayKAfterDate(List<StockDay> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) >= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// 查找日期索引，返回list中某日期（含）之前的第一天index索引
	static public int indexDayKBeforeDate(List<StockDay> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) <= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// 计算i到j日的最高价格的索引
	static public int indexHigh(List<StockDay> dayklist, int i, int j)
	{
		int index = i;
		float high = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high > high) 
			{
				high = cDayKDataTmp.high;
				index = k;
			}
		}
		return index;
	}
	
	// 计算i到j日的最低价格的索引
	static public int indexLow(List<StockDay> dayklist, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			StockDay cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low < low) 
			{
				low = cDayKDataTmp.low;
				index = k;
			}
		}
		return index;
	}
	
	public static List<StockDay> subStockDayData(List<StockDay> oriList, String fromDate, String endDate)
	{
		List<StockDay> newStockDayData = new ArrayList<StockDay>();
		for(int i = 0; i <oriList.size(); i++)  
        {  
			StockDay cStockDay = oriList.get(i);  
			if(cStockDay.date.compareTo(fromDate) >= 0 &&
					cStockDay.date.compareTo(endDate) <= 0)
			{
				StockDay cNewStockDay = new StockDay();
				cNewStockDay.CopyFrom(cStockDay);
				newStockDayData.add(cNewStockDay);
			}
        }
		return newStockDayData;
	}
}
