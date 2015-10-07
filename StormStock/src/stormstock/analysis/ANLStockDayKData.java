package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataEngine.ExKData;
import stormstock.data.WebStockAllList.StockItem;
 
public class ANLStockDayKData {
	public ANLStockDayKData()
	{
		priceList = new ArrayList<Float>();
		volumeList = new ArrayList<Float>();
	} 
	
	public float getMA(int dayCnt)
	{
		int myIndex = 0;
		for(int i = 0; i < ref_ANLStock.historyData.size(); i++)  
        {  
			ANLStockDayKData cANLStockDayKData = ref_ANLStock.historyData.get(i);
			if(cANLStockDayKData.date.contains(date))
			{
				myIndex = i;
			}
        }
		int beforeIndex = myIndex - dayCnt;
		float avePriceMA = 0.0f;
		for(int i = beforeIndex; i <= myIndex; i++)  
        {
			ANLStockDayKData cANLStockDayKData = ref_ANLStock.historyData.get(i);
			avePriceMA = avePriceMA + cANLStockDayKData.close;
        }
		avePriceMA = avePriceMA/(myIndex-beforeIndex+1);
		return avePriceMA;
	}
	
	public float getVolMA(int dayCnt)
	{
		int myIndex = 0;
		for(int i = 0; i < ref_ANLStock.historyData.size(); i++)  
        {  
			ANLStockDayKData cANLStockDayKData = ref_ANLStock.historyData.get(i);
			if(cANLStockDayKData.date.contains(date))
			{
				myIndex = i;
			}
        }
		int beforeIndex = myIndex - dayCnt;
		float aveVolMA = 0.0f;
		for(int i = beforeIndex; i <= myIndex; i++)  
        {
			ANLStockDayKData cANLStockDayKData = ref_ANLStock.historyData.get(i);
			aveVolMA = aveVolMA + cANLStockDayKData.volume;
        }
		aveVolMA = aveVolMA/(myIndex-beforeIndex+1);
		return aveVolMA;
	}
	
	public int LoadDetail()
	{
		int iSizePriceList = priceList.size();
		int iSizeVolumeList = volumeList.size();
		if(iSizePriceList == iSizeVolumeList && iSizePriceList != 0)
		{
			return 0;
		}
		
		if(null == ref_ANLStock) return -10;
		if(date.length() < 6) return -20;
		
		// load new detail data
		List<ExKData> retList = new ArrayList<ExKData>();
		int ret = DataEngine.get5MinKDataOneDay(ref_ANLStock.id, date, retList);
		if(0 == ret && retList.size() != 0)
		{
			// 由于可能是复权价位，需要重新计算相对价格
			float baseOpenPrice = open;
			float actruaFirstPrice = retList.get(0).open;
			for(int i = 0; i < retList.size(); i++)  
	        {  
				ExKData cExKData = retList.get(i);  
//	            System.out.println(cExKData.datetime + "," 
//	            		+ cExKData.open + "," + cExKData.close + "," 
//	            		+ cExKData.low + "," + cExKData.high + "," 
//	            		+ cExKData.volume);  
				
				float actrualprice = cExKData.close;
				float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
				float changedprice = baseOpenPrice + baseOpenPrice * changeper;
				
				priceList.add(changedprice);
				volumeList.add(cExKData.volume);
	        } 
		}
		else
		{
			// System.out.println("ERROR: LoadDetail");
			return -30;
		}
		return 0;
	}
	
	public String date;
	public float open;
	public float close;
	public float high;
	public float low;
	public float volume;
	
	public List<Float> priceList;
	public List<Float> volumeList;
	
	public ANLStock ref_ANLStock;
	
	
	public static void main(String[] args){
		System.out.println("main begin");
		ANLStock cANLStock = new ANLStock();
		cANLStock.id = "300163";
		ANLStockDayKData cANLStockDayKData = new ANLStockDayKData();
		cANLStockDayKData.date = "2015-09-30";
		cANLStockDayKData.ref_ANLStock = cANLStock;
		
		if(0 == cANLStockDayKData.LoadDetail())
		{
			for(int i = 0; i < cANLStockDayKData.priceList.size(); i++)  
	        {  
				float price = cANLStockDayKData.priceList.get(i);  
	            System.out.println("price:" + price);  
	        } 
		}
		System.out.println("main end");
	}
}