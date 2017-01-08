package stormstock.fw.acc;

import stormstock.fw.base.BModuleBase;

public class AccountModule extends BModuleBase {

	public AccountModule() {
		super("Account");
		m_accountControler = new AccountControler();
		m_accountModuleIF = new AccountModuleIF(m_accountControler);
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
		return m_accountModuleIF;
	}

	private AccountModuleIF m_accountModuleIF;
	private AccountControler m_accountControler;
}
