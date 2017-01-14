package stormstock.app.sample.regressiontest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.IStrategyCreate;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StrategyCreate extends IStrategyCreate {

	@Override
	public void strategy_create(TranContext ctx, CreateResult out_sr) {
		
		BLog.output("TEST", "StrategyCreate %s %s\n", ctx.date(), ctx.time());
		
		String stockID = ctx.target().stock().getCurLatestStockInfo().ID;
		
		String stockTimeStr = "";
		List<StockTime> stockTimeList = ctx.target().stock().getLatestStockTimeList();
		for(int i=0; i<stockTimeList.size(); i++)
		{
			StockTime cStockTime = stockTimeList.get(i);
			stockTimeStr = stockTimeStr + String.format("%.2f(%s) ", cStockTime.price, cStockTime.time);
		}

		BLog.output("TEST", "%s %s\n", stockID, stockTimeStr);
		
		if(ctx.time().compareTo("14:00:00") >= 0)
			out_sr.bCreate = true;
	}

	@Override
	public int strategy_create_max_count() {
		// TODO Auto-generated method stub
		return 3;
	}

}
