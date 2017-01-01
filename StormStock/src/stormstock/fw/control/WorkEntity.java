package stormstock.fw.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.event.Transaction;
import stormstock.fw.stockdata.Stock;
import stormstock.fw.stockdata.StockDataProvider;
import stormstock.fw.stockdata.StockDay;
import stormstock.fw.stockdata.StockUtils;

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
			int iB = StockUtils.indexDayKAfterDate(cStockShangZheng.historyData, m_beginDate);
			int iE = StockUtils.indexDayKBeforeDate(cStockShangZheng.historyData, m_endDate);
			
			for(int i = iB; i <= iE; i++)  
	        {  
				StockDay cStockDayShangZheng = cStockShangZheng.historyData.get(i);  
				String curDateStr = cStockDayShangZheng.date;
				m_hisTranDate.add(curDateStr);
	        }
		}
		
	}

	void work()
	{
		String dateStr = getStartDate();
		while(true) // ÿ�����ѭ��
		{
			if(isTranDate(dateStr))
			{
				String timestr = "00:00:00";
				
				// 9:30-11:30 1:00-3:00 ���ڼ�����ͽ����źţ��ȴ��źŴ������֪ͨ
				String timestr_begin = "09:30:00";
				String timestr_end = "11:30:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						BLog.output("CTRL", "[%s %s] tran \n", dateStr, timestr);
					}
					timestr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, 60);
					if(timestr.compareTo(timestr_end) > 0) break;
				}
				
				timestr_begin = "13:00:00";
				timestr_end = "15:00:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						BLog.output("CTRL", "[%s %s] tran \n", dateStr, timestr);
					}
					timestr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, 60);
					if(timestr.compareTo(timestr_end) > 0) break;
				}
				
				
				// 20:00 ������ʷ����֪ͨ �ȴ��������֪ͨ
				timestr = "20:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					BLog.output("CTRL", "[%s %s] UpdateDataReq \n", dateStr, timestr);
				}
				
				// 22:00 ѡ�� �ȴ�ѡ�����
				timestr = "22:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					BLog.output("CTRL", "[%s %s] Select \n", dateStr, timestr);
				}
				
			}
			
			// ��ȡ��һ����
			dateStr = getNextDate();
			if(null == dateStr) break;
		}
		
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_ENGINEEXIT", Transaction.TranEngineExitNotify.newBuilder().build());
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
	
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	
	private List<String> m_hisTranDate;
	private String m_curDate;
}
