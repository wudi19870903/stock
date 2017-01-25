package stormstock.fw.tranengine;

import java.util.HashMap;
import java.util.Map;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleManager;
import stormstock.fw.base.BPath;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.EventDef;
import stormstock.fw.event.Transaction;
import stormstock.fw.event.Transaction.ControllerStartNotify;
import stormstock.fw.control.FlowController;
import stormstock.fw.report.ReportModule;
import stormstock.fw.stockclearanalyzer.StockClearAnalyzer;
import stormstock.fw.stockcreateanalyzer.StockCreateAnalyzer;
import stormstock.fw.stockselectanalyzer.StockSelectAnalyzer;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.com.IEigenStock;
import stormstock.fw.tranbase.com.IStrategyClear;
import stormstock.fw.tranbase.com.IStrategyCreate;
import stormstock.fw.tranbase.com.IStrategySelect;
import stormstock.fw.tranbase.com.ITranStockSetFilter;
import stormstock.fw.tranbase.stockdata.StockDataIF;

public class TranEngine {
	
	public enum TRANTIMEMODE
	{
		HISTORYMOCK,
		REALTIME,
	}
	
	public enum TRANACCOUNTTYPE
	{
		MOCK,
		REAL,
	}
	
	public TranEngine()
	{
		m_waitObj = new Object();
		m_exitFlag = false;
		// log start 
		BLog.start();
		
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
		m_cModuleMgr.regModule(new FlowController());  // Controller Module
		m_cModuleMgr.regModule(new StockSelectAnalyzer()); 	// Selector Module
		m_cModuleMgr.regModule(new StockCreateAnalyzer()); 	// Create Module
		m_cModuleMgr.regModule(new StockClearAnalyzer()); 		// Clear Module
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
		m_eAccType = null; 
		m_cEigenMap = new HashMap<String, IEigenStock>();
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
		// log stop
		BLog.stop();
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
	
	public void setTranMode(TRANTIMEMODE eTranMode)
	{
		m_eTranMode = eTranMode;
	}
	
	public void setHistoryTimeSpan(String beginDate, String endDate)
	{
		m_beginDate = beginDate;
		m_endDate = endDate;
	}
	
	public void setAccountType(TRANACCOUNTTYPE accType)
	{
		m_eAccType = accType;
	}
	
	public void addStockEigen(IEigenStock cIEigenStock)
	{
		String name = cIEigenStock.getClass().getSimpleName();
		m_cEigenMap.put(name, cIEigenStock);
	}
	
	public void run()
	{
		if(null == m_eTranMode)
		{
			BLog.error("TRAN", "m_eTranMode is null!\n");
			exitCommand();
			return; // 交易模式未设置 直接退出
		}
		
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
		if(null == m_eAccType)
		{
			m_eAccType = TRANACCOUNTTYPE.MOCK;
			BLog.output("TRAN", "m_cAcc is null!, set default\n");
		}
		
		// 保存对象
		GlobalUserObj.setCurrentTranStockSetFilter(m_cTranStockSet); // 当前交易集
		GlobalUserObj.setCurrentStrategySelect(m_cStrategySelect); // 当前选股策略
		GlobalUserObj.setCurrentStrategyCreate(m_cStrategyCreate); // 当前建仓策略
		GlobalUserObj.setCurrentStrategyClear(m_cStrategyClear); // 当前清仓策略
		GlobalUserObj.setCurrentStockEigenMap(m_cEigenMap); // 当前特征表
		
		// 账户控制器接口全局设置
		AccountControlIF cAccountControlIF = new AccountControlIF();
		if(m_eAccType == TRANACCOUNTTYPE.MOCK)
		{
			cAccountControlIF.setAccountType(ACCOUNTTYPE.MOCK);
		}
		else
		{
			cAccountControlIF.setAccountType(ACCOUNTTYPE.REAL);
		}
		GlobalUserObj.setCurrentAccountControlIF(cAccountControlIF);
		GlobalUserObj.getCurAccountControlIF().printAccount(null, null);
		
		// 股票数据接口全局设置
		StockDataIF cStockDataIF = new StockDataIF();
		GlobalUserObj.setCurrentStockDataIF(cStockDataIF);
		
		// 发送开始交易命令到控制器
		BLog.output("TRAN", "Start Trasection\n");
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		Transaction.ControllerStartNotify.Builder msg_builder = Transaction.ControllerStartNotify.newBuilder();
		if (TRANTIMEMODE.HISTORYMOCK == m_eTranMode)
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
		else if (TRANTIMEMODE.REALTIME == m_eTranMode)
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
	TRANTIMEMODE             m_eTranMode;
	String                   m_beginDate;
	String                   m_endDate;
	TRANACCOUNTTYPE          m_eAccType;
	Map<String, IEigenStock> m_cEigenMap;
	
	// module manager
	private BModuleManager m_cModuleMgr;
	// exit flag
	private Object m_waitObj;
	private boolean m_exitFlag;
	// event receiver
	private EventReceiver m_eventRecever;
}
