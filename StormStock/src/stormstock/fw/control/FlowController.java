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
public class FlowController extends BModuleBase {

	public FlowController() {
		super("FlowController");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("ControllerReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_CONTROLLERSTARTNOTIFY", this, "onTranStartNotify");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKSELECTANALYSISCOMPLETENOTIFY", this, "onStockSelectAnalysisCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCREATEANALYSISCOMPLETENOTIFY", this, "onStockCreateAnalysisCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCLEARANALYSISCOMPLETENOTIFY", this, "onStockClearAnalysisCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_TRANINFOCOLLECTCOMPLETENOTIFY", this, "onTranInfoCollectCompleteNotify");
		m_eventRecever.Subscribe("BEV_TRAN_GENERATEREPORTCOMPLETENOTIFY", this, "onGenerateReportCompleteNotify");
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
		// BLog.output("CTRL", "    Controller onTranStartNotify\n");
		m_cWorkThread = new WorkThread(startNotify);
		m_cWorkThread.startThread();

	}

	public void onStockSelectAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("CTRL", "    Controller onSelectStockCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onStockSelectAnalysisCompleteNotify(m);
		}
	}
	public void onStockCreateAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("CTRL", "    Controller onStockCreateCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onStockCreateAnalysisCompleteNotify(m);
		}
	}
	public void onStockClearAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("CTRL", "    Controller onStockClearCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onStockClearAnalysisCompleteNotify(m);
		}
	}
	public void onTranInfoCollectCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("CTRL", "    Controller onTranInfoCollectCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onTranInfoCollectCompleteNotify(m);
		}
	}
	public void onGenerateReportCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("CTRL", "    Controller onGenerateReportCompleteNotify\n");
		if(null != m_cWorkThread)
		{
			m_cWorkThread.onGenerateReportCompleteNotify(m);
		}
	}

	// event receiver
	private EventReceiver m_eventRecever;
	private WorkThread m_cWorkThread;
}
