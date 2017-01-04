package stormstock.fw.stockdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngine.ExKData;
import stormstock.ori.stockdata.DataEngineBase.StockBaseInfo;
import stormstock.fw.tran.IEigen;

public class Stock {
	public Stock()
	{
		latestInfo = new StockInfo();
		historyData = new ArrayList<StockDay>();
	}	 
 
	public Stock subObj(String fromDate, String endDate)
	{
		Stock cSubObj = new Stock();
		cSubObj.id = this.id;
		cSubObj.latestInfo = new StockInfo();
		cSubObj.latestInfo.CopyFrom(this.latestInfo);
		cSubObj.historyData = new ArrayList<StockDay>();
		for(int i = 0; i < this.historyData.size(); i++)  
        {  
			StockDay cDayKData = historyData.get(i);  
			if(cDayKData.date.compareTo(fromDate) >= 0 &&
					cDayKData.date.compareTo(endDate) <= 0)
			{
				StockDay cNewStockDay = new StockDay();
				cNewStockDay.CopyFrom(cDayKData);
				cSubObj.historyData.add(cNewStockDay);
			}
        }
		return cSubObj;
	}
	
	// 获得最后一天的昨日收盘价
	public float GetLastYesterdayClosePrice()
	{
		if(historyData.size() > 1) // 2天以上
			return historyData.get(historyData.size()-2).close;
		else if(historyData.size() > 0) // 只有一天情况，昨收就是今开
			return historyData.get(historyData.size()-1).open;
		else
			return 0.0f;
	}
	
	// 获得最后一天的开盘价
	public float GetLastOpenPrice()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).open;
		else
			return 0.0f;
	}

	// 获得最后一天的开盘时的百分比
	public float GetLastOpenRatio()
	{
		if(historyData.size() > 0)
		{
			float fYesterdayClose = GetLastYesterdayClosePrice();
			float fLastOpen = GetLastOpenPrice();
			float fLastOpenRatio = (fLastOpen - fYesterdayClose)/fYesterdayClose;
			return fLastOpenRatio;
		}
		else
			return 0.0f;
	}
	
	// 获得最后一天的收盘价
	public float GetLastClosePrice()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).close;
		else
			return 0.0f;
	}
	
	// 获得第一天的日期
	public String GetFirstDate()
	{
		if(historyData.size() > 0)
			return historyData.get(0).date;
		else
			return "0000-00-00";
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
		int iE = StockUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = historyData.get(i);  
			sum = sum + cDayKData.close;
			sumcnt++;
			//Log.outputConsole("%s %.2f\n", cDayKData.date, cDayKData.close);
        }
		value = sum/sumcnt;
		return value;
	}
	
	// 高值计算，计算date日期前count天最高价格
	public float GetHigh(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = StockUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = historyData.get(i);  
			if(cDayKData.high >= value)
			{
				value = cDayKData.high;
			}
			//Log.outputConsole("%s %.2f\n", cDayKData.date, cDayKData.close);
        }
		return value;
	}
	
	// 低值计算，计算date日期前count天最低价格
	public float GetLow(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 10000.0f;
		int iE = StockUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			StockDay cDayKData = historyData.get(i);  
			if(cDayKData.low <= value)
			{
				value = cDayKData.low;
			}
			//Log.outputConsole("%s %.2f\n", cDayKData.date, cDayKData.close);
        }
		return value;
	}
	
	public StockDay GetDayK(String date)
	{
		int i = StockUtils.indexDayKAfterDate(historyData, date);
		if(i>=0)
		{
			return historyData.get(i);
		}
		else
		{
			return null;
		}
	}
	
	public StockDetailDay getDetailDay(String date)
	{
		StockDetailDay cStockDetailDay = new StockDetailDay();
		
		StockDay cDayKData = GetDayK(date);
		if(null != cDayKData && date.length() == 6)
		{
			// load new detail data
			List<ExKData> retList = new ArrayList<ExKData>();
			int ret = DataEngine.get1MinKDataOneDay(id, date, retList);
			if(0 == ret && retList.size() != 0)
			{
				// 由于可能是复权价位，需要重新计算相对价格
				float baseOpenPrice = cDayKData.open;
				float actruaFirstPrice = retList.get(0).open;
				for(int i = 0; i < retList.size(); i++)  
		        {  
					ExKData cExKData = retList.get(i);  
//		            System.out.println(cExKData.datetime + "," 
//		            		+ cExKData.open + "," + cExKData.close + "," 
//		            		+ cExKData.low + "," + cExKData.high + "," 
//		            		+ cExKData.volume);  
					
					float actrualprice = cExKData.close;
					float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
					float changedprice = baseOpenPrice + baseOpenPrice * changeper;
					
					StockDetailTime cStockDetailTime = new StockDetailTime();
					cStockDetailTime.price = changedprice;
					cStockDetailTime.time = cExKData.getTime();
					cStockDetailDay.detailDataList.add(cStockDetailTime);
		        } 
			}
		}
		return cStockDetailDay;
	}
	
	public String id;
	public StockInfo latestInfo; // 最新基本信息
	public List<StockDay> historyData; // 股票历史数据
}
