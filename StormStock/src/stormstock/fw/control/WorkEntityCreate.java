package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;

public class WorkEntityCreate {
	public WorkEntityCreate()
	{
		m_WaitObjForCreate = new BWaitObj();
	}
	void stockCreate(String dateStr, String timeStr)
	{
		m_reqCreateDate = dateStr;
		m_reqCreateTime = timeStr;
		String reqCreateDateTime = m_reqCreateDate + " " + m_reqCreateTime;
		BLog.output("CTRL", "    - reqCreateDateTime [%s]\n", reqCreateDateTime);
		
		Transaction.StockCreateNotify.Builder msg_builder = Transaction.StockCreateNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.StockCreateNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCREATENOTIFY", msg);
	
		m_WaitObjForCreate.Wait();
	}
	public void onStockCreateCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		Transaction.StockCreateCompleteNotify msg = (Transaction.StockCreateCompleteNotify)m;
		String createdDate = msg.getDate();
		String createdTime = msg.getTime();
		String createdDateTime = createdDate + " " + createdTime;
		String reqCreateDateTime = m_reqCreateDate + " " + m_reqCreateTime;

		BLog.output("CTRL", "    - createdDateTime [%s]\n", createdDateTime);
		if(createdDateTime.compareTo(reqCreateDateTime) == 0)
		{
			m_WaitObjForCreate.Notify();
		}
	}
	
	private String m_reqCreateDate;
	private String m_reqCreateTime;
	private BWaitObj m_WaitObjForCreate;
}
