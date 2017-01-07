package stormstock.fw.control;

import java.util.List;

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
		BLog.output("CTRL", "    reqSelectDateTime [%s]\n", reqSelectDateTime);
		
		Transaction.SelectStockNotify.Builder msg_builder = Transaction.SelectStockNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		List<String> cTranStockIDSet = StockObjFlow.getTranStockIDSet();
		for(int i=0;i<cTranStockIDSet.size();i++)
		{
			msg_builder.addStockID(cTranStockIDSet.get(i));
		}
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
		List<String> cSelectedIDList = msg.getSelectedIDList();
		String selectedDateTime = selectedDate + " " + selectedTime;
		String reqSelectDateTime = m_reqSelectDate + " " + m_reqSelectTime;
		
		String logStr = "";
		logStr += String.format("    select(%d) [ ", cSelectedIDList.size());
		if(cSelectedIDList.size() == 0) logStr += "null ";
		for(int i=0; i< cSelectedIDList.size(); i++)
		{
			String stockId = cSelectedIDList.get(i);
			logStr += String.format("%s ", stockId);
			if (i >= 7 && cSelectedIDList.size()-1 > 8) {
				logStr += String.format("... ", stockId);
				break;
			}
		}
		logStr += String.format("]");
		
		BLog.output("CTRL", "%s\n", logStr);
		
		// 保存选择结果到股票对象流
		StockObjFlow.setStockIDSelect(cSelectedIDList);

		if(selectedDateTime.compareTo(reqSelectDateTime) == 0)
		{
			m_WaitObjForSelect.Notify();
		}
	}
	
	private String m_reqSelectDate;
	private String m_reqSelectTime;
	private BWaitObj m_WaitObjForSelect;
}
