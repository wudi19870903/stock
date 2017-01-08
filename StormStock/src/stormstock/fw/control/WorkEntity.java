package stormstock.fw.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stormstock.analysis.ANLDataProvider;
import stormstock.analysis.ANLLog;
import stormstock.analysis.ANLStock;
import stormstock.fw.acc.AccountModuleIF;
import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.base.BWaitObj;
import stormstock.fw.event.Transaction;
import stormstock.fw.event.Transaction.ControllerStartNotify;
import stormstock.fw.objmgr.GlobalModuleObj;
import stormstock.fw.objmgr.GlobalUserObj;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockInfo;
import stormstock.fw.stockdata.StockUtils;
import stormstock.fw.tran.ITranStockSetFilter;

public class WorkEntity {
	public WorkEntity(boolean bHistoryTest, String beginDate, String endDate)
	{
		// 保存基本参数
		m_bHistoryTest = bHistoryTest;
		m_beginDate = beginDate;
		m_endDate = endDate;
		
		// 初始化历史交易日表
		if(m_bHistoryTest)
		{
			m_hisTranDate = new ArrayList<String>();
			Stock cStockShangZheng = StockDataProvider.getStock("999999");
			int iB = StockUtils.indexDayKAfterDate(cStockShangZheng.getCurStockDayData(), m_beginDate);
			int iE = StockUtils.indexDayKBeforeDate(cStockShangZheng.getCurStockDayData(), m_endDate);
			
			for(int i = iB; i <= iE; i++)  
	        {  
				StockDay cStockDayShangZheng = cStockShangZheng.getCurStockDayData().get(i);  
				String curDateStr = cStockDayShangZheng.date;
				m_hisTranDate.add(curDateStr);
	        }
		}
		
		m_entityDataUpdate = new WorkEntityDataUpdate();
		m_entitySelect = new WorkEntitySelect();
		m_entityCreate = new WorkEntityCreate();
		m_entityClear = new WorkEntityClear();
	}
	
	void work()
	{
		// 加载测试集
		BLog.output("CTRL", "LoadStockIDSet ...\n");
		int stockSetSize = LoadStockIDSet();
		BLog.output("CTRL", "LoadStockIDSet OK stockCnt(%d) \n", stockSetSize);
		
		// 每天进行循环
		String dateStr = getStartDate();
		while(true) 
		{
			if(isTranDate(dateStr))
			{
				BLog.output("CTRL", "[%s] #################################################### \n", 
						dateStr);
				
				String timestr = "00:00:00";
				
				// 01:00 账户新交易日初始化
				timestr = "01:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
					BLog.output("CTRL", "[%s %s] account newDayInit \n", dateStr, timestr);
					accIF.newDayInit();
				}
				
				// 9:30-11:30 1:00-3:00 定期间隔发送交易信号，等待信号处理完毕通知
				int interval_min = 60;
				String timestr_begin = "09:30:00";
				String timestr_end = "11:30:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						BLog.output("CTRL", "[%s %s] stockClear & stockCreate \n", dateStr, timestr);
						m_entityClear.stockClear(dateStr, timestr);
						m_entityCreate.stockCreate(dateStr, timestr);
					}
					timestr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, interval_min);
					if(timestr.compareTo(timestr_end) > 0) break;
				}
				
				timestr_begin = "13:00:00";
				timestr_end = "15:00:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						BLog.output("CTRL", "[%s %s] stockClear & stockCreate \n", dateStr, timestr);
						m_entityClear.stockClear(dateStr, timestr);
						m_entityCreate.stockCreate(dateStr, timestr);
					}
					timestr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, interval_min);
					if(timestr.compareTo(timestr_end) > 0) break;
				}
				
				// 20:00 更新历史数据通知 等待更新完毕通知
				timestr = "20:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					BLog.output("CTRL", "[%s %s] updateStockData \n", dateStr, timestr);
					m_entityDataUpdate.updateStockData(dateStr, timestr);
				}
				
				// 22:00 选股 等待选股完毕
				timestr = "22:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					BLog.output("CTRL", "[%s %s] Select \n", dateStr, timestr);
					m_entitySelect.selectStock(dateStr, timestr);
				}
			}
			
			// 获取下一日期
			dateStr = getNextDate();
			if(null == dateStr) break;
		}
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_ENGINEEXIT", Transaction.TranEngineExitNotify.newBuilder().build());
	}
	
	public void onDataUpdateCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		m_entityDataUpdate.onDataUpdateCompleteNotify(m);
	}
	public void onSelectStockCompleteNotify(com.google.protobuf.GeneratedMessage m) 
	{
		m_entitySelect.onSelectStockCompleteNotify(m);
	}
	public void onStockCreateCompleteNotify(com.google.protobuf.GeneratedMessage m)
	{
		m_entityCreate.onStockCreateCompleteNotify(m);
	}
	public void onStockClearCompleteNotify(com.google.protobuf.GeneratedMessage m) {
		m_entityClear.onStockClearCompleteNotify(m);
	}
	
	/*
	 * realtime模式
	 * 	一直等待到9:25返回是否是交易日，根据上证指数实时变化确定
	 * historymock模式
	 * 	根据上证指数直接确定是否是交易日
	 */
	private boolean isTranDate(String date)
	{
		if(m_bHistoryTest)
		{
			return m_hisTranDate.contains(date);
		}
		else
		{
			BUtilsDateTime.waitDateTime(date, "09:25:00"); //等到时间
			
			// date日期时 检查web上证是否是交易日
		}
		return false;
	}
	
	private String getStartDate()
	{
		if(m_bHistoryTest)
		{
			m_curDate = m_beginDate;
			return m_curDate;
		}
		else
		{
			String curDateStr = BUtilsDateTime.GetDateStr(new Date());
			return curDateStr;
		}
	}
	private String getNextDate()
	{
		if(m_bHistoryTest)
		{
			m_curDate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, 1);
			if(m_curDate.compareTo(m_endDate) > 0)
			{
				return null;
			}
			else
			{
				return m_curDate;
			}
		}
		else
		{
			m_curDate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, 1);
		}
		return null;
	}
	
	/*
	 * realtime模式
	 * 	等待日期时间成功，返回true
	 * 	等待失败，返回false，比如等待的时间已经过期
	 * historymock模式
	 * 	直接返回true
	 */
	private boolean waitForDateTime(String date, String time)
	{
		if(m_bHistoryTest)
		{
			return true;
		}
		else
		{
			return BUtilsDateTime.waitDateTime(date, time);
		}
	}
	
	private int LoadStockIDSet()
	{
		ITranStockSetFilter cTranStockSetFilter = GlobalUserObj.getCurrentTranStockSetFilter();
		
		List<String> cStockIDSet = new ArrayList<String>();
		
		List<String> cStockAllList = StockDataProvider.getAllStockID();
		for(int i=0; i<cStockAllList.size();i++)
		{
			String stockID = cStockAllList.get(i);
			StockInfo cStockInfo = StockDataProvider.getLatestStockInfo(stockID);

			if(null != cStockInfo && cTranStockSetFilter.tran_stockset_byLatestStockInfo(cStockInfo))
			{
				cStockIDSet.add(stockID);
			}
		}
		// 股票交易集保存
		StockObjFlow.setTranStockIDSet(cStockIDSet);
		return cStockIDSet.size();
	}
	
	// 基本参数
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	
	// 历史交易日
	private List<String> m_hisTranDate;
	
	// 当前日期
	private String m_curDate;
	
	private WorkEntityDataUpdate m_entityDataUpdate;
	private WorkEntitySelect m_entitySelect;
	private WorkEntityCreate m_entityCreate;
	private WorkEntityClear m_entityClear;

}
