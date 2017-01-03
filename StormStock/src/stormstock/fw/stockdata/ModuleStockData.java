package stormstock.fw.stockdata;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.control.WorkThread;
import stormstock.fw.event.Transaction;
import stormstock.ori.stockdata.DataEngine;

public class ModuleStockData extends BModuleBase {
	
	public ModuleStockData() {
		super("StockData");
		// TODO Auto-generated constructor stub
	}

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

	@Override
	public BModuleInterface getIF() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// callback
	public void onDataUpdateNotify(com.google.protobuf.GeneratedMessage m) {
		Transaction.DataUpdateNotify dataUpdateNotify = (Transaction.DataUpdateNotify)m;

		BLog.output("STOCKDATA", "ModuleStockData onDataUpdateNotify\n");
		String dateStr = dataUpdateNotify.getDate();
		String timeStr = dataUpdateNotify.getTime();
		String reqUpdateDateTime = dateStr + " " + timeStr;
		
		String updatedDate = DataEngine.getUpdatedStocksDate();
		String updatedTime = "23:59:59";
		String updatedDateTime = updatedDate + " " + updatedTime;
		if(reqUpdateDateTime.compareTo(updatedDateTime) > 0)
		{
			// 全更新
			DataEngine.updateAllLocalStocks("2100-01-01");
			updatedDate = DataEngine.getUpdatedStocksDate();
		}
		
		// 发送数据更新通知
		Transaction.DataUpdateCompleteNotify.Builder msg_builder = Transaction.DataUpdateCompleteNotify.newBuilder();
		msg_builder.setDate(updatedDate);
		msg_builder.setTime(updatedTime);
		Transaction.DataUpdateCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_DATAUPDATECOMPLETENOTIFY", msg);
	}
	
	// event receiver
	private EventReceiver m_eventRecever;
}
