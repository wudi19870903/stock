package stormstock.fw.report;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BEventSys;
import stormstock.fw.base.BPath;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.fw.base.BImageCurve;
import stormstock.fw.base.BLog;
import stormstock.fw.event.ReportAnalysis;
import stormstock.fw.base.BImageCurve.CurvePoint;
import stormstock.fw.report.InfoCollector.DailyReport;

public class GenerateReportRequest  extends BQThreadRequest {
	
	public GenerateReportRequest(String date, String time, InfoCollector cInfoCollector)
	{
		m_date = date;
		m_time = time;
		m_cInfoCollector = cInfoCollector;
		String outputDir = BPath.getOutputDir();
		String imgfilename = outputDir + "\\report.jpg";
		m_imgReport = new BImageCurve(1600,900,imgfilename);
	}
	
	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		BLog.output("REPORT", "GenerateReportRequest.doAction [%s %s]\n", m_date, m_time);
		
		List<CurvePoint> cCurvePointList_TotalAssets = new ArrayList<CurvePoint>();
		List<CurvePoint> cCurvePointList_SHComposite = new ArrayList<CurvePoint>();
		
		List<DailyReport> cDailyReportList = m_cInfoCollector.getDailyReportList();
		for(int i =0; i< cDailyReportList.size(); i++)
		{
			DailyReport cDailyReport = cDailyReportList.get(i);
			
			cCurvePointList_TotalAssets.add(new CurvePoint(i, cDailyReport.fTotalAssets));
			
			cCurvePointList_SHComposite.add(new CurvePoint(i, cDailyReport.fSHComposite));
		}
		
		m_imgReport.addLogicCurveSameRatio(cCurvePointList_SHComposite, 1);
		m_imgReport.addLogicCurveSameRatio(cCurvePointList_TotalAssets, 2);
		m_imgReport.GenerateImage();
		
		
		ReportAnalysis.GenerateReportCompleteNotify.Builder msg_builder = ReportAnalysis.GenerateReportCompleteNotify.newBuilder();
		msg_builder.setDate(m_date);
		msg_builder.setTime(m_time);

		ReportAnalysis.GenerateReportCompleteNotify msg = msg_builder.build();
		BEventSys.EventSender cSender = new BEventSys.EventSender();
		cSender.Send("BEV_TRAN_GENERATEREPORTCOMPLETENOTIFY", msg);
	}
	
	private String m_date;
	private String m_time;
	private InfoCollector m_cInfoCollector;
	private BImageCurve m_imgReport;
}
