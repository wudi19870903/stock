package stormstock.fw.tran;

import stormstock.fw.acc.AccountModule;
import stormstock.fw.acc.IAccount;
import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleManager;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.control.Controller;
import stormstock.fw.event.Base;
import stormstock.fw.event.EventDef;
import stormstock.fw.event.Notifytest2;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.ObjManager;
import stormstock.fw.report.ReportModule;
import stormstock.fw.select.Selector;

public class TranEngine {
	
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
		m_eventRecever.Subscribe("BEV_BASE_STORMEXIT", this, "onStormExit");
		m_eventRecever.startReceive();
		
		// start modules
		m_cModuleMgr = new BModuleManager();
		m_cModuleMgr.regModule(new Controller());  // Controller Module
		m_cModuleMgr.regModule(new Selector()); 	// Selector Module
		m_cModuleMgr.regModule(new AccountModule()); 	// AccountModule Module
		m_cModuleMgr.regModule(new ReportModule()); 	// ReportEngine Module
		m_cModuleMgr.initialize();
		m_cModuleMgr.start();
		
		// 初始化用户设置
		m_cTranStockSet = null; 
		m_cStrategySelect = null; 
		m_beginDate = null; 
		m_endDate = null; 
		m_cAcc = null; 
	}
	
	public void onStormExit(com.google.protobuf.GeneratedMessage msg) {
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
		cSender.Send("BEV_BASE_STORMEXIT", Base.StormExit.newBuilder().build());
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
	
	public void setTimeSpan(String beginDate, String endDate)
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
		
		// 保存对象
		ObjManager.setCurrentTranStockSetFilter(m_cTranStockSet);
		ObjManager.setCurrentStrategySelect(m_cStrategySelect);
		ObjManager.setCurrentStrategyCreate(m_cStrategyCreate);
		ObjManager.setCurrentStrategyClear(m_cStrategyClear);
		
		// 发送开始交易命令到控制器
		BLog.output("TRAN", "Start Trasection\n");
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		Transaction.StartNotify.Builder msg_builder = Transaction.StartNotify.newBuilder();
		if(null == m_beginDate && null == m_endDate)
		{
			msg_builder.setHistoryTestTran(false);
		}
		else
		{
			msg_builder.setHistoryTestTran(true);
			msg_builder.setBeginDate(m_beginDate);
			msg_builder.setEndDate(m_endDate);
		}
		Transaction.StartNotify msg = msg_builder.build();
		cSender.Send("BEV_TRAN_STARTNOTIFY", msg);
	}
	
	// 用户设置
	ITranStockSetFilter      m_cTranStockSet; 
	IStrategySelect          m_cStrategySelect;
	IStrategyCreate          m_cStrategyCreate;
	IStrategyClear           m_cStrategyClear;
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
