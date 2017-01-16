package stormstock.fw.tranengine_lite;

import java.util.ArrayList;
import java.util.List;

import stormstock.ori.stockdata.DataEngine;
import stormstock.ori.stockdata.DataEngineBase.ResultStockBaseData;
import stormstock.ori.stockdata.DataWebStockAllList.ResultAllStockList;
import stormstock.ori.stockdata.DataWebStockDayK.ResultDayKData;
import stormstock.ori.stockdata.CommonDef.*;

public class ANLDataProvider {
	// 获得所有股票id列表
	public static List<String> getAllStocks()
	{
		List<String> retList = new ArrayList<String>();
		
		ResultAllStockList cResultAllStockList = DataEngine.getLocalAllStock();
		
		for(int i=0; i<cResultAllStockList.resultList.size();i++)
		{
			String stockId = cResultAllStockList.resultList.get(i).id;
			retList.add(stockId);
		}
		
		return retList;
	}
	
	public static ANLStock getANLStock(String id, String fromDate, String endDate)
	{
		ResultDayKData cResultDayKData = DataEngine.getDayKDataQianFuQuan(id);
		if(0 != cResultDayKData.error || cResultDayKData.resultList.size() == 0)
		{
			return null;
		}
			
		ANLStock cANLStock = new ANLStock();
		cANLStock.id = id;
		
		ResultStockBaseData cResultStockBaseData = DataEngine.getBaseInfo(id);
		
		for(int i = 0; i < cResultDayKData.resultList.size(); i++)  
        {  
			DayKData cDayKData = cResultDayKData.resultList.get(i);  
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
	// 新数据加载
	public static ANLStock getANLStock(String id)
	{
		return getANLStock(id, "2100-01-01");
	}
}
