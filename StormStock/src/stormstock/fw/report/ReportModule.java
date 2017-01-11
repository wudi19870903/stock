package stormstock.fw.report;

import stormstock.fw.base.BModuleBase;
import stormstock.fw.base.BQThread;
import stormstock.fw.base.BEventSys.EventReceiver;

public class ReportModule extends BModuleBase {

	public ReportModule() {
		super("Report");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BModuleInterface getIF() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventReceiver m_eventRecever;
	private BQThread m_qThread;
}
