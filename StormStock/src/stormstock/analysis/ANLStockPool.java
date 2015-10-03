package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.WebStockDayDetail.DayDetailItem;
import stormstock.data.WebStockDayK.DayKData;
import stormstock.data.WebStockRealTimeInfo.RealTimeInfo;

public class ANLStockPool {
	// 新数据加载
	public static ANLStock getANLStockNF(String id)
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
		if(0 != ret || retList.size() == 0)
		{
			return null;
		}
			
		ANLStock cANLStock = new ANLStock();
		cANLStock.id = id;
		
		for(int i = 0; i < retList.size(); i++)  
        {  
			ANLStockDayKData cANLStockDayKData = new ANLStockDayKData();
			DayKData cDayKData = retList.get(i);  
			cANLStockDayKData.date = cDayKData.date;
			cANLStockDayKData.open = cDayKData.open;
			cANLStockDayKData.close = cDayKData.close;
			cANLStockDayKData.low = cDayKData.low;
			cANLStockDayKData.high = cDayKData.high;
			cANLStockDayKData.volume = cDayKData.volume;
//            System.out.println(cDayKData.date + "," 
//            		+ cDayKData.open + "," + cDayKData.close); 
			cANLStock.historyData.add(cANLStockDayKData);
        } 
		
		return cANLStock;
	}
	public static int getANLStockDayDetailNF(String id, String date, ANLStockDayKData in_ANLStockDayKData)
	{
		in_ANLStockDayKData.priceList.clear();
		in_ANLStockDayKData.volumeList.clear();
		List<DayDetailItem> retList = new ArrayList<DayDetailItem>();
		int ret = DataEngine.getDayDetail(id, date, retList);
		if(0 == ret  && retList.size() != 0)
		{
			float baseOpenPrice = in_ANLStockDayKData.open;
			
			float actruaFirstPrice = retList.get(0).price;
			
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DayDetailItem cDayDetailItem = retList.get(i);  
				float actrualprice = cDayDetailItem.price;
				float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
				float changedprice = baseOpenPrice + baseOpenPrice * changeper;
//	            System.out.println(cDayDetailItem.time + "," 
//	            		+ cDayDetailItem.price + "," + cDayDetailItem.volume); 
				in_ANLStockDayKData.priceList.add(changedprice);
				in_ANLStockDayKData.volumeList.add(cDayDetailItem.volume);
	        } 
		}
		else
		{
			return -10;
		}
		return 0;
	}
	
	// 旧测试数据加载
	public static ANLStock getANLStock(String id)
	{
		List<DataEngine.StockKData> listStockKData =  DataEngine.getStock(id);
		if(null == listStockKData)
		{
			return null;
		}
		
		ANLStock cANLStock = new ANLStock();
		cANLStock.id = id;
		
		List<DataEngine.StockKData> tmpLisStockKData =new ArrayList<DataEngine.StockKData>();
		
		for(int iIndex = 0; iIndex < listStockKData.size(); iIndex++)  
		{  
			DataEngine.StockKData cStockKData = listStockKData.get(iIndex);  
			String currentdate = cStockKData.datetime.split(" ")[0].replace("-","");
			tmpLisStockKData.add(cStockKData);
			if(iIndex != listStockKData.size()-1)
			{
				DataEngine.StockKData cStockKDataNext = listStockKData.get(iIndex+1); 
				String nextlinedate = cStockKDataNext.datetime.split(" ")[0].replace("-","");
				if(!currentdate.equals(nextlinedate))
				{
					ANLStockDayKData cANLStockDayKData = getANLStockDayKDataFromSession(tmpLisStockKData);
					cANLStock.historyData.add(cANLStockDayKData);
					tmpLisStockKData.clear();
				}
			}
			else
			{
				ANLStockDayKData cANLStockDayKData = getANLStockDayKDataFromSession(tmpLisStockKData);
				cANLStock.historyData.add(cANLStockDayKData);
				tmpLisStockKData.clear();
			} 
		}  
		
		return cANLStock;
	}
	private static ANLStockDayKData getANLStockDayKDataFromSession(List<DataEngine.StockKData> listStockKData)
	{
		
		ANLStockDayKData cANLStockDayKData = new ANLStockDayKData();
		float curVolume = 0;
		for(int iIndex = 0; iIndex < listStockKData.size(); iIndex++)  
		{
			DataEngine.StockKData cStockKData = listStockKData.get(iIndex);  
			if(0 == iIndex)
			{
				cANLStockDayKData.date = cStockKData.datetime.split(" ")[0].replace("-","");
				cANLStockDayKData.open = cStockKData.open;
				cANLStockDayKData.low = cStockKData.open;
				cANLStockDayKData.high = cStockKData.open;
			}
			if(listStockKData.size() - 1 == iIndex)
			{
				cANLStockDayKData.close = cStockKData.close;
			}
			curVolume = curVolume + cStockKData.volume;
			
			if(cStockKData.low < cANLStockDayKData.low)
			{
				cANLStockDayKData.low = cStockKData.low;
			}
			if(cStockKData.high > cANLStockDayKData.high)
			{
				cANLStockDayKData.high = cStockKData.high;
			}	
			cANLStockDayKData.priceList.add(cStockKData.close);
			cANLStockDayKData.volumeList.add(cStockKData.volume);
		}
		cANLStockDayKData.volume = curVolume;
		return cANLStockDayKData;
	}
	
	public static void main(String[] args){
		System.out.println("main begin");
		ANLStockDayKData cANLStockDayKData = new ANLStockDayKData();
		ANLStockPool.getANLStockDayDetailNF("601766", "2010-12-08", cANLStockDayKData);
		System.out.println("main end");
	}
}
