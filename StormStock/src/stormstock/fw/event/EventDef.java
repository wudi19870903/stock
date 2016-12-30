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
    }};
}
