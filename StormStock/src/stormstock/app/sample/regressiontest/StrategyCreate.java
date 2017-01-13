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
		BLog.output("TEST", "StrategyCreate %s %s\n", ctx.date(), ctx.time());
		
//		BLog.output("TEST", "%s LatestPrice %.2f\n", 
//				ctx.stock().getCurLatestStockInfo().ID,
//				ctx.stock().getLatestPrice());
//	
//		List<StockTime> stockTimeList = ctx.stock().getLatestStockTimeList();
//		for(int i=0; i<stockTimeList.size(); i++)
//		{
//			StockTime cStockTime = stockTimeList.get(i);
//			BLog.output("TEST", "cStockTime %s %.2f\n", cStockTime.time,cStockTime.price);
//		}
		
		out_sr.bCreate = true;
	}

	@Override
	public int strategy_create_max_count() {
		// TODO Auto-generated method stub
		return 3;
	}

}
