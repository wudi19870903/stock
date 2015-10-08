package stormstock.analysis;

public abstract class ANLPolicyBase {
	public static class RetExitCheck
	{
		public RetExitCheck(boolean in_hasEnoughDays, int in_iExit, float in_profitPer)
		{
			hasEnoughDays = in_hasEnoughDays;
			iExit = in_iExit; 
			profitPer = in_profitPer;
		}
		public RetExitCheck(RetExitCheck cRetExitCheck)
		{
			hasEnoughDays = cRetExitCheck.hasEnoughDays;
			iExit = cRetExitCheck.iExit; 
			profitPer = cRetExitCheck.profitPer;
		}
		public boolean hasEnoughDays;
		public int iExit;
		public float profitPer;
	}
	
	// 检查进入
	public abstract boolean enterCheck(ANLStock cANLStock, int iCheckDay);
	
	// 一定要有退出日，所以返回void
	public abstract RetExitCheck exitCheck(ANLStock cANLStock, int iEnter);
}
