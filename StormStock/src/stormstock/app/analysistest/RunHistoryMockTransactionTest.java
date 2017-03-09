package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.EStockComplexDXCheck.ComplexDXCheckResult;
import stormstock.app.analysistest.EStockDayPriceDrop.ResultCheckPriceDrop;
import stormstock.app.analysistest.EStockDayVolumeLevel.VOLUMELEVEL;
import stormstock.fw.base.BLog;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.com.IEigenStock;
import stormstock.fw.tranbase.com.IStrategyClear;
import stormstock.fw.tranbase.com.IStrategyCreate;
import stormstock.fw.tranbase.com.IStrategySelect;
import stormstock.fw.tranbase.com.ITranStockSetFilter;
import stormstock.fw.tranbase.com.TranContext;
import stormstock.fw.tranbase.stockdata.Stock;
import stormstock.fw.tranbase.stockdata.StockDay;
import stormstock.fw.tranbase.stockdata.StockInfo;
import stormstock.fw.tranbase.stockdata.StockTime;
import stormstock.fw.tranengine.TranEngine;
import stormstock.fw.tranengine.TranEngine.TRANACCOUNTTYPE;
import stormstock.fw.tranengine.TranEngine.TRANTIMEMODE;

public class RunHistoryMockTransactionTest {
	// 测试集
	public static class TranStockSet extends ITranStockSetFilter {
		@Override
		public boolean tran_stockset_byLatestStockInfo(StockInfo cStockInfo) {
			if(cStockInfo.ID.compareTo("000000") >= 0 && cStockInfo.ID.compareTo("002000") <= 0) {	
				return true;
			}
			return false;
		}
	}
	// 选股
	public static class StrategySelect extends IStrategySelect {

		@Override
		public void strategy_select(TranContext ctx, SelectResult out_sr) {
			
			String stockId = ctx.target().stock().getCurLatestStockInfo().ID;
			List<StockDay> cStockDayList = ctx.target().stock().getCurStockDayData();
			
			ComplexDXCheckResult cComplexDXCheckResult = EStockComplexDXCheck.check(stockId, cStockDayList, cStockDayList.size()-1);
			if (cComplexDXCheckResult.bCheck)
			{
				out_sr.bSelect = true;
				out_sr.fPriority = - cComplexDXCheckResult.x;
			}

		}

		@Override
		public int strategy_select_max_count() {
			// TODO Auto-generated method stub
			return 10;
		}

	}
	// 建仓
	public static class StrategyCreate extends IStrategyCreate {

		@Override
		public void strategy_create(TranContext ctx, CreateResult out_sr) {
			List<StockTime> list_stockTime = ctx.target().stock().getLatestStockTimeList();

//			ResultXiaCuoQiWen cResultXiaCuoQiWen = cEStockTimePriceDropStable.checkXiaCuoQiWen_single(list_stockTime, list_stockTime.size()-1);
//			if (cResultXiaCuoQiWen.bCheck)
//			{
//				//BLog.output("TEST", "     --->>> StrategyCreate %s %s \n", ctx.date(), ctx.time());
//				out_sr.bCreate = true;
//				out_sr.fMaxPositionRatio = 0.15f;
//			}
			

			// 建仓为跌幅一定时
			float fYesterdayClosePrice = ctx.target().stock().GetLastYesterdayClosePrice();
			float fNowPrice = ctx.target().stock().getLatestPrice();
			float fRatio = (fNowPrice - fYesterdayClosePrice)/fYesterdayClosePrice;
			if(fRatio < -0.02)
			{
				out_sr.bCreate = true;
				out_sr.fMaxPositionRatio = 0.2f;
				
			}
			
//			out_sr.bCreate = true;
//			out_sr.fMaxPositionRatio = 0.15f;
			
		}
		@Override
		public int strategy_create_max_count() {
			return 5;
		}

	}
	// 清仓
	public static class StrategyClear extends IStrategyClear {
		@Override
		public void strategy_clear(TranContext ctx, ClearResult out_sr) {
			HoldStock cHoldStock = ctx.target().holdStock();
			if(cHoldStock.investigationDays >= 20) // 调查天数控制
			{
				out_sr.bClear = true;
			}
			if(cHoldStock.profitRatio() > 0.05 || cHoldStock.profitRatio() < -0.05) // 止盈止损x个点卖
			{
				out_sr.bClear = true;
			}
		}
	}
	public static void main(String[] args) {
		BLog.output("TEST", "--->>> MainBegin\n");
		TranEngine cTranEngine = new TranEngine();
		
		cTranEngine.setStockSet(new TranStockSet());
		cTranEngine.setSelectStockStrategy(new StrategySelect());
		cTranEngine.setCreatePositonStrategy(new StrategyCreate());
		cTranEngine.setClearPositonStrategy(new StrategyClear());
		
		cTranEngine.setAccountType(TRANACCOUNTTYPE.MOCK); 
		cTranEngine.setTranMode(TRANTIMEMODE.HISTORYMOCK);
		cTranEngine.setHistoryTimeSpan("2016-08-01", "2017-02-01");
		
		cTranEngine.run();
		cTranEngine.mainLoop();
		
		BLog.output("TEST", "--->>> MainEnd\n");
	}
}
