package stormstock.fw.tranbase.account;

import java.util.List;

import stormstock.fw.base.BTypeDefine.RefFloat;
import stormstock.fw.tranbase.account.AccountPublicDef.CommissionOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;
import stormstock.fw.tranbase.account.AccountPublicDef.HoldStock;
import stormstock.fw.tranbase.stockdata.StockDay;

abstract public class IAccountOpe {
	public IAccountOpe() { }
	// 隔日开始账户初始化
	abstract public int newDayInit(String date, String time);
	// 隔日开始账户交易结束
	abstract public int newDayTranEnd(String date, String time);
	// 推送买单委托
	abstract public int pushBuyOrder(String date, String time, String id, int amount, float price); 
	// 推送卖单委托
	abstract public int pushSellOrder(String date, String time, String id, int amount, float price);
	// 获得账户可用资金
	abstract public int getAvailableMoney(RefFloat out_availableMoney);
	// 获得委托单列表
	abstract public int getCommissionOrderList(List<CommissionOrder> out_list);
	// 获得持股列表
	abstract public int getHoldStockList(String date, String time, List<HoldStock> out_list);
	// 获得当日成交单列表
	abstract public int getDealOrderList(List<DealOrder> out_list);
}
