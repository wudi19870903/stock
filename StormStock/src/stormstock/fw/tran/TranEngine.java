package stormstock.fw.tran;

import stormstock.fw.acc.AccountModule;
import stormstock.fw.acc.IAccount;
import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleManager;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.control.ModuleController;
import stormstock.fw.event.EventDef;
import stormstock.fw.event.Transaction;
import stormstock.fw.event.Transaction.ControllerStartNotify;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.objmgr.GlobalModuleObj;
import stormstock.fw.report.ReportModule;
import stormstock.fw.stockclear.ModuleClear;
import stormstock.fw.stockcreate.ModuleCreate;
import stormstock.fw.stockselect.ModuleSelector;
import stormstock.fw.tran.strategy.DefaultStrategyClear;
import stormstock.fw.tran.strategy.DefaultStrategyCreate;
import stormstock.fw.tran.strategy.DefaultStrategySelect;
import stormstock.fw.tran.strategy.IStrategyClear;
import stormstock.fw.tran.strategy.IStrategyCreate;
import stormstock.fw.tran.strategy.IStrategySelect;
import stormstock.fw.stockdata.ModuleStockData;

public class TranEngine {
	
	public enum TRANMODE
	{
		HISTORYMOCK,
		REALTIME,
	}
	
	public TranEngine()
	{
		m_waitObj = new Object();
		m_exitFlag = false;
		// init eventsys
		BLog.output( "BASE", "BModuleManager EventSys Start\n");
		BEventSys.registerEventMap(EventDef.s_EventNameMap);
		BEventSys.start();
		// subscribe BEV_BASE_STORMEXIT
		m_eventRecever = new EventReceiver("BModuleManager");
		m_eventRecever.Subscribe("BEV_TRAN_ENGINEEXIT", this, "onTranEngineExitNotify");
		m_eventRecever.startReceive();
		
		// start modules
		m_cModuleMgr = new BModuleManager();
		m_cModuleMgr.regModule(new ModuleController());  // Controller Module
		m_cModuleMgr.regModule(new ModuleStockData());   // StockData Module
		m_cModuleMgr.regModule(new ModuleSelector()); 	// Selector Module
		m_cModuleMgr.regModule(new ModuleCreate()); 	// Create Module
		m_cModuleMgr.regModule(new ModuleClear()); 		// Clear Module
		m_cModuleMgr.regModule(new AccountModule()); 	// AccountModule Module
		m_cModuleMgr.regModule(new ReportModule()); 	// ReportEngine Module
		m_cModuleMgr.initialize();
		m_cModuleMgr.start();
		
		// 初始化用户设置
		m_cTranStockSet = null; 
		m_cStrategySelect = null; 
		m_cStrategyCreate = null;
		m_cStrategyClear = null;
		m_eTranMode = null;
		m_beginDate = null; 
		m_endDate = null; 
		m_cAcc = null; 
	}
	
	public void onTranEngineExitNotify(com.google.protobuf.GeneratedMessage msg) {
		m_exitFlag = true;
		synchronized (m_waitObj) {
			m_waitObj.notify();
		}
	}
	public void mainLoop()
	{
		BLog.output( "BASE", "BModuleManager enter mainLoop ...\n");
		// waiting exit cmd
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
		// stop modules
		m_cModuleMgr.stop();
		m_cModuleMgr.unInitialize();
		// eventsys stop
		BLog.output( "BASE", "BModuleManager EventSys Stop\n");
		BEventSys.stop();
	}
	
	// send exit cmd 
	public void exitCommand()
	{
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_ENGINEEXIT", Transaction.TranEngineExitNotify.newBuilder().build());
	}
	
	public void setStockSet(ITranStockSetFilter cTranStockSet)
	{
		m_cTranStockSet = cTranStockSet;
	}
	
	public void setSelectStockStrategy(IStrategySelect cStrategySelect)
	{
		m_cStrategySelect = cStrategySelect;
	}
	
	public void setCreatePositonStrategy(IStrategyCreate cStrategyCreate)
	{
		m_cStrategyCreate = cStrategyCreate;
	}
	
	public void setClearPositonStrategy(IStrategyClear cStrategyClear)
	{
		m_cStrategyClear = cStrategyClear;
	}
	
	public void setTranMode(TRANMODE eTranMode)
	{
		m_eTranMode = eTranMode;
	}
	
	public void setHistoryTimeSpan(String beginDate, String endDate)
	{
		m_beginDate = beginDate;
		m_endDate = endDate;
	}
	
	public void setAccount(IAccount cAcc)
	{
		m_cAcc = cAcc;
	}
	
	public void run()
	{
		if(null == m_cTranStockSet)
		{
			m_cTranStockSet = new DefaultTranStockSet();
			BLog.output("TRAN", "m_cTranStockSet is null, set default\n");
		}
		if(null == m_cStrategySelect)
		{
			m_cStrategySelect = new DefaultStrategySelect();
			BLog.output("TRAN", "m_cStrategySelect is null, set default\n");
		}
		if(null == m_cStrategyCreate)
		{
			m_cStrategyCreate = new DefaultStrategyCreate();
			BLog.output("TRAN", "m_cStrategyCreate is null, set default\n");
		}
		if(null == m_cStrategyClear)
		{
			m_cStrategyClear = new DefaultStrategyClear();
			BLog.output("TRAN", "m_cStrategyClear is null, set default\n");
		}
		if(null == m_eTranMode)
		{
			BLog.error("TRAN", "m_eTranMode is null!\n");
			exitCommand();
			return;
		}
		
		// 保存对象
		GlobalUserObj.setCurrentTranStockSetFilter(m_cTranStockSet);
		GlobalUserObj.setCurrentStrategySelect(m_cStrategySelect);
		GlobalUserObj.setCurrentStrategyCreate(m_cStrategyCreate);
		GlobalUserObj.setCurrentStrategyClear(m_cStrategyClear);
		
		// 发送开始交易命令到控制器
		BLog.output("TRAN", "Start Trasection\n");
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		Transaction.ControllerStartNotify.Builder msg_builder = Transaction.ControllerStartNotify.newBuilder();
		if (TRANMODE.HISTORYMOCK == m_eTranMode)
		{
			if(null == m_beginDate || null == m_endDate)
			{
				BLog.error("TRAN", "HISTORYMOCK need set beginDate endDate!\n");
				exitCommand();
				return;
			}
			msg_builder.setETranMode(ControllerStartNotify.TRANMODE.HISTORYMOCK);
			msg_builder.setBeginDate(m_beginDate);
			msg_builder.setEndDate(m_endDate);
		}
		else if (TRANMODE.REALTIME == m_eTranMode)
		{
			if(null != m_beginDate || null != m_endDate)
			{
				BLog.error("TRAN", "REALTIME need NOT to set beginDate or endDate!\n");
				exitCommand();
				return;
			}
			msg_builder.setETranMode(ControllerStartNotify.TRANMODE.REALTIME);
		}
		Transaction.ControllerStartNotify msg = msg_builder.build();
		cSender.Send("BEV_TRAN_CONTROLLERSTARTNOTIFY", msg);
	}
	
	// 用户设置
	ITranStockSetFilter      m_cTranStockSet; 
	IStrategySelect          m_cStrategySelect;
	IStrategyCreate          m_cStrategyCreate;
	IStrategyClear           m_cStrategyClear;
	TRANMODE                 m_eTranMode;
	String                   m_beginDate;
	String                   m_endDate;
	IAccount                 m_cAcc;
	
	// module manager
	private BModuleManager m_cModuleMgr;
	// exit flag
	private Object m_waitObj;
	private boolean m_exitFlag;
	// event receiver
	private EventReceiver m_eventRecever;
}
