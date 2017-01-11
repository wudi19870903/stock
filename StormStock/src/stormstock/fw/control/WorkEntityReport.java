package stormstock.fw.control;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.event.StockCreateAnalysis;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.GlobalUserObj;

public class WorkEntityReport {
	public WorkEntityReport()
	{
		m_WaitObjForCreate = new BWaitObj();
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
		m_WaitObjForCreate.Wait();
	}
	public void onTranInfoCollectCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		ReportAnalysis.TranInfoCollectCompleteNotify msg = (ReportAnalysis.TranInfoCollectCompleteNotify)m;
		String tranInfoCollectedDate = msg.getDate();
		String tranInfoCollectedTime = msg.getTime();
		String tranInfoCollectedDateTime = tranInfoCollectedDate + " " + tranInfoCollectedTime;
		
		BLog.output("CTRL", "    tranInfoCollectedDateTime [%s]\n", tranInfoCollectedDateTime);
		m_WaitObjForCreate.Notify();
	}
	
	private String m_reqTranInfoCollectDate;
	private String m_reqTranInfoCollectTime;
	private BWaitObj m_WaitObjForCreate;
}
