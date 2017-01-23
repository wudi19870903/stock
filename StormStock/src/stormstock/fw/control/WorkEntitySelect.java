package stormstock.fw.control;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.StockSelectAnalysis;
import stormstock.fw.event.Transaction;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.com.GlobalUserObj;

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
		// BLog.output("CTRL", "    reqSelectDateTime [%s]\n", reqSelectDateTime);
		
		StockSelectAnalysis.StockSelectAnalysisRequest.Builder msg_builder = StockSelectAnalysis.StockSelectAnalysisRequest.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		
		List<String> cTranStockIDSet = StockObjFlow.getTranStockIDSet();
		for(int i=0;i<cTranStockIDSet.size();i++)
		{
			msg_builder.addStockID(cTranStockIDSet.get(i));
		}
		
		StockSelectAnalysis.StockSelectAnalysisRequest msg = msg_builder.build();
		// 存在交易集的时候。进行选股分析
		if(msg.getStockIDList().size() > 0)
		{
			BEventSys.EventSender cSender = new BEventSys.EventSender();
			cSender.Send("BEV_TRAN_STOCKSELECTANALYSISREQUEST", msg);

			m_WaitObjForSelect.Wait(Long.MAX_VALUE);
		}
	}
	public void onStockSelectAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		StockSelectAnalysis.StockSelectAnalysisCompleteNotify msg = (StockSelectAnalysis.StockSelectAnalysisCompleteNotify)m;
		String selectedDate = msg.getDate();
		String selectedTime = msg.getTime();
		List<String> cSelectedIDList = msg.getStockIDList();
		String selectedDateTime = selectedDate + " " + selectedTime;
		String reqSelectDateTime = m_reqSelectDate + " " + m_reqSelectTime;
		
		String logStr = "";
		logStr += String.format("    selectedDateTime[%s] (%d) [ ", selectedDateTime, cSelectedIDList.size());
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
		
		// 保存选股列表到账户模块
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		accIF.setStockSelectList(cSelectedIDList);

		if(selectedDateTime.compareTo(reqSelectDateTime) == 0)
		{
			m_WaitObjForSelect.Notify();
		}
	}
	
	private String m_reqSelectDate;
	private String m_reqSelectTime;
	private BWaitObj m_WaitObjForSelect;
}
