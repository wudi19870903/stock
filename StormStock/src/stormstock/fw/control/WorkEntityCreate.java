package stormstock.fw.control;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.StockCreateAnalysis;
import stormstock.fw.event.Transaction;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.com.GlobalUserObj;

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
		//BLog.output("CTRL", "    - reqCreateDateTime [%s]\n", reqCreateDateTime);
		
		StockCreateAnalysis.StockCreateAnalysisRequest.Builder msg_builder = StockCreateAnalysis.StockCreateAnalysisRequest.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		
		// ���˻���ȡ��ѡ��Ʊ
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		List<String> cSelectIDList = accIF.getStockSelectList(); 
		for(int i=0;i<cSelectIDList.size();i++)
		{
			msg_builder.addStockID(cSelectIDList.get(i));
		}
		
		StockCreateAnalysis.StockCreateAnalysisRequest msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCREATEANALYSISREQUEST", msg);
	
		m_WaitObjForCreate.Wait();
	}
	public void onStockCreateAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		StockCreateAnalysis.StockCreateAnalysisCompleteNotify msg = (StockCreateAnalysis.StockCreateAnalysisCompleteNotify)m;
		String createdDate = msg.getDate();
		String createdTime = msg.getTime();
		String createdDateTime = createdDate + " " + createdTime;
		String reqCreateDateTime = m_reqCreateDate + " " + m_reqCreateTime;

		if(createdDateTime.compareTo(reqCreateDateTime) == 0)
		{
			List<StockCreateAnalysis.StockCreateAnalysisCompleteNotify.CreateItem> cCreateItemList = msg.getItemList();
			BLog.output("CTRL", "    createdDateTime [%s] count(%d)\n", createdDateTime, cCreateItemList.size());
			
			for(int i=0;i<cCreateItemList.size();i++)
			{
				String stockID = cCreateItemList.get(i).getStockID();
				float price = cCreateItemList.get(i).getPrice();
				int amount = cCreateItemList.get(i).getAmount();	
				
				
				AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
				int succCnt = accIF.pushBuyOrder(stockID, price, amount); // �����˻�ģ�������Ʊ
				if(succCnt > 0)
				{
					BLog.output("CTRL", "        -buyStock(%s) price(%.2f) amount(%d)\n", stockID, price,succCnt);
				}
			}
			m_WaitObjForCreate.Notify();
		}
	}
	
	private String m_reqCreateDate;
	private String m_reqCreateTime;
	private BWaitObj m_WaitObjForCreate;
}
