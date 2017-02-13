package stormstock.fw.report;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BQThread;
import stormstock.fw.base.BEventSys.EventReceiver;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.event.StockClearAnalysis;
import stormstock.fw.stockclearanalyzer.ClearWorkRequest;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.GlobalUserObj;

public class ReportModule extends BModuleBase {

	public ReportModule() {
		super("Report");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		m_infoCollector = new InfoCollector();
		m_qThread = new BQThread();
		m_eventRecever = new EventReceiver("ReportReceiver");
		m_eventRecever.Subscribe("BEV_TRAN_TRANINFOCOLLECTREQUEST", this, "onTranInfoCollectRequest");
		m_eventRecever.Subscribe("BEV_TRAN_GENERATEREPORTREQUEST", this, "onGenerateReportRequest");
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
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
	
	public void onTranInfoCollectRequest(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("REPORT", "ReportModule onTranInfoCollectRequest\n");
		ReportAnalysis.TranInfoCollectRequest tranInfoCollectRequest = (ReportAnalysis.TranInfoCollectRequest)m;
		
		String dateStr = tranInfoCollectRequest.getDate();
		String timeStr = tranInfoCollectRequest.getTime();
		
		m_qThread.postRequest(new TranInfoCollectWorkRequest(dateStr, timeStr, m_infoCollector));
	}
	
	public void onGenerateReportRequest(com.google.protobuf.GeneratedMessage m) {
		// BLog.output("REPORT", "ReportModule onGenerateReportRequest\n");
		ReportAnalysis.GenerateReportRequest generateReportRequest = (ReportAnalysis.GenerateReportRequest)m;
		
		String dateStr = generateReportRequest.getDate();
		String timeStr = generateReportRequest.getTime();
		
		m_qThread.postRequest(new GenerateReportRequest(dateStr, timeStr, m_infoCollector));
	}

	private EventReceiver m_eventRecever;
	private InfoCollector m_infoCollector;
	private BQThread m_qThread;
}
