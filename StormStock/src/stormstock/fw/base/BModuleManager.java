package stormstock.fw.base;

import java.util.ArrayList;
import java.util.List;

public class BModuleManager {
	
	public BModuleManager()
	{
		m_moduleList = new ArrayList<BModuleBase>();
	}
	
	public void regModule(BModuleBase module)
	{
		if(null != m_moduleList)
		{
			m_moduleList.add(module);
		}
	}
	
	public void initialize()
	{
		// init modules
		for(int i = 0; i< m_moduleList.size(); i++)
		{
			BModuleBase cModule = m_moduleList.get(i);
			BLog.output( "BASE", "BModuleManager Call Initialize for module [%s]\n", cModule.moduleName());
			cModule.initialize();
		}
	}
	
	public void start()
	{
		// start modules
		for(int i = 0; i< m_moduleList.size(); i++)
		{
			BModuleBase cModule = m_moduleList.get(i);
			BLog.output( "BASE", "BModuleManager Call Start for module [%s]\n", cModule.moduleName());
			cModule.start();
		}
	}
	
	public void stop()
	{
		// stop modules
		for(int i = m_moduleList.size()-1; i>=0; i--)
		{
			BModuleBase cModule = m_moduleList.get(i);
			BLog.output( "BASE", "BModuleManager Call Stop for module [%s]\n", cModule.moduleName());
			cModule.stop();
		}
	}
	
	public void unInitialize()
	{
		// modules UnInitialize
		for(int i = m_moduleList.size()-1; i>=0; i--)
		{
			BModuleBase cModule = m_moduleList.get(i);
			BLog.output( "BASE", "BModuleManager Call UnInitialize for module [%s]\n", cModule.moduleName());
			cModule.unInitialize();
		}
	}
	
	private List<BModuleBase> m_moduleList;
}
