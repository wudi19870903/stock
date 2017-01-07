package stormstock.fw.stockcreate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.event.Transaction;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.stockdata.StockTime;
import stormstock.fw.tran.strategy.IStrategyCreate;
import stormstock.fw.tran.strategy.IStrategyCreate.CreateResult;
import stormstock.fw.tran.strategy.IStrategySelect.SelectResult;
import stormstock.fw.tran.strategy.StockContext;

public class CreateWorkRequest extends BQThreadRequest {
	
	/*
	 * CreateResultWrapper��
	 */
	static private class CreateResultWrapper {
		public CreateResultWrapper(){
			createRes = new CreateResult();
		}
		public String stockId;
		public float fPrice;
		public CreateResult createRes;
	}
	
	public CreateWorkRequest(String date, String time, List<String> stockIDList)
	{
		m_date = date;
		m_time = time;
		m_stockIDList = stockIDList;
	}
	
	@Override
	public void doAction() {
		BLog.output("CREATE", "CreateWorkRequest.doAction [%s %s]\n", m_date, m_time);
		
		IStrategyCreate cIStrategyCreate = GlobalUserObj.getCurrentStrategyCreate();
		List<String> stockIDSelectList = m_stockIDList;
		
		BLog.output("CREATE", "    stockIDSelectList count(%s)\n", stockIDSelectList.size());
		
		List<CreateResultWrapper> cCreateResultWrapperList = new ArrayList<CreateResultWrapper>();
		
		for(int i=0; i<stockIDSelectList.size(); i++)
		{
			String stockID = stockIDSelectList.get(i);
			
			// ���쵱ʱ��Ʊ����(������K�����յ�ǰ��ʱ)
			
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
			
			BLog.output("CREATE", "    ID:%s cStockTimeList size(%d)\n", stockID, cStockTimeList.size());
			
			Stock cStock = new Stock();
			cStock.setDate(m_date);
			cStock.setTime(m_time);
			cStock.setCurLatestStockInfo(cStockInfo);
			cStock.setCurStockDayData(cStockDayList);
			cStock.setCurStockTimeData(cStockTimeList);
			
			StockContext ctx = new StockContext();
			ctx.setCurStock(cStock);
			
			if(bGetStockTime) // ֻ�л�ȡ��ǰ�۸�ɹ�ʱ�Żص����û�
			{
				CreateResultWrapper cCreateResultWrapper = new CreateResultWrapper();
				cCreateResultWrapper.stockId = stockID;
				cCreateResultWrapper.fPrice = cStockTime.price;
				cIStrategyCreate.strategy_create(ctx, cCreateResultWrapper.createRes);
				if(cCreateResultWrapper.createRes.bCreate)
				{
					cCreateResultWrapperList.add(cCreateResultWrapper);
				}
			}
		}
			
		Transaction.StockCreateCompleteNotify.Builder msg_builder = Transaction.StockCreateCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);
		for(int i = 0; i< cCreateResultWrapperList.size(); i++)
		{
			CreateResultWrapper cCreateResultWrapper = cCreateResultWrapperList.get(i);
			Transaction.StockCreateCompleteNotify.CreateItem.Builder cItemBuild = msg_builder.addItemBuilder();
			cItemBuild.setStockID(cCreateResultWrapper.stockId);
			cItemBuild.setPrice(cCreateResultWrapper.fPrice);
		}
		Transaction.StockCreateCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		
		BLog.output("CREATE", "    stockIDCreateList count(%d)\n", msg.getItemList().size());
		cSender.Send("BEV_TRAN_STOCKCREATECOMPLETENOTIFY", msg);
		
	}
	
	private String m_date;
	private String m_time;
	private List<String> m_stockIDList;
}
