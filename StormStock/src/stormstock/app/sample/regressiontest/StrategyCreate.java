package stormstock.app.sample.regressiontest;

import java.util.List;

import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.IStrategyCreate;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockTime;

public class StrategyCreate extends IStrategyCreate {

	@Override
	public void strategy_create(TranContext ctx, CreateResult out_sr) {
		
//		Stock curStock = ctx.target().stock();
//		String stockTimeStr = "";
//		List<StockTime> stockTimeList = ctx.target().stock().getLatestStockTimeList();
//		for(int i=0; i<stockTimeList.size(); i++)
//		{
//			StockTime cStockTime = stockTimeList.get(i);
//			stockTimeStr = stockTimeStr + String.format("%.2f(%s) ", cStockTime.price, cStockTime.time);
//		}
//		BLog.output("TEST", "        [%s %s] strategy_create stockID:%s (%s)  %s\n", 
//				ctx.date(), ctx.time(), 
//				curStock.getCurLatestStockInfo().ID, curStock.GetLastDate() , stockTimeStr);
		
		// 建仓为跌幅一定时
		float fYesterdayClosePrice = ctx.target().stock().GetLastYesterdayClosePrice();
		float fNowPrice = ctx.target().stock().getLatestPrice();
		float fRatio = (fNowPrice - fYesterdayClosePrice)/fYesterdayClosePrice;
		
		if(fRatio < -0.02)
		{
			out_sr.bCreate = true;
		}
	}

	@Override
	public int strategy_create_max_count() {
		// TODO Auto-generated method stub
		return 3;
	}

}
