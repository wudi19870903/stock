package stormstock.fw.select;

import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BEventSys.EventSender;
import stormstock.fw.event.Base;

public class Selector extends BModuleBase {

	@Override
	public void initialize() {

	}

	@Override
	public void start() {
//		EventSender cSender = new EventSender();
//		cSender.Send("BEV_BASE_STORMEXIT", Base.StormExit.newBuilder().build());
	}

	@Override
	public void stop() {

	}

	@Override
	public void unInitialize() {

	}

}
