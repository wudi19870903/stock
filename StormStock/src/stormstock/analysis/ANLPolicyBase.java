package stormstock.analysis;

public abstract class ANLPolicyBase {
	public static class RetExitCheck
	{
		public RetExitCheck(int in_iExit, float in_profitPer)
		{
			iExit = in_iExit;
			profitPer = in_profitPer;
		}
		public int iExit;
		public float profitPer;
	}
	
	// 检查进入
	public abstract boolean enterCheck(ANLStock cANLStock, int iCheckDay);
	
	// 一定要有退出日，所以返回void
	public abstract RetExitCheck exitCheck(ANLStock cANLStock, int iEnter);
}
