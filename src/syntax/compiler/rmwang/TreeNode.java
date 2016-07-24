package syntax.compiler.rmwang;

import java.util.ArrayList;

import org.jdom.Element;

import bit.minisys.minicc.icgen.FourElement;

import scanner.complier.rmwang.Token;

public class TreeNode{
	public TreeNode parent;
	public ArrayList<TreeNode> childs;
	public VLetter letter;
	public int id;
	public Token token;
	//�������ԣ�
	public int val;//���ŵ�ֵ
	public int type;//���ŵ����ԣ���Ӧ�ķ�����id
	public int valType;//����ֵ�����ͣ����͵�
	public ArrayList<Integer> plistType;//��������list
	public ArrayList<String> plistString;//������list
	public ArrayList<semanticAndInterCode.compiler.rmwang.FourElement> codeElements;//��Ԫʽlist
	public String nameString;//���������ı�ʶ�����֣���������
	public int opType;//�������ԣ�0return��1+
	public String arg1;
	public String arg2;
	public String rlt;
	public TreeNode(TreeNode pa,ArrayList<TreeNode> ch,VLetter v,int id,Token t)
	{
		this.parent=pa;
		this.childs=ch;
		this.letter=v;
		this.id=id;
		this.token=t;
		val=-1;
		type=-1;
		valType=-1;
		plistType=new ArrayList<Integer>();
		plistString=new ArrayList<String>();
		nameString="";
		codeElements=new ArrayList<semanticAndInterCode.compiler.rmwang.FourElement>();
		opType=-1;
		arg1="";
		arg2="";
		rlt="";
	}
	public TreeNode(TreeNode n)
	{
		this.parent=n.parent;
		this.childs=n.childs;
		this.letter=n.letter;
		this.id=n.id;
		this.token=n.token;
		this.val=n.val;
		this.valType=n.valType;
		this.type=n.type;
		this.plistType=n.plistType;
		this.plistString=n.plistString;
		this.codeElements=n.codeElements;
		this.nameString=n.nameString;
		this.rlt=n.rlt;
	}
}
