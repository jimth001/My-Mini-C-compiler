package scanner.complier.rmwang;

public class Token{
	public static int nilType=-1;
	public int id;
	public String value;
	public int type;
	public int line;
	public boolean valid;
	String typedes;
	String wrongInf;
	public String srcString;
 	public Token(int id,String value,int type,int line,boolean valid,String src)
	{
		this.id=id;
		this.value=value;
		this.type=type;
		this.line=line;
		this.valid=valid;
		this.typedes="";
		this.wrongInf="нч";
		this.srcString=src;
	}
	public Token()
	{
		id=0;
		value="";
		type=nilType;
		line=-1;
		valid=true;
		srcString="";
	}
	public Token(Token t)
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

