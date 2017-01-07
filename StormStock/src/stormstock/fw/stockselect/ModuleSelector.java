package stormstock.fw.stockselect;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BQThread;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.base.BEventSys.EventSender;
import stormstock.fw.event.Transaction;

public class ModuleSelector extends BModuleBase {

	public ModuleSelector() {
		super("Selector");
	}

	@Override
	public void initialize() {
		m_qThread = new BQThread();
		m_eventRecever = new EventReceiver("SelectorReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_SELECTSTOCKNOTIFY", this, "onSelectStockNotify");
	}

	@Override
	public void start() {
		m_qThread.startThread();
		m_eventRecever.startReceive();
	}

	@Override
	public void stop() {
		m_eventRecever.stopReceive();
		m_qThread.stopThread();
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
	public void onSelectStockNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.SelectStockNotify selectStockNotify = (Transaction.SelectStockNotify)m;

		BLog.output("SELECT", "ModuleSelector onSelectStockNotify\n");
		String dateStr = selectStockNotify.getDate();
		String timeStr = selectStockNotify.getTime();
		List<String> stockIDList = selectStockNotify.getStockIDList();
		
		m_qThread.postRequest(new SelectWorkRequest(dateStr, timeStr, stockIDList));
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
	private BQThread m_qThread;
}
