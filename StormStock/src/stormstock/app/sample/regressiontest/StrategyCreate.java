package stormstock.app.sample.regressiontest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.com.IStrategyCreate;
import stormstock.fw.tranbase.com.StockContext;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StrategyCreate extends IStrategyCreate {

	@Override
	public void strategy_create(StockContext ctx, CreateResult out_sr) {
		// TODO Auto-generated method stub
//		BLog.output("TEST", "StrategyCreate %s\n", ctx.getCurStock().getCurLatestStockInfo().name);
//		BLog.output("TEST", " %s\n", ctx.getCurStock().GetLastDate());
//		
		
		List<StockTime> cStockTimeList = ctx.getCurStock().getCurStockTimeData();
		
		//BLog.output("TEST", "strategy_create ID %s\n", ctx.getCurStock().getCurLatestStockInfo().ID);
		
		for(int i=0; i<cStockTimeList.size();i++ )
		{
			StockTime cStockTime = cStockTimeList.get(i);
			//BLog.output("TEST", "    %s %.2f\n", cStockTime.time, cStockTime.price);
		}
		
		
		out_sr.bCreate = true;
		
	}

	@Override
	public int strategy_create_max_count() {
		// TODO Auto-generated method stub
		return 3;
	}

}
