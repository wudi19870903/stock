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
	// 推送买单委托，返回实际下单量
	abstract public int pushBuyOrder(String date, String time, String id, float price, int amount); 
	// 推送卖单委托，返回实际下单量
	abstract public int pushSellOrder(String date, String time, String id, float price, int amount);
	// 获得账户可用资金（现金）
	abstract public int getAvailableMoney(RefFloat out_availableMoney);
	
	// 设定获取已选股列表
	abstract public int setStockSelectList(List<String> stockIDList);
	abstract public int getStockSelectList(List<String> out_list);
	
	// 获得委托列表(未成交的，包含买入和卖出的)
	abstract public int getCommissionOrderList(List<CommissionOrder> out_list);
	// 获得持股列表（包含已经持有的，与当天下单成交的）
	abstract public int getHoldStockList(String date, String time, List<HoldStock> out_list);
	// 获得当日交割单列表（已成交的，包含买入和卖出的）
	abstract public int getDealOrderList(List<DealOrder> out_list);
}
