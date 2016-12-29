package stormstock.fw.base;

public class BPath {
	public static String getOutputDir()
	{
		return s_outputDir;
	}
	public static void setOutputDir(String dirName)
	{
		s_outputDir = dirName;
	}
	
	private static String s_outputDir = "output";
}
