package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ANLPolicy {
	static class ANLUserStockPool 
	{
		ANLUserStockPool()
		{
			stockList = new ArrayList<ANLStock>();
		}
		public List<ANLStock> stockList;
	}
	
	static public Formatter fmt = new Formatter(System.out);
	public String strLogName;
	private List<ANLStock> stockListstore;
	
	public ANLPolicy()
	{
		strLogName = this.getClass().getSimpleName() + ".txt";
		stockListstore = new ArrayList<ANLStock>();
	}
	
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
	
	public void buy(String stockId, float price)
	{
		
	}
	
	public void sell(String stockId, float price)
	{
		
	}
	
	// ������������
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
		// �������й�Ʊ�����û�ɸѡ���û���Ʊ��
		fmt.format("loading user stock pool ...\n");
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
		
		// ����ָ֤����ȷ�ϻص�����
		ANLUserStockPool cANLUserStockPool = new ANLUserStockPool();
		ANLStock cANLStock = ANLStockPool.getANLStock("999999");
		int iB = indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
            // fmt.format("%s data generate\n", cANLDayKData.date);
			// �Ӵ洢��Ʊ�б�����ȡ��Ӧ���������ݵ��û���Ʊ���У��ص����û�����
			for(int iS=0;iS<stockListstore.size();iS++)
			{
				ANLStock cANLStockStore = stockListstore.get(iS);
				// fmt.format("   Stock %s generate\n", cANLStockStore.id);
				// ��ȡ�û����е���Ӧ��Ʊ����
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
				// ������Ӧ���������ݣ�������Ϻ��Ƴ�
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
			
			// �ص����û�
            check_today(cANLDayKData.date, cANLUserStockPool);
        } 
	}
}