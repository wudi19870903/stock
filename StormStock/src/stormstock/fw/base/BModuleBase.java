package stormstock.fw.base;

abstract public class BModuleBase {
	
	public static class BModuleInterface {}
	
	abstract public BModuleInterface getIF();
	abstract public void initialize();
	abstract public void start();
	abstract public void stop();
	abstract public void unInitialize();
	
	public BModuleBase(String moduleName)
	{
		m_moduleName = moduleName;
	}
	public String moduleName()
	{
		return m_moduleName;
	}
	private String m_moduleName;
}
