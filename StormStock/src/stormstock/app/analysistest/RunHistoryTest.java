package stormstock.app.analysistest;

import java.util.List;

import stormstock.app.analysistest.RunSimpleStockDayListTest.ResultCheckStockDayList;
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
import stormstock.fw.tranengine.TranEngine;
import stormstock.fw.tranengine.TranEngine.TRANACCOUNTTYPE;
import stormstock.fw.tranengine.TranEngine.TRANTIMEMODE;

public class RunHistoryTest {
	// 测试集
	public static class TranStockSet extends ITranStockSetFilter {
		@Override
		public boolean tran_stockset_byLatestStockInfo(StockInfo cStockInfo) {
			if(cStockInfo.ID.compareTo("000000") >= 0 && cStockInfo.ID.compareTo("0002000") <= 0) {	
				return true;
			}
			return false;
		}
	}
	// 选股
	public static class StrategySelect extends IStrategySelect {

		@Override
		public void strategy_select(TranContext ctx, SelectResult out_sr) {
			List<StockDay> cStockDayList = ctx.target().stock().getCurStockDayData();
			
			ResultCheckStockDayList cResultCheck = RunSimpleStockDayListTest.check(cStockDayList, cStockDayList.size()-1);
			if(cResultCheck.bCheck)
			{
				out_sr.bSelect = true;
				out_sr.fPriority = -cResultCheck.data;
			}
		}

		@Override
		public int strategy_select_max_count() {
			// TODO Auto-generated method stub
			return 5;
		}

	}
	// 建仓
	public static class StrategyCreate extends IStrategyCreate {

		@Override
		public void strategy_create(TranContext ctx, CreateResult out_sr) {
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
			return 3;
		}

	}
	// 清仓
	public static class StrategyClear extends IStrategyClear {
		@Override
		public void strategy_clear(TranContext ctx, ClearResult out_sr) {
			HoldStock cHoldStock = ctx.target().holdStock();
			if(cHoldStock.investigationDays >= 5) // 调查天数控制
			{
				out_sr.bClear = true;
			}
			if(cHoldStock.profitRatio() > 0.03 || cHoldStock.profitRatio() < -0.03) // 止盈止损2个点卖
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
		cTranEngine.setHistoryTimeSpan("2011-01-01", "2012-01-01");
		
		cTranEngine.run();
		cTranEngine.mainLoop();
		
		BLog.output("TEST", "--->>> MainEnd\n");
	}
}
