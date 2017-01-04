package stormstock.app.progtran;

import stormstock.fw.acc.MockAccount;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleManager;
import stormstock.fw.tran.TranEngine;
import stormstock.fw.tran.TranEngine.TRANMODE;

public class RunProgTran {

	public static void main(String[] args) {
		BLog.output("TEST", "--->>> MainBegin\n");
		
		//BLog.config_setTag("EVENT", true);
		//BLog.config_setTag("BASE", true);
		BLog.config_setTag("CTRL", true);
		//BLog.config_setTag("STOCKDATA", true);
				
		TranEngine cTranEngine = new TranEngine();

		cTranEngine.setStockSet(new TranStockSet());
		cTranEngine.setSelectStockStrategy(new StrategySelect());
		cTranEngine.setAccount(new MockAccount(100000.00f, 0.0016f)); 
		cTranEngine.setTranMode(TRANMODE.HISTORYMOCK);
		cTranEngine.setHistoryTimeSpan("2016-01-01", "2016-01-11");
		cTranEngine.run();
		
		cTranEngine.mainLoop();
		
		BLog.output("TEST", "--->>> MainEnd\n");
		//BLog.config_output();
	}
}