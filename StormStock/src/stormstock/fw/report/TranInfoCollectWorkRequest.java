package stormstock.fw.report;

import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.com.GlobalUserObj;

public class TranInfoCollectWorkRequest extends BQThreadRequest {
	
	public TranInfoCollectWorkRequest(String date, String time)
	{
		m_date = date;
		m_time = time;
	}

	@Override
	public void doAction() {
		BLog.output("REPORT", "TranInfoCollectWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		AccountControlIF cAccountControlIF = GlobalUserObj.getCurAccountControlIF();
		float fAvailableMoney = cAccountControlIF.getAvailableMoney();
		List<HoldStock> cStockHoldList = cAccountControlIF.getStockHoldList();
		List<DeliveryOrder> cDeliveryOrderList = cAccountControlIF.getDeliveryOrderList();
		
		
		BLog.output("REPORT", "    -AvailableMoney: %.2f\n", fAvailableMoney);
		for(int i=0; i<cStockHoldList.size(); i++ )
		{
			HoldStock cHoldStock = cStockHoldList.get(i);
			BLog.output("REPORT", "    -HoldStock: %s %.2f %d\n", 
					cHoldStock.stockID, cHoldStock.holdAvePrice, cHoldStock.totalAmount);
		}
		for(int i=0; i<cDeliveryOrderList.size(); i++ )
		{
			DeliveryOrder cDeliveryOrder = cDeliveryOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cDeliveryOrder.tranOpe == TRANACT.SELL ) tranOpe = "SELL";
				
			BLog.output("REPORT", "    -DeliveryOrder: %s %s %.2f %.2f %d (%.2f)\n", 
					tranOpe, cDeliveryOrder.stockID, 
					cDeliveryOrder.holdAvePrice, cDeliveryOrder.tranPrice, cDeliveryOrder.amount,
					cDeliveryOrder.transactionCost);
		}
		
		
		ReportAnalysis.TranInfoCollectCompleteNotify.Builder msg_builder = ReportAnalysis.TranInfoCollectCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);

		ReportAnalysis.TranInfoCollectCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_TRANINFOCOLLECTCOMPLETENOTIFY", msg);
	}

	private String m_date;
	private String m_time;
}
