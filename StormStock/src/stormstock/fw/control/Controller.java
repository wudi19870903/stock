package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.event.Base;
import stormstock.fw.event.Notifytest1;
import stormstock.fw.event.Notifytest2;
import stormstock.fw.event.Transaction;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.base.BEventSys.EventSender;

/*
 * ������ģ��
 * ����ָ��ʱ���������ĵ���
 */
public class Controller extends BModuleBase {

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("ControllerReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_STARTNOTIFY", this, "onTranStartNotify");
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
		BLog.output("TEST", "Controller onTranStartNotify\n");
		Transaction.StartNotify startNotify = (Transaction.StartNotify)msg;
		BLog.output("TEST", "getHistoryTestTran %b\n", startNotify.getHistoryTestTran());
		BLog.output("TEST", "getBeginDate %s\n", startNotify.getBeginDate());
		BLog.output("TEST", "getEndDate %s\n", startNotify.getEndDate());
		
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_BASE_STORMEXIT", Base.StormExit.newBuilder().build());
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
}
