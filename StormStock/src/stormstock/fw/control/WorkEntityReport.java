package stormstock.fw.control;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.event.StockCreateAnalysis;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.GlobalUserObj;

public class WorkEntityReport {
	public WorkEntityReport()
	{
		m_WaitObjForTranInfoCollect = new BWaitObj();
		m_WaitObjForGenerateReport = new BWaitObj();
	}
	
	public void tranInfoCollect(String dateStr, String timeStr)
	{
		m_reqTranInfoCollectDate = dateStr;
		m_reqTranInfoCollectTime = timeStr;
		
		ReportAnalysis.TranInfoCollectRequest.Builder msg_builder = ReportAnalysis.TranInfoCollectRequest.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		
		ReportAnalysis.TranInfoCollectRequest msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_TRANINFOCOLLECTREQUEST", msg);
		m_WaitObjForTranInfoCollect.Wait(Long.MAX_VALUE);
	}
	public void onTranInfoCollectCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		ReportAnalysis.TranInfoCollectCompleteNotify msg = (ReportAnalysis.TranInfoCollectCompleteNotify)m;
		String tranInfoCollectedDate = msg.getDate();
		String tranInfoCollectedTime = msg.getTime();
		String tranInfoCollectedDateTime = tranInfoCollectedDate + " " + tranInfoCollectedTime;
		
		BLog.output("CTRL", "    tranInfoCollectedDateTime [%s]\n", tranInfoCollectedDateTime);
		m_WaitObjForTranInfoCollect.Notify();
	}
	
	private String m_reqTranInfoCollectDate;
	private String m_reqTranInfoCollectTime;
	private BWaitObj m_WaitObjForTranInfoCollect;
	
	
	// ************************************************************************************************
	
	public void generateReport(String dateStr, String timeStr)
	{
		m_reqGenerateReportDate = dateStr;
		m_reqGenerateReportTime = timeStr;
		ReportAnalysis.GenerateReportRequest.Builder msg_builder = ReportAnalysis.GenerateReportRequest.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		
		ReportAnalysis.GenerateReportRequest msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_GENERATEREPORTREQUEST", msg);
		m_WaitObjForGenerateReport.Wait(Long.MAX_VALUE);
	}
	public void onGenerateReportCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		ReportAnalysis.GenerateReportCompleteNotify msg = (ReportAnalysis.GenerateReportCompleteNotify)m;
		String generatedReportDate = msg.getDate();
		String generatedReportTime = msg.getTime();
		String generatedReportDateTime = generatedReportDate + " " + generatedReportTime;
		
		BLog.output("CTRL", "    generatedReportDateTime [%s]\n", generatedReportDateTime);
		m_WaitObjForGenerateReport.Notify();
	}
	
	private String m_reqGenerateReportDate;
	private String m_reqGenerateReportTime;
	private BWaitObj m_WaitObjForGenerateReport;
}
