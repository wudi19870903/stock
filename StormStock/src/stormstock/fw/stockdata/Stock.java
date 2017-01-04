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
		latestBaseInfo = new StockBaseInfo();
		historyData = new ArrayList<StockDay>();
	}	 
 
	public Stock subObj(String fromDate, String endDate)
	{
		Stock cSubObj = new Stock();
		cSubObj.id = this.id;
		cSubObj.latestBaseInfo = new StockBaseInfo();
		cSubObj.latestBaseInfo.CopyFrom(this.latestBaseInfo);
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
	
	// ������һ����������̼�
	public float GetLastYesterdayClosePrice()
	{
		if(historyData.size() > 1) // 2������
			return historyData.get(historyData.size()-2).close;
		else if(historyData.size() > 0) // ֻ��һ����������վ��ǽ�
			return historyData.get(historyData.size()-1).open;
		else
			return 0.0f;
	}
	
	// ������һ��Ŀ��̼�
	public float GetLastOpenPrice()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).open;
		else
			return 0.0f;
	}

	// ������һ��Ŀ���ʱ�İٷֱ�
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
	
	// ������һ������̼�
	public float GetLastClosePrice()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).close;
		else
			return 0.0f;
	}
	
	// ��õ�һ�������
	public String GetFirstDate()
	{
		if(historyData.size() > 0)
			return historyData.get(0).date;
		else
			return "0000-00-00";
	}
	
	// ������һ�������
	public String GetLastDate()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).date;
		else
			return "0000-00-00";
	}
	
	// ���߼��㣬����date����ǰcount����߼۸�
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
	
	// ��ֵ���㣬����date����ǰcount����߼۸�
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
	
	// ��ֵ���㣬����date����ǰcount����ͼ۸�
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
				// ���ڿ����Ǹ�Ȩ��λ����Ҫ���¼�����Լ۸�
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
	public StockBaseInfo latestBaseInfo; // ���»�����Ϣ
	public List<StockDay> historyData; // ��Ʊ��ʷ����
}