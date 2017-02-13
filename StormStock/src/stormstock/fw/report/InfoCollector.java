package stormstock.fw.report;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.tranbase.account.AccountPublicDef.DealOrder;

/*
 * 信息收集器
 */
public class InfoCollector {
	/*
	 * 日报告结构体
	 */
	public static class DailyReport
	{
		public DailyReport(String date)
		{
			cClearDealOrder = new ArrayList<DealOrder>();
		}
		public String date; // 日期
		public float fTotalAssets; // 总资产
		public float fAvailableMoney; // 可用资金
		public List<DealOrder> cClearDealOrder; // 清仓交割单列表
		
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
