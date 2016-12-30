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
public class Controller extends BModuleBase {

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("ControllerReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_CONTROLLERSTARTNOTIFY", this, "onTranStartNotify");
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
	
	// callback
	public void onTranStartNotify(com.google.protobuf.GeneratedMessage msg) {
		Transaction.ControllerStartNotify startNotify = (Transaction.ControllerStartNotify)msg;

		m_ctrlWorkThread = new CtrlWorkThread(startNotify);
		m_ctrlWorkThread.startThread();
		
//		BEventSys.EventSender cSender = new BEventSys.EventSender();
//		cSender.Send("BEV_BASE_STORMEXIT", Base.StormExit.newBuilder().build());
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
	private CtrlWorkThread m_ctrlWorkThread;
}
