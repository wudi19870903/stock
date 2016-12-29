package stormstock.app.progtran;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleManager;
import stormstock.fw.control.Controller;
import stormstock.fw.select.Selector;

public class RunProgTran {
	public static void ProgTran()
	{
//		// 设置股票集  支持分类，自定义，全局
//		ProgTran.setStockSet();
//		
//		// 设置选股策略，建仓清仓策略（可选）
//		ProgTran.setSelectStockStrategy();
//		ProgTran.setCreatePositonStrategy();
//		ProgTran.setClearPositonStrategy();
//		
//		// 设置测试时间段，支持历史时间段，实盘时间
//		ProgTran.setTimeSpan();
//		
//		// 设置账户，支持模拟与真实
//		ProgTran.setAccount(); 
		
		//		ProgTran.Start();
	}
	 
	public static void main(String[] args) {
		BLog.output("TEST", "--->>> MainBegin\n");
		//BLog.config_setTag("EVENT", true);
		BLog.config_setTag("BASE", true);
		
		BModuleManager cModuleMgr = new BModuleManager();
		cModuleMgr.regModule(new Controller()); 
		cModuleMgr.regModule(new Selector()); 
		
		cModuleMgr.initialize();
		cModuleMgr.start();
		
		// program transaction
		ProgTran();
		
		cModuleMgr.mainLoop();
		
		cModuleMgr.stop();
		cModuleMgr.unInitialize();
		
		BLog.output("TEST", "--->>> MainEnd\n");
		BLog.config_output();
	}
}
