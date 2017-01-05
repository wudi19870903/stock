package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;

public class WorkEntityDataUpdate {
	public WorkEntityDataUpdate()
	{
		m_WaitObjForDataUpate = new BWaitObj();
	}
	void updateStockData(String dateStr, String timeStr)
	{	
		m_reqUpdateDate = dateStr;
		m_reqUpdateTime = timeStr;
		String reqUpdateDateTime = m_reqUpdateDate + " " + m_reqUpdateTime;
		// BLog.output("CTRL", "    - reqUpdateDateTime [%s]\n", reqUpdateDateTime);
		
		// �������ݸ���֪ͨ
		Transaction.DataUpdateNotify.Builder msg_builder = Transaction.DataUpdateNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.DataUpdateNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_DATAUPDATENOTIFY", msg);
		// �ȴ��������֪ͨ
		m_WaitObjForDataUpate.Wait();
	}
	public void onDataUpdateCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		Transaction.DataUpdateCompleteNotify msg = (Transaction.DataUpdateCompleteNotify)m;
		String updatedDate = msg.getDate();
		String updatedTime = msg.getTime();
		String updatedDateTime = updatedDate + " " + updatedTime;
		String reqUpdateDateTime = m_reqUpdateDate + " " + m_reqUpdateTime;
		BLog.output("CTRL", "    updatedDateTime [%s]\n", updatedDateTime);
		if(updatedDateTime.compareTo(reqUpdateDateTime) >= 0)
		{
			m_WaitObjForDataUpate.Notify();
		}
	}
	// ��Ʊ���ݸ���
	private String m_reqUpdateDate;
	private String m_reqUpdateTime;
	private BWaitObj m_WaitObjForDataUpate;
}
