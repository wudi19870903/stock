package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;

public class WorkEntityClear {
	public WorkEntityClear()
	{
		m_WaitObjForClear = new BWaitObj();
	}
	void stockClear(String dateStr, String timeStr)
	{
		m_reqClearDate = dateStr;
		m_reqClearTime = timeStr;
		String reqClearDateTime = m_reqClearDate + " " + m_reqClearTime;
		// BLog.output("CTRL", "    - reqClearDateTime [%s]\n", reqClearDateTime);
		
		Transaction.StockClearNotify.Builder msg_builder = Transaction.StockClearNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.StockClearNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCLEARNOTIFY", msg);
		
		m_WaitObjForClear.Wait();
	}
	public void onStockClearCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		Transaction.StockClearCompleteNotify msg = (Transaction.StockClearCompleteNotify)m;
		String clearedDate = msg.getDate();
		String clearedTime = msg.getTime();
		String clearedDateTime = clearedDate + " " + clearedTime;
		String reqClearDateTime = m_reqClearDate + " " + m_reqClearTime;

		BLog.output("CTRL", "    clearedDateTime [%s]\n", clearedDateTime);
		if(clearedDateTime.compareTo(reqClearDateTime) == 0)
		{
			m_WaitObjForClear.Notify();
		}
	}
	
	private String m_reqClearDate;
	private String m_reqClearTime;
	private BWaitObj m_WaitObjForClear;
}
