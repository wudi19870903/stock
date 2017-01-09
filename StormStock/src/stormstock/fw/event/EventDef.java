package stormstock.fw.event;

import java.util.HashMap;
import java.util.Map;

public class EventDef {

	/*
	 * EventMapDef
	 */
	public static Map<String, String> s_EventNameMap = new HashMap<String, String>() {{
		
		put("BEV_TRAN_ENGINEEXIT", "stormstock.fw.event.Transaction$TranEngineExitNotify");
		
		put("BEV_TRAN_CONTROLLERSTARTNOTIFY", "stormstock.fw.event.Transaction$ControllerStartNotify");
		
		put("BEV_TRAN_STOCKSELECTANALYSISREQUEST", "stormstock.fw.event.StockSelectAnalysis$StockSelectAnalysisRequest");
		put("BEV_TRAN_STOCKSELECTANALYSISCOMPLETENOTIFY", "stormstock.fw.event.StockSelectAnalysis$StockSelectAnalysisCompleteNotify");
		
		put("BEV_TRAN_STOCKCREATEANALYSISREQUEST", "stormstock.fw.event.StockCreateAnalysis$StockCreateAnalysisRequest");
		put("BEV_TRAN_STOCKCREATEANALYSISCOMPLETENOTIFY", "stormstock.fw.event.StockCreateAnalysis$StockCreateAnalysisCompleteNotify");
		
		put("BEV_TRAN_STOCKCLEARANALYSISREQUEST", "stormstock.fw.event.StockClearAnalysis$StockClearAnalysisRequest");
		put("BEV_TRAN_STOCKCLEARANALYSISCOMPLETENOTIFY", "stormstock.fw.event.StockClearAnalysis$StockClearAnalysisCompleteNotify");
    }};
}
