package stormstock.app.sample.regressiontestlite;

import stormstock.fw.tranengine_lite.ANLBTEngine;
import stormstock.fw.tranengine_lite.ANLLog;
import stormstock.fw.tranengine_lite.ANLStock;
import stormstock.fw.tranengine_lite.ANLStrategy;
import stormstock.fw.tranengine_lite.ANLStrategy.SelectResult;

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
				float EigenPriceLocLong = (float)in_stock.getEngen("EigenPriceLoc", 250);
				float EigenPriceLocMid = (float)in_stock.getEngen("EigenPriceLoc", 60);
				float EigenPriceLocShort = (float)in_stock.getEngen("EigenPriceLoc", 20);
				float EigenPriceDrop = (float)in_stock.getEngen("EigenPriceDrop");
				if(EigenPriceDrop>-0.2) return;
				out_sr.bSelect = true;
//				float PriceLocfenshu = StrategyPriceLocShort*(3/10.0f) + StrategyPriceLocMid*(4/10.0f) + StrategyPriceLocLong*(3/10.0f);
//				ANLLog.outputLog("    stock %s %s %s %.2f test(%.3f) \n", in_stock.id, in_stock.curBaseInfo.name,
//						in_stock.GetLastDate(), in_stock.GetLastPrice(),StrategyPriceDrop);
			}
		}
		
		public static void main(String[] args) {
			ANLBTEngine cANLBTEngine = new ANLBTEngine("RunBTStrategyX");
			// 添加特征
			cANLBTEngine.addEigen(new EigenPriceLoc());
			cANLBTEngine.addEigen(new EigenPriceDrop());
			// 设置策略
			cANLBTEngine.setStrategy(new StrategyX());
			// 进行回测
			cANLBTEngine.runBT("2016-01-01", "2016-01-20");
		}
}
