package stormstock.fw.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BPath {
	public static String getOutputDir()
	{
		return s_outputDir;
	}
	public static boolean createDir(String dirName)
	{
		File folder = new File(dirName);
		folder.mkdirs();
		return true;
	}
	
	private static String initOutputDir()
	{
		String outputDir = "output\\";
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
		outputDir = outputDir + sdf.format(new Date());
		createDir(outputDir);
		return outputDir;
	}
	private static String s_outputDir = initOutputDir();
}
