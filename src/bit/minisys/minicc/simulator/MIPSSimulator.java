package bit.minisys.minicc.simulator;

public class MIPSSimulator {
	public void run(String iFile){
		/**
		 * �ֶ�����һ��String[] args����ʼ��Ϊ��
		 * Ϊ����Ӧmars.MarsLaunch(String[] args)�Ĺ��캯��
		 * */
		String [] args = new String[]{};
		new mars.MarsLaunch(args);
		
		/**
		 * ����ʱ������һ��Ȩ�����⣺java.util.prefs.WindowsPreferences <init>
         * WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs 
         * at root 0x80000002. Windows RegCreateKeyEx(...) returned error code 5.
         * �޸�ע���Ȩ�޼���
         * 1. Go into your Start Menu and type regedit into the search field.
		 * 2. Navigate to path HKEY_LOCAL_MACHINE\Software\JavaSoft
		 * 3. Right click on the JavaSoft folder and click on New -> Key
		 * 4. Name the new Key Prefs and everything should work.
		 * 
		 * Alternatively, save and execute a *.reg file with the following content:
		 * ��Windows Registry Editor Version 5.00
		 * [HKEY_LOCAL_MACHINE\Software\JavaSoft\Prefs]��
		 * */
		System.out.println("7. Simulate not finished!");
	}
}
