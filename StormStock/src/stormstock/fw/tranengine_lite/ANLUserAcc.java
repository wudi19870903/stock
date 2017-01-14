package stormstock.fw.tranengine_lite;

import java.util.ArrayList;
import java.util.List;

public class ANLUserAcc {
	public class ANLUserAccStock
	{
		public String id; // 股票ID
		public int totalAmount; // 持有量（股）
		public float buyPrices; // 成本价格/买入价格
		public int holdDayCnt; // 持有天数
		public float transactionCosts; // 交易费用
	}
	public ANLStockPool ref_UserStockPool;
	public String curDate;
	public float money;
	public List<ANLUserAccStock> stockList; 
	public float transactionCostsRatio;
	
	ANLUserAcc(ANLStockPool userStockPool)
	{
		ref_UserStockPool = userStockPool;
		transactionCostsRatio = 0.0016f;
	}
	public void init(float in_money) 
	{
		money = in_money;
		stockList = new ArrayList<ANLUserAccStock>();
	}
	void update(String date)
	{
		curDate = date;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock cANLUserAccStock = stockList.get(i);
			cANLUserAccStock.holdDayCnt = cANLUserAccStock.holdDayCnt + 1;
		}
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock cANLUserAccStock = stockList.get(i);
			if(cANLUserAccStock.totalAmount == 0)
			{
				stockList.remove(i);
			}
		}
	}
	public float GetTotalAssets()
	{
		float all_marketval = 0.0f;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock tmpANLUserAccStock = stockList.get(i);
			float cprice = ref_UserStockPool.getStock(tmpANLUserAccStock.id).GetLastClosePrice();
			all_marketval = all_marketval + tmpANLUserAccStock.totalAmount*cprice;
		}
		float all_asset = all_marketval + money;
		return all_asset;
	}
	public float GetTotalMoney(){
		return money;
	}
	public int GetStockAmount(String stockId){
		int amount = 0;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock tmpANLUserAccStock = stockList.get(i);
			if(tmpANLUserAccStock.id == stockId)
			{
				amount = tmpANLUserAccStock.totalAmount;
				break;
			}
		}
		return amount;
	}
	public boolean buyStock(String id, float price, int amount)
	{
		ANLUserAccStock cANLUserAccStock = null;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock cTmp = stockList.get(i);
			if(cTmp.id.compareTo(id) == 0)
			{
				cANLUserAccStock = cTmp;
				break;
			}
		}
		float transactionCosts = transactionCostsRatio*price*amount;
		if(null == cANLUserAccStock)
		{
			cANLUserAccStock = new ANLUserAccStock();
			cANLUserAccStock.id = id;
			cANLUserAccStock.holdDayCnt =0;
			cANLUserAccStock.buyPrices = price;
			cANLUserAccStock.totalAmount = amount;
			cANLUserAccStock.transactionCosts = transactionCosts;
			stockList.add(cANLUserAccStock);
		}
		else
		{
			int oriAmount = cANLUserAccStock.totalAmount;
			float oriPrice = cANLUserAccStock.buyPrices;
			cANLUserAccStock.totalAmount = cANLUserAccStock.totalAmount + amount;
			cANLUserAccStock.buyPrices = (oriPrice*oriAmount + price*amount)/cANLUserAccStock.totalAmount;
			cANLUserAccStock.transactionCosts = cANLUserAccStock.transactionCosts + transactionCosts;
		}
		money = money - price*amount;
		ANLLog.outputLog("    # UserAccOpe buyStock [%s %.3f %d %.3f(%.3f) %.3f] \n", 
				id, price, amount, price*amount, transactionCosts, money);
		return true;
	}
	public boolean sellStock(String id, float price, int amount)
	{
		float transactionCosts = 0.0f;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock cANLUserAccStock = stockList.get(i);
			if(cANLUserAccStock.id.compareTo(id) == 0)
			{
				int oriAmount = cANLUserAccStock.totalAmount;
				float oriPrice = cANLUserAccStock.buyPrices;
				cANLUserAccStock.totalAmount = cANLUserAccStock.totalAmount - amount;
				cANLUserAccStock.buyPrices = (oriPrice*oriAmount - price*amount)/cANLUserAccStock.totalAmount;
				money = money + price*amount - cANLUserAccStock.transactionCosts;
				transactionCosts = cANLUserAccStock.transactionCosts;
				ANLLog.outputLog("    # UserAccOpe sellStock [%s %.3f %d %.3f(%.3f) %.3f]\n", 
						id, price, amount, price*amount, transactionCosts, money);
				
				if(cANLUserAccStock.totalAmount == 0)
				{
					stockList.remove(i);
				}
				
				break;
			}
		}
		return true;
	}
	
	public void printInfo()
	{
		//ANLLog.outputLog("    | UserAccInfo %s\n", curDate);
		float marketVal = 0.0f;
		float allTransactionCosts = 0.0f;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock cANLUserAccStock = stockList.get(i);
			String id = cANLUserAccStock.id;
			int oriAmount = cANLUserAccStock.totalAmount;
			float oriPrice = cANLUserAccStock.buyPrices;
			int holdDayCnt = cANLUserAccStock.holdDayCnt;
			float transactionCosts = cANLUserAccStock.transactionCosts;
			float cprice = ref_UserStockPool.getStock(id).GetLastClosePrice();
			ANLLog.outputLog("    # UserAccInfo Hold [%s %d %.3f %.3f %.3f(%.3f) %d]  \n", 
					id, oriAmount, oriPrice, cprice, oriAmount*cprice, transactionCosts, holdDayCnt);
			marketVal = marketVal + oriAmount*cprice;
			allTransactionCosts = allTransactionCosts + transactionCosts;
		}
		ANLLog.outputLog("    # UserAccInfo %.3f(%.3f) %.3f %.3f\n", 
				marketVal, allTransactionCosts, money, marketVal+money);
	}
}
