package stormstock.fw.report;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.tranbase.account.AccountPublicDef.DeliveryOrder;

/*
 * ��Ϣ�ռ���
 */
public class InfoCollector {
	/*
	 * �ձ���ṹ��
	 */
	public static class DailyReport
	{
		public DailyReport(String date)
		{
			cClearDeliveryOrder = new ArrayList<DeliveryOrder>();
		}
		public String date; // ����
		public float fTotalAssets; // ���ʲ�
		public float fAvailableMoney; // �����ʽ�
		public List<DeliveryOrder> cClearDeliveryOrder; // ��ֽ���б�
		
		public float fSHComposite;
	}
	
	public InfoCollector()
	{
		m_cDailyReportList = new ArrayList<DailyReport>();
	}
	public void addDailyReport(DailyReport cDailyReport)
	{
		m_cDailyReportList.add(cDailyReport);
	}
	public List<DailyReport> getDailyReportList()
	{
		return m_cDailyReportList;
	}
	public void clearDailyReport()
	{
		m_cDailyReportList.clear();
	}
	
	private List<DailyReport> m_cDailyReportList;
}
