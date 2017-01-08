package stormstock.fw.objmgr;

import java.util.HashMap;
import java.util.Map;

import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BModuleBase.BModuleInterface;

public class GlobalModuleObj {
	/*
	 * Modules
	 */
	public static void addModule(BModuleBase moduleObj)
	{
		s_moduleMap.put(moduleObj.moduleName(), moduleObj);
	}
	public static BModuleBase getModule(String moduleName)
	{
		if(s_moduleMap.containsKey(moduleName))
			return s_moduleMap.get(moduleName);
		else
			return null;
	}
	public static BModuleInterface getModuleIF(String moduleName)
	{
		if(s_moduleMap.containsKey(moduleName))
			return s_moduleMap.get(moduleName).getIF();
		else
			return null;
	}
	
	private static Map<String, BModuleBase> s_moduleMap = new HashMap<String, BModuleBase>();
}
