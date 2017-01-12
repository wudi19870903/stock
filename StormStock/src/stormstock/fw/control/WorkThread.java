package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.event.Transaction;
import stormstock.fw.event.Transaction.ControllerStartNotify;

public class WorkThread extends BThread {
	
	public WorkThread(Transaction.ControllerStartNotify cControllerStartNotify)
	{
		m_cControllerStartNotify = cControllerStartNotify;
		m_cWorkEntity = null;
	}
	
	@Override
	public void run() {
		
		boolean bHistoryTest = false;
		String beginDate = null;
		String endDate = null;
		
		if(m_cControllerStartNotify.getETranMode() == ControllerStartNotify.TRANMODE.HISTORYMOCK)
		{
			bHistoryTest = true;
			beginDate = m_cControllerStartNotify.getBeginDate();
			endDate = m_cControllerStartNotify.getEndDate();
			BLog.output("CTRL", "WorkThread cEntityHistoryTest.run [%s -> %s]\n", beginDate, endDate);
		}
		else if(m_cControllerStartNotify.getETranMode() == ControllerStartNotify.TRANMODE.REALTIME)
		{
			bHistoryTest = false;
			BLog.output("CTRL", "WorkThread cEntityRealTime.run\n");
		}
		
		m_cWorkEntity = new WorkEntity(bHistoryTest, beginDate, endDate);
		m_cWorkEntity.work();
	}
	
	public void onStockSelectAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		if(null != m_cWorkEntity)
		{
			m_cWorkEntity.onStockSelectAnalysisCompleteNotify(m);
		}
	}
	public void onStockCreateAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		if(null != m_cWorkEntity)
		{
			m_cWorkEntity.onStockCreateAnalysisCompleteNotify(m);
		}
	}
	public void onStockClearAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		if(null != m_cWorkEntity)
		{
			m_cWorkEntity.onStockClearAnalysisCompleteNotify(m);
		}
	}
	public void onTranInfoCollectCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		if(null != m_cWorkEntity)
		{
			m_cWorkEntity.onTranInfoCollectCompleteNotify(m);
		}
	}
	public void onGenerateReportCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		if(null != m_cWorkEntity)
		{
			m_cWorkEntity.onGenerateReportCompleteNotify(m);
		}
	}

	private WorkEntity m_cWorkEntity;
	private Transaction.ControllerStartNotify m_cControllerStartNotify;
}
