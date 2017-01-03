package stormstock.fw.objmgr;

import java.util.HashMap;
import java.util.Map;

import stormstock.fw.base.BModuleBase;
import stormstock.fw.tran.IStrategyClear;
import stormstock.fw.tran.IStrategyCreate;
import stormstock.fw.tran.IStrategySelect;
import stormstock.fw.tran.ITranStockSetFilter;

public class GlobalModuleObj {
	/*
	 * Modules
	 */
	public static BModuleBase getModule(String moduleName)
	{
		if(s_moduleMap.containsKey(moduleName))
			return s_moduleMap.get(moduleName);
		else
			return null;
	}
	public static void addModule(BModuleBase moduleObj)
	{
		s_moduleMap.put(moduleObj.moduleName(), moduleObj);
	}
	private static Map<String, BModuleBase> s_moduleMap = new HashMap<String, BModuleBase>();
}
