package stormstock.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.analysis.ANLPolicy.ANLUserAcc.ANLUserAccStock;
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
		cUserAcc.init(100000.0f);
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
				|| cANLStock.curBaseInfo.peRatio == 0)
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
		
//		if(cANLStock.id.compareTo("603099") == 0 )
//		{
//			return true;
//		}
//		else{
//			return false;
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
			if(cANLStock.historyData.size() == 0) continue;
			
			ANLPolicyStockCK cANLPolicyStockCK = new ANLPolicyStockCK();
			cANLPolicyStockCK.stockID = cANLStock.id;
//			logstr = String.format("%s %s ", date, cANLPolicyStockCK.stockID);
//			outputLog(logstr);
			
			// 长期价格均值作为基准值
			float long_base_price = cANLStock.GetMA(500, date);
			// 当前价格参数
			float cur_price = cANLStock.historyData.get(cANLStock.historyData.size()-1).close;
			float cur_pricePa = (cur_price - long_base_price)/long_base_price;
			
			// 计算短期偏离比
			float short_low_price = cANLStock.GetLow(20, date);
			float short_low_pricePa = (short_low_price - long_base_price)/long_base_price;
			float short_high_price = cANLStock.GetHigh(20, date);
			float short_high_pricePa = (short_high_price - long_base_price)/long_base_price;
			float short_pianlirate = 0.0f;
			if(short_high_pricePa-short_low_pricePa != 0)
				short_pianlirate = (cur_pricePa-short_low_pricePa)/(short_high_pricePa-short_low_pricePa);

			// 计算中期偏离比
			float mid_low_price = cANLStock.GetLow(60, date);
			float mid_low_pricePa = (mid_low_price - long_base_price)/long_base_price;
			float mid_high_price = cANLStock.GetHigh(60, date);
			float mid_high_pricePa = (mid_high_price - long_base_price)/long_base_price;
			float mid_pianlirate = 0.0f;
			if(mid_high_pricePa-mid_low_pricePa != 0)
				mid_pianlirate = (cur_pricePa-mid_low_pricePa)/(mid_high_pricePa-mid_low_pricePa);

			// 计算长期偏离比
			float long_low_price = cANLStock.GetLow(250, date);
			float long_low_pricePa = (long_low_price - long_base_price)/long_base_price;
			float long_high_price = cANLStock.GetHigh(250, date);
			float long_high_pricePa = (long_high_price - long_base_price)/long_base_price;
			float long_pianlirate = 0.0f;
			if(long_high_pricePa-long_low_pricePa != 0)
				long_pianlirate = (cur_pricePa-long_low_pricePa)/(long_high_pricePa-long_low_pricePa);
			
//			logstr = String.format("test[ %.3f %.3f %.3f]\n",
//					short_pianlirate,mid_pianlirate,long_pianlirate);
//			outputLog(logstr);
			
			// 短期均值参数
			cANLPolicyStockCK.c1 = short_pianlirate*(5/10.0f) + mid_pianlirate*(3/10.0f) + long_pianlirate*(2/10.0f);
			
			stockCKList.add(cANLPolicyStockCK);
			Collections.sort(stockCKList); //分值表排序
		}
		if(stockCKList.size() != 0) 
		{
			for(int i = 0; i < stockCKList.size(); i++)
			{
				ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(i);
//				logstr = String.format("    %s c1[ %.3f ]\n", cANLPolicyStockCK.stockID, cANLPolicyStockCK.c1);
//				outputLog(logstr);
			}
			
			// 用户操作 ---------------------------------------------------
			for(int i = 0; i < cUserAcc.stockList.size(); i++)
			{
				ANLUserAccStock cANLUserAccStock = cUserAcc.stockList.get(i);
				if(cANLUserAccStock.holdDayCnt > 5)
				{
					float cprice = spool.getStock(cANLUserAccStock.id).GetCurPrice();
					cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
				}
			}
			if(cUserAcc.stockList.size() == 0)
			{
				int indexBuy = 5;
				if(stockCKList.size() < 6) indexBuy = stockCKList.size()-1;
				
				ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(indexBuy);
				String buy_id = cANLPolicyStockCK.stockID;
				float buy_price = spool.getStock(buy_id).GetCurPrice();
				int buy_amount = (int)(cUserAcc.money/buy_price)/100*100;
				cUserAcc.buyStock(buy_id, buy_price, buy_amount);
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.rmlog();
		cANLPolicyJZHG.run("2014-08-25", "2016-08-25");
		fmt.format("main end\n");
	}
}
