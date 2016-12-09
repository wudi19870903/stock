package stormstock.run.bt;

import stormstock.analysis.ANLBTEngine;
import stormstock.analysis.ANLLog;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStrategy;
import stormstock.analysis.ANLStrategy.SelectResult;

public class RunBTStrategyX {
	// 策略StrategySample类
		public static class StrategyX extends ANLStrategy {
			@Override
			public boolean strategy_preload(ANLStock cANLStock) {
				if(cANLStock.id.compareTo("000001") >= 0 && cANLStock.id.compareTo("000200") <= 0) {	
					ANLLog.outputLog("add stockpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
					return true;
				}
				return false;
			}
			
			@Override
			public void strategy_select(String in_date, ANLStock in_stock, SelectResult out_sr) {
				float StrategyPriceLocLong = (float)in_stock.eigenMap.get("StrategyPriceLocLong");
				float StrategyPriceLocMid = (float)in_stock.eigenMap.get("StrategyPriceLocMid");
				float StrategyPriceLocShort = (float)in_stock.eigenMap.get("StrategyPriceLocShort");
				float StrategyPriceDrop = (float)in_stock.eigenMap.get("StrategyPriceDrop");
				if(StrategyPriceDrop>-0.2) return;
				out_sr.bSelect = true;
//				float PriceLocfenshu = StrategyPriceLocShort*(3/10.0f) + StrategyPriceLocMid*(4/10.0f) + StrategyPriceLocLong*(3/10.0f);
//				ANLLog.outputLog("    stock %s %s %s %.2f test(%.3f) \n", in_stock.id, in_stock.curBaseInfo.name,
//						in_stock.GetLastDate(), in_stock.GetLastPrice(),StrategyPriceDrop);
			}
		}
		
		public static void main(String[] args) {
			ANLBTEngine cANLBTEngine = new ANLBTEngine("RunBTStrategyX");
			// 添加特征
			cANLBTEngine.addEigen(new StrategyPriceLoc.StrategyPriceLocLong());
			cANLBTEngine.addEigen(new StrategyPriceLoc.StrategyPriceLocMid());
			cANLBTEngine.addEigen(new StrategyPriceLoc.StrategyPriceLocShort());
			cANLBTEngine.addEigen(new StrategyPriceDrop());
			// 设置策略
			cANLBTEngine.setStrategy(new StrategyX());
			// 进行回测
			cANLBTEngine.runBT("2016-01-01", "2016-01-20");
		}
}
