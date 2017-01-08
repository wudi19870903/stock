package stormstock.fw.stockcreate;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BQThread;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.Transaction;

public class ModuleCreate extends BModuleBase {

	public ModuleCreate() {
		super("Create");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		m_qThread = new BQThread();
		m_eventRecever = new EventReceiver("CreateReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCREATENOTIFY", this, "onStockCreateNotify");
	}

	@Override
	public void start() {
		m_qThread.startThread();
		m_eventRecever.startReceive();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		m_eventRecever.stopReceive();
		m_qThread.stopThread();
	}

	@Override
	public void unInitialize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public BModuleInterface getIF() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// callback
	public void onStockCreateNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.StockCreateNotify stockCreateNotify = (Transaction.StockCreateNotify)m;

		BLog.output("CREATE", "ModuleCreate onStockCreateNotify\n");
		String dateStr = stockCreateNotify.getDate();
		String timeStr = stockCreateNotify.getTime();
		List<String> stockIDList = stockCreateNotify.getStockIDList();
		
		m_qThread.postRequest(new CreateWorkRequest(dateStr, timeStr, stockIDList));
	}
	
	private EventReceiver m_eventRecever;
	private BQThread m_qThread;
}
