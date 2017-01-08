package stormstock.fw.control;

import java.util.List;

import stormstock.fw.acc.AccountModuleIF;
import stormstock.fw.acc.AccountControler.StockCreate;
import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.GlobalModuleObj;

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
		AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
		List<StockCreate> cStockCreateList = accIF.getStockCreateList(); 
		for(int i=0;i<cStockCreateList.size();i++)
		{
			StockCreate cStockCreate = cStockCreateList.get(i);
			msg_builder.addStockID(cStockCreate.stockID);
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
				
				AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
				int succCnt = accIF.sellStock(stockID, price, amount); // 调用账户模块卖出股票
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
