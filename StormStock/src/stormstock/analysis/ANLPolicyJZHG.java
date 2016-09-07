package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.analysis.ANLPolicy.ANLUserStockPool;

// 价值回归
public class ANLPolicyJZHG extends ANLPolicy {
	
	// init 初始化参数
	public void init()
	{
		fmt.format("init\n");
	}

	// 所有股票回调筛选函数，系统自动回调，反悔true的被加入考察股票池
	public boolean stock_filter(ANLStock cANLStock)
	{
		if(cANLStock.id.compareTo("300312") == 0 || cANLStock.id.compareTo("300314") == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void check_today(String date, ANLUserStockPool spool)
	{
		String logstr = String.format("check_today %s\n", date);
		outputLog(logstr);
		for(int i=0; i<spool.stockList.size();i++)
		{
			ANLStock cANLStock = spool.stockList.get(i);
			logstr = String.format("    cANLStock %s\n", cANLStock.id);
			outputLog(logstr);
			for(int j = 0; j < cANLStock.historyData.size(); j++)  
	        {  
				ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
				logstr = String.format("        - date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
				outputLog(logstr);
	        }
		}
	}
	public static void main(String[] args) throws InterruptedException {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.run("2016-01-05", "2016-01-15");
		List<ANLStock> cStockObjList = new ArrayList<ANLStock>();
		fmt.format("main end\n");
	}

}
