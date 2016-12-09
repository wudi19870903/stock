package stormstock.run;

import java.util.Arrays;
import java.util.List;

import stormstock.analysis.ANLBTEngine;
import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLLog;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockPool;
import stormstock.analysis.ANLStrategy;
import stormstock.analysis.ANLStrategy.SelectResult;

public class RunBTStrategySample {
	// 特征EigenSample1类
	public static class EigenSample1 extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			// 计算离60日均线的价格偏离百分比
			float MA60 = cANLStock.GetMA(60, cANLStock.GetLastDate());
			float curPrice = cANLStock.GetLastPrice();
			float val = (curPrice-MA60)/MA60;
			return val;
		}

	}
	// 特征EigenSample2类
	public static class EigenSample2 extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock)
		{
			// 计算离250日均线的价格偏离百分比
			float MA250 = cANLStock.GetMA(250, cANLStock.GetLastDate());
			float curPrice = cANLStock.GetLastPrice();
			float val = (curPrice-MA250)/MA250;
			return val;
		}
	}

	// 策略StrategySample类
	public static class StrategySample extends ANLStrategy {
		@Override
		public boolean strategy_preload(ANLStock cANLStock) {
			if(cANLStock.id.compareTo("000001") >= 0 && cANLStock.id.compareTo("000200") <= 0) {	
				//ANLLog.outputLog("add stockpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
				return true;
			}
			return false;
		}
		
		@Override
		public void strategy_select(String in_date, ANLStock in_stock, SelectResult out_sr) {
			float EigenSample1 = (float)in_stock.eigenMap.get("EigenSample1");
			float EigenSample2 = (float)in_stock.eigenMap.get("EigenSample2");
			if(EigenSample1 < -0.03 && EigenSample2 < -0.03) {
				out_sr.bSelect = true;
				//out_sr.fPriority = (float) Math.random();
				//ANLLog.outputLog("    stock %s %s %s %.2f EigenSample1(%.3f) EigenSample2(%.3f)\n", in_stock.id, in_stock.curBaseInfo.name, in_stock.GetLastDate(), in_stock.GetLastPrice(),EigenSample1,EigenSample2);
			}
		}
	}
	
	public static void main(String[] args) {
		ANLBTEngine cANLBTEngine = new ANLBTEngine("Sample");
		// 添加特征
		cANLBTEngine.addEigen(new EigenSample1());
		cANLBTEngine.addEigen(new EigenSample2());
		// 设置策略
		cANLBTEngine.setStrategy(new StrategySample());
		// 进行回测
		cANLBTEngine.runBT("2016-01-01", "2016-12-31");
	}
}
