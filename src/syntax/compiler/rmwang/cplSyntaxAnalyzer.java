package syntax.compiler.rmwang;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.xml.sax.SAXException;

import com.sun.org.apache.bcel.internal.classfile.Attribute;

import scanner.complier.rmwang.*;
import bit.minisys.minicc.parser.IMiniCCParser;





public class cplSyntaxAnalyzer implements IMiniCCParser{

	/**
	 * @param args
	 */
	ArrayList<Integer> stack;
	ArrayList<Token> tokenList;
	public Tree parseTree;
	public RCLG myDicOfRCLG;
	public cplSyntaxAnalyzer(ArrayList<Token> t){
		stack=new ArrayList<Integer>();
		tokenList=t;
		myDicOfRCLG=new RCLG();
		parseTree=new Tree();
	}
	@Override
	public void run(String iFile, String oFile)
			throws ParserConfigurationException, SAXException, IOException {
		// TODO 自动生成的方法存根
		
		if(SyntaxAnaylse())//语法分析通过
		{
			System.out.println("语法分析通过");
			//myDicOfRCLG.PrintG();
			outputTree(oFile);
		}
		else{
			System.out.println("语法分析失败");
		}
	}
	public boolean SyntaxAnaylse(){
		tokenList.add(new Token(-1,"#",100,-1,false,""));
		int inputLen=tokenList.size();
		int i=0;//属性字符流指针
		int j=0;//分析栈栈顶指针
		int tmpi;//栈顶符号编号
		VLetter tmpltt;//栈顶符号
		int productionId;//产生式id
		production p;//产生式
		stack.add(new Integer(19+RCLG.VtOffset));
		stack.add(new Integer(0));//文法开始符号入栈
		j++;
		int recordListIdForTree=0;
		TreeNode tmpTreeNode;
		parseTree.root=new TreeNode(null, new ArrayList<TreeNode>(), myDicOfRCLG.vList.get(0),0,null);
		tmpTreeNode=parseTree.root;
		while(i<inputLen||j>=0)//j>=0意味着stack里还有#，i<inputlen意味着输入流中还包含末尾的#未匹配，二者等价
		{
			
			tmpi=stack.get(j).intValue();//栈顶符号编号
			tmpltt=myDicOfRCLG.vList.get(tmpi);//栈顶符号
			if(tmpltt.isVn)//是非终结符
			{
				Token token=tokenList.get(i);
				int VLetterId=myDicOfRCLG.getVLetterByWordType(token.type);//字符流中当前扫描字符的id
				if(VLetterId==-1)
				{
					System.out.println("遇到了未知类型的属性字符流,出错字符："+token.value+",所在行:"+token.line);
					System.out.println("文件源："+token.srcString);
					stack.clear();
					return false;
				}
				productionId=myDicOfRCLG.table[tmpi][VLetterId-RCLG.VtOffset];
				if(productionId==-1)
				{
					//System.out.println(tmpi+" "+VLetterId+" "+token.type+" "+token.value);
					searchTree(parseTree.root, 0);
					System.out.println("LL(1)分析失败，没有可用的产生式，出错字符："+token.value+",所在行:"+token.line);
					System.out.println("文件源："+token.srcString);
					stack.clear();
					return false;
				}
				p=myDicOfRCLG.pList.get(productionId);
				stack.remove(j);//被替换符号移除
				j--;
				for(int x=p.len-1;x>=0;x--)//产生式右部入栈
				{
					int ti=p.pright[x];
					stack.add(new Integer(ti));
					j++;
					tmpTreeNode.childs.add(new TreeNode(tmpTreeNode,new ArrayList<TreeNode>(),myDicOfRCLG.vList.get(ti),p.len-1-x,null));
				}
				recordListIdForTree=tmpTreeNode.childs.size()-1;//新加入的子节点最左下标
				if(recordListIdForTree>=0)//下标大于等于0证明推出的不是空串，生成了子树
				{
					tmpTreeNode=tmpTreeNode.childs.get(recordListIdForTree);
				}
				else{//推出了空串
					if(tmpTreeNode.id>0)//右边还有兄弟
					{
						tmpTreeNode=tmpTreeNode.parent.childs.get(tmpTreeNode.id-1);
					}
					else {//右边没有兄弟，回溯到父亲的兄弟
						while(tmpTreeNode.parent!=null)
						{
							tmpTreeNode=tmpTreeNode.parent;
							if(tmpTreeNode.id>0)
							{
								tmpTreeNode=tmpTreeNode.parent.childs.get(tmpTreeNode.id-1);
								break;
							}
						}
					}
				}
				
			}
			else{//是终结符
				Token token=tokenList.get(i);
				int VLetterId=myDicOfRCLG.getVLetterByWordType(token.type);
				if(tmpi==VLetterId)
				{
					stack.remove(j);
					j--;
					i++;
					tmpTreeNode.token=token;
					if(tmpTreeNode.id>0)
					{
						tmpTreeNode=tmpTreeNode.parent.childs.get(tmpTreeNode.id-1);
					}
					else {
						while(tmpTreeNode.parent!=null)
						{
							tmpTreeNode=tmpTreeNode.parent;
							if(tmpTreeNode.id>0)
							{
								tmpTreeNode=tmpTreeNode.parent.childs.get(tmpTreeNode.id-1);
								break;
							}
						}
					}
				}
				else {//不能匹配
					//System.out.println(tmpi+" "+VLetterId);
					searchTree(parseTree.root, 0);
					System.out.println("LL(1)分析失败,两个终结符不匹配,出错字符："+token.value+" ,所在行:"+token.line);
					System.out.println("文件源："+token.srcString);
					stack.clear();
					return false;
				}
			}
			/*searchTree(parseTree.root, 0);
			System.out.println(tmpTreeNode.letter.nameString);
			System.out.println("11111111111111111111111111111");*/
		}
		//searchTree(parseTree.root, 0);
		stack.clear();
		return true;
	}
	
	  public void searchTree(TreeNode node,int i)
	  {
		  int k=0;
		  for(k=0;k<i;k++)
		  {
			  System.out.print("  ");
		  }   
		  System.out.println(node.letter.nameString);
		  /*for(k=node.childs.size()-1;k>=0;k--)
		  {
			  System.out.print(node.childs.get(k).letter.nameString+" ");
		  }
		  System.out.println();*/
		  for(k=node.childs.size()-1;k>=0;k--)
		  {
			  searchTree(node.childs.get(k), i+1);
		  }
	  }
	  public String outputTree(String output) throws IOException {
		  String path = output;
		  Element root = new Element("ParserTree").setAttribute("name", ".tree.xml");
		  org.jdom.Document Doc = new org.jdom.Document(root);
		  Element start = new Element("Pg");
		  root.addContent(start);
		  buildXMLTree(start,parseTree.root);
		  Format format = Format.getPrettyFormat();
		  XMLOutputter XMLOut = new XMLOutputter(format);
		  XMLOut.output(Doc, new FileOutputStream(path));
		  return path;
	 }
	  public void buildXMLTree(Element start,TreeNode node) {
		  Element Gsymbol=new Element("文法符号"+node.letter.id);//文法符号
		  Element isVN=new Element("isVN");//是否是非终结符
		  Element value=new Element("value");//对应的单词值
		  Element line=new Element("line");//所在行
		  Element src=new Element("src");//所属文件
		  if(node.letter.isVn)
		  {
			  Gsymbol.setAttribute("name", node.letter.nameString);
			  Gsymbol.setAttribute("isVn", "true");
			  
			  start.addContent(Gsymbol);
			  start=Gsymbol;
			  int len=node.childs.size();
			  if(len==0) {
				  Gsymbol.setAttribute("noChildVn", "true");
			  }
			  else {
				  Gsymbol.setAttribute("noChildVn", "false");
			  }
			  for(int i=len-1;i>=0;i--)
			  {
				  buildXMLTree(start, node.childs.get(i));
			  }
		  }
		  else{
			  Gsymbol.setAttribute("name", node.letter.nameString);
			  Gsymbol.setAttribute("isVn", "false");
			  value.setText(node.token.value);
			  src.setText(node.token.srcString);
			  //System.out.println("src:"+node.token.srcString);
			  line.setText(new Integer(node.token.line).toString());
			  start.addContent(Gsymbol);
			  Gsymbol.addContent(value);
			  Gsymbol.addContent(line);
			  Gsymbol.addContent(src);
		  }
		  
	  }
}
