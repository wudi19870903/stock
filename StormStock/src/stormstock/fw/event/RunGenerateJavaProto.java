package stormstock.fw.event;

import java.io.File;
import java.io.IOException;

import stormstock.fw.base.BLog;

public class RunGenerateJavaProto {

	public static void runCmd(String command)
	{
		Runtime rn = Runtime.getRuntime();
		try {
			Process p = rn.exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getCurDir()
	{
		String curdir = "";
		File directory = new File("");
		try {
			curdir = directory.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return curdir;
	}
	public static void main(String[] args) {

		BLog.output("EVENT", "getCurDir:%s\n", getCurDir());
		String projEventDir = "src\\stormstock\\fw\\event";
		String protocPath = "src\\stormstock\\fw\\event\\protoc.exe";
		
		File root = new File(projEventDir);
		File[] fs = root.listFiles();
		for(int i=0; i<fs.length; i++){
			String filefullname = fs[i].getAbsolutePath();
			String filename = fs[i].getName();
			if(filename.endsWith(".proto"))
			{
				// src\dreamstock\event\fw\protoc.exe --java_out=src src\dreamstock\event\fw\basetest.proto
				String cmd = protocPath 
						+ " --java_out=" + "src"
						+ " " + projEventDir + "\\" + filename;
				BLog.output("TEST", "%s\n", cmd);
				runCmd(cmd);
			}
		}
		
		BLog.output("EVENT", "RunGenerateJavaProto OK!\n");
		
	}
}
