package stormstock.fw.control;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;

public class WorkEntitySelect {
	public WorkEntitySelect()
	{
		m_WaitObjForSelect = new BWaitObj();
	}
	void selectStock(String dateStr, String timeStr)
	{
		m_reqSelectDate = dateStr;
		m_reqSelectTime = timeStr;
		String reqSelectDateTime = m_reqSelectDate + " " + m_reqSelectTime;
		BLog.output("CTRL", "    - reqSelectDateTime [%s]\n", reqSelectDateTime);
		
		Transaction.SelectStockNotify.Builder msg_builder = Transaction.SelectStockNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		Transaction.SelectStockNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_SELECTSTOCKNOTIFY", msg);

		m_WaitObjForSelect.Wait();
	}
	public void onSelectStockCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		Transaction.SelectStockCompleteNotify msg = (Transaction.SelectStockCompleteNotify)m;
		String selectedDate = msg.getDate();
		String selectedTime = msg.getTime();
		String selectedDateTime = selectedDate + " " + selectedTime;
		String reqSelectDateTime = m_reqSelectDate + " " + m_reqSelectTime;

		BLog.output("CTRL", "    - selectedDateTime [%s]\n", selectedDateTime);
		if(selectedDateTime.compareTo(reqSelectDateTime) == 0)
		{
			m_WaitObjForSelect.Notify();
		}
	}
	
	private String m_reqSelectDate;
	private String m_reqSelectTime;
	private BWaitObj m_WaitObjForSelect;
}
