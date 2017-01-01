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
 * ������ģ��
 * ����ָ��ʱ���������ĵ���
 */
public class ModuleController extends BModuleBase {

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

		BLog.output("CTRL", "Controller onTranStartNotify\n");
		
		m_cWorkThread = new WorkThread(startNotify);
		m_cWorkThread.startThread();

	}
	
	// event receiver
	private EventReceiver m_eventRecever;
	private WorkThread m_cWorkThread;
}
