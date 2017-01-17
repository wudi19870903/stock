package stormstock.fw.report;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.base.BImageCurve;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.account.AccountPublicDef.TRANACT;
import stormstock.fw.tranbase.com.GlobalUserObj;
import stormstock.fw.tranbase.stockdata.StockDataIF;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockDataIF.ResultHistoryData;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.report.InfoCollector.DailyReport;

public class TranInfoCollectWorkRequest extends BQThreadRequest {
	
	public TranInfoCollectWorkRequest(String date, String time, InfoCollector cInfoCollector)
	{
		m_date = date;
		m_time = time;
		m_cInfoCollector = cInfoCollector;
		if(m_bCreateReport){
			String imgfilename = date + "-report.jpg";
			m_imgReport = new BImageCurve(1600,900,imgfilename);
		}
	}

	@Override
	public void doAction() {
		BLog.output("REPORT", "TranInfoCollectWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		// 创建DailyReport
		DailyReport cDailyReport = new DailyReport(m_date);
		
		StockDataIF cStockDataIF = GlobalUserObj.getCurStockDataIF();
		AccountControlIF cAccountControlIF = GlobalUserObj.getCurAccountControlIF();
		float fTotalAssets = cAccountControlIF.getTotalAssets(m_date, m_time);
		float fAvailableMoney = cAccountControlIF.getAvailableMoney();
		List<HoldStock> cStockHoldList = cAccountControlIF.getStockHoldList(m_date, m_time);
		List<DeliveryOrder> cDeliveryOrderList = cAccountControlIF.getDeliveryOrderList();
		
		cDailyReport.fTotalAssets = fTotalAssets;
		cDailyReport.fAvailableMoney = fAvailableMoney;
		
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData("999999", m_date, m_date);
		List<StockDay> cSHCompositeList = cResultHistoryData.resultList;
		cDailyReport.fSHComposite = cSHCompositeList.get(0).close();
		
		BLog.output("REPORT", "    -TotalAssets: %.3f\n", fTotalAssets);
		BLog.output("REPORT", "    -AvailableMoney: %.3f\n", fAvailableMoney);
		for(int i=0; i<cStockHoldList.size(); i++ )
		{
			HoldStock cHoldStock = cStockHoldList.get(i);
			BLog.output("REPORT", "    -HoldStock: %s %s %s %.3f %.3f %d %.3f(%.3f) %d\n", 
					cHoldStock.stockID, cHoldStock.createDate, cHoldStock.createTime,
					cHoldStock.holdAvePrice, cHoldStock.curPrice, cHoldStock.totalAmount,
					cHoldStock.curPrice*cHoldStock.totalAmount, cHoldStock.transactionCosts,
					cHoldStock.holdDayCnt);
		}
		for(int i=0; i<cDeliveryOrderList.size(); i++ )
		{
			DeliveryOrder cDeliveryOrder = cDeliveryOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cDeliveryOrder.tranOpe == TRANACT.SELL ) tranOpe = "SELL";
				
			BLog.output("REPORT", "    -DeliveryOrder: %s %s %s %s %.3f %.3f %d %.3f(%.3f)\n", 
					cDeliveryOrder.date, cDeliveryOrder.time,
					tranOpe, cDeliveryOrder.stockID, 
					cDeliveryOrder.holdAvePrice, cDeliveryOrder.tranPrice, cDeliveryOrder.amount,
					cDeliveryOrder.tranPrice*cDeliveryOrder.amount, cDeliveryOrder.transactionCost);
			
			// 判断清仓交割单
			boolean bIsClearDeliveryOrder = false;
			for(int j=0; j<cStockHoldList.size(); j++ )
			{
				HoldStock cHoldStock = cStockHoldList.get(j);
				if(cHoldStock.stockID.equals(cDeliveryOrder.stockID))
				{
					bIsClearDeliveryOrder = false;
					break;
				}
			}
			if(bIsClearDeliveryOrder)
			{
				cDailyReport.cClearDeliveryOrder.add(cDeliveryOrder);
			}
		}
		
		// 添加DailyReport
		m_cInfoCollector.addDailyReport(cDailyReport);
		
		ReportAnalysis.TranInfoCollectCompleteNotify.Builder msg_builder = ReportAnalysis.TranInfoCollectCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);

		ReportAnalysis.TranInfoCollectCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_TRANINFOCOLLECTCOMPLETENOTIFY", msg);
	}

	private String m_date;
	private String m_time;
	
	private boolean m_bCreateReport;
	private BImageCurve m_imgReport;
	
	private InfoCollector m_cInfoCollector;
}
