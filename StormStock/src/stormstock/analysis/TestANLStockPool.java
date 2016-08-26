package stormstock.analysis;

import java.util.Formatter;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;

public class TestANLStockPool {
	public static Formatter fmt = new Formatter(System.out);
	public static void main(String[] args) {

		ANLStock cANLStock = ANLStockPool.getANLStock("000920");
		fmt.format("cANLStockId:%s\n", cANLStock.id);
		for(int i = 0; i < cANLStock.historyData.size(); i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
            fmt.format("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
            if(i == cANLStock.historyData.size()-1)
            {
            	cANLDayKData.LoadDetail();
            	for(int j = 0; j < cANLDayKData.detailDataList.size(); j++)  
            	{
            		fmt.format("    %s %.2f\n", 
            				cANLDayKData.detailDataList.get(j).time,
            				cANLDayKData.detailDataList.get(j).price);
            	}
            }
        } 
	}
}
