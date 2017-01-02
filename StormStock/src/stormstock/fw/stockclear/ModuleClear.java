package stormstock.fw.stockclear;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.Transaction;

public class ModuleClear  extends BModuleBase {

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("ClearReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCLEARNOTIFY", this, "onStockClearNotify");
	}

	@Override
	public void start() {
		m_eventRecever.startReceive();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInitialize() {
		// TODO Auto-generated method stub
		
	}

	// callback
	public void onStockClearNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.StockClearNotify stockClearNotify = (Transaction.StockClearNotify)m;

		BLog.output("SELECT", "ModuleClear onStockClearNotify\n");
		String dateStr = stockClearNotify.getDate();
		String timeStr = stockClearNotify.getTime();
		
		Transaction.StockClearCompleteNotify.Builder msg_builder = Transaction.StockClearCompleteNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.StockClearCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCLEARCOMPLETENOTIFY", msg);
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
}
