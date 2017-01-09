package stormstock.app.sample.regressiontest;

import stormstock.fw.base.BLog;
import stormstock.fw.tranengine.TranEngine;
import stormstock.fw.tranengine.TranEngine.TRANACCOUNTTYPE;
import stormstock.fw.tranengine.TranEngine.TRANTIMEMODE;

public class Application {

	public static void main(String[] args) {
		BLog.output("TEST", "--->>> MainBegin\n");
		
		//BLog.config_setTag("BASE", true);
		//BLog.config_setTag("EVENT", true);
		BLog.config_setTag("CTRL", true);
		//BLog.config_setTag("STOCKDATA", true);
		//BLog.config_setTag("SELECT", true);
		//BLog.config_setTag("CREATE", true);
		//BLog.config_setTag("CLEAR", true);
		
		TranEngine cTranEngine = new TranEngine();
		
		cTranEngine.setStockSet(new TranStockSet());
		
		cTranEngine.addStockEigen(new StockEigen.EigenSamplePriceLoc());
		cTranEngine.addStockEigen(new StockEigen.EigenSampleMADeviation());
		
		cTranEngine.setSelectStockStrategy(new StrategySelect());
		cTranEngine.setCreatePositonStrategy(new StrategyCreate());
		cTranEngine.setClearPositonStrategy(new StrategyClear());
		
		cTranEngine.setAccountType(TRANACCOUNTTYPE.MOCK); 
		
		cTranEngine.setTranMode(TRANTIMEMODE.HISTORYMOCK);
		cTranEngine.setHistoryTimeSpan("2016-01-01", "2016-01-06");
		
		cTranEngine.run();
		
		cTranEngine.mainLoop();
		BLog.output("TEST", "--->>> MainEnd\n");
		//BLog.config_output();
	}
}
