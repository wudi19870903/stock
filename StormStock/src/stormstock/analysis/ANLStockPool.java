package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class ANLStockPool {
	// 新数据加载
	public static ANLStock getANLStock(String id)
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
			cANLStockDayKData.ref_ANLStock = cANLStock;
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
}
