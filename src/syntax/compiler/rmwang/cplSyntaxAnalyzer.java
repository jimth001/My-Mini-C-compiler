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
		// TODO �Զ����ɵķ������
		
		if(SyntaxAnaylse())//�﷨����ͨ��
		{
			System.out.println("�﷨����ͨ��");
			//myDicOfRCLG.PrintG();
			outputTree(oFile);
		}
		else{
			System.out.println("�﷨����ʧ��");
		}
	}
	public boolean SyntaxAnaylse(){
		tokenList.add(new Token(-1,"#",100,-1,false,""));
		int inputLen=tokenList.size();
		int i=0;//�����ַ���ָ��
		int j=0;//����ջջ��ָ��
		int tmpi;//ջ�����ű��
		VLetter tmpltt;//ջ������
		int productionId;//����ʽid
		production p;//����ʽ
		stack.add(new Integer(19+RCLG.VtOffset));
		stack.add(new Integer(0));//�ķ���ʼ������ջ
		j++;
		int recordListIdForTree=0;
		TreeNode tmpTreeNode;
		parseTree.root=new TreeNode(null, new ArrayList<TreeNode>(), myDicOfRCLG.vList.get(0),0,null);
		tmpTreeNode=parseTree.root;
		while(i<inputLen||j>=0)//j>=0��ζ��stack�ﻹ��#��i<inputlen��ζ���������л�����ĩβ��#δƥ�䣬���ߵȼ�
		{
			
			tmpi=stack.get(j).intValue();//ջ�����ű��
			tmpltt=myDicOfRCLG.vList.get(tmpi);//ջ������
			if(tmpltt.isVn)//�Ƿ��ս��
			{
				Token token=tokenList.get(i);
				int VLetterId=myDicOfRCLG.getVLetterByWordType(token.type);//�ַ����е�ǰɨ���ַ���id
				if(VLetterId==-1)
				{
					System.out.println("������δ֪���͵������ַ���,�����ַ���"+token.value+",������:"+token.line);
					System.out.println("�ļ�Դ��"+token.srcString);
					stack.clear();
					return false;
				}
				productionId=myDicOfRCLG.table[tmpi][VLetterId-RCLG.VtOffset];
				if(productionId==-1)
				{
					//System.out.println(tmpi+" "+VLetterId+" "+token.type+" "+token.value);
					searchTree(parseTree.root, 0);
					System.out.println("LL(1)����ʧ�ܣ�û�п��õĲ���ʽ�������ַ���"+token.value+",������:"+token.line);
					System.out.println("�ļ�Դ��"+token.srcString);
					stack.clear();
					return false;
				}
				p=myDicOfRCLG.pList.get(productionId);
				stack.remove(j);//���滻�����Ƴ�
				j--;
				for(int x=p.len-1;x>=0;x--)//����ʽ�Ҳ���ջ
				{
					int ti=p.pright[x];
					stack.add(new Integer(ti));
					j++;
					tmpTreeNode.childs.add(new TreeNode(tmpTreeNode,new ArrayList<TreeNode>(),myDicOfRCLG.vList.get(ti),p.len-1-x,null));
				}
				recordListIdForTree=tmpTreeNode.childs.size()-1;//�¼�����ӽڵ������±�
				if(recordListIdForTree>=0)//�±���ڵ���0֤���Ƴ��Ĳ��ǿմ�������������
				{
					tmpTreeNode=tmpTreeNode.childs.get(recordListIdForTree);
				}
				else{//�Ƴ��˿մ�
					if(tmpTreeNode.id>0)//�ұ߻����ֵ�
					{
						tmpTreeNode=tmpTreeNode.parent.childs.get(tmpTreeNode.id-1);
					}
					else {//�ұ�û���ֵܣ����ݵ����׵��ֵ�
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
			else{//���ս��
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
				else {//����ƥ��
					//System.out.println(tmpi+" "+VLetterId);
					searchTree(parseTree.root, 0);
					System.out.println("LL(1)����ʧ��,�����ս����ƥ��,�����ַ���"+token.value+" ,������:"+token.line);
					System.out.println("�ļ�Դ��"+token.srcString);
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
		  Element Gsymbol=new Element("�ķ�����"+node.letter.id);//�ķ�����
		  Element isVN=new Element("isVN");//�Ƿ��Ƿ��ս��
		  Element value=new Element("value");//��Ӧ�ĵ���ֵ
		  Element line=new Element("line");//������
		  Element src=new Element("src");//�����ļ�
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
