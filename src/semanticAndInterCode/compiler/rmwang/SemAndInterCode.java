package semanticAndInterCode.compiler.rmwang;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;

import mars.venus.editors.jeditsyntax.InputHandler.insert_break;

import syntax.compiler.rmwang.*;
import bit.minisys.minicc.icgen.IMiniCCICGen;
import bit.minisys.minicc.parser.TreeNode;
class FormalPara{//形参定义
	String nameString;
	int type;//整型.etc
	public FormalPara(String name,int tp)
	{
		this.nameString=name;
		this.type=tp;
	}
	public FormalPara(FormalPara f)
	{
		this.nameString=f.nameString;
		this.type=f.type;
	}
}
class FunInformation{//函数信息表
	ArrayList<FormalPara> fPList;//形参表
	int returnType;//返回值类型
}
class Identifier{//符号
	String nameString;//符号名称
	int type;//符号类型：整型变量，函数
	//1为函数，24为整形变量，3为临时结果
	FunInformation funInf;//
	String actionScopeString;//作用域，指向函数名，一切变量都是函数中的
	public Identifier(String name,int tp,FunInformation funif,String actionscope)
	{
		this.nameString=name;
		this.type=tp;
		this.funInf=funif;
		this.actionScopeString=actionscope;
	}
	public Identifier(Identifier i)
	{
		this.nameString=i.nameString;
		this.type=i.type;
		this.funInf=i.funInf;
		this.actionScopeString=i.actionScopeString;
	}
}
class IdfTable{//符号表
	ArrayList<Identifier> it;
	public int find(String s,int type)//搜索符号，用于定位，查重等
	{
		int i;
		for(i=0;i<it.size();i++)
		{
			if(s.equals(it.get(i).nameString)&&it.get(i).type==type)
			{
				return i;
			}
		}
		return -1;
	}
	public void print()
	{
		for(int i=0;i<it.size();i++)
		{
			System.out.println(it.get(i).nameString+" "+it.get(i).type);
		}
	}
}
class Label {//标号表
	int id;//标号id
	boolean isdef;//定义否
	int addr;//地址，指向四元式标号
	String nameString;//标号名字
}
class LabelTable{//标号表
	ArrayList<Label> lbList;
}

public class SemAndInterCode implements IMiniCCICGen{
	Tree synTree;
	ArrayList<FourElement> FElist;
	RCLG myRclg;
	LabelTable lbTB;
	IdfTable idfTB;
	int FEid;
	public SemAndInterCode(Tree tr,RCLG r) {
		synTree=tr;
		FElist=new ArrayList<FourElement>();
		myRclg=r;
		lbTB=new LabelTable();
		idfTB=new IdfTable();
		lbTB.lbList=new ArrayList<Label>();
		idfTB.it=new ArrayList<Identifier>();
	}
	@Override
	public void run(String iFile, String oFile) throws IOException {
		// TODO 自动生成的方法存根
		FEid=0;
		syntax.compiler.rmwang.TreeNode tmpNode=synTree.root;
		Analyse(tmpNode);
		if(true)
		{
			System.out.println("语义分析通过，已生成四元式");
			/*for(int i=0;i<FElist.size();i++)
			{
				FourElement tFourElement=FElist.get(i);
				System.out.println(tFourElement.opId+" "+tFourElement.arg1+" "+tFourElement.arg2+" "+tFourElement.resultId+" "+tFourElement.addr);
			}*/
			outputFourElement(oFile);
		}
		else{
			System.out.println("语义分析失败");
		}
	}
	public void Analyse(syntax.compiler.rmwang.TreeNode node)//后序遍历，属性向上传
	{
		VLetter ltt=node.letter;
		//myRclg.getVLetterByWordType(ltt)
		switch (ltt.id) {
		case 0:for(int i=node.childs.size()-1;i>=0;i--)//先分析所有子树，再分析自己
		   {
			 Analyse(node.childs.get(i));
		   }break;
		case 1:for(int i=node.childs.size()-1;i>=0;i--)//先分析所有子树，再分析自己
		   {
			 Analyse(node.childs.get(i));
		   }break;
		case 2:node.childs.get(1).plistString=node.plistString;
			   node.childs.get(1).plistType=node.plistType;
			   for(int i=node.childs.size()-1;i>=0;i--)//先分析所有子树，再分析自己
			   {
				 Analyse(node.childs.get(i));
			   }
			   Identifier tmpidf=new Identifier(node.nameString,1,new FunInformation(),null);
			   tmpidf.funInf.returnType=node.valType;
			   tmpidf.funInf.fPList=new ArrayList<FormalPara>();
			   for(int i=0;i<node.plistString.size();i++)
			   {
				   tmpidf.funInf.fPList.add(new FormalPara(node.plistString.get(i),node.plistType.get(i)));
			   }
			   idfTB.it.add(tmpidf);
			   /*for(int i=0;i<node.plistString.size();i++)
			   {
				   idfTB.it.add(new Identifier(node.plistString.get(i),node.plistType.get(i),null,""));
			   }*/
			   break;
		case 3:/*if(node.plistString!=null&&node.plistType!=null)//处于plist下的type
		       {
			      node.plistType.add(new Integer(myRclg.getVLetterByWordType(node.childs.get(0).token.type)));//int type char type
		       }
			   else {//变量声明或定义，属性向上传播
				   node.parent.valType=node.childs.get(0).id;
			   }
			   //下面的节点不走*/
			   break;
		case 4:node.childs.get(1).plistString=node.plistString;//tplist
			   node.childs.get(1).plistType=node.plistType;
			   for(int i=node.childs.size()-1;i>=0;i--)
			   {
				 Analyse(node.childs.get(i));
			   }
			   
			   break;
		case 5:for(int i=node.childs.size()-1;i>=0;i--)//tp
			   {
					node.childs.get(i).plistString=node.plistString;
					node.childs.get(i).plistType=node.plistType;
			   }
				node.plistString.add(node.childs.get(1).token.value);
				node.plistType.add(new Integer(myRclg.getVLetterByWordType(node.childs.get(2).childs.get(0).token.type)));
				idfTB.it.add(new Identifier(node.plistString.get(node.plistString.size()-1),node.plistType.get(node.plistString.size()-1),null,""));
				for(int i=node.childs.size()-1;i>=0;i--)
				{
					Analyse(node.childs.get(i));
				}
			   
			   
			   break;
		case 6://fbody
			   //idfTB.print();
			   for(int i=node.childs.size()-1;i>=0;i--)
			   {
			   		Analyse(node.childs.get(i));
			   }
			   node.codeElements=node.childs.get(1).codeElements;
			   break;
			   
		case 7://senlist
			   for(int i=node.childs.size()-1;i>=0;i--)
		       {
				   Analyse(node.childs.get(i));
		       }
			   for(int i=node.childs.size()-1;i>=0;i--)
		       {
				  syntax.compiler.rmwang.TreeNode tmpNode=node.childs.get(i);
			      for(int j=0;j<tmpNode.codeElements.size();j++)
			      {
			    	  node.codeElements.add(tmpNode.codeElements.get(i));
			      }
		       }
			break;
		case 8://sen
			  for(int i=node.childs.size()-1;i>=0;i--)
	          {
		    	  node.childs.get(i).codeElements=node.codeElements;
	          }
			  for(int i=node.childs.size()-1;i>=0;i--)
	          {
			      Analyse(node.childs.get(i));
	          }
			  if(node.childs.get(node.childs.size()-1).letter.id==22+RCLG.VtOffset)
			  {
				  node.opType=22+RCLG.VtOffset;
			  }
			  if(node.childs.get(node.childs.size()-2).letter.id==11)
			  {
				  node.arg1=node.childs.get(node.childs.size()-2).rlt;
				  node.arg2="null";
				  node.rlt="null";
			  }
			  idfTB.it.add(new Identifier("T"+FEid,3,null,""));
			  FourElement fe=new FourElement(FEid, node.opType, idfTB.find(node.arg1,3), idfTB.find(node.arg2,24), -1);//return
		      node.codeElements.add(fe);
		      FElist.add(fe);
		      FEid++;
	          break;
		case 9:break;
		case 10:break;
		case 11:for(int i=node.childs.size()-1;i>=0;i--)//mov
			    {
		      		Analyse(node.childs.get(i));
			    }
				node.arg1=node.childs.get(2).token.value;
				node.arg2=node.childs.get(1).arg2;
				//System.out.println("arg1:"+node.arg1+"arg2:"+node.arg2);
				node.opType=node.childs.get(1).opType;
				idfTB.it.add(new Identifier("T"+FEid,3,null,""));
				FourElement fet=new FourElement(FEid,node.opType,idfTB.find(node.arg1, 24),idfTB.find(node.arg2, 24),idfTB.find("T"+FEid, 3));
				node.codeElements.add(fet);
				node.rlt="T"+FEid;
				FElist.add(fet);
				FEid++;
				break;
		case 12://op
			for(int i=node.childs.size()-1;i>=0;i--)
		    {
	      		Analyse(node.childs.get(i));
		    }
			node.opType=myRclg.getVLetterByWordType(node.childs.get(1).token.type);//+
			node.arg2=node.childs.get(0).arg2;
			break;
		case 13://val
			for(int i=node.childs.size()-1;i>=0;i--)
		    {
	      		Analyse(node.childs.get(i));
		    }
			node.arg2=node.childs.get(0).token.value;
			break;
		case 14://opcall
			for(int i=node.childs.size()-1;i>=0;i--)
		    {
	      		Analyse(node.childs.get(i));
		    }
			node.arg2=node.childs.get(0).arg2;
			node.opType=node.childs.get(0).opType;
			break;
		case 15:break;
		case 16:break;
		case 17:break;
		case 18:break;
		case 19:break;
		case 20:break;
		case 21:break;
		case 22:if (node.childs.size()==0) break;
			node.childs.get(0).plistString=node.plistString;
			node.childs.get(0).plistType=node.plistType;
			for(int i=node.childs.size()-1;i>=0;i--)
		    {
	      		Analyse(node.childs.get(i));
		    }
			break;
		case 23:break;
		case 24:break;
		case 25:break;
		case 26:break;
		case 27:break;
		case 28:break;
		case 29:break;
		case 30:break;
		case 31:break;
		case 32:break;
		case 33:break;
		case 34:break;
		case 35:break;
		case 36:break;
		case 37:break;
		case 38:break;
		case 39:break;
		case 40:break;
		case 41:break;
		case 42:break;
		case 43:break;
		case 44:break;
		case 45:break;
		case 46:break;
		case 47:break;
		case 48:break;
		case 49:break;
		default:
			break;
		}
	}
	public String translateOPid(int id)
	{
		switch (id) {
		case 34:return "+";
		case 35:return "";
		case 36:return "";
		case 37:return "";
		case 38:return "";
		case 39:return "";
		case 40:return "";
		case 41:return "";
		case 42:return "";
		case 43:return "";
		case 44:return "";
		case 45:return "";
		case 46:return "return";
		default:return "";
		
		}
	}
	public String outputFourElement(String output) throws IOException 
	{
		String path = output;
		  Element root = new Element("IC").setAttribute("name", ".ic.xml");
		  org.jdom.Document Doc = new org.jdom.Document(root);
		  Element icElement;
		  //root.addContent(start);
		  for(int i=0;i<FElist.size();i++)
		  {
			  FourElement tfe=FElist.get(i);
			  icElement=new Element("quaternion");
			  if(tfe.resultId!=-1)
			  {
				  icElement.setAttribute("result",idfTB.it.get(tfe.resultId).nameString);
			  }
			  else {
				  icElement.setAttribute("result","");
			  }
			  if(tfe.arg2!=-1)
			  {
				  icElement.setAttribute("arg2",idfTB.it.get(tfe.arg2).nameString);
			  }
			  else {
				  icElement.setAttribute("arg2","");
			  }
			  if(tfe.arg1!=-1)
			  {
				  icElement.setAttribute("arg1",idfTB.it.get(tfe.arg1).nameString);
			  }
			  else {
				  icElement.setAttribute("arg1","");
			  }
			 
			  icElement.setAttribute("op",translateOPid(tfe.opId));
			  icElement.setAttribute("addr",""+tfe.addr);
			  root.addContent(icElement);
		  }
		  Format format = Format.getPrettyFormat();
		  XMLOutputter XMLOut = new XMLOutputter(format);
		  XMLOut.output(Doc, new FileOutputStream(path));
		  return path;
	}
}
