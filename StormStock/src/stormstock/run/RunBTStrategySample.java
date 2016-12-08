package stormstock.run;

import java.util.Arrays;
import java.util.List;

import stormstock.analysis.ANLBTEngine;
import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLLog;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockPool;
import stormstock.analysis.ANLStrategy;

public class RunBTStrategySample {
	// 特征EISample1类
	public static class EigenSample1 extends ANLEigen {
		@Override
		public float calc(ANLStock cANLStock) {
			// 计算离60日均线的价格偏离百分比
			float MA60 = cANLStock.GetMA(60, cANLStock.GetLastDate());
			float curPrice = cANLStock.GetLastPrice();
			float val = (curPrice-MA60)/MA60;
			return val;
		}

	}
	// 特征EISample2类
	public static class EigenSample2 extends ANLEigen {
		@Override
		public float calc(ANLStock cANLStock)
		{
			// 计算离250日均线的价格偏离百分比
			float MA250 = cANLStock.GetMA(250, cANLStock.GetLastDate());
			float curPrice = cANLStock.GetLastPrice();
			float val = (curPrice-MA250)/MA250;
			return val;
		}
	}

	// 策略Sample类
	public static class StrategySample extends ANLStrategy {
		@Override
		public boolean strategy_preload(ANLStock cANLStock) {
			// TODO Auto-generated method stub
			if(cANLStock.id.compareTo("000001") >= 0
				&& cANLStock.id.compareTo("000200") <= 0)
			{	
				ANLLog.outputLog("add stockpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
				return true;
			} else {
				return false;
			}
		}
		@Override
		public void strategy_select(String date, ANLStockPool spool, List<String> selectStockList) {
			for(int i = 0; i < spool.stockList.size(); i++)
			{
				ANLStock cANLStock = spool.stockList.get(i);
				float EigenSample1 = cANLStock.eigenMap.get("EigenSample1");
				float EigenSample2 = cANLStock.eigenMap.get("EigenSample2");
				ANLLog.outputLog("    stock %s %s %s %.2f EigenSample1(%.3f) EigenSample2(%.3f)\n", 
						cANLStock.id, cANLStock.curBaseInfo.name, 
						cANLStock.GetLastDate(), cANLStock.GetLastPrice(),
						EigenSample1,EigenSample2);
				if(EigenSample1 < -0.03 && EigenSample2 < -0.02) {
					selectStockList.add(cANLStock.id);
				}
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
