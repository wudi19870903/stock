package stormstock.fw.stockcreate;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.Transaction;

public class ModuleCreate extends BModuleBase {

	public ModuleCreate() {
		super("Create");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		m_eventRecever = new EventReceiver("CreateReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCREATENOTIFY", this, "onStockCreateNotify");
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
		
		Transaction.StockCreateCompleteNotify.Builder msg_builder = Transaction.StockCreateCompleteNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.StockCreateCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCREATECOMPLETENOTIFY", msg);
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
}
