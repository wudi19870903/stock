package stormstock.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.analysis.ANLPolicy.ANLUserStockPool;
import stormstock.analysis.ANLPolicyAve.BounceData;

// 价值回归
public class ANLPolicyJZHG extends ANLPolicy {
	// 计算股票各项系数表单
	static class ANLPolicyStockCK implements Comparable
	{
		public String stockID;
		float c1; // 价值回归分值   系数(当前价-半年最高)+(当前价-半年最低)+(当前价-15日均)
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			ANLPolicyStockCK ck = (ANLPolicyStockCK)arg0;
			if(this.c1 <= ck.c1)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
	}
	
	// init 初始化参数
	public void init()
	{
		String logstr = String.format("init\n");
		outputLog(logstr);
	}

	// 所有股票回调筛选函数，系统自动回调，反悔true的被加入考察股票池
	public boolean stock_filter(ANLStock cANLStock)
	{
		// 市值过大过滤掉
		if(cANLStock.curBaseInfo.allMarketValue > 200.0f 
				|| cANLStock.curBaseInfo.circulatedMarketValue > 100.0f)
		{
			return false;
		}
		// 交易历史上市时间较短的过滤掉
		if(cANLStock.historyData.size() < 250*2)
		{
			return false;
		}
		// PE市盈率过大的过滤掉
		if(cANLStock.curBaseInfo.peRatio > 100.0f 
				&& cANLStock.curBaseInfo.peRatio > 0)
		{
			return false;
		}
		// 名字过滤
		if(cANLStock.curBaseInfo.name.contains("S")
				|| cANLStock.curBaseInfo.name.contains("*")
				|| cANLStock.curBaseInfo.name.contains("N"))
		{
			return false;
		}
		
//		if(cANLStock.id.compareTo("300312") == 0 || cANLStock.id.compareTo("300314") == 0)
//		{
//			return true;
//		}
		String logstr = String.format("add userpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
		outputLog(logstr);
		return true;
	}
	
	public void check_today(String date, ANLUserStockPool spool)
	{
		String logstr = String.format("check_today %s --->>>\n", date);
		outputLog(logstr);
		
		//创建计算分值表
		List<ANLPolicyStockCK> stockCKList = new ArrayList<ANLPolicyStockCK>();
		// 计算所有股票检查表值，并添加
		for(int i = 0; i < spool.stockList.size(); i++)
		{
			ANLStock cANLStock = spool.stockList.get(i);
			ANLPolicyStockCK cANLPolicyStockCK = new ANLPolicyStockCK();
			cANLPolicyStockCK.stockID = cANLStock.id;
			
			// 长期价格均值作为基准值
			float long_base_price = cANLStock.GetMA(500, date);
			// 当前价格参数
			float cur_price = cANLStock.historyData.get(cANLStock.historyData.size()-1).close;
			float cur_pricePa = (cur_price - long_base_price)/long_base_price;
			// 中期最高价格参数
			float mid_high_price = cANLStock.GetHigh(120, date);
			float mid_high_pricePa = (mid_high_price - long_base_price)/long_base_price;
			// 中期最低价格参数
			float mid_low_price = cANLStock.GetLow(120, date);
			float mid_low_pricePa = (mid_low_price - long_base_price)/long_base_price;
			// 短期均值参数
			float short_ma_price = cANLStock.GetMA(15, date);
			float short_ma_pricePa = (short_ma_price - long_base_price)/long_base_price;
						
			cANLPolicyStockCK.c1 = (cur_price-mid_high_price)
					+(cur_price-mid_low_pricePa)+(cur_price-short_ma_pricePa);
			
			stockCKList.add(cANLPolicyStockCK);
//			logstr = String.format("    %s test[ %.3f ]\n", cANLPolicyStockCK.stockID, cANLPolicyStockCK.c1);
//			outputLog(logstr);
		}
		//分值表排序
		Collections.sort(stockCKList);
		
		for(int i = 0; i < stockCKList.size(); i++)
		{
			ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(i);
			logstr = String.format("    %s c1[ %.3f ]\n", cANLPolicyStockCK.stockID, cANLPolicyStockCK.c1);
			outputLog(logstr);
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.rmlog();
		cANLPolicyJZHG.run("2016-09-09", "2016-09-09");
		List<ANLStock> cStockObjList = new ArrayList<ANLStock>();
		fmt.format("main end\n");
	}
}
