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
		// �����������
		m_bHistoryTest = bHistoryTest;
		m_beginDate = beginDate;
		m_endDate = endDate;
		
		// ��ʼ����ʷ�����ձ�
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
		// ���ز��Լ�
		BLog.output("CTRL", "LoadStockIDSet ...\n");
		int stockSetSize = LoadStockIDSet();
		BLog.output("CTRL", "LoadStockIDSet OK stockCnt(%d) \n", stockSetSize);
		
		// ÿ�����ѭ��
		String dateStr = getStartDate();
		while(true) 
		{
			if(isTranDate(dateStr))
			{
				BLog.output("CTRL", "[%s] #################################################### \n", 
						dateStr);
				
				String timestr = "00:00:00";
				
				// 01:00 �˻��½����ճ�ʼ��
				timestr = "01:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					AccountModuleIF accIF = (AccountModuleIF)GlobalModuleObj.getModuleIF("Account");
					BLog.output("CTRL", "[%s %s] account newDayInit \n", dateStr, timestr);
					accIF.newDayInit();
				}
				
				// 9:30-11:30 1:00-3:00 ���ڼ�����ͽ����źţ��ȴ��źŴ������֪ͨ
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
				
				// 20:00 ������ʷ����֪ͨ �ȴ��������֪ͨ
				timestr = "20:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					BLog.output("CTRL", "[%s %s] updateStockData \n", dateStr, timestr);
					m_entityDataUpdate.updateStockData(dateStr, timestr);
				}
				
				// 22:00 ѡ�� �ȴ�ѡ�����
				timestr = "22:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					BLog.output("CTRL", "[%s %s] Select \n", dateStr, timestr);
					m_entitySelect.selectStock(dateStr, timestr);
				}
			}
			
			// ��ȡ��һ����
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
	 * realtimeģʽ
	 * 	һֱ�ȴ���9:25�����Ƿ��ǽ����գ�������ָ֤��ʵʱ�仯ȷ��
	 * historymockģʽ
	 * 	������ָ֤��ֱ��ȷ���Ƿ��ǽ�����
	 */
	private boolean isTranDate(String date)
	{
		if(m_bHistoryTest)
		{
			return m_hisTranDate.contains(date);
		}
		else
		{
			BUtilsDateTime.waitDateTime(date, "09:25:00"); //�ȵ�ʱ��
			
			// date����ʱ ���web��֤�Ƿ��ǽ�����
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
	 * realtimeģʽ
	 * 	�ȴ�����ʱ��ɹ�������true
	 * 	�ȴ�ʧ�ܣ�����false������ȴ���ʱ���Ѿ�����
	 * historymockģʽ
	 * 	ֱ�ӷ���true
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
		// ��Ʊ���׼�����
		StockObjFlow.setTranStockIDSet(cStockIDSet);
		return cStockIDSet.size();
	}
	
	// ��������
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	
	// ��ʷ������
	private List<String> m_hisTranDate;
	
	// ��ǰ����
	private String m_curDate;
	
	private WorkEntityDataUpdate m_entityDataUpdate;
	private WorkEntitySelect m_entitySelect;
	private WorkEntityCreate m_entityCreate;
	private WorkEntityClear m_entityClear;

}
