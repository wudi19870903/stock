package stormstock.fw.tranengine_lite;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngineBase.StockBaseInfo;
import stormstock.ori.stockdata.DataWebStockAllList.StockItem;
import stormstock.ori.stockdata.DataWebStockDayDetail.DayDetailItem;
import stormstock.ori.stockdata.DataWebStockDayK.DayKData;
import stormstock.ori.stockdata.DataWebStockRealTimeInfo.RealTimeInfo;

public class ANLDataProvider {
	// ������й�Ʊid�б�
	public static List<String> getAllStocks()
	{
		List<String> retList = new ArrayList<String>();
		
		List<StockItem> cStockList = DataEngine.getLocalAllStock();
		
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i).id;
			retList.add(stockId);
		}
		
		return retList;
	}
	
	public static ANLStock getANLStock(String id, String fromDate, String endDate)
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
		if(0 != ret || retList.size() == 0)
		{
			return null;
		}
			
		ANLStock cANLStock = new ANLStock();
		cANLStock.id = id;
		
		DataEngine.getStockBaseData(id, cANLStock.curBaseInfo);
		
		for(int i = 0; i < retList.size(); i++)  
        {  
			DayKData cDayKData = retList.get(i);  
			if(cDayKData.date.compareTo(fromDate) >= 0
					&& cDayKData.date.compareTo(endDate) <= 0)
			{
				ANLStockDayKData cANLStockDayKData = new ANLStockDayKData();
				cANLStockDayKData.ref_ANLStock = cANLStock;
				cANLStockDayKData.date = cDayKData.date;
				cANLStockDayKData.open = cDayKData.open;
				cANLStockDayKData.close = cDayKData.close;
				cANLStockDayKData.low = cDayKData.low;
				cANLStockDayKData.high = cDayKData.high;
				cANLStockDayKData.volume = cDayKData.volume;
//	            System.out.println(cDayKData.date + "," 
//	            		+ cDayKData.open + "," + cDayKData.close); 
				cANLStock.historyData.add(cANLStockDayKData);
			}
        } 
		
		return cANLStock;
	}
	public static ANLStock getANLStock(String id, String endDate)
	{
		return getANLStock(id, "2000-01-01", endDate);
	}
	// �����ݼ���
	public static ANLStock getANLStock(String id)
	{
		return getANLStock(id, "2100-01-01");
	}
}
