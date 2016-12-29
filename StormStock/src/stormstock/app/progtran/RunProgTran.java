package stormstock.app.progtran;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BModuleManager;
import stormstock.fw.control.Controller;
import stormstock.fw.select.Selector;

public class RunProgTran {
	public static void ProgTran()
	{
//		// ���ù�Ʊ��  ֧�ַ��࣬�Զ��壬ȫ��
//		ProgTran.setStockSet();
//		
//		// ����ѡ�ɲ��ԣ�������ֲ��ԣ���ѡ��
//		ProgTran.setSelectStockStrategy();
//		ProgTran.setCreatePositonStrategy();
//		ProgTran.setClearPositonStrategy();
//		
//		// ���ò���ʱ��Σ�֧����ʷʱ��Σ�ʵ��ʱ��
//		ProgTran.setTimeSpan();
//		
//		// �����˻���֧��ģ������ʵ
//		ProgTran.setAccount(); 
		
		//		ProgTran.Start();
	}
	 
	public static void main(String[] args) {
		BLog.output("TEST", "--->>> MainBegin\n");
		//BLog.config_setTag("EVENT", true);
		BLog.config_setTag("BASE", true);
		
		BModuleManager cModuleMgr = new BModuleManager();
		cModuleMgr.regModule(new Controller()); 
		cModuleMgr.regModule(new Selector()); 
		
		cModuleMgr.initialize();
		cModuleMgr.start();
		
		// program transaction
		ProgTran();
		
		cModuleMgr.mainLoop();
		
		cModuleMgr.stop();
		cModuleMgr.unInitialize();
		
		BLog.output("TEST", "--->>> MainEnd\n");
		BLog.config_output();
	}
}
