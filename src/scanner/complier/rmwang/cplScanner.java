package scanner.complier.rmwang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.regex.*;

import bit.minisys.minicc.scanner.IMiniCCScanner;
import bit.minisys.minicc.util.MiniCCUtil;

/*auto 局部变量（自动储存）
break无条件退出程序最内层循环
case   switch语句中选择项
char单字节整型数据
const定义不可更改的常量值
continue中断本次循环，并转向下一次循环
default switch语句中的默认选择项
do  用于构成do.....while循环语句
double定义双精度浮点型数据
else构成if.....else选择程序结构
enum枚举
extern在其它程序模块中说明了全局变量
float定义单精度浮点型数据
for构成for循环语句
goto构成goto转移结构
if构成if....else选择结构
int基本整型数据
long长整型数据
registerCPU内部寄存的变量
return用于返回函数的返回值
short短整型数据
signed有符号数
sizoef计算表达式或数据类型的占用字节数
static定义静态变量
struct定义结构类型数据
switch构成switch选择结构
typedef重新定义数据类型
union联合类型数据
unsigned定义无符号数据
void定义无类型数据
volatile该变量在程序中执行中可被隐含地改变
while用于构成do...while或while循环结构*/
class typeDescription{
	public String des;
	public int key;
	public static final int inttype=10000;
	public static final int floatOrDoubleType=10001;
	public static final int stringType=10002;
	public static final int charType=10003;
	public static final int idType=10004;
	public static final int OctIntType=10005;
	public static final int HexIntType=10005;
	public typeDescription(String d,int k)
	{
		des=d;
		key=k;
	}
}
class dictionary{
	HashMap<String,typeDescription> identifierMap;
	HashMap<String,typeDescription> operatorMap;
	String id_pattern="^([A-Za-z_])+[A-Za-z0-9_]*$";
	String int_num="^-?\\d+$";//整数
	String float_num= "^(-?\\d+)(\\.\\d+)?$";//浮点数
	String Oct="^0[0-7]+$";
	String Hex="^0x[0-9a-fA-F]+$";
	String num_o="^[0-7][0-7]?[0-7]?$";//匹配1-3位八进制数
	String num_hex="^0x[0-9a-fA-f][0-9a-fA-f]?$";//匹配1-2位16进制数
	String unicodeChar="^[\u4e00-\u9fa5]$";//匹配中文
	String validString="^\"([^\\\\]+|\\\\[0-7|a|b|r|t|n|v|f|?|'|\"|\\\\]|\\\\[0-7][0-7]?[0-7]?|\\\\0x[0-9a-fA-f][0-9a-fA-f]?)*\"$";
	String includeString ="^(\\s)*#include(\\s)+\"([^\\\\]+|\\\\[0-7|a|b|r|t|n|v|f|?|'|\"|\\\\]|\\\\[0-7][0-7]?[0-7]?|\\\\0x[0-9a-fA-f][0-9a-fA-f]?)*[\"|>](.)*";
	Pattern intPattern;
	Pattern floatPattern;
	Pattern idPattern;
	Pattern num_oPattern;
	Pattern num_hexPattern;
	Pattern unicodeCharPattern;
	Pattern validStringPattern;
	Pattern octPattern;
	Pattern hexPattern;
	Pattern includePattern;
	public dictionary()
	{
		intPattern=Pattern.compile(int_num);
		floatPattern=Pattern.compile(float_num);
		idPattern=Pattern.compile(id_pattern);
		num_oPattern=Pattern.compile(num_o);
		num_hexPattern=Pattern.compile(num_hex);
		unicodeCharPattern=Pattern.compile(unicodeChar);
		validStringPattern=Pattern.compile(validString);
		octPattern=Pattern.compile(Oct);
		hexPattern=Pattern.compile(Hex);
		includePattern=Pattern.compile(includeString);
		identifierMap=new HashMap<String, typeDescription>();
		operatorMap=new HashMap<String, typeDescription>();
		identifierMap.put("auto", new typeDescription("kw1",1));
		identifierMap.put("break", new typeDescription("kw2",2));
		identifierMap.put("case", new typeDescription("kw3",3));
		identifierMap.put("char", new typeDescription("kw4",4));
		identifierMap.put("const", new typeDescription("kw5",5));
		identifierMap.put("continue", new typeDescription("kw6",6));
		identifierMap.put("default", new typeDescription("kw7",7));
		identifierMap.put("do", new typeDescription("kw8",8));
		identifierMap.put("double", new typeDescription("kw9",9));
		identifierMap.put("else", new typeDescription("kw10",10));
		identifierMap.put("enum", new typeDescription("kw11",11));
		identifierMap.put("extern", new typeDescription("kw12",12));
		identifierMap.put("float", new typeDescription("kw13",13));
		identifierMap.put("for", new typeDescription("kw14",14));
		identifierMap.put("goto", new typeDescription("kw15",15));
		identifierMap.put("if", new typeDescription("kw16",16));
		identifierMap.put("int", new typeDescription("kw17",17));
		identifierMap.put("long", new typeDescription("kw18",18));
		identifierMap.put("register", new typeDescription("kw19",19));
		identifierMap.put("return", new typeDescription("kw20",20));
		identifierMap.put("short", new typeDescription("kw21",21));
		identifierMap.put("signed", new typeDescription("kw22",22));
		identifierMap.put("sizoef", new typeDescription("kw23",23));
		identifierMap.put("static", new typeDescription("kw24",24));
		identifierMap.put("struct", new typeDescription("kw25",25));
		identifierMap.put("switch", new typeDescription("kw26",26));
		identifierMap.put("typedef", new typeDescription("kw27",27));
		identifierMap.put("union", new typeDescription("kw28",28));
		identifierMap.put("unsigned", new typeDescription("kw29",29));
		identifierMap.put("void", new typeDescription("kw30",30));
		identifierMap.put("volatile", new typeDescription("kw31",31));
		identifierMap.put("while", new typeDescription("kw32",32));
		identifierMap.put("define", new typeDescription("宏定义标识符",101));
		identifierMap.put("include", new typeDescription("包含文件标识符",102));
		operatorMap.put("+", new typeDescription("加",33));
		operatorMap.put("-", new typeDescription("减",34));
		operatorMap.put("*", new typeDescription("乘/取指针内容",35));
		operatorMap.put("/", new typeDescription("除",36));
		operatorMap.put("++", new typeDescription("自增",37));
		operatorMap.put("--", new typeDescription("自减",38));
		operatorMap.put("+=", new typeDescription("加赋值",39));
		operatorMap.put("*=", new typeDescription("乘赋值",40));
		operatorMap.put("/=", new typeDescription("除赋值",41));
		operatorMap.put("-=", new typeDescription("减赋值",42));
		operatorMap.put("%", new typeDescription("模",43));
		operatorMap.put("%=", new typeDescription("模赋值",44));
		operatorMap.put("&", new typeDescription("位与/取地址",45));
		operatorMap.put("|", new typeDescription("位或",46));
		operatorMap.put("!", new typeDescription("非",47));
		operatorMap.put("&&", new typeDescription("与",48));
		operatorMap.put("||", new typeDescription("或",49));
		operatorMap.put("->", new typeDescription("取成员",50));
		operatorMap.put(".", new typeDescription("取成员",51));
		operatorMap.put(">", new typeDescription("大于",52));
		operatorMap.put("<", new typeDescription("小于",53));
		operatorMap.put(">>", new typeDescription("右移",54));
		operatorMap.put("<<", new typeDescription("左移",55));
		operatorMap.put(">=", new typeDescription("大于等于",56));
		operatorMap.put("<=", new typeDescription("小于等于",57));
		operatorMap.put("?", new typeDescription("?:算符",58));
		operatorMap.put(":", new typeDescription("?:算符",59));
		operatorMap.put("!=", new typeDescription("不等于",60));
		operatorMap.put("==", new typeDescription("等于判断",61));
		operatorMap.put(",", new typeDescription("逗号算符",62));
		operatorMap.put("(", new typeDescription("括号s",63));
		operatorMap.put(")", new typeDescription("括号e",64));
		operatorMap.put("[", new typeDescription("取数组成员s",65));
		operatorMap.put("]", new typeDescription("取数组成员e",66));
		operatorMap.put("~", new typeDescription("位非",67));
		operatorMap.put("^", new typeDescription("位异或",68));
		operatorMap.put("&=", new typeDescription("位与赋值",69));
		operatorMap.put("|=", new typeDescription("位或赋值",70));
		operatorMap.put("^=", new typeDescription("位异或赋值",71));
		operatorMap.put(">>=", new typeDescription("右移赋值",72));
		operatorMap.put("<<=", new typeDescription("左移赋值",73));
		operatorMap.put(";", new typeDescription("语句分隔符",74));
		operatorMap.put("=", new typeDescription("赋值运算符",75));
		operatorMap.put("{", new typeDescription("花括号s",76));
		operatorMap.put("}", new typeDescription("花括号e",77));
		operatorMap.put("#", new typeDescription("预编译符号",100));
	}
}
class charType{
	public static final int number=1;
	public static final int letter=2;
	public static final int separator_birth_1=3;//  space tab \r]n
	public static final int separator_birth_2=4;//  ,;:(){}
	public static final int stringMatch=5;//"
	public static final int charMatch=6;//'
	public static final int operator_birth_1=7;//*/%!^ 这些只可能和=结合
	public static final int trans=8;//转义符\
	public static final int illegal=9;//非法字符，即不是上面那些
	public static final int leftarrow=10;//<
	public static final int rightarrow=11;//>可能匹配>,>>
	public static final int minusflag=12;//-可能匹配-，--，->,-=
	public static final int illegal_unANSI=13;
	public static final int plusOperator=14;//+
	public static final int equalOperator=15;//=
	public static final int AndOperator=16;//&
	public static final int OrOperator=17;//|
	public static final int xiahuaxian=18;//_
	public static final int dot=19;
	public static final int sim_askflag=20;//类？标志，包括？~，这两种都是不能后接运算符
	public static final int huanhang=21;//换行
	public static final int includeflag=22;
	public static int getCharType(char a,dictionary dic)
	{
		if(isNum(a))
		{
			//System.out.println("数字出现:"+a);
			return number;
		}
		if(isLetter(a))
		{
			//System.out.println("字母出现"+a);
			return letter;
		}
		if(!dic.unicodeCharPattern.matcher(String.valueOf(a)).matches())
		{
			switch (a) {
			case '_':return xiahuaxian;
			case ' ':return separator_birth_1;
			case '	':return separator_birth_1;//tab
			case '\r':return separator_birth_1;
			case ',':return separator_birth_2;
			case ';':return separator_birth_2;
			case ':':return separator_birth_2;
			case '(':return separator_birth_2;
			case ')':return separator_birth_2;
			case '{':return separator_birth_2;
			case '}':return separator_birth_2;
			case '<':return leftarrow;
			case '>':return rightarrow;
			case '\'':return charMatch;
			case '\"':return stringMatch;
			case '*':return operator_birth_1;
			case '/':return operator_birth_1;
			case '%':return operator_birth_1;
			case '!':return operator_birth_1;
			case '~':return sim_askflag;
			case '^':return operator_birth_1;
			case '?':return sim_askflag;
			case '+':return plusOperator;
			case '-':return minusflag;
			case '=':return equalOperator;
			case '&':return AndOperator;
			case '|':return OrOperator;
			case '\\':return trans;
			case '.':return dot;
			case '\n':return huanhang;
			case '#':return includeflag;
			default:return illegal;
			}
		}
		else {
			return illegal_unANSI;//是一个中文字符
		}
	}
	public static boolean isNum(char a)
	{
		if(a>='0'&&a<='9')
				return true;
		return false;
	}
	public static boolean isLetter(char a)
	{
		if(a>='a'&&a<='z')
				return true;
		if(a>='A'&&a<='Z')
				return true;
		return false;
	}
}
class token{
	public static int nilType=-1;
	int id;
	String value;
	int type;
	int line;
	boolean valid;
	String typedes;
	String wrongInf;
	String srcString;
 	public token(int id,String value,int type,int line,boolean valid,String src)
	{
		this.id=id;
		this.value=value;
		this.type=type;
		this.line=line;
		this.valid=valid;
		this.typedes="";
		this.wrongInf="无";
		this.srcString=src;
	}
	public token()
	{
		id=0;
		value="";
		type=nilType;
		line=-1;
		valid=true;
		srcString="";
	}
	public token(token t)
	{
		this.id=t.id;
		this.value=t.value;
		this.type=t.type;
		this.line=t.line;
		this.valid=t.valid;
		this.typedes=t.typedes;
		this.wrongInf=t.wrongInf;
		this.srcString=t.srcString;
	}
}
class DFA{
	public int index;
	public String inputString;
	public String nowString;
	public int dfa_state;
	public int def_state;
	public int def_index;
	public int rec_index_def;
	public char nowChar;
	public ArrayList<token> myTokenArrayList;
	public int wordcounter;
	public int linecounter;
	public int thislineforDEF;
	public dictionary myDictionary;
	public ArrayList<token> tokenListForDEF;
	public ArrayList<token> tokenListForDEFReplace;
	public HashMap<String,String> defineID;
	public ArrayList<includeInf> includeInfArrayList;
	public String srcString;
	//状态类型
	public static final int state_init=1;//初始状态
	public static final int state_num_m=2;//
	public static final int state_num_dot_m=10002;//
	public static final int state_num_dot_f_b=10005;//f表示终态,b表示回退一个字符
	public static final int state_letterorxhx_m=3;//
	public static final int state_letterorxhx_f_b=10004;//
	public static final int state_sp1_f=4;
	public static final int state_sp2_f=5;
	public static final int state_op1_m=6;
	public static final int state_op1_equal_f=1110;
	public static final int state_op1_f_b=1111;
	public static final int state_askflag_f=5555;
	public static final int state_string_m=7;
	public static final int state_string_trans_m=8;
	public static final int state_string_f=9;
	public static final int state_char_m=10;
	public static final int state_char_trans_m=11;
	public static final int state_char_f=12;
	public static final int state_plus_m=13;
	public static final int state_equal_m=14;
	public static final int state_and_m=15;
	public static final int state_or_m=16;
	public static final int state_plus_d_f=17;
	public static final int state_plus_equal_f=18;
	public static final int state_plus_f_b=19;
	public static final int state_equal_d_f=20;
	public static final int state_equal_f_b=21;
	public static final int state_and_d_f=22;
	public static final int state_and_f_b=23;
	public static final int state_or_d_f=24;
	public static final int state_or_f_b=25;
	public static final int state_larrow_m=26;
	public static final int state_rarrow_m=27;
	public static final int state_larrow_d_f=28;
	public static final int state_larrow_f_b=29;
	public static final int state_larrow_equal_f=1100;
	public static final int state_rarrow_d_f=30;
	public static final int state_rarrow_f_b=31;
	public static final int state_rarrow_equal_f=1101;
	public static final int state_minus_m=32;
	public static final int state_minus_d_f=33;
	public static final int state_minus_arrow_f=34;
	public static final int state_minus_equal_f=6000;
	public static final int state_minus_f_b=35;
	public static final int state_illegal_f=36;
	//单词大类
	public static final int op_wordtype=100;
	public static final int sp_wordtype=101;
	public static final int id_wordtype=102;
	public static final int str_wordtype=103;
	public static final int char_wordtype=104;
	public static final int ansi_illegal_wordtype=105;
	public static final int unicode_illegal_wordtype=106;
	public static final int illegal_trans_wordtype=107;//非法的转义字符
	public static final int illegal_unknown_wordtype=109;//非法未知类型
	public static final int illegal_end=200;//非法的结尾
	//state for define checking DFA:
	public static final int sInit=701;
	public static final int sSharp=702;
	public static final int sNoSharp_m=703;
	public static final int sNoSharp_f_b=704;
	public static final int sIllegalSharp_f=705;
	public static final int sDefine=706;
	public static final int sEpID_m=707;
	public static final int sEpID_f_b=708;
	public static final int sUnDefine_f=709;//不完整的define
	public static final int sID=710;
	public static final int sID_p_f_b=711;//p表示define A B
	public static final int sID_f_b=712;//表示define A
	public void goBackIndexForDEF()
	{
		def_index--;
	}
	public boolean isIDofDEF(int i)
	{
		token t=myTokenArrayList.get(i);
		if(i-2<0)
			return false;
		else {
			token lt=myTokenArrayList.get(i-1);
			token llt=myTokenArrayList.get(i-2);
			if(lt.type==myDictionary.identifierMap.get("define").key&&llt.type==myDictionary.operatorMap.get("#").key)
			{
				if(i==2&&lt.line==llt.line&&lt.line==t.line)
				{
					return true;
				}
				if(i>2)
				{
					token lllt=myTokenArrayList.get(i-3);
					if(lt.line==llt.line&&lt.line==t.line&&lllt.line!=t.line)
					{
						return true;
					}
				}
			}
			return false;
		}
	}
	public void dealTokenList(boolean success)
	{
		if(success)//合法的宏定义
		{
			
			token t=tokenListForDEF.get(tokenListForDEF.size()-1);//取标识符
			//System.out.println("合法宏："+t.value);
			token c_pToken;
			int i=def_index+1;//下一行的起始位置
			//int len=myTokenArrayList.size();
			while(i<myTokenArrayList.size())//
			{
				c_pToken=myTokenArrayList.get(i);
				//System.out.println(c_pToken.value);
				if(c_pToken.value.equals(t.value)&&!isIDofDEF(i))//找到了要替换的位置并且这个位置不是下面define中的标识符
				{
					int size=tokenListForDEFReplace.size();
					for(int j=0;j<size;j++)
					{
						
						token tmpToken=new token(tokenListForDEFReplace.get(j));
						tmpToken.line=c_pToken.line;//更正行数
						tmpToken.typedes="#define"+"  "+t.value;
						//System.out.println(tmpToken.typedes+"  "+i);
						myTokenArrayList.add(i+j,new token(tmpToken));
					}
					i+=size;
					myTokenArrayList.remove(i);
				}
				else {
					i++;
				}
			}
			for(i=rec_index_def;i<myTokenArrayList.size();)
			{
				token tk=myTokenArrayList.get(i);
				if(tk.line==thislineforDEF)
				{
					myTokenArrayList.remove(i);
				}
				else{
					break;
				}
			}
			tokenListForDEF.clear();
			tokenListForDEFReplace.clear();
			def_index=rec_index_def-1;
		}
		else {//非法的宏定义
			tokenListForDEF.clear();
			tokenListForDEFReplace.clear();
		}
	}
	public void stateTransAndActionForDefine(token t)
	{
		switch (def_state) {
		case sInit:thislineforDEF=t.line;
			rec_index_def=def_index;
			if(t.type==myDictionary.operatorMap.get("#").key)
			{
				def_state=sSharp;
				tokenListForDEF.add(new token(t));
				return;
			}
			else{
				def_state=sNoSharp_m;
				return;
			}
		case sSharp:
			if(t.line!=thislineforDEF)
			{
				goBackIndexForDEF();
				dealTokenList(false);
				def_state=sInit;
			}
			else {
				typeDescription tDescription=myDictionary.identifierMap.get("define");
				if(t.type==tDescription.key)
				{
					tokenListForDEF.add(new token(t));
					def_state=sDefine;
				}
				else {
					def_state=sNoSharp_m;
				}
			}
			return;
		case sNoSharp_m:
			if(t.line==thislineforDEF)
			{
				if(t.type==myDictionary.operatorMap.get("#").key)
				{
					t.valid=false;
					t.wrongInf="此处不需要出现#";
				}
			}
			else {
				def_state=sInit;
				goBackIndexForDEF();
				dealTokenList(false);
			}
			return;
		case sNoSharp_f_b:return;
		case sIllegalSharp_f:return;
		case sDefine:
			if(t.line!=thislineforDEF)
			{
				goBackIndexForDEF();
				dealTokenList(false);
				def_state=sInit;
			}
			else {
				if(t.type==typeDescription.idType)
				{
					if(defineID.containsKey(t.value))
					{
						t.valid=false;
						t.wrongInf="重定义的宏";
						def_state=sNoSharp_m;
					}
					else{
						tokenListForDEF.add(new token(t));
						def_state=sID;
						defineID.put(t.value, t.value);
					}
				}
				else {
					t.valid=false;
					t.wrongInf="应输入标识符";
					def_state=sEpID_m;
				}
			}
			return;
		case sEpID_m:
			if(t.line==thislineforDEF)
			{
				if(t.type==myDictionary.operatorMap.get("#").key)
				{
					t.valid=false;
					t.wrongInf="此处不需要出现#";
				}
			}
			else {
				def_state=sInit;
				goBackIndexForDEF();
				dealTokenList(false);
			}
			return;
		case sEpID_f_b:return;
		case sUnDefine_f:return;
		case sID:
			if(t.line==thislineforDEF)
			{
				tokenListForDEFReplace.add(new token(t));
			}
			else {
				goBackIndexForDEF();
				dealTokenList(true);
				def_state=sInit;
			}
			return;
		case sID_p_f_b:return;
		case sID_f_b:return;
		default:
			break;
		}
	}
 	public DFA(String in,dictionary dic,ArrayList<includeInf> incldlist,String src)
	{
 		thislineforDEF=0;
		index=0;
		rec_index_def=0;
		dfa_state=state_init;
		def_state=sInit;
		def_index=0;
		inputString=in;
		nowString="";
		myTokenArrayList=new ArrayList<token>();
		tokenListForDEF=new ArrayList<token>();
		tokenListForDEFReplace=new ArrayList<token>();
		defineID=new HashMap<String,String>();
		linecounter=1;
		wordcounter=0;
		myDictionary=dic;
		includeInfArrayList=incldlist;
		srcString=src;
	}
	public void pointerBack()
	{
		nowString=nowString.substring(0,nowString.length()-1);
		index--;
		
	}
	public void addWord(int type,boolean valid)//同时负责清空缓冲nowString
	{
		myTokenArrayList.add(new token(wordcounter,nowString,type,linecounter,valid,""));
		wordcounter++;
		nowString="";
	}
	public void stateTransAndAction(int chartype)
	{
		
		switch (dfa_state) {
		case state_init://初始状态
			if(chartype==charType.illegal) {dfa_state=state_illegal_f;addWord(ansi_illegal_wordtype,false);dfa_state=state_init;return;}
			if(chartype==charType.letter) {dfa_state=state_letterorxhx_m;return;}
			if(chartype==charType.number) {dfa_state=state_num_m;return;}
			if(chartype==charType.xiahuaxian) {dfa_state=state_letterorxhx_m;return;}
			if(chartype==charType.separator_birth_1){nowString="";dfa_state=state_init;return;}
			if(chartype==charType.separator_birth_2){dfa_state=state_sp2_f;addWord(sp_wordtype, true);dfa_state=state_init;return;}
			if(chartype==charType.operator_birth_1){dfa_state=state_op1_m;return;}
			if(chartype==charType.plusOperator){dfa_state=state_plus_m;return;}
			if(chartype==charType.equalOperator){dfa_state=state_equal_m;return;}
			if(chartype==charType.AndOperator){dfa_state=state_and_m;return;}
			if(chartype==charType.OrOperator){dfa_state=state_or_m;return;}
			if(chartype==charType.leftarrow){dfa_state=state_larrow_m;return;}
			if(chartype==charType.rightarrow){dfa_state=state_rarrow_m;return;}
			if(chartype==charType.minusflag){dfa_state=state_minus_m;return;}
			if(chartype==charType.stringMatch){dfa_state=state_string_m;return;}
			if(chartype==charType.charMatch){dfa_state=state_char_m;return;}
			if(chartype==charType.illegal_unANSI){dfa_state=state_illegal_f;addWord(unicode_illegal_wordtype,false);dfa_state=state_init;return;}
			if(chartype==charType.trans){dfa_state=state_illegal_f;addWord(illegal_trans_wordtype,false);dfa_state=state_init;return;}
			if(chartype==charType.dot){addWord(op_wordtype,true);dfa_state=state_init;return;}
			if(chartype==charType.sim_askflag){dfa_state=state_askflag_f;addWord(op_wordtype, true);dfa_state=state_init;return;}
			if(chartype==charType.huanhang){nowString="";linecounter++;dfa_state=state_init;return;}
			if(chartype==charType.includeflag){addWord(op_wordtype, true);dfa_state=state_init;return;}
			System.out.println("state_init下charType枚举不全:"+chartype);addWord(illegal_unknown_wordtype, false);dfa_state=state_init;return;
		case state_num_m:
			switch (chartype) {
			case charType.number:return;
			case charType.dot:dfa_state=state_num_dot_m;return;
			case charType.xiahuaxian:dfa_state=state_letterorxhx_m;return;
			case charType.letter:dfa_state=state_letterorxhx_m;return;
			default:pointerBack();addWord(id_wordtype, true);dfa_state=state_init;return;
			}//
		case state_num_dot_m:
			switch (chartype) {
			case charType.number:break;
			default:pointerBack();addWord(id_wordtype, true);dfa_state=state_init;return;
			}
		case state_num_dot_f_b://final状态，跳转到final状态时已经处理了并自动转回了初态（jump和action合并到了一个函数），所以不会遇到此状态
			return;
		case state_letterorxhx_m://
			switch (chartype) {
			case charType.number:return;
			case charType.letter:return;
			case charType.xiahuaxian:return;
			default:pointerBack();addWord(id_wordtype, true);dfa_state=state_init;return;
			}
		case state_letterorxhx_f_b:
			return;
		case state_sp1_f:return;
		case state_sp2_f:return;
		case state_op1_m:
			switch (chartype) {
			case charType.equalOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_op1_equal_f:return;
		case state_op1_f_b:return;
		case state_askflag_f:return;
		case state_string_m:
			switch (chartype) {
			case charType.trans:dfa_state=state_string_trans_m;return;
			case charType.stringMatch:dfa_state=state_string_f;addWord(str_wordtype, true);dfa_state=state_init;return;
			case charType.huanhang:pointerBack();addWord(str_wordtype, true);dfa_state=state_init;return;
			default:return;
			}
		case state_string_trans_m:dfa_state=state_string_m;return;
		case state_string_f:return;
		case state_char_m:
			switch (chartype) {
			case charType.trans:dfa_state=state_char_trans_m;return;
			case charType.charMatch:dfa_state=state_char_f;addWord(char_wordtype, true);dfa_state=state_init;return;
			case charType.huanhang:pointerBack();addWord(char_wordtype, true);dfa_state=state_init;return;
			default:return;
			}
		case state_char_trans_m:dfa_state=state_char_m;return;
		case state_char_f:return;
		case state_plus_m:
			switch (chartype) {
			case charType.plusOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			case charType.equalOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_equal_m:
			switch (chartype) {
			case charType.equalOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_and_m:
			switch (chartype) {
			case charType.AndOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_or_m:
			switch (chartype) {
			case charType.OrOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_plus_d_f:return;
		case state_plus_equal_f:return;
		case state_plus_f_b:return;
		case state_equal_d_f:return;
		case state_equal_f_b:return;
		case state_and_d_f:return;
		case state_and_f_b:return;
		case state_or_d_f:return;
		case state_or_f_b:return;
		case state_larrow_m:
			switch (chartype) {
			case charType.leftarrow:addWord(op_wordtype, true);dfa_state=state_init;return;
			case charType.equalOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_rarrow_m:
			switch (chartype) {
			case charType.rightarrow:addWord(op_wordtype, true);dfa_state=state_init;return;
			case charType.equalOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_larrow_d_f:return;
		case state_larrow_f_b:return;
		case state_larrow_equal_f:return;
		case state_rarrow_d_f:return;
		case state_rarrow_f_b:return;
		case state_rarrow_equal_f:return;
		case state_minus_m:
			switch (chartype) {
			case charType.rightarrow:addWord(op_wordtype, true);dfa_state=state_init;return;
			case charType.equalOperator:addWord(op_wordtype, true);dfa_state=state_init;return;
			case charType.minusflag:addWord(op_wordtype, true);dfa_state=state_init;return;
			/*case charType.number:
				token t;
				if(wordcounter>0)
				{
					t=myTokenArrayList.get(wordcounter-1);
					if(t.type==id_wordtype)//如果前面是个标识符，那就是减号，否则认为是负号
					{
						pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
					}
					else {
						dfa_state=state_num_m;
						return;
					}
				}
				else {
					dfa_state=state_num_m;
					return;
				}*/
			default:pointerBack();addWord(op_wordtype, true);dfa_state=state_init;return;
			}
		case state_minus_d_f:return;
		case state_minus_arrow_f:return;
		case state_minus_equal_f:return;
		case state_minus_f_b:return;
		case state_illegal_f:return;
		default:System.out.println("非法的状态值："+dfa_state);
			break;
		}
	}
	public void run()
	{
		int len=inputString.length();
		int i;
		//分词和基本类别判定：
		while(index<len)
		{
			nowChar=inputString.substring(index,index+1).charAt(0);
			nowString+=String.valueOf(nowChar);
			index++;
			stateTransAndAction(charType.getCharType(nowChar,myDictionary));
		}
		if(dfa_state!=state_init)//如果遇到非法的结尾
		{
			addWord(illegal_end, false);
		}
		for(i=0;i<myTokenArrayList.size();i++)
		{
			token t=myTokenArrayList.get(i);
			switch (t.type) {
			case op_wordtype:
				//System.out.println("op:"+t.value);
				judgeForOperatorAndSP(t);break;
			case sp_wordtype:
				//System.out.println("sp:"+t.value);
				judgeForOperatorAndSP(t);break;
			case id_wordtype:
				//System.out.println("id:"+t.value);
				judgeForIdentifier(t);break;
			case str_wordtype:
				//System.out.println("str:"+t.value);
				judgeForString(t);break;
			case char_wordtype:
				//System.out.println("char:"+t.value);
				judgeForChar(t);break;
			case ansi_illegal_wordtype:t.typedes="非法的ANSI字符";t.wrongInf=t.typedes;
			case unicode_illegal_wordtype:t.typedes="非法的unicode字符";t.wrongInf=t.typedes;
			case illegal_trans_wordtype:t.typedes="非法位置的转义字符";t.wrongInf=t.typedes;
			case illegal_unknown_wordtype:t.typedes="process error：未知的Type,DFA没有识别所有type类型";t.wrongInf=t.typedes;
			case illegal_end:judgeForChar(t);
				if(t.valid==true)
					break;
				judgeForIdentifier(t);
				if(t.valid==true)
					break;
				judgeForOperatorAndSP(t);
				if(t.valid==true)
					break;
				judgeForString(t);
				if(t.valid==true)
					break;
				t.valid=false;
				t.typedes="不合法类型的结尾";
				t.wrongInf="不合法类型的结尾";
			default:
				break;
			}
		}
		while(def_index<myTokenArrayList.size())
		{
			token tmpToken=myTokenArrayList.get(def_index);
			stateTransAndActionForDefine(tmpToken);
			def_index++;
		}
		int j=includeInfArrayList.size()-1;
		if(j>=0)
		{
			includeInf incInf=includeInfArrayList.get(j);
			int offsetsum=incInf.lineOffset;
			for(i=0;i<myTokenArrayList.size();i++)
			{
				token t=myTokenArrayList.get(i);
				if(t.line<=offsetsum)
				{
					t.srcString=srcString.substring(0,srcString.lastIndexOf("\\"))+"\\"+incInf.fileNameString;
					t.line=t.line-offsetsum+incInf.lineOffset;
				}
				else{
					if(j>0)
					{
						j--;
						incInf=includeInfArrayList.get(j);
						offsetsum+=incInf.lineOffset;
						t.srcString=srcString.substring(0,srcString.lastIndexOf("\\"))+"\\"+incInf.fileNameString;//incInf.fileNameString;
						t.line=t.line-offsetsum+incInf.lineOffset;
					}
					else {
						t.srcString=srcString;
						t.line=t.line-offsetsum;
					}
				}
			}
		}
		
		
	}
	public void judgeForIdentifier(token t)//判断标识为id_wordtype的单词的具体类型，是否有错
	{
		if(myDictionary.identifierMap.containsKey(t.value))
		{
			//System.out.println("关键字："+t.value);
			typeDescription mydes=myDictionary.identifierMap.get(t.value);
			t.type=mydes.key;
			t.typedes=mydes.des;
			t.valid=true;
			return;
		}
		else{//不是关键字
			if(myDictionary.intPattern.matcher(t.value).matches())
			{
				//System.out.println("数字："+t.value);
				t.valid=true;
				t.type=typeDescription.inttype;
				t.typedes="int";
				return;
			}
			if(myDictionary.floatPattern.matcher(t.value).matches())
			{
				//System.out.println("数字："+t.value);
				t.valid=true;
				t.type=typeDescription.floatOrDoubleType;
				t.typedes="float or double";
				return;
			}
			if(myDictionary.hexPattern.matcher(t.value).matches())
			{
				//System.out.println("数字："+t.value);
				t.valid=true;
				t.type=typeDescription.inttype;
				t.typedes="Hex";
				return;
			}
			if(myDictionary.octPattern.matcher(t.value).matches())
			{
				//System.out.println("数字："+t.value);
				t.valid=true;
				t.type=typeDescription.inttype;
				t.typedes="Oct";
				return;
			}
			if(myDictionary.idPattern.matcher(t.value).matches())
			{
				//System.out.println("标识符："+t.value);
				t.valid=true;
				t.type=typeDescription.idType;
				t.typedes="identifier";
				return;
			}
			
			t.valid=false;
			t.type=typeDescription.idType;
			t.typedes="illegal identifier";
			t.wrongInf="非法的标识符";
			return ;
		}
	}
	public void judgeForOperatorAndSP(token t)//判断运算符的具体类型和分隔符具体类型。这里执行后只会把；看作分隔符，其余初始划分在分隔符中的符号都将变成运算符类型
	{
		if(myDictionary.operatorMap.containsKey(t.value))
		{
			typeDescription mydes=myDictionary.operatorMap.get(t.value);
			t.type=mydes.key;
			t.typedes=mydes.des;
			t.valid=true;
		}
		else {
			t.typedes=" ";
			t.wrongInf="未知的运算符，dictionary与DFA不匹配！";
			t.valid=false;
		}
	}
	public void judgeForChar(token t)//判断单字符的合法性
	{
		t.typedes="字符常量";
		char arr[]=t.value.toCharArray();
		switch (arr.length) {
		case 1:t.wrongInf="非法的字符结尾，期待'";t.valid=false;break;
		case 2:t.wrongInf="字符常量至少应包含一个字符";t.valid=false;break;
		case 3:if(arr[arr.length-1]=='\''&&arr[1]!='\\') {t.valid=true;}else {t.valid=false;t.wrongInf="非法的字符结尾，期待'";}break;
		case 4:
			if(arr[1]!='\\'||arr[3]!='\'') 
			{t.valid=false;t.wrongInf="非法的字符结尾，期待'";}
			else {
				t.valid=judgeForTrans(arr[2]);
				if(t.valid==false)
				{
					t.wrongInf="不可识别的字符转义序列";
				}
			}
			break;
		case 5://两个数字
			String tmp1=t.value.substring(2, 4);
			if(arr[4]!='\''){
				t.valid=false;
				t.wrongInf="非法的字符结尾，期待'";
				break;
			}
			if(myDictionary.num_oPattern.matcher(tmp1).matches())
			{
				t.valid=true;
				break;
			}
			if(myDictionary.num_hexPattern.matcher(tmp1).matches())
			{
				t.valid=true;
				break;
			}
			t.valid=false;
			t.wrongInf="不可识别的字符转义序列";
			break;
		case 6://三个数字
			String tmp2=t.value.substring(2, 5);
			if(arr[5]!='\''){
				t.valid=false;
				t.wrongInf="非法的字符结尾，期待'";
				break;
			}
			if(myDictionary.num_oPattern.matcher(tmp2).matches())
			{
				t.valid=true;
				break;
			}
			if(myDictionary.num_hexPattern.matcher(tmp2).matches())
			{
				t.valid=true;
				break;
			}
			t.valid=false;
			t.wrongInf="不可识别的字符转义序列";
			break;
		case 7://0xhh
			String tmp3=t.value.substring(2, 6);
			if(arr[6]!='\''){
				t.valid=false;
				t.wrongInf="非法的字符结尾，期待'";
				break;
			}
			if(myDictionary.num_hexPattern.matcher(tmp3).matches())
			{
				t.valid=true;
				break;
			}
			t.valid=false;
			t.wrongInf="不可识别的字符转义序列";
			break;
		default:
			t.valid=false;
			t.wrongInf="字符常量中的字符过多";
			break;
		}
	}
	public void judgeForString(token t)//判断string的合法性,未完成，一律认为合法
	{
		t.typedes="字符串常量";
		
		int len=t.value.length();
		char arr[]=t.value.toCharArray();
		if(arr[0]!='\"'||arr[len-1]!='\"'||len<2)
		{
			t.valid=false;
			t.wrongInf="非法的字符串结尾";
			return;
		}
		else {
			if(len==2)
			{
				t.valid=true;
				return;
			}
			else {
				if(arr[len-2]=='\\')
				{
					t.valid=false;
					t.wrongInf="非法的字符串结尾";
					return;
				}
				else {
					if(myDictionary.validStringPattern.matcher(t.value).matches())
					{
						t.valid=true;
						return;
					}
					else {
						t.valid=false;
						t.wrongInf="字符串中存在不可识别的字符转义序列";
						return;
					}
				}
			}
		}
	}
	public boolean judgeForTrans(char t)//判断转义的合法性,在判断字符或string合法性时调用
	{
		switch (t) {
		case 'a':return true;
		case 'b':return true;
		case 'f':return true;
		case 'n':return true;
		case 'r':return true;
		case 't':return true;
		case 'v':return true;
		case '\\':return true;
		case '\'':return true;
		case '\"':return true;
		case '?':return true;
		case '0':return true;
		case '1':return true;
		case '2':return true;
		case '3':return true;
		case '4':return true;
		case '5':return true;
		case '6':return true;
		case '7':return true;
		default:return false;
		}
	}
	public boolean isLexAnalysePass()//是否通过了词法分析
	{
		int i=0;
		int len=myTokenArrayList.size();
		token t;
		for(i=0;i<len;i++)
		{
			t=myTokenArrayList.get(i);
			if(t.valid==false)
			{
				return false;
			}
		}
		return true;
	}
	public String outputWordList(String output,boolean wrongInfSwitch) throws IOException
	{
			    Element root = new Element("project").setAttribute("name", "test.l");
			    
			    Document Doc = new Document(root);
			    
			    Element tokens = new Element("tokens");
			    root.addContent(tokens);
			    
			    for (int i = 0; i < this.myTokenArrayList.size(); i++) {
			      token word = (token)this.myTokenArrayList.get(i);
			      
			      Element elements = new Element("token");
			      
			      elements.addContent(new Element("number").setText(new Integer(word.id).toString()));
			      elements.addContent(new Element("value").setText(word.value));
			      elements.addContent(new Element("type").setText(new Integer(word.type).toString()));
			      elements.addContent(new Element("line").setText(new Integer(word.line).toString()));
			      elements.addContent(new Element("valid").setText(new Boolean(word.valid).toString()));
			      if(wrongInfSwitch)
			      {
			    	  elements.addContent(new Element("typeDescription").setText(word.typedes));
				      elements.addContent(new Element("wrongInf").setText(word.wrongInf));
				      elements.addContent(new Element("src").setText(word.srcString));
			      }
			     
			      tokens.addContent(elements);
			    }
			    Format format = Format.getPrettyFormat();
			    XMLOutputter XMLOut = new XMLOutputter(format);
			    XMLOut.output(Doc, new FileOutputStream(output));
			    return output;
	}
}
class recordNoteLoc{//记录注释符号/*的位置信息
    public int line;
    public recordNoteLoc(int a)
    {
    	line=a;
    }
    public recordNoteLoc(recordNoteLoc t)
    {
    	this.line=t.line;
    }
}
class includeInf{//记录include信息
	public String fileNameString;
	public int lineOffset;//行偏移量，用于修正单词流中单词对应源文件的行数而设置
	public includeInf(String name,int lineoffset)
	{
		fileNameString=name;
		lineOffset=lineoffset;
	}
	public includeInf(includeInf inf)
	{
		this.fileNameString=inf.fileNameString;
		this.lineOffset=inf.lineOffset;
	}
}
class preprocesser{
	public String path;
	public String allTestString;
	public String inFileType;
	public String outFileType;
	//public static final String out2=".pp2.c";
	//public static final String out3=".pp3.c";
	public ArrayList<includeInf> includeInfArrayList;
	public HashMap<String, String> includeRecorder;
	public ArrayList<recordNoteLoc> f_rnl;
	public ArrayList<recordNoteLoc> b_rnl;
	public dictionary dic;
	public String processed = "";
	public int linenum=1;
	public preprocesser(String src,dictionary dic,String intype)
	{
		path=src;
		f_rnl=new ArrayList<recordNoteLoc>();
		b_rnl=new ArrayList<recordNoteLoc>();
		includeInfArrayList=new ArrayList<includeInf>();
		includeRecorder=new HashMap<String,String>();
		inFileType=intype;
	}
	public preprocesser(String src,dictionary dic,HashMap<String, String> includeRecorder,String intype)
	{
		path=src;
		f_rnl=new ArrayList<recordNoteLoc>();
		b_rnl=new ArrayList<recordNoteLoc>();
		includeInfArrayList=new ArrayList<includeInf>();
		this.includeRecorder=includeRecorder;
		inFileType=intype;
	}
 	public boolean pp(String output)
	{
 		if (!MiniCCUtil.checkFile(this.path)) {
 		      return false;
 		    }
 		processed = "";
 		//int readtimes=0;
 		
 		File file = new File(this.path);
	    try
	    {
	      BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
	      String line = "";
	      
	      while ((line = reader.readLine()) != null)
	      {
	    	//readtimes++;
	    	//System.out.println(readtimes+" times:"+line);
	        int start = line.indexOf("//");
	        if (start != 0)
	        {
	          if (start > 0) {
	            line = line.substring(0, start);
	          }
	          start = line.indexOf("/*");
	          int end = line.indexOf("*/");
	          if ((start >= 0) && (end >= 0) && (end - start >= 2)) {
	            line = line.substring(0, start)+line.substring(end+2);
	          }
	          processed = processed + line+"\r\n";
	        }
	        else {//从头开始是注释
	        	processed +="\r\n";
			}
	       }
	      reader.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    allTestString=processed;
	    //System.out.println("read times:"+readtimes);
	    MiniCCUtil.createAndWriteFile(output.replace(inFileType, ".pp1"+inFileType), processed);
	    return true;
	}
 	public boolean pp2(String output)
	{
 		String out1path=output.replace(inFileType, ".pp1"+inFileType);
 		if (!MiniCCUtil.checkFile(out1path))
 		{
 			System.out.println("预处理错误："+out1path+"不存在");
 		      return false;
 		}
 		processed = "";
 		linenum=1;
 		boolean findfN=false;
 		File file = new File(out1path);
	    try
	    {
	      BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
	      String line = "";
	      
	      while ((line = reader.readLine()) != null)
	      {
	    	int start=line.indexOf("/*");
	    	int end=line.indexOf("*/");
	        if(start>=0&&findfN==false)
	        {
	        	f_rnl.add(new recordNoteLoc(linenum));
	        	findfN=true;
	        }
	        if(findfN==true&&end>=0)
	        {
	        	b_rnl.add(new recordNoteLoc(linenum));
	        	findfN=false;
	        }
	        
	        processed = processed + line+"\r\n";
	        linenum++;
	      }
	      int s_loc=0;
	      int e_loc=0;
	      int lastloc=0;
	      int times=Math.min(f_rnl.size(), b_rnl.size());
	      for(int r=0;r<times;r++)
	      {
	    	  s_loc=processed.indexOf("/*",lastloc);
	    	  e_loc=processed.indexOf("*/",s_loc+2);
	    	  recordNoteLoc srnloc=f_rnl.get(r);
	    	  recordNoteLoc ernloc=b_rnl.get(r);
	    	  String rnString="";
	    	  for(int k=0;k<ernloc.line-srnloc.line;k++)
	    	  {
	    		  rnString+="\r\n";
	    	  }
	    	  processed=processed.substring(0,s_loc)+rnString+processed.substring(e_loc+2);
	    	  lastloc=s_loc+2*(ernloc.line-srnloc.line);
	      }
	      
	      
	      reader.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    allTestString=processed;
	    //System.out.println("read times:"+readtimes);
	    MiniCCUtil.createAndWriteFile(output.replace(inFileType, ".pp2"+inFileType), processed);
	    return true;
	}
 	public boolean pp3(String output)
 	{
 		String includeString ="^(\\s)*#include(\\s)+\"([^\\\\]+|\\\\[0-7|a|b|r|t|n|v|f|?|'|\"|\\\\]|\\\\[0-7][0-7]?[0-7]?|\\\\0x[0-9a-fA-f][0-9a-fA-f]?)*[\"|>](.)*";
 		String out2path=output.replace(inFileType, ".pp2"+inFileType);
 		if (!MiniCCUtil.checkFile(out2path))
 		{
 		      return false;
 		}
 		processed = "";
 		
 		linenum=1;
 		File file = new File(out2path);
	    try
	    {
	      BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
	      String line = "";
	      
	      while ((line = reader.readLine()) != null)
	      {
	    	if(Pattern.compile(includeString).matcher(line).matches())
	    	{
	    		int start=line.indexOf("\"");
	    		int end=line.indexOf("\"", start+1);
	    		includeInf tmpIncludeInf=new includeInf(line.substring(start+1,end),0);
	    		if(includeRecorder.containsKey(tmpIncludeInf.fileNameString))
	    		{
	    			processed=processed+"\r\n";
	    		}
	    		else {
	    			includeInfArrayList.add(tmpIncludeInf);
	    			includeRecorder.put(tmpIncludeInf.fileNameString, "");
	    			String tmpsrcString=path.substring(0,path.lastIndexOf("\\"))+"\\"+tmpIncludeInf.fileNameString;
		    		preprocesser tmpPreprocesser=new preprocesser(tmpsrcString,dic,includeRecorder,".h");
		    		if(tmpPreprocesser.pp(tmpsrcString)==false)
		    			return false;
		    		tmpPreprocesser.pp2(tmpsrcString);
		    		tmpPreprocesser.pp3(tmpsrcString);
		    		processed=tmpPreprocesser.processed+processed+"\r\n";
		    		tmpIncludeInf.lineOffset=tmpPreprocesser.linenum-1;
				}
	    		linenum++;
	    	}
	    	else {
	    		processed = processed + line+"\r\n";
		        linenum++;
			}
	        
	      }
	      
	      reader.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    allTestString=processed;
	    //System.out.println("read times:"+readtimes);
	    MiniCCUtil.createAndWriteFile(output.replace(inFileType, ".pp3"+inFileType), processed);
	    return true;
 	}
}
public class cplScanner implements IMiniCCScanner{

	/**
	 * @param args
	 */
	public cplScanner() {
		// TODO 自动生成的构造函数存根
	}
	public boolean whetherLexAnalysePass()
	{
		return isLexAnalysePass;
	}
	  private boolean isLexAnalysePass;
	  public void run(String iFile, String oFile) throws IOException {
		dictionary dic=new dictionary();
		preprocesser myPreprocesser=new preprocesser(iFile,dic,".c");
		
		if(myPreprocesser.pp(iFile)&&myPreprocesser.pp2(iFile)&&myPreprocesser.pp3(iFile))//预处理成功
		{
			DFA myDFA=new DFA(myPreprocesser.allTestString,dic,myPreprocesser.includeInfArrayList,iFile);
			myDFA.run();
			isLexAnalysePass=myDFA.isLexAnalysePass();
		    
		    
		    if(isLexAnalysePass)
		    {
		    	System.out.println("2. LexAnalyse Succeed!");
		    	myDFA.outputWordList(oFile,false);
		    }
		    else {
		    	System.out.println("2. LexAnalyse failed!");
		    	myDFA.outputWordList(oFile,true);
			}
		}
		else {
			System.out.println("存在无法找到的目标文件");
		}
		//System.out.print(myPreprocesser.allTestString);
		
	 }
	
}
