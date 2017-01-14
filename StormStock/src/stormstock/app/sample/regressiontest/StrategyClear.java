package stormstock.app.sample.regressiontest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.IStrategyClear;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StrategyClear extends IStrategyClear {

	@Override
	public void strategy_clear(TranContext ctx, ClearResult out_sr) {
		
		BLog.output("TEST", "strategy_clear %s %s\n", ctx.date(), ctx.time());
		
		String stockID = ctx.target().stock().getCurLatestStockInfo().ID;
		
		String stockTimeStr = "";
		List<StockTime> stockTimeList = ctx.target().stock().getLatestStockTimeList();
		for(int i=0; i<stockTimeList.size(); i++)
		{
			StockTime cStockTime = stockTimeList.get(i);
			stockTimeStr = stockTimeStr + String.format("%.2f(%s) ", cStockTime.price, cStockTime.time);
		}

		BLog.output("TEST", "%s %s\n", stockID, stockTimeStr);
		
		HoldStock cHoldStock = ctx.target().holdStock();
		if(null != cHoldStock)
			BLog.output("TEST", "HoldStock %s %.2f %d\n", stockID, cHoldStock.curPrice, cHoldStock.holdDayCnt);
		
		
		if(cHoldStock.holdDayCnt > 10) // 持股天数大于10 卖出
		{
			out_sr.bClear = true;
		}
			
		if(cHoldStock.profitRatio() > 0.02 || cHoldStock.profitRatio() < -0.02)
		{
			out_sr.bClear = true;
		}
	}

}
