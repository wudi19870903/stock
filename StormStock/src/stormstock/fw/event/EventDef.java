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
		
		put("BEV_TRAN_DATAUPDATENOTIFY", "stormstock.fw.event.Transaction$DataUpdateNotify");
		put("BEV_TRAN_DATAUPDATECOMPLETENOTIFY", "stormstock.fw.event.Transaction$DataUpdateCompleteNotify");
		
		put("BEV_TRAN_SELECTSTOCKNOTIFY", "stormstock.fw.event.Transaction$SelectStockNotify");
		put("BEV_TRAN_SELECTSTOCKCOMPLETENOTIFY", "stormstock.fw.event.Transaction$SelectStockCompleteNotify");
		
		put("BEV_TRAN_STOCKCREATENOTIFY", "stormstock.fw.event.Transaction$StockCreateNotify");
		put("BEV_TRAN_STOCKCREATECOMPLETENOTIFY", "stormstock.fw.event.Transaction$StockCreateCompleteNotify");
		
		put("BEV_TRAN_STOCKCLEARNOTIFY", "stormstock.fw.event.Transaction$StockClearNotify");
		put("BEV_TRAN_STOCKCLEARCOMPLETENOTIFY", "stormstock.fw.event.Transaction$StockClearCompleteNotify");
    }};
}
