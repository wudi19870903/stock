package stormstock.fw.base;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.EventDef;

public class BModuleManager {
	
	public BModuleManager()
	{
		m_waitObj = new Object();
		m_exitFlag = false;
		
		// init eventsys
		BEventSys.registerEventMap(EventDef.s_EventNameMap);
		BEventSys.start();
		
		m_moduleList = new ArrayList<BModuleBase>();
		m_eventRecever = new EventReceiver("BModuleManager");
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
		m_eventRecever.Subscribe("BEV_BASE_STORMEXIT", this, "onStormExit");
		m_eventRecever.startReceive();
		
		// init modules
		for(int i = 0; i< m_moduleList.size(); i++)
		{
			BModuleBase cModule = m_moduleList.get(i);
			String moduleName = cModule.getClass().getSimpleName();
			BLog.output( "BASE", "BModuleManager Call Initialize for module [%s]\n", moduleName);
			cModule.initialize();
		}
	}
	
	public void start()
	{
		// start modules
		for(int i = 0; i< m_moduleList.size(); i++)
		{
			BModuleBase cModule = m_moduleList.get(i);
			String moduleName = cModule.getClass().getSimpleName();
			BLog.output( "BASE", "BModuleManager Call Start for module [%s]\n", moduleName);
			cModule.start();
		}
	}
	
	public void mainLoop()
	{
		BLog.output( "BASE", "BModuleManager enter mainLoop ...\n");
		while(!m_exitFlag)
		{
			try {
				synchronized (m_waitObj) {
					m_waitObj.wait(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void stop()
	{
		// stop modules
		for(int i = m_moduleList.size()-1; i>=0; i--)
		{
			BModuleBase cModule = m_moduleList.get(i);
			String moduleName = cModule.getClass().getSimpleName();
			BLog.output( "BASE", "BModuleManager Call Stop for module [%s]\n", moduleName);
			cModule.stop();
		}
	}
	
	public void unInitialize()
	{
		// modules UnInitialize
		for(int i = m_moduleList.size()-1; i>=0; i--)
		{
			BModuleBase cModule = m_moduleList.get(i);
			String moduleName = cModule.getClass().getSimpleName();
			BLog.output( "BASE", "BModuleManager Call UnInitialize for module [%s]\n", moduleName);
			cModule.unInitialize();
		}
		
		//m_eventRecever.stop();
		
		// eventsys stop
		BEventSys.stop();
	}
	
	public void onStormExit(com.google.protobuf.GeneratedMessage msg) {
		m_exitFlag = true;
		synchronized (m_waitObj) {
			m_waitObj.notify();
		}
	}
	
	private Object m_waitObj;
	private boolean m_exitFlag;
	
	private List<BModuleBase> m_moduleList;
	private EventReceiver m_eventRecever;
}
