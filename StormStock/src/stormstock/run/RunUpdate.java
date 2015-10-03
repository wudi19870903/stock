package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stormstock.data.DataEngineBase;
import stormstock.data.WebStockAllList;
import stormstock.data.WebStockAllList.StockItem;
import stormstock.data.WebStockDayK;
import stormstock.data.WebStockDayK.DayKData;
import stormstock.data.WebStockDividendPayout.DividendPayout;
import stormstock.data.WebStockRealTimeInfo;
import stormstock.data.WebStockRealTimeInfo.RealTimeInfo;

public class RunUpdate {
	public static int updateStock(String id)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String CurrentDate = df.format(new Date());
		int curyear = Integer.parseInt(CurrentDate.split("-")[0]);
		int curmonth = Integer.parseInt(CurrentDate.split("-")[1]);
		int curday = Integer.parseInt(CurrentDate.split("-")[2]);
		Calendar xcal = Calendar.getInstance();
		xcal.set(curyear, curmonth-1, curday);
		int cw = xcal.get(Calendar.DAY_OF_WEEK);
		while(cw == 1 || cw == 7)
		{
			xcal.add(Calendar.DATE, -1);
			cw = xcal.get(Calendar.DAY_OF_WEEK);
		}
		Date curValiddate = xcal.getTime();
		String curValiddateStr = df.format(curValiddate);
		// 当前有效日期，上一个交易日（非周六周日）
		// System.out.println("CurrentValidDate:" + curValiddateStr);
		
		List<DayKData> retListLocal = new ArrayList<DayKData>();
		int retgetDayKData = DataEngineBase.getDayKData(id, retListLocal);
		List<DividendPayout> retListLocalDividend = new ArrayList<DividendPayout>();
		int retgetDividendPayout = DataEngineBase.getDividendPayout(id, retListLocalDividend);
		
		if(0 == retgetDayKData 
			&& 0 == retgetDividendPayout 
			&& retListLocal.size() != 0 
			&& retListLocalDividend.size() != 0 )
		{
			// 本地有数据
			DayKData cDayKDataLast = retListLocal.get(retListLocal.size()-1);
			String localDataLastDate = cDayKDataLast.date; // 本地数据最后日期
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// 如果当前日期大于本地最后数据日期，需要继续检测
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				RealTimeInfo cRealTimeInfo = new RealTimeInfo();
				int retgetRealTimeInfo = WebStockRealTimeInfo.getRealTimeInfo(id, cRealTimeInfo);
				if(0 == retgetRealTimeInfo)
				{
					String webValidLastDate = cRealTimeInfo.date;
					if(cRealTimeInfo.time.compareTo("15:00:00") < 0)
					{
						int year = Integer.parseInt(cRealTimeInfo.date.split("-")[0]);
						int month = Integer.parseInt(cRealTimeInfo.date.split("-")[1]);
						int day = Integer.parseInt(cRealTimeInfo.date.split("-")[2]);
						int hour = Integer.parseInt(cRealTimeInfo.time.split(":")[0]);
						int min = Integer.parseInt(cRealTimeInfo.time.split(":")[1]);
						int sec = Integer.parseInt(cRealTimeInfo.time.split(":")[2]);
						Calendar cal0 = Calendar.getInstance();
						cal0.set(year, month-1, day, hour, min, sec);
						// 获取上一个非周末的日期
						cal0.add(Calendar.DATE, -1);
						int webwk = cal0.get(Calendar.DAY_OF_WEEK);
						while(webwk == 1 || webwk == 7)
						{
							cal0.add(Calendar.DATE, -1);
							webwk = cal0.get(Calendar.DAY_OF_WEEK);
						}
						
						Date vdate = cal0.getTime();
						webValidLastDate = df.format(vdate);
					}
					// System.out.println("webValidLastDate:" + webValidLastDate);
					
					if(webValidLastDate.compareTo(cDayKDataLast.date) > 0)
					{
						// 需要追加更新
						int year = Integer.parseInt(localDataLastDate.split("-")[0]);
						int month = Integer.parseInt(localDataLastDate.split("-")[1]);
						int day = Integer.parseInt(localDataLastDate.split("-")[2]);
						Calendar cal1 = Calendar.getInstance();
						cal1.set(year, month-1, day);
						cal1.add(Calendar.DATE, 1);
						Date fromDate = cal1.getTime();
						String fromDateStr = df.format(fromDate).replace("-", "");
						String toDateStr = webValidLastDate.replace("-", "");
						//System.out.println("fromDateStr:" + fromDateStr);
						//System.out.println("toDateStr:" + toDateStr);
						
						List<DayKData> retListMore = new ArrayList<DayKData>();
						int retgetDayKDataMore = WebStockDayK.getDayKData(id, fromDateStr, toDateStr, retListMore);
						if(0 == retgetDayKDataMore)
						{
							for(int i = 0; i < retListMore.size(); i++)  
					        {  
								DayKData cDayKData = retListMore.get(i);  
								retListLocal.add(cDayKData);
					        } 
							int retsetDayKData = DataEngineBase.setDayKData(id, retListLocal);
							if(0 == retsetDayKData)
							{
								// 更新复权因子数据
								if(0 == DataEngineBase.downloadStockDividendPayout(id))
									// 追加成功
									return 0;
								else
									// 更新复权因子失败
									return -80;
							}
							else
							{
								//保存本地数据失败
								return -50;
							}
						}
						else
						{
							// 网络获取追加数据失败
							return -40;
						}
						
					}
					else
					{
						// 已经和网络最新有效日线一样
						return 0;
					}
				}
				else
				{
					// 获取网络最新有效交易日期失败
					return -20;
				}
			}
			else
			{
				// 本地数据已经是最新
				return 0;
			}
		}
		else
		{
			// 本地没有数据，需要试图重新下载
			int retdownloadStockDayk =  DataEngineBase.downloadStockDayk(id);
			int retdownloadStockDividendPayout =  DataEngineBase.downloadStockDividendPayout(id);
			if(0 == retdownloadStockDayk 
					&& 0 == retdownloadStockDividendPayout)
			{
				//最新数据下载成功
				return 0;
			}
			else
			{
				// 重新下载失败
				return -10;
			}
		}
		//return -100;
	}
	
	public static void main(String[] args) {

		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = WebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id); 
	            if(0 == updateStock(cStockItem.id))
	            {
	            	System.out.println("update success: " +  cStockItem.id);
	            }
	            else
	            {
	            	System.out.println("update ERROR:" + cStockItem.id);
	            }
	            
	        } 
			System.out.println("count:" + retList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
