package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.event.Notifytest1;
import stormstock.fw.event.Notifytest2;
import stormstock.fw.event.Transaction;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.base.BEventSys.EventSender;

/*
 * 控制器模块
 * 负责指定时间点做任务的调度
 */
public class ModuleController extends BModuleBase {

	public ModuleController() {
		super("Controller");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("ControllerReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_CONTROLLERSTARTNOTIFY", this, "onTranStartNotify");
		m_eventRecever.Subscribe("BEV_TRAN_DATAUPDATECOMPLETENOTIFY", this, "onDataUpdateCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_SELECTSTOCKCOMPLETENOTIFY", this, "onSelectStockCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCREATECOMPLETENOTIFY", this, "onStockCreateCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCLEARCOMPLETENOTIFY", this, "onStockClearCompleteNotify");
		
	}

	@Override
	public void start() {
		m_eventRecever.startReceive();
	}

	@Override
	public void stop() {

	}

	@Override
	public void unInitialize() {
		
	}
	
	@Override
	public BModuleInterface getIF() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// callback
	public void onTranStartNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.ControllerStartNotify startNotify = (Transaction.ControllerStartNotify)m;

		BLog.output("CTRL", "    Controller onTranStartNotify\n");
		
		m_cWorkThread = new WorkThread(startNotify);
		m_cWorkThread.startThread();

	}
	public void onDataUpdateCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		BLog.output("CTRL", "    Controller onDataUpdateCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onDataUpdateCompleteNotify(m);
		}
	}
	public void onSelectStockCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		BLog.output("CTRL", "    Controller onSelectStockCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onSelectStockCompleteNotify(m);
		}
	}
	public void onStockCreateCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		BLog.output("CTRL", "    Controller onStockCreateCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onStockCreateCompleteNotify(m);
		}
	}
	public void onStockClearCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		BLog.output("CTRL", "    Controller onStockClearCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onStockClearCompleteNotify(m);
		}
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
	private WorkThread m_cWorkThread;
}
