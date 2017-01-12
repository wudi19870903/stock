package stormstock.fw.stockclearanalyzer;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BQThread;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.StockClearAnalysis;
import stormstock.fw.event.Transaction;

public class StockClearAnalyzer  extends BModuleBase {

	public StockClearAnalyzer() {
		super("StockClearAnalyzer");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		m_qThread = new BQThread();
		m_eventRecever = new EventReceiver("ClearReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_STOCKCLEARANALYSISREQUEST", this, "onStockClearAnalysisRequest");
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
	public void onStockClearAnalysisRequest(com.google.protobuf.GeneratedMessage m) {
		BLog.output("CLEAR", "ModuleClear onStockClearNotify\n");
		StockClearAnalysis.StockClearAnalysisRequest stockClearNotify = (StockClearAnalysis.StockClearAnalysisRequest)m;
		
		String dateStr = stockClearNotify.getDate();
		String timeStr = stockClearNotify.getTime();
		List<String> stockIDList = stockClearNotify.getStockIDList();
		
		m_qThread.postRequest(new ClearWorkRequest(dateStr, timeStr, stockIDList));
	}
	
	private EventReceiver m_eventRecever;
	private BQThread m_qThread;
}
