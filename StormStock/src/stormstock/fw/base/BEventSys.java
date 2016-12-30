package stormstock.fw.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import stormstock.fw.base.BAutoSync.BSyncObj;

public class BEventSys {

	private static Map<String, Method> m_EventCreateMap = new HashMap<String, Method>();
	private static String s_point = "inproc://dreamstock";
	private static String s_QuitCmdPrefix = "EVENTSYS_QUIT_";
	private static Context s_context;
	private static List<EventReceiver> s_receiverList = new ArrayList<EventReceiver>();
	
	public static boolean registerEventMap(Map<String, String> evMap) // evName,evClsName
	{
		for (Map.Entry<String, String> entry : evMap.entrySet()) {
			String evName = entry.getKey();
			String clsName = entry.getValue();
			
			try {
				Class<?> clz = Class.forName(clsName);
				Method methodObj = clz.getMethod("parseFrom", byte[].class);
				m_EventCreateMap.put(evName, methodObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static boolean start()
	{
		BLog.output("", "BL_EventSys Initialized...\n");
		try {
			// init context
			s_context = ZMQ.context(1);
			// init pub
			EventSender.initialize();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean stop()
	{
		BLog.output("EVENT", "BL_EventSys UnInitialize...\n");

		// stop all receiver
		for(int i=0; i< BEventSys.s_receiverList.size(); i++)
		{
			EventReceiver cReceiver = BEventSys.s_receiverList.get(i);
			cReceiver.stopReceive();
		}
		// unInit pub
		EventSender.unInitialize();
		s_context.term();
		return true;
	}

	/*
	 * Sender
	 */
	public static class EventSender
	{
		private static Socket s_PubSocket;
		private static BSyncObj s_PubSync; 
		
		public static boolean initialize()
		{
			s_PubSocket = s_context.socket(ZMQ.PUB);
			s_PubSocket.setLinger(5000);
			s_PubSocket.setSndHWM(0);
			s_PubSocket.bind(s_point);
			s_PubSync = new BSyncObj();
			return true;
		}
		public static boolean unInitialize()
		{
			s_PubSocket.close();
			return true;
		}
		
		public boolean Send(String name, com.google.protobuf.GeneratedMessage proto)
		{
			s_PubSync.Lock();
			if(null == proto) 
			{
				BLog.output("EVENT", "Sender EvName(%s) Data(null)\n", name);
				s_PubSocket.send(name.getBytes(), ZMQ.SNDMORE); 
				s_PubSocket.send("null".getBytes(), 0);
			}
			else
			{
				BLog.output("EVENT", "Sender EvName(%s) Data(...)\n", name);
				byte[] protoData= proto.toByteArray();
				s_PubSocket.send(name.getBytes(), ZMQ.SNDMORE); 
				s_PubSocket.send(protoData, 0);
			}
			s_PubSync.UnLock();
			return true;
		}
	}
	
	/*
	 * Receiver
	 */
	public static class EventReceiver
	{
		public static interface EventReceiverCB {
			void callback(com.google.protobuf.GeneratedMessage data);
		}
		
		private static class ReceiverThread extends Thread
		{
			public ReceiverThread(EventReceiver receiver)
			{
				m_receiver = receiver;
			}
			@Override
	        public void run()
	        {
				BLog.output("EVENT", "EventReceiver(%s) thread running.\n",  m_receiver.m_ReceiverName);
				while(!m_receiver.m_Quit){
					
					m_receiver.m_SubSync.Lock();
					
					byte[] namebyte = m_receiver.m_SubSocket.recv(0);
					String name = new String(namebyte);
					byte[] databyte = m_receiver.m_SubSocket.recv(0);
					
					BLog.output("EVENT", "Receiver(%s) EvName(%s) Data(...)\n", m_receiver.m_ReceiverName, name);
					
					// 退出命令
					if((s_QuitCmdPrefix+m_receiver.m_ReceiverName).compareTo(name) == 0)
					{
						break;
					}
					
					// 用户回调函数
					FuncObj funcObj = m_receiver.m_cbMap.get(name);
					
					// 构造事件实例
					com.google.protobuf.GeneratedMessage proto = null;
					Method method = m_EventCreateMap.get(name);
					if(null == method)
					{
						String dataStr = new String(databyte);
						if(dataStr.compareTo("null") != 0)
						{
							continue;
						}
					}
					else
					{
						try {
							proto = (com.google.protobuf.GeneratedMessage) method.invoke(null, databyte);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}

					m_receiver.m_SubSync.UnLock();
					
					// 回到用户函数
					try {
						funcObj.m.invoke(funcObj.o, proto);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				m_receiver.m_SubSocket.close();
				BLog.output("EVENT", "EventReceiver(%s) thread exit!\n", m_receiver.m_ReceiverName);
	        }
			public EventReceiver m_receiver;
		}
		
		private ReceiverThread m_receiverThread;
		private String m_ReceiverName;
		private Socket m_SubSocket;
		private BSyncObj m_SubSync; 
		private static class FuncObj
		{
			public FuncObj(Object obj, Method md)
			{
				o = obj;
				m = md;
			}
			public Object o;
			public Method m;
		}
		private Map<String, FuncObj> m_cbMap;
		private boolean m_Quit;
		
		public EventReceiver(String name)
		{
			m_receiverThread = new ReceiverThread(this);
			m_Quit = false;
			m_ReceiverName = name;
			Context context = BEventSys.s_context;
			m_SubSocket = context.socket(ZMQ.SUB);
			m_SubSocket.connect(s_point);
			m_SubSync = new BSyncObj();
			m_cbMap = new HashMap<String, FuncObj>();
			Subscribe(s_QuitCmdPrefix+m_ReceiverName, null, null);
			// add list
			BEventSys.s_receiverList.add(this);
		}
		
		public boolean Subscribe(String name, Object obj, String methodname)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, com.google.protobuf.GeneratedMessage.class);
					m_cbMap.put(name, new FuncObj(obj, md));
				}
				m_SubSocket.subscribe(name.getBytes());
				BLog.output("EVENT", "%s Subscribe %s\n", m_ReceiverName, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		public boolean startReceive()
		{
			BLog.output("EVENT", "EventReceiver(%s) startReceive\n", m_ReceiverName);
			m_receiverThread.start();
			return true;
		}
		
		public boolean stopReceive()
		{
			BLog.output("EVENT", "EventReceiver(%s) stopReceive\n", m_ReceiverName);
			EventSender cSender = new EventSender();
			cSender.Send(s_QuitCmdPrefix+m_ReceiverName, null);
			try {
				m_receiverThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		public String getName()
		{
			return m_ReceiverName;
		}
	}
}
