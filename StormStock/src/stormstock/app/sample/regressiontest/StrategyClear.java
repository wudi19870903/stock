package stormstock.app.sample.regressiontest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.IStrategyClear;
import stormstock.fw.tranbase.com.StockContext;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StrategyClear extends IStrategyClear {

	@Override
	public void strategy_clear(StockContext ctx, ClearResult out_sr) {
		
		BLog.output("TEST", "strategy_clear %s %s\n", ctx.date(), ctx.time());
		
		String stockID = ctx.stock().getCurLatestStockInfo().ID;
		
		String stockTimeStr = "";
		List<StockTime> stockTimeList = ctx.stock().getLatestStockTimeList();
		for(int i=0; i<stockTimeList.size(); i++)
		{
			StockTime cStockTime = stockTimeList.get(i);
			stockTimeStr = stockTimeStr + String.format("%.2f(%s) ", cStockTime.price, cStockTime.time);
		}

		BLog.output("TEST", "%s %s\n", stockID, stockTimeStr);
		
		HoldStock cHoldStock = ctx.account().getStockHold(stockID);
		if(null != cHoldStock)
			BLog.output("TEST", "HoldStock %s %.2f\n", stockID, cHoldStock.curPrice);
		
		if(ctx.time().compareTo("14:00:00") >= 0)
			out_sr.bClear = true;
	}

}
