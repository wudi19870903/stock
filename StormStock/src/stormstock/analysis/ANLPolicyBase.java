package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ANLPolicyBase {
	public static class ANLUserAcc
	{
		public class ANLUserAccStock
		{
			public String id;
			public int totalAmount;
			public float buyPrices;
			public int holdDayCnt;
		}
		public ANLUserStockPool ref_UserStockPool;
		public String curDate;
		public float money;
		public List<ANLUserAccStock> stockList; 
		
		ANLUserAcc(ANLUserStockPool userStockPool)
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

			float all_marketval = 0.0f;
			for(int i=0;i<stockList.size();i++)
			{
				ANLUserAccStock tmpANLUserAccStock = stockList.get(i);
				float cprice = ref_UserStockPool.getStock(tmpANLUserAccStock.id).GetLastPrice();
				all_marketval = all_marketval + tmpANLUserAccStock.totalAmount*cprice;
			}
			float all_asset = all_marketval + money;
			s_fmt.format("# UserAccOpe buyStock [%s %s %.2f %d] [%.2f %.2f] \n", curDate, id, price, amount, all_marketval, all_asset);
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
				s_fmt.format("# UserAccOpe sellStock [%s %s %.2f %d] [%.2f %.2f]\n", curDate, id, price, amount, all_marketval, all_asset);
			}
			return true;
		}
		
	}
	public static class ANLUserStockPool 
	{
		ANLUserStockPool()
		{
			stockList = new ArrayList<ANLStock>();
		}
		public List<ANLStock> stockList;
		public ANLStock getStock(String id)
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
	
	public ANLPolicyBase()
	{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		s_strLogName = this.getClass().getSimpleName() + ".txt";
		// delete log
		File cfile =new File(s_strLogName);
		cfile.delete();
		// create inner object
		stockListstore = new ArrayList<ANLStock>();
		cANLUserStockPool = new ANLUserStockPool();
		cUserAcc = new ANLUserAcc(cANLUserStockPool);
	}
	
	public static void outputLog(String format, Object... args)
	{
		String logstr = String.format(format, args);
		s_fmt.format("%s", logstr);
		File cfile =new File(s_strLogName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(logstr.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
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
	protected void run(String beginDate, String endDate) {
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
		s_fmt.format("load success, stock count : %d\n", stockListstore.size());
		
		// 从上证指数中确认回调天数
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
			cUserAcc.update(cANLDayKData.date);
            check_today(cANLDayKData.date, cANLUserStockPool);
        } 
	}
	
	static public Formatter s_fmt = new Formatter(System.out);
	static public String s_strLogName;
	private List<ANLStock> stockListstore;
	public ANLUserAcc cUserAcc;
	private ANLUserStockPool cANLUserStockPool;
}
