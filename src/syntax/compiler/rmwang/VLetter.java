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
		if(isvn)//�Ƿ��ս��
		{
			this.id=id;
			vtType=-1;
		}
		else{//���ս��
			this.id=id+RCLG.VtOffset;//����arraylist���
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