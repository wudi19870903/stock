package stormstock.fw.control;

import java.util.ArrayList;
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
		
		// 从账户拉取已选股票
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		List<String> cSelectIDList = new ArrayList<String>();
		accIF.getStockSelectList(cSelectIDList); 
		for(int i=0;i<cSelectIDList.size();i++)
		{
			msg_builder.addStockID(cSelectIDList.get(i));
		}
		
		StockCreateAnalysis.StockCreateAnalysisRequest msg = msg_builder.build();
		// 存在已选股，进行建仓分析
		if(msg.getStockIDList().size() > 0)
		{
			BEventSys.EventSender cSender = new BEventSys.EventSender();
			cSender.Send("BEV_TRAN_STOCKCREATEANALYSISREQUEST", msg);
		
			m_WaitObjForCreate.Wait(Long.MAX_VALUE);
		}

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
				int iPush = accIF.pushBuyOrder(createdDate, createdTime, stockID, amount, price); // 调用账户模块买入股票
				if(iPush == 0)
				{
					BLog.output("CTRL", "        -buyStock(%s) amount(%d) price(%.2f)\n", stockID, amount, price);
				}
			}
			m_WaitObjForCreate.Notify();
		}
	}
	
	private String m_reqCreateDate;
	private String m_reqCreateTime;
	private BWaitObj m_WaitObjForCreate;
}
