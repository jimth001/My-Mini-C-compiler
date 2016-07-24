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
	//语义属性：
	public int val;//符号的值
	public int type;//符号的属性，对应文法符号id
	public int valType;//符号值的类型，整型等
	public ArrayList<Integer> plistType;//参数类型list
	public ArrayList<String> plistString;//参数名list
	public ArrayList<semanticAndInterCode.compiler.rmwang.FourElement> codeElements;//四元式list
	public String nameString;//传递上来的标识符名字，用作函数
	public int opType;//操作属性，0return，1+
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
