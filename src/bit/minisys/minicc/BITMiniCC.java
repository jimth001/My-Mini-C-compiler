package bit.minisys.minicc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.corba.se.spi.orbutil.fsm.Input;

public class BITMiniCC {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		//System.out.println(System.getProperty("user.dir"));
		String file;
		if(args.length < 1){
			usage();
			//新增：控制台输入文件路径
			System.out.println("请在控制台中输入源文件");
			Scanner myScanner=new Scanner(System.in);
			file=myScanner.next();
			myScanner.close();
			//return;
		}
		else {
			file = args[0];
		}
		
		if(file.indexOf(".c") < 0){
			System.out.println("Incorrect input file:" + file);
			return;
		}
//		System.out.println(file);
		MiniCCompiler cc = new MiniCCompiler();
		System.out.println("Start to compile ...");
//		System.out.println(System.getProperty("user.dir"));
		cc.run(file);
		System.out.println("Compiling completed!");
	}
	
	public static void usage(){
		System.out.println("USAGE: BITMiniCC FILE_NAME.c");
	}
}
