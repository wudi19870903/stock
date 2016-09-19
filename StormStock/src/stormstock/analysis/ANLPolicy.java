package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLPolicyJZHG.ANLPolicyStockCK;

public class ANLPolicy {
	static public Formatter fmt = new Formatter(System.out);
	public String strLogName;
	private List<ANLStock> stockListstore;
	
	public ANLUserAcc cUserAcc;
	private ANLUserStockPool cANLUserStockPool;
	
	static class ANLUserAcc
	{
		class ANLUserAccStock
		{
			public String id;
			public int totalAmount;
			public float buyPrices;
			public int holdDayCnt;
		}
		public String curDate;
		public float money;
		public List<ANLUserAccStock> stockList; 
		
		void init(float in_money) 
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
		public float GetMountAmount(){
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
			fmt.format("# buyStock [%s %s %.2f %d]\n", curDate, id, price, amount);
			return true;
		}
		public boolean sellStock(String id, float price, int amount)
		{
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
					fmt.format("# sellStock [%s %s %.2f %d] %.2f \n", curDate, id, price, amount, money);
					break;
				}
			}
			return true;
		}
		
	}
	static class ANLUserStockPool 
	{
		ANLUserStockPool()
		{
			stockList = new ArrayList<ANLStock>();
		}
		public List<ANLStock> stockList;
		ANLStock getStock(String id)
		{
			ANLStock cANLStock = null;
			for(int i=0;i<stockList.size();i++)
			{
				ANLStock tmp = stockList.get(i);
				if(tmp.id.compareTo(id) == 0)
				{
					cANLStock = tmp;
					break;
				}
			}
			return cANLStock;
		}
	}
	
	public ANLPolicy()
	{
		strLogName = this.getClass().getSimpleName() + ".txt";
		stockListstore = new ArrayList<ANLStock>();
		
		cUserAcc = new ANLUserAcc();
		cANLUserStockPool = new ANLUserStockPool();
	}
	
	// --------------------------------------------------------------
	// log
	public void rmlog()
	{
		File cfile =new File(strLogName);
		cfile.delete();
	}
	public void outputLog(String s, boolean enable)
	{
		if(!enable) return;
		fmt.format("%s", s);
		File cfile =new File(strLogName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	public void outputLog(String s)
	{
		outputLog(s, true);
	}
	
	// --------------------------------------------------------------
	// user imp
	public void init(){}
	public boolean stock_filter(ANLStock cANLStock){ return false;}
	public void check_today(String date, ANLUserStockPool spool) {}
	// --------------------------------------------------------------
	
	// 查找日期索引
	public int indexDayKAfterDate(List<ANLStockDayKData> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) >= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	public int indexDayKBeforeDate(List<ANLStockDayKData> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) <= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	void run()
	{
		ANLStock cANLStock = ANLStockPool.getANLStock("999999");
		ANLStockDayKData cANLDayKDataBegin = cANLStock.historyData.get(0);  
		ANLStockDayKData cANLDayKDataEnd = cANLStock.historyData.get(cANLStock.historyData.size()-1);  
		run(cANLDayKDataBegin.date, cANLDayKDataEnd.date);
	}
	void run(String beginDate, String endDate) {
		init();
		// 遍历所有股票，让用户筛选到用户股票池
		// fmt.format("loading user stock pool ...\n");
		List<String> cStockList = ANLStockPool.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			//outputLog(stockId + "\n");
			ANLStock cANLStock = ANLStockPool.getANLStock(stockId);
			if(null!= cANLStock && stock_filter(cANLStock))
			{
				stockListstore.add(cANLStock);
				// fmt.format("stockListstore id:%s \n", cANLStock.id);
			}
		}
		fmt.format("load success, stock count : %d\n", stockListstore.size());
		
		// 从上证指数中确认回调天数
		ANLUserStockPool cANLUserStockPool = new ANLUserStockPool();
		ANLStock cANLStock = ANLStockPool.getANLStock("999999");
		int iB = indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
            // fmt.format("%s data generate\n", cANLDayKData.date);
			// 从存储股票列表中提取相应天数的数据到用户股票池中，回调给用户测试
			for(int iS=0;iS<stockListstore.size();iS++)
			{
				ANLStock cANLStockStore = stockListstore.get(iS);
				// fmt.format("   Stock %s generate\n", cANLStockStore.id);
				// 获取用户池中的相应股票对象
				ANLStock cANLStockUser = null;
				for(int iUS=0;iUS<cANLUserStockPool.stockList.size();iUS++)
				{
					ANLStock cANLStockUserFind = cANLUserStockPool.stockList.get(iUS);
					if(cANLStockUserFind.id.compareTo(cANLStockStore.id) == 0)
					{
						cANLStockUser = cANLStockUserFind;
					}
				}
				if(null == cANLStockUser)
				{
					cANLStockUser = new  ANLStock(cANLStockStore.id, cANLStockStore.curBaseInfo);
					cANLUserStockPool.stockList.add(cANLStockUser);
				}
				// 添加相应的天数数据，添加完毕后移除
				int iRemoveCnt = 0;
				for(int iStore = 0; iStore<cANLStockStore.historyData.size();iStore++)
				{
					ANLStockDayKData cANLStockStoreKData = cANLStockStore.historyData.get(iStore);
					// fmt.format("   check date %s\n", cANLStockStoreKData.date);
					if(cANLStockStoreKData.date.compareTo(cANLDayKData.date) <= 0)
					{
						ANLStockDayKData cpObj = new ANLStockDayKData(cANLStockStoreKData, cANLStockUser);
						cANLStockUser.historyData.add(cpObj);
						iRemoveCnt++;
					}
				}
				for(int iRmCnt = 0;iRmCnt<iRemoveCnt;iRmCnt++)
				{
					cANLStockStore.historyData.remove(0);
				}
			}
			
			// 回调给用户
            check_today(cANLDayKData.date, cANLUserStockPool);
            cUserAcc.update(cANLDayKData.date);
        } 
	}
}
