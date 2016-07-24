package semanticAndInterCode.compiler.rmwang;

public class FourElement{//四元式定义
	//String opString;
	int opId;//10+24 +,22+24 return
	int arg1;
	int arg2;
	int resultId;
	int addr;
	public FourElement(int addr,int op,int arg1,int arg2,int rlt)
	{
		this.addr=addr;
		this.opId=op;
		this.arg1=arg1;
		this.arg2=arg2;
		this.resultId=rlt;
	}
	public FourElement(FourElement f)
	{
		this.addr=f.addr;
		this.opId=f.opId;
		this.arg1=f.arg1;
		this.arg2=f.arg2;
		this.resultId=f.resultId;
	}
}
