package stormstock.app.sample.regressiontest;

import stormstock.fw.base.BLog;
import stormstock.fw.tranengine.TranEngine;
import stormstock.fw.tranengine.TranEngine.TRANACCOUNTTYPE;
import stormstock.fw.tranengine.TranEngine.TRANTIMEMODE;

public class Application {

	public static void main(String[] args) {
		BLog.output("TEST", "--->>> MainBegin\n");
		
		TranEngine cTranEngine = new TranEngine();
		
		cTranEngine.setStockSet(new TranStockSet());
		
		cTranEngine.addStockEigen(new StockEigen.EigenSamplePriceLoc());
		cTranEngine.addStockEigen(new StockEigen.EigenSampleMADeviation());
		
		cTranEngine.setSelectStockStrategy(new StrategySelect());
		cTranEngine.setCreatePositonStrategy(new StrategyCreate());
		cTranEngine.setClearPositonStrategy(new StrategyClear());
		
		cTranEngine.setAccountType(TRANACCOUNTTYPE.MOCK); 
		
		//cTranEngine.setTranMode(TRANTIMEMODE.REALTIME);
		cTranEngine.setTranMode(TRANTIMEMODE.HISTORYMOCK);
		cTranEngine.setHistoryTimeSpan("2016-03-01", "2016-03-10");
		
		cTranEngine.run();
		
		cTranEngine.mainLoop();
		BLog.output("TEST", "--->>> MainEnd\n");
		//BLog.config_output();
	}
}
