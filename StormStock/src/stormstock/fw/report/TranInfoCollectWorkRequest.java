package stormstock.fw.report;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.base.BTypeDefine.RefFloat;
import stormstock.fw.base.BImageCurve;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.tranbase.account.AccountControlIF;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
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
	}

	@Override
	public void doAction() {
		BLog.output("REPORT", "TranInfoCollectWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		StockDataIF cStockDataIF = GlobalUserObj.getCurStockDataIF();
		AccountControlIF cAccountControlIF = GlobalUserObj.getCurAccountControlIF();
		
		// 创建DailyReport
		DailyReport cDailyReport = new DailyReport(m_date);
		
		// 添加当天上证指数
		ResultHistoryData cResultHistoryData = cStockDataIF.getHistoryData("999999", m_date, m_date);
		List<StockDay> cSHCompositeList = cResultHistoryData.resultList;
		cDailyReport.fSHComposite = cSHCompositeList.get(0).close();
		
		
		float fTotalAssets = cAccountControlIF.getTotalAssets(m_date, m_time);
		RefFloat availableMoney = new RefFloat();
		cAccountControlIF.getAvailableMoney(availableMoney);
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		cAccountControlIF.getHoldStockList(m_date, m_time, cHoldStockList);
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		cAccountControlIF.getDealOrderList(cDealOrderList);
		List<String> cStockSelectList = new ArrayList<String>();
		cAccountControlIF.getStockSelectList(cStockSelectList);
		
		// 打印添加当前总资产，可用钱
		cDailyReport.fTotalAssets = fTotalAssets;
		cDailyReport.fAvailableMoney = availableMoney.value;
		BLog.output("REPORT", "    -TotalAssets: %.3f\n", fTotalAssets);
		BLog.output("REPORT", "    -AvailableMoney: %.3f\n", availableMoney.value);
		
		// 打印持股
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			BLog.output("REPORT", "    -HoldStock: %s %d %d %.3f %.3f %.3f %d\n", 
					cHoldStock.stockID,
					cHoldStock.totalAmount, cHoldStock.availableAmount, 
					cHoldStock.refPrimeCostPrice, cHoldStock.curPrice, cHoldStock.totalAmount*cHoldStock.curPrice, 
					cHoldStock.investigationDays);
		}
		// 打印当日成交
		for(int i=0; i<cDealOrderList.size(); i++ )
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cDealOrder.tranAct == TRANACT.SELL ) tranOpe = "SELL";
				
			BLog.output("REPORT", "    -DealOrder: %s %s %s %d %.3f\n", 
					cDealOrder.time, tranOpe, cDealOrder.stockID, 
					cDealOrder.amount, cDealOrder.price);
			
			// 判断清仓交割单
			boolean bIsClearDealOrder = false;
			for(int j=0; j<cHoldStockList.size(); j++ )
			{
				HoldStock cHoldStock = cHoldStockList.get(j);
				if(cHoldStock.stockID.equals(cDealOrder.stockID))
				{
					bIsClearDealOrder = false;
					break;
				}
			}
			if(bIsClearDealOrder)
			{
				// 添加清仓交割单
				cDailyReport.cClearDealOrder.add(cDealOrder);
			}
		}
		
		// 打印晚间选股
		if(cStockSelectList.size() > 0)
		{
			String logStr = "";
			logStr += String.format("    -SelectList:[ ");
			for(int i=0; i<cStockSelectList.size(); i++ )
			{
				String stockId = cStockSelectList.get(i);
				logStr += String.format("%s ", stockId);
				if (i >= 7 && cStockSelectList.size()-1 > 16) {
					logStr += String.format("... ", stockId);
					break;
				}
			}
			logStr += String.format("]");
			BLog.output("REPORT", "%s\n", logStr);
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
	
	private InfoCollector m_cInfoCollector;
}
