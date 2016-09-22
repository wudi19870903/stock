package stormstock.analysis;

import java.util.Formatter;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;

public class TestANLStockPool {
	public static Formatter fmt = new Formatter(System.out);
	public static void main(String[] args) {

		ANLStock cANLStock = ANLStockPool.getANLStock("000678");
		fmt.format("cANLStockId:%s\n", cANLStock.id);
		fmt.format("    -name:%s\n", cANLStock.curBaseInfo.name);
		fmt.format("    -price:%.3f\n", cANLStock.curBaseInfo.price);
		fmt.format("    -allMarketValue:%.3f\n", cANLStock.curBaseInfo.allMarketValue);
		fmt.format("    -circulatedMarketValue:%.3f\n", cANLStock.curBaseInfo.circulatedMarketValue);
		fmt.format("    -PE:%.3f\n", cANLStock.curBaseInfo.peRatio);
		// MA
		String ckDate = "2014-08-01";
		int MACnt = 500;
		fmt.format("    -MA%d(%s):%.2f\n", MACnt, ckDate, cANLStock.GetMA(MACnt, ckDate));
		fmt.format("    -HI%d(%s):%.2f\n", MACnt, ckDate, cANLStock.GetHigh(MACnt, ckDate));
		fmt.format("    -LO%d(%s):%.2f\n", MACnt, ckDate, cANLStock.GetLow(MACnt, ckDate));
		
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
