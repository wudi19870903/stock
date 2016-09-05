package stormstock.analysis;

import java.util.List;

// 价值回归
public class ANLPolicyJZHG extends ANLPolicy {

	public void check_today(String date)
	{
		fmt.format("check_today %s\n", date);
		// 股票全列表，输出所有股票id
		List<String> cStockList = ANLStockPool.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			//outputLog(stockId + "\n");
			// 输出一只股票所有日k数据
			ANLStock cANLStock = ANLStockPool.getANLStock(stockId, date);
			if(null == cANLStock) continue;
			
			for(int j = 0; j < cANLStock.historyData.size(); j++)  
	        {  
				ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
	            //fmt.format("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
	        }
		}
	}
	public static void main(String[] args) {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.run("2016-01-01", "2016-01-08");
		fmt.format("main end\n");
	}

}
