package syntax.compiler.rmwang;

public class VLetter{
	public boolean isVn;
	public String nameString;
	public int id;//
	public int vtType;
	public VLetter(boolean isvn,String name,int id,int tp)
	{
		this.isVn=isvn;
		this.nameString=name;
		if(isvn)//是非终结符
		{
			this.id=id;
			vtType=-1;
		}
		else{//是终结符
			this.id=id+RCLG.VtOffset;//适配arraylist编号
			vtType=tp;
		}
	}
	public VLetter(VLetter v)
	{
		this.isVn=v.isVn;
		this.nameString=v.nameString;
		this.id=v.id;
	}
}