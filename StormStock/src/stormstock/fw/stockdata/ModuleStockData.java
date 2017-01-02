package stormstock.fw.stockdata;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.control.WorkThread;
import stormstock.fw.event.Transaction;

public class ModuleStockData extends BModuleBase {

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("StockDataReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_DATAUPDATENOTIFY", this, "onDataUpdateNotify");
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
	public void onDataUpdateNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.DataUpdateNotify dataUpdateNotify = (Transaction.DataUpdateNotify)m;

		BLog.output("STOCKDATA", "ModuleStockData onDataUpdateNotify\n");
		String dateStr = dataUpdateNotify.getDate();
		String timeStr = dataUpdateNotify.getTime();
		
		// 发送数据更新通知
		Transaction.DataUpdateCompleteNotify.Builder msg_builder = Transaction.DataUpdateCompleteNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.DataUpdateCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_DATAUPDATECOMPLETENOTIFY", msg);
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
}
