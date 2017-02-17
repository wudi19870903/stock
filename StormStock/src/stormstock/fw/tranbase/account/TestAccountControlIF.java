package stormstock.fw.tranbase.account;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.account.AccountPublicDef.ACCOUNTTYPE;

public class TestAccountControlIF {

	public static void main(String[] args) {
		BLog.output("TEST", "AccountControlIF begin\n");
		BLog.start();
		
		AccountControlIF cAccCtrlIF = new AccountControlIF();
		cAccCtrlIF.setAccountType(ACCOUNTTYPE.MOCK);
		
		String curDate = BUtilsDateTime.GetCurDateStr();
		String curTime = BUtilsDateTime.GetCurTimeStr();
		
		cAccCtrlIF.printAccount(null, null);
		
		BLog.stop();
		BLog.output("TEST", "AccountControlIF end\n");
	}
}
