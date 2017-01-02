package stormstock.fw.stockselect;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.base.BEventSys.EventSender;
import stormstock.fw.event.Transaction;

public class ModuleSelector extends BModuleBase {

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("SelectorReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_SELECTSTOCKNOTIFY", this, "onSelectStockNotify");
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
	public void onSelectStockNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.SelectStockNotify selectStockNotify = (Transaction.SelectStockNotify)m;

		BLog.output("SELECT", "ModuleSelector onSelectStockNotify\n");
		String dateStr = selectStockNotify.getDate();
		String timeStr = selectStockNotify.getTime();
		
		Transaction.SelectStockCompleteNotify.Builder msg_builder = Transaction.SelectStockCompleteNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.SelectStockCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_SELECTSTOCKCOMPLETENOTIFY", msg);
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
}
