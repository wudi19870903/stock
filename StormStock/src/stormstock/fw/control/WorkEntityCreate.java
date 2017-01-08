package stormstock.fw.control;

import java.util.List;

import stormstock.fw.acc.AccountModuleIF;
import stormstock.fw.acc.AccountControler.StockCreate;
import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.GlobalModuleObj;

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
		// BLog.output("CTRL", "    - reqCreateDateTime [%s]\n", reqCreateDateTime);
		
		Transaction.StockCreateNotify.Builder msg_builder = Transaction.StockCreateNotify.newBuilder();
		msg_builder.setDate(dateStr);
		msg_builder.setTime(timeStr);
		AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
		List<String> cSelectIDList = accIF.getStockSelectList(); // 从账户模块获选入列表
		for(int i=0;i<cSelectIDList.size();i++)
		{
			msg_builder.addStockID(cSelectIDList.get(i));
		}
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

		if(createdDateTime.compareTo(reqCreateDateTime) == 0)
		{
			List<Transaction.StockCreateCompleteNotify.CreateItem> cCreateItemList = msg.getItemList();
			BLog.output("CTRL", "    createdDateTime [%s] count(%d)\n", createdDateTime, cCreateItemList.size());
			
			for(int i=0;i<cCreateItemList.size();i++)
			{
				String stockID = cCreateItemList.get(i).getStockID();
				float price = cCreateItemList.get(i).getPrice();
				int amount = cCreateItemList.get(i).getAmount();	
				
				
				AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
				int succCnt = accIF.buyStock(stockID, price, amount); // 调用账户模块买入股票
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
