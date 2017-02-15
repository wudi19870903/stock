package stormstock.fw.control;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.StockClearAnalysis;
import stormstock.fw.event.Transaction;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.GlobalUserObj;

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
		
		StockClearAnalysis.StockClearAnalysisRequest.Builder msg_builder = StockClearAnalysis.StockClearAnalysisRequest.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		
		// 从账户拉取已持股
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		accIF.getHoldStockList(null,null,cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			if(cHoldStock.availableAmount > 0)
			{
				msg_builder.addStockID(cHoldStock.stockID);
			}
		}
		
		StockClearAnalysis.StockClearAnalysisRequest msg = msg_builder.build();
		// 存在可卖股的时候，进行清仓分析
		if(msg.getStockIDList().size() > 0)
		{
			BEventSys.EventSender cSender = new BEventSys.EventSender();
			cSender.Send("BEV_TRAN_STOCKCLEARANALYSISREQUEST", msg);
			
			m_WaitObjForClear.Wait(Long.MAX_VALUE);
		}
	}
	public void onStockClearAnalysisCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		StockClearAnalysis.StockClearAnalysisCompleteNotify msg = (StockClearAnalysis.StockClearAnalysisCompleteNotify)m;
		String clearedDate = msg.getDate();
		String clearedTime = msg.getTime();
		String clearedDateTime = clearedDate + " " + clearedTime;
		String reqClearDateTime = m_reqClearDate + " " + m_reqClearTime;

		if(clearedDateTime.compareTo(reqClearDateTime) == 0)
		{
			List<StockClearAnalysis.StockClearAnalysisCompleteNotify.ClearItem> cClearItemList = msg.getItemList();
			BLog.output("CTRL", "    clearedDateTime [%s] count(%d)\n", clearedDateTime, cClearItemList.size());
			
			for(int i=0;i<cClearItemList.size();i++)
			{
				String stockID = cClearItemList.get(i).getStockID();
				float price = cClearItemList.get(i).getPrice();
				int amount = cClearItemList.get(i).getAmount();	
				
				AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
				int iPush = accIF.pushSellOrder(clearedDate, clearedTime, stockID, amount, price); // 调用账户模块卖出股票
				if(iPush == 0)
				{
					BLog.output("CTRL", "        -sellStock(%s) amount(%d) price(%.2f)\n", stockID, amount, price);
				}
			}
			m_WaitObjForClear.Notify();
		}
	}
	
	private String m_reqClearDate;
	private String m_reqClearTime;
	private BWaitObj m_WaitObjForClear;
}
