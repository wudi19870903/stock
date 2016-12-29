package stormstock.fw.event;

import java.util.HashMap;
import java.util.Map;

public class EventDef {

	/*
	 * EventMapDef
	 */
	public static Map<String, String> s_EventNameMap = new HashMap<String, String>() {{
		put("BEV_BASE_STORMEXIT", "stormstock.fw.event.Base$StormExit");
    }};
}
