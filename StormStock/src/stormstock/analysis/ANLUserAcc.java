package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.analysis.ANLStockPool;

public class ANLUserAcc {
	public class ANLUserAccStock
	{
		public String id;
		public int totalAmount;
		public float buyPrices;
		public int holdDayCnt;
	}
	public ANLStockPool ref_UserStockPool;
	public String curDate;
	public float money;
	public List<ANLUserAccStock> stockList; 
	
	ANLUserAcc(ANLStockPool userStockPool)
	{
		ref_UserStockPool = userStockPool;
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
			float cprice = ref_UserStockPool.getStock(tmpANLUserAccStock.id).GetLastPrice();
			all_marketval = all_marketval + tmpANLUserAccStock.totalAmount*cprice;
		}
		float all_asset = all_marketval + money;
		return all_asset;
	}
	public float GetTotalMoney(){
		return money;
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
		if(null == cANLUserAccStock)
		{
			cANLUserAccStock = new ANLUserAccStock();
			cANLUserAccStock.id = id;
			cANLUserAccStock.holdDayCnt =0;
			cANLUserAccStock.buyPrices = price;
			cANLUserAccStock.totalAmount = amount;
			stockList.add(cANLUserAccStock);
		}
		else
		{
			int oriAmount = cANLUserAccStock.totalAmount;
			float oriPrice = cANLUserAccStock.buyPrices;
			cANLUserAccStock.totalAmount = cANLUserAccStock.totalAmount + amount;
			cANLUserAccStock.buyPrices = (oriPrice*oriAmount + price*amount)/cANLUserAccStock.totalAmount;
		}
		money = money - price*amount;

		float all_marketval = 0.0f;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock tmpANLUserAccStock = stockList.get(i);
			float cprice = ref_UserStockPool.getStock(tmpANLUserAccStock.id).GetLastPrice();
			all_marketval = all_marketval + tmpANLUserAccStock.totalAmount*cprice;
		}
		float all_asset = all_marketval + money;
		ANLLog.outputLog("    # UserAccOpe buyStock [%s %s %.2f %d] [%.2f %.2f] \n", curDate, id, price, amount, all_marketval, all_asset);
		return true;
	}
	public boolean sellStock(String id, float price, int amount)
	{
		boolean sellflag =false;
		for(int i=0;i<stockList.size();i++)
		{
			ANLUserAccStock cANLUserAccStock = stockList.get(i);
			if(cANLUserAccStock.id.compareTo(id) == 0)
			{
				int oriAmount = cANLUserAccStock.totalAmount;
				float oriPrice = cANLUserAccStock.buyPrices;
				cANLUserAccStock.totalAmount = cANLUserAccStock.totalAmount - amount;
				cANLUserAccStock.buyPrices = (oriPrice*oriAmount - price*amount)/cANLUserAccStock.totalAmount;
				money = money + price*amount;
				sellflag = true;
				break;
			}
		}
		if(sellflag)
		{
			float all_marketval = 0.0f;
			for(int i=0;i<stockList.size();i++)
			{
				ANLUserAccStock cANLUserAccStock = stockList.get(i);
				float cprice = ref_UserStockPool.getStock(cANLUserAccStock.id).GetLastPrice();
				all_marketval = all_marketval + cANLUserAccStock.totalAmount*cprice;
			}
			float all_asset = all_marketval + money;
			ANLLog.outputLog("    # UserAccOpe sellStock [%s %s %.2f %d] [%.2f %.2f]\n", curDate, id, price, amount, all_marketval, all_asset);
		}
		return true;
	}
}
