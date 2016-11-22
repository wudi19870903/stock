package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Formatter;

public class ANLLog {
	public static void init(String filename)
	{
		// delete log
		s_strLogName = filename;
		File cfile =new File(s_strLogName);
		cfile.delete();
	}
	public static void outputLog(String format, Object... args)
	{
		String logstr = String.format(format, args);
		outputConsole("%s", logstr);
		File cfile =new File(s_strLogName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(logstr.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	public static void outputConsole(String format, Object... args)
	{
		String logstr = String.format(format, args);
		s_fmt.format("%s", logstr);
	}
	static private Formatter s_fmt = new Formatter(System.out);
	static private String s_strLogName = "default.log";
}
