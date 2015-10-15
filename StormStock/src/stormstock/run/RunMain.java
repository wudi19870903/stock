package stormstock.run;

import java.util.Formatter;

public class RunMain {
	public static Formatter fmt = new Formatter(System.out);
	public static void printInfo()
	{
		fmt.format("VERSION: 1.01\n");
		fmt.format("NAME\n");
		fmt.format("        java -jar stormstock [MainOpe]\n");
		fmt.format("\n");
		fmt.format("DESCRIPTION\n");
		fmt.format("        [MainOpe]\n");
		fmt.format("            the main operation for the stormstock application\n");
		fmt.format("            1.UpdateData\n");
		fmt.format("              update all the stockdata to your local.\n");
		fmt.format("            2.SuccRateCheck\n");
		fmt.format("              use the policy to check all stock.\n");
		fmt.format("            3.RealTransection\n");
		fmt.format("              the function is developing now.\n");
	}
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			printInfo();
			return;
		}
		
		String MainCallOpe = args[0];
		
		if(MainCallOpe.equals("UpdateData"))
		{
			RunUpdateData.main(null);
		}
		else if(MainCallOpe.equals("SuccRateCheck"))
		{
			RunSuccRateCheckByDays.main(null);
		}
		else if(MainCallOpe.equals("RealTransection"))
		{
			RunAutoRealTimeTransection.main(null);
		}
		else
		{
			printInfo();
		}
	}
}
