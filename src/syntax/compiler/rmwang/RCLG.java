package syntax.compiler.rmwang;

import java.util.ArrayList;
class production{//产生式
	VLetter pleft;
	int len;
	int id;
	int pright[];
	public production(VLetter v,int length,int id,int []pr) {
		// TODO 自动生成的构造函数存根
		this.pleft=v;
		this.len=length;
		this.id=id;
		this.pright=pr;
	}
	public production(production p)
	{
		this.pleft=p.pleft;
		this.len=p.len;
		this.id=p.id;
		this.pright=p.pright;
	}
}
public class RCLG{
	ArrayList<VLetter> vList;
	ArrayList<production> pList;
	int table[][];
	public static final int VtOffset=24;
	public static final int VtNum=23;
 	public int getVLetterByWordType(int wordType)
	{
		int i;
		for(i=0;i<vList.size();i++)
		{
			if(!vList.get(i).isVn&&vList.get(i).vtType==wordType)
			{
				return i;
			}
		}
		return -1;
	}
	public RCLG()
	{
		vList=new ArrayList<VLetter>();
		pList=new ArrayList<production>();
		table=new int[VtOffset][VtNum];
		for(int i=0;i<VtOffset;i++)
		{
			for(int j=0;j<VtNum;j++)
			{
				table[i][j]=-1;
			}
		}
		vList.add(new VLetter(true,"Pg",0,0));
		vList.add(new VLetter(true,"T1",1,0));
		vList.add(new VLetter(true,"fun",2,0));
		vList.add(new VLetter(true,"type",3,0));
		vList.add(new VLetter(true,"tplist",4,0));
		vList.add(new VLetter(true,"tp",5,0));
		vList.add(new VLetter(true,"fbody",6,0));
		vList.add(new VLetter(true,"senlist",7,0));
		vList.add(new VLetter(true,"sen",8,0));
		vList.add(new VLetter(true,"moveorop",9,0));
		vList.add(new VLetter(true,"T2",10,0));
		vList.add(new VLetter(true,"mov",11,0));
		vList.add(new VLetter(true,"op",12,0));
		vList.add(new VLetter(true,"val",13,0));
		vList.add(new VLetter(true,"opcall",14,0));
		vList.add(new VLetter(true,"plist",15,0));
		vList.add(new VLetter(true,"p",16,0));
		vList.add(new VLetter(true,"declordef",17,0));
		vList.add(new VLetter(true,"T3",18,0));
		vList.add(new VLetter(true,"if-else",19,0));
		vList.add(new VLetter(true,"while-sen",20,0));
		vList.add(new VLetter(true,"call",21,0));
		vList.add(new VLetter(true,"ltp",22,0));
		vList.add(new VLetter(true,"lp",23,0));
		vList.add(new VLetter(false,"int",0,17));
		vList.add(new VLetter(false,"char",1,4));
		vList.add(new VLetter(false,"(",2,63));
		vList.add(new VLetter(false,")",3,64));
		vList.add(new VLetter(false,",",4,62));
		vList.add(new VLetter(false,"{",5,76));
		vList.add(new VLetter(false,"}",6,77));
		vList.add(new VLetter(false,"if",7,16));
		vList.add(new VLetter(false,"else",8,10));
		vList.add(new VLetter(false,";",9,74));
		vList.add(new VLetter(false,"+",10,33));
		vList.add(new VLetter(false,"-",11,34));
		vList.add(new VLetter(false,"*",12,35));
		vList.add(new VLetter(false,"/",13,36));
		vList.add(new VLetter(false,"<",14,52));
		vList.add(new VLetter(false,">",15,53));
		vList.add(new VLetter(false,"==",16,61));
		vList.add(new VLetter(false,"=",17,75));
		vList.add(new VLetter(false,"while",18,32));
		vList.add(new VLetter(false,"#",19,100));
		vList.add(new VLetter(false,"idf",20,10004));
		vList.add(new VLetter(false,"const",21,10000));
		vList.add(new VLetter(false,"return",22,20));
		pList.add(new production(vList.get(0),2,0,new int[]{2,1}));//vletter,len,id,
		pList.add(new production(vList.get(1),1,1,new int[]{0}));
		pList.add(new production(vList.get(1),0,2,new int[]{-1}));//空串
		pList.add(new production(vList.get(3),1,3,new int[]{VtOffset+0}));
		pList.add(new production(vList.get(3),1,4,new int[]{VtOffset+1}));
		pList.add(new production(vList.get(2),4,5,new int[]{3,VtOffset+20,4,6}));
		pList.add(new production(vList.get(4),3,6,new int[]{VtOffset+2,5,VtOffset+3}));
		pList.add(new production(vList.get(5),3,7,new int[]{3,VtOffset+20,22}));
		pList.add(new production(vList.get(5),0,8,new int[]{-1}));
		pList.add(new production(vList.get(6),3,9,new int[]{VtOffset+5,7,VtOffset+6}));
		pList.add(new production(vList.get(7),2,10,new int[]{8,7}));
		pList.add(new production(vList.get(7),0,11,new int[]{-1}));
		pList.add(new production(vList.get(8),1,12,new int[]{19}));
		pList.add(new production(vList.get(8),1,13,new int[]{20}));
		pList.add(new production(vList.get(8),1,14,new int[]{9}));
		pList.add(new production(vList.get(8),1,15,new int[]{17}));
		pList.add(new production(vList.get(8),3,16,new int[]{VtOffset+5,7,VtOffset+6}));
		pList.add(new production(vList.get(8),2,17,new int[]{VtOffset+22,11}));
		pList.add(new production(vList.get(8),1,18,new int[]{VtOffset+9}));
		pList.add(new production(vList.get(9),2,19,new int[]{VtOffset+20,10}));
		pList.add(new production(vList.get(10),2,20,new int[]{VtOffset+17,11}));
		pList.add(new production(vList.get(10),2,21,new int[]{12,VtOffset+9}));
		pList.add(new production(vList.get(10),2,22,new int[]{21,VtOffset+9}));
		pList.add(new production(vList.get(11),3,23,new int[]{VtOffset+20,14,VtOffset+9}));
		pList.add(new production(vList.get(11),3,24,new int[]{VtOffset+21,12,VtOffset+9}));
		pList.add(new production(vList.get(14),1,25,new int[]{12}));
		pList.add(new production(vList.get(14),1,26,new int[]{21}));
		pList.add(new production(vList.get(21),1,27,new int[]{15}));
		pList.add(new production(vList.get(12),2,28,new int[]{VtOffset+10,13}));
		pList.add(new production(vList.get(12),2,29,new int[]{VtOffset+12,13}));
		pList.add(new production(vList.get(12),2,30,new int[]{VtOffset+11,13}));
		pList.add(new production(vList.get(12),2,31,new int[]{VtOffset+13,13}));
		pList.add(new production(vList.get(12),2,32,new int[]{VtOffset+14,13}));
		pList.add(new production(vList.get(12),2,33,new int[]{VtOffset+15,13}));
		pList.add(new production(vList.get(12),2,34,new int[]{VtOffset+16,13}));
		pList.add(new production(vList.get(12),0,35,new int[]{-1}));
		pList.add(new production(vList.get(13),1,36,new int[]{VtOffset+20}));
		pList.add(new production(vList.get(13),1,37,new int[]{VtOffset+21}));
		pList.add(new production(vList.get(15),3,38,new int[]{VtOffset+2,16,VtOffset+3}));
		pList.add(new production(vList.get(16),2,39,new int[]{13,23}));
		pList.add(new production(vList.get(16),0,40,new int[]{-1}));
		pList.add(new production(vList.get(17),3,41,new int[]{3,VtOffset+20,18}));
		pList.add(new production(vList.get(18),1,42,new int[]{VtOffset+9}));
		pList.add(new production(vList.get(18),2,43,new int[]{VtOffset+17,11}));
		pList.add(new production(vList.get(19),8,44,new int[]{VtOffset+7,VtOffset+2,13,12,VtOffset+3,8,VtOffset+8,8}));
		pList.add(new production(vList.get(20),6,45,new int[]{VtOffset+18,VtOffset+2,13,12,VtOffset+3,8}));//46
		pList.add(new production(vList.get(22),2,46,new int[]{VtOffset+4,5}));
		pList.add(new production(vList.get(22),0,47,new int[]{-1}));
		pList.add(new production(vList.get(23),2,48,new int[]{VtOffset+4,16}));
		pList.add(new production(vList.get(23),0,49,new int[]{-1}));
		table[0][0]=0;table[0][1]=0;
		table[1][0]=1;table[1][1]=1;table[1][19]=2;
		table[2][0]=5;table[2][1]=5;
		table[3][0]=3;table[3][1]=4;
		table[4][2]=6;
		table[5][0]=7;table[5][1]=7;table[5][3]=8;
		table[6][5]=9;
		table[7][0]=10;table[7][1]=10;table[7][5]=10;table[7][6]=11;table[7][7]=10;table[7][9]=10;table[7][18]=10;table[7][20]=10;table[7][22]=10;
		table[8][0]=15;table[8][1]=15;table[8][5]=16;table[8][7]=12;table[8][9]=18;table[8][18]=13;table[8][20]=14;table[8][22]=17;
		table[9][20]=19;
		table[10][2]=22;table[10][10]=21;table[10][11]=21;table[10][12]=21;table[10][13]=21;table[10][14]=21;table[10][15]=21;table[10][16]=21;table[10][17]=20;
		table[11][20]=23;table[11][21]=24;
		table[12][9]=35;table[12][10]=28;table[12][11]=30;table[12][12]=29;table[12][13]=31;table[12][14]=32;table[12][15]=33;table[12][16]=34;
		table[13][20]=36;table[13][21]=37;
		table[14][2]=26;table[14][9]=25;table[14][10]=25;table[14][11]=25;table[14][12]=25;table[14][13]=25;table[14][14]=25;table[14][15]=25;table[14][16]=25;
		table[15][2]=38;
		table[16][3]=40;table[16][20]=39;table[16][21]=39;
		table[17][0]=41;table[17][1]=41;
		table[18][9]=42;table[18][17]=43;
		table[19][7]=44;
		table[20][18]=45;
		table[21][2]=27;
		table[22][4]=46;table[22][3]=47;
		table[23][4]=48;table[23][3]=49;
	}
	void PrintG(){//输出文法
		//输出字符集：
		VLetter ltt;
		System.out.println("非终结符集：");
		int i;
		for(i=0;i<vList.size();i++)
		{
			ltt=vList.get(i);
			if(ltt.isVn)
			{
				System.out.print(ltt.nameString+" ");
			}
			else{
				System.out.println();
				break;
			}
		}
		System.out.println("终结符集：");
		while(i<vList.size())
		{
			ltt=vList.get(i);
			System.out.print(ltt.nameString+" ");
			i++;
		}
		System.out.println();
		//输出产生式规则：
		System.out.println("产生式规则：");
		for(i=0;i<pList.size();i++)
		{
			production p=pList.get(i);
			System.out.print(p.pleft.nameString+"->");
			int j;
			VLetter tmpVLetter;
			for(j=0;j<p.len;j++)
			{
				tmpVLetter=vList.get(p.pright[j]);
				System.out.print(tmpVLetter.nameString+" ");
			}
			if(j==0){//长度为零，推出空串
				System.out.println("e");
			}
			else{
				System.out.println();
			}
		}
		//输出LL1分析表：
		System.out.println("LL(1)分析表：");
		for(int x=0;x<VtOffset;x++)
		{
			for(int y=0;y<VtNum;y++)
			{
				System.out.printf("%4s",table[x][y]+"  ");
			}
			System.out.println("");
		}
	}
}
