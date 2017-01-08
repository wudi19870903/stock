package stormstock.fw.stockclear;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.acc.AccountControler.StockCreate;
import stormstock.fw.acc.AccountModuleIF;
import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.GlobalModuleObj;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.stockdata.StockTime;
import stormstock.fw.tran.strategy.StockContext;
import stormstock.fw.tran.strategy.IStrategyClear;
import stormstock.fw.tran.strategy.IStrategyClear.ClearResult;

public class ClearWorkRequest  extends BQThreadRequest {
	/*
	 * ClearResultWrapper类
	 */
	static private class ClearResultWrapper {
		public ClearResultWrapper(){
			clearRes = new ClearResult();
		}
		public String stockId;
		public float fPrice;
		public ClearResult clearRes;
	}
	
	public ClearWorkRequest(String date, String time, List<String> stockIDList)
	{
		m_date = date;
		m_time = time;
		m_stockIDList = stockIDList;
	}
	
	@Override
	public void doAction() {
		
		BLog.output("CLEAR", "ClearWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		IStrategyClear cIStrategyClear = GlobalUserObj.getCurrentStrategyClear();
		List<String> stockIDCreateList = m_stockIDList;
		
		BLog.output("CLEAR", "    stockIDCreateList count(%s)\n", stockIDCreateList.size());
		
		List<ClearResultWrapper> cClearResultWrapperList = new ArrayList<ClearResultWrapper>();
		
		for(int i=0; i<stockIDCreateList.size(); i++)
		{
			String stockID = stockIDCreateList.get(i);
			
			// 构造当时股票数据(昨日日K，今日当前分时)
			
			StockInfo cStockInfo = StockDataProvider.getLatestStockInfo(stockID);
			
			String yesterday_date = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_date, -1);
			List<StockDay> cStockDayList = StockDataProvider.getHistoryData(stockID, yesterday_date);
			
			StockTime cStockTime = new StockTime();
			boolean bGetStockTime = StockDataProvider.getStockTime(stockID, m_date, m_time, cStockTime);
			if(bGetStockTime)
			{
				StockTimeDataCache.addStockTime(stockID, m_date, cStockTime);
			}
			List<StockTime> cStockTimeList = StockTimeDataCache.getStockTimeList(stockID, m_date);
			
			BLog.output("CLEAR", "        -Stock:%s cStockTimeList size(%d)\n", stockID, cStockTimeList.size());
			
			Stock cStock = new Stock();
			cStock.setDate(m_date);
			cStock.setTime(m_time);
			cStock.setCurLatestStockInfo(cStockInfo);
			cStock.setCurStockDayData(cStockDayList);
			cStock.setCurStockTimeData(cStockTimeList);
			
			StockContext ctx = new StockContext();
			ctx.setCurStock(cStock);
			
			if(bGetStockTime) // 只有获取当前价格成功时才回调给用户
			{
				ClearResultWrapper cClearResultWrapper = new ClearResultWrapper();
				cClearResultWrapper.stockId = stockID;
				cClearResultWrapper.fPrice = cStockTime.price;
				cIStrategyClear.strategy_clear(ctx, cClearResultWrapper.clearRes);
				if(cClearResultWrapper.clearRes.bClear)
				{
					cClearResultWrapperList.add(cClearResultWrapper);
				}
			}
		}
		
		AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
		
		Transaction.StockClearCompleteNotify.Builder msg_builder = Transaction.StockClearCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		for(int i = 0; i< cClearResultWrapperList.size(); i++)
		{
			ClearResultWrapper cClearResultWrapper = cClearResultWrapperList.get(i);
			Transaction.StockClearCompleteNotify.ClearItem.Builder cItemBuild = msg_builder.addItemBuilder();
			cItemBuild.setStockID(cClearResultWrapper.stockId);
			cItemBuild.setPrice(cClearResultWrapper.fPrice);
			// 调用账户获取持有量
			StockCreate cStockCreate = accIF.getStockCreate(cClearResultWrapper.stockId);
			cItemBuild.setAmount(cStockCreate.amount);
		}
		Transaction.StockClearCompleteNotify msg = msg_builder.build();
		
		BLog.output("CLEAR", "    stockIDClearList count(%d)\n", msg.getItemList().size());
		for(int i=0; i< msg.getItemList().size(); i++)
		{
			String stockID = msg.getItemList().get(i).getStockID();
			float price = msg.getItemList().get(i).getPrice();
			int amount = msg.getItemList().get(i).getAmount();
			BLog.output("CLEAR", "        -Stock(%s) price(%.2f) amount(%d)\n", stockID,price,amount);
		}
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_STOCKCLEARCOMPLETENOTIFY", msg);
		
	}

	private String m_date;
	private String m_time;
	private List<String> m_stockIDList;
}
