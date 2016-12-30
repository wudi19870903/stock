package stormstock.fw.event;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import stormstock.fw.base.BLog;

public class RunGenerateJavaProto {

	public static void runCmd(String command)
	{
		try {
            Process process = Runtime.getRuntime().exec(command);
            InputStreamReader ir = new InputStreamReader(process.getErrorStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null)
            	System.out.println(line);
        } catch (java.io.IOException e) {
            System.err.println("IOException " + e.getMessage());
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
		System.out.println("RunGenerateJavaProto END\n");
	}
}
