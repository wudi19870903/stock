package stormstock.fw.control;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.IAccountOpe.HoldStock;
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
		
		Transaction.StockClearNotify.Builder msg_builder = Transaction.StockClearNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		
		// 从账户拉取已持股
		AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
		List<HoldStock> cStockHoldList = accIF.getStockHoldList();
		for(int i=0;i<cStockHoldList.size();i++)
		{
			HoldStock cHoldStock = cStockHoldList.get(i);
			if(cHoldStock.totalCanSell > 0)
			{
				msg_builder.addStockID(cHoldStock.id);
			}
		}
		
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

		if(clearedDateTime.compareTo(reqClearDateTime) == 0)
		{
			List<Transaction.StockClearCompleteNotify.ClearItem> cClearItemList = msg.getItemList();
			BLog.output("CTRL", "    clearedDateTime [%s] count(%d)\n", clearedDateTime, cClearItemList.size());
			
			for(int i=0;i<cClearItemList.size();i++)
			{
				String stockID = cClearItemList.get(i).getStockID();
				float price = cClearItemList.get(i).getPrice();
				int amount = cClearItemList.get(i).getAmount();	
				
				AccountControlIF accIF = GlobalUserObj.getCurAccountControlIF();
				int succCnt = accIF.pushSellOrder(stockID, price, amount); // 调用账户模块卖出股票
				if(succCnt >= 0)
				{
					BLog.output("CTRL", "        -sellStock(%s) price(%.2f) amount(%d)\n", stockID, price,succCnt);
				}
			}
			m_WaitObjForClear.Notify();
		}
	}
	
	private String m_reqClearDate;
	private String m_reqClearTime;
	private BWaitObj m_WaitObjForClear;
}
