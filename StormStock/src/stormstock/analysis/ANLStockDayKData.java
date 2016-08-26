package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataEngine.ExKData;
import stormstock.data.DataWebStockAllList.StockItem;
 
public class ANLStockDayKData {
	public class DetailData
	{
		public Float price;
		public String time;
	}
	public ANLStockDayKData()
	{
		detailDataList = new ArrayList<DetailData>();
	} 
	
	public int LoadDetail()
	{
		int iSizedetailDataList = detailDataList.size();
		if(iSizedetailDataList != 0)
		{
			return 0;
		}
		
		if(null == ref_ANLStock) return -10;
		if(date.length() < 6) return -20;
		
		// load new detail data
		List<ExKData> retList = new ArrayList<ExKData>();
		int ret = DataEngine.get1MinKDataOneDay(ref_ANLStock.id, date, retList);
		if(0 == ret && retList.size() != 0)
		{
			// 由于可能是复权价位，需要重新计算相对价格
			float baseOpenPrice = open;
			float actruaFirstPrice = retList.get(0).open;
			for(int i = 0; i < retList.size(); i++)  
	        {  
				ExKData cExKData = retList.get(i);  
	            System.out.println(cExKData.datetime + "," 
	            		+ cExKData.open + "," + cExKData.close + "," 
	            		+ cExKData.low + "," + cExKData.high + "," 
	            		+ cExKData.volume);  
				
				float actrualprice = cExKData.close;
				float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
				float changedprice = baseOpenPrice + baseOpenPrice * changeper;
				
				DetailData cDetail = new DetailData();
				cDetail.price = changedprice;
				cDetail.time = cExKData.getTime();
				detailDataList.add(cDetail);
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
	
	public List<DetailData> detailDataList;
	
	public ANLStock ref_ANLStock;
}