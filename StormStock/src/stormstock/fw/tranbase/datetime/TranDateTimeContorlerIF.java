package stormstock.fw.tranbase.datetime;

import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.datetime.TranDateTimeContorlerPublicDef.DATETIMEMODE;

/*
 * ȫ�ֽ�������ʱ�������
 * ���н������ģ����ô˽ӿ�����ȡ��ǰʱ��
 * ��ʱ�������̿��������� 
 */
public class TranDateTimeContorlerIF {
	
	public static boolean initialize(DATETIMEMODE eMode)
	{
		s_dateTimeControlerEntity = new DateTimeControlerEntity(eMode);
		return true;
	}
	
	/*
	 * realtimeģʽ
	 * 	�ȴ�����ʱ��ɹ�������true
	 * 	�ȴ�ʧ�ܣ�����false������ȴ���ʱ���Ѿ�����
	 * historymockģʽ
	 * 	ֱ�ӷ���true
	 */
	public static boolean waitDateTime(String date, String time)
	{
		return s_dateTimeControlerEntity.waitDateTime(date, time);
	}
	
	public static String getCurDate()
	{
		return s_dateTimeControlerEntity.getCurDate();
	}
	
	public static String getCurTime()
	{
		return s_dateTimeControlerEntity.getCurTime();
	}
	
	private static DateTimeControlerEntity s_dateTimeControlerEntity;
}
