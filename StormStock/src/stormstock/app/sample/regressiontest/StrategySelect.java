package stormstock.app.sample.regressiontest;
import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.com.IStrategySelect;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDay;

public class StrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(TranContext ctx, SelectResult out_sr) {
		
		Stock curStock = ctx.target().stock();
		
		
		BLog.output("TEST", "[%s %s] strategy_select stockID:%s close:%.2f (%s)\n", 
				ctx.date(), ctx.time(), 
				curStock.getCurLatestStockInfo().ID, curStock.GetLastClosePrice(),curStock.GetLastDate());
		
		// ����3������
		
		List<StockDay> cStockDayList = curStock.getCurStockDayData();
		int iSize = cStockDayList.size();
		if(iSize > 4)
		{
			StockDay cStockDayCur = cStockDayList.get(iSize-1);
			StockDay cStockDayBefore1 = cStockDayList.get(iSize-2);
			StockDay cStockDayBefore2 = cStockDayList.get(iSize-3);
			StockDay cStockDayBefore3 = cStockDayList.get(iSize-4);
			
			if(cStockDayCur.close() <= cStockDayBefore1.close()
					&& cStockDayBefore1.close() <= cStockDayBefore2.close()
					&& cStockDayBefore2.close() <= cStockDayBefore3.close()
					)
			{
				out_sr.bSelect = true;
				out_sr.fPriority = cStockDayBefore3.close() - cStockDayCur.close();
			}
		}
	}

	@Override
	public int strategy_select_max_count() {
		// TODO Auto-generated method stub
		return 5;
	}

}
