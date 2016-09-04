package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Formatter;
import java.util.List;

public class ANLPolicy {
	public Formatter fmt = new Formatter(System.out);
	public String strLogName;
	public ANLPolicy()
	{
		strLogName = this.getClass().getSimpleName() + ".txt";
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
	
	public void check_today(String date)
	{
		
	}
	public void buy(String stockId, float price)
	{
		
	}
	public void sell(String stockId, float price)
	{
		
	}
	// 查找日期索引
	public int indexDayK(List<ANLStockDayKData> dayklist, String dateStr)
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
		ANLStock cANLStock = ANLStockPool.getANLStock("999999");
		int iB = indexDayK(cANLStock.historyData, beginDate);
		int iE = indexDayK(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
            // fmt.format("%s\n", cANLDayKData.date);
            check_today(cANLDayKData.date);
        } 
	}
}
