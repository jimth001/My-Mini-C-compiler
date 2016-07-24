#语法分析，语义分析和中间代码生成部分说明
##author: Yunli Wang
##语法分析实验报告
###1.	语法分析器特性:
###1.1	实现的文法如下：
开始符号：Pg
非终结符集：24个
Pg T1 fun type tplist tp fbody senlist sen moveorop T2 mov op val opcall plist p declordef T3 if-else while-sen call ltp lp 
终结符集：23个
int char ( ) , { } if else ; + - * / < > == = while # idf const return 
产生式规则：(e表示空串)
```
Pg->fun T1 
T1->Pg 
T1->e
type->int 
type->char 
fun->type idf tplist fbody 
tplist->( tp ) 
tp->type idf ltp 
tp->e
fbody->{ senlist } 
senlist->sen senlist 
senlist->e
sen->if-else 
sen->while-sen 
sen->moveorop 
sen->declordef 
sen->{ senlist } 
sen->return mov 
sen->; 
moveorop->idf T2 
T2->= mov 
T2->op ; 
T2->call ; 
mov->idf opcall ; 
mov->const op ; 
opcall->op 
opcall->call 
call->plist 
op->+ val 
op->* val 
op->- val 
op->/ val 
op->< val 
op->> val 
op->== val 
op->e
val->idf 
val->const 
plist->( p ) 
p->val lp 
p->e
declordef->type idf T3 
T3->; 
T3->= mov 
if-else->if ( val op ) sen else sen 
while-sen->while ( val op ) sen 
ltp->, tp 
ltp->e
lp->, p 
lp->e
```
###1.2	实现的c语言基本特性及一些文法自身特性
支持+,-,*,/,>,<,== 的二元运算构成的简单表达式
支持赋值运算	
支持int,char数据类型
支持函数调用
支持return 返回表达式/常量/函数调用返回值
支持if-else分支语句
支持while循环语句
支持递归调用

不支持全局声明,无全局变量
不支持函数声明,函数调用无需声明,只要定义过即可直接调用
参数列表允许,)或)结尾(编写时建议不要用”,)”结尾)
###1.3	出错处理
支持的错误类型:没有可用的产生式规则/未知的属性字符流/两终结符不匹配
支持定义错误行数，单词值，文件源
###1.4	xml输出说明
例：<文法符号0 name="Pg" isVn="true" noChildVn="false">
文法符号0 指该符号在文法符号集中标号为0
name是文法符号的名字
isVn为true表示为非终结符，false为终结符
noChildVn为true表示非终结符下没有孩子节点，即该非终结符推出了空串（对于终结符没有这一属性）
<文法符号44 name="idf" isVn="false">
<value>main</value>
<line>2</line>
<src>F:\compilertest\test.c</src>
</文法符号44>
对于终结符，下面有value,line,src三项，分别表示该终结符对应的单词值，所在行，对应文件的位置
###2.	语法分析关键技术
###2.1	RCLG(精简c语言文法)的设计
使用认真设计和注重语言精简实用的技术，根据支持的语言特性设计文法，消除左递归，提取左公因子，化简文法
一个文法符号定义如下：
```java
public class VLetter{
	boolean isVn;
	String nameString;
	int id;// 
	int vtType;
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
```
一个产生式规则定义如下：
```java
class production{//产生式
	VLetter pleft;//左部
	int len;
	int id;
	int pright[];//右部，存储的是对应arraylist vlist中的id
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
```
文法符号存储在Arraylist vlist中,非终结符在前，终结符在后，故终结符在list中的id和在table中的id之间有基于偏移量(offset)的映射关系
产生式规则存储在Arraylist plist中
###2.2	LL(1)语法分析算法
根据设计的文法求相关first,follow集合,构建LL(1)分析表
分析表用二维表int table[][]存储
###2.3	语法树的建立
使用arraylist实现伪兄弟孩子表示法的树
在LL(1)分析过程中同步建树
因栈式分析的实现原因，Arraylist中下标最大的表示的是最小的孩子（最左孩子节点）
树节点定义如下：
```java
class TreeNode{
	TreeNode parent;//指向父亲节点
	ArrayList<TreeNode> childs;//孩子节点list，没有孩子则size为0
	VLetter letter;//该节点代表的文法符号
	int id;
	Token token;//该节点对应的属性字符流值（终结符节点才有）
	public TreeNode(TreeNode pa,ArrayList<TreeNode> ch,VLetter v,int id,Token t)
	{
		this.parent=pa;
		this.childs=ch;
		this.letter=v;
		this.id=id;
		this.token=t;
	}
	public TreeNode(TreeNode n)
	{
		this.parent=n.parent;
		this.childs=n.childs;
		this.letter=n.letter;
		this.id=n.id;
		this.token=n.token;
	}
}
```
###2.4	XML的输出
根据LL(1)分析过程中建立的语法树，构造相应的xml数据结构并输出
###3.	语法分析器测试用例：
使用框架中input下自带的test.c进行测试：
输入：
```c
int main(int a, int b){	//main function
    return a + b;
}
```
输出：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ParserTree name=".tree.xml">
  <Pg>
    <文法符号0 name="Pg" isVn="true" noChildVn="false">
      <文法符号2 name="fun" isVn="true" noChildVn="false">
        <文法符号3 name="type" isVn="true" noChildVn="false">
          <文法符号24 name="int" isVn="false">
            <value>int</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号24>
        </文法符号3>
        <文法符号44 name="idf" isVn="false">
          <value>main</value>
          <line>1</line>
          <src>F:\compilertest\test.c</src>
        </文法符号44>
        <文法符号4 name="tplist" isVn="true" noChildVn="false">
          <文法符号26 name="(" isVn="false">
            <value>(</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号26>
          <文法符号5 name="tp" isVn="true" noChildVn="false">
            <文法符号3 name="type" isVn="true" noChildVn="false">
              <文法符号24 name="int" isVn="false">
                <value>int</value>
                <line>1</line>
                <src>F:\compilertest\test.c</src>
              </文法符号24>
            </文法符号3>
            <文法符号44 name="idf" isVn="false">
              <value>a</value>
              <line>1</line>
              <src>F:\compilertest\test.c</src>
            </文法符号44>
            <文法符号22 name="ltp" isVn="true" noChildVn="false">
              <文法符号28 name="," isVn="false">
                <value>,</value>
                <line>1</line>
                <src>F:\compilertest\test.c</src>
              </文法符号28>
              <文法符号5 name="tp" isVn="true" noChildVn="false">
                <文法符号3 name="type" isVn="true" noChildVn="false">
                  <文法符号24 name="int" isVn="false">
                    <value>int</value>
                    <line>1</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号24>
                </文法符号3>
                <文法符号44 name="idf" isVn="false">
                  <value>b</value>
                  <line>1</line>
                  <src>F:\compilertest\test.c</src>
                </文法符号44>
                <文法符号22 name="ltp" isVn="true" noChildVn="true" />
              </文法符号5>
            </文法符号22>
          </文法符号5>
          <文法符号27 name=")" isVn="false">
            <value>)</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号27>
        </文法符号4>
        <文法符号6 name="fbody" isVn="true" noChildVn="false">
          <文法符号29 name="{" isVn="false">
            <value>{</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号29>
          <文法符号7 name="senlist" isVn="true" noChildVn="false">
            <文法符号8 name="sen" isVn="true" noChildVn="false">
              <文法符号46 name="return" isVn="false">
                <value>return</value>
                <line>2</line>
                <src>F:\compilertest\test.c</src>
              </文法符号46>
              <文法符号11 name="mov" isVn="true" noChildVn="false">
                <文法符号44 name="idf" isVn="false">
                  <value>a</value>
                  <line>2</line>
                  <src>F:\compilertest\test.c</src>
                </文法符号44>
                <文法符号14 name="opcall" isVn="true" noChildVn="false">
                  <文法符号12 name="op" isVn="true" noChildVn="false">
                    <文法符号34 name="+" isVn="false">
                      <value>+</value>
                      <line>2</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号34>
                    <文法符号13 name="val" isVn="true" noChildVn="false">
                      <文法符号44 name="idf" isVn="false">
                        <value>b</value>
                        <line>2</line>
                        <src>F:\compilertest\test.c</src>
                      </文法符号44>
                    </文法符号13>
                  </文法符号12>
                </文法符号14>
                <文法符号33 name=";" isVn="false">
                  <value>;</value>
                  <line>2</line>
                  <src>F:\compilertest\test.c</src>
                </文法符号33>
              </文法符号11>
            </文法符号8>
            <文法符号7 name="senlist" isVn="true" noChildVn="true" />
          </文法符号7>
          <文法符号30 name="}" isVn="false">
            <value>}</value>
            <line>3</line>
            <src>F:\compilertest\test.c</src>
          </文法符号30>
        </文法符号6>
      </文法符号2>
      <文法符号1 name="T1" isVn="true" noChildVn="true" />
    </文法符号0>
  </Pg>
</ParserTree>
```
另一个测试用例：
```c
int foo(int p){
	int i = 0;
	
	if(p > 0){
		i=i+1;
	}else{
		i=i-1;
	}
}

int main(){
	foo(10);
}
```
输出：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ParserTree name=".tree.xml">
  <Pg>
    <文法符号0 name="Pg" isVn="true" noChildVn="false">
      <文法符号2 name="fun" isVn="true" noChildVn="false">
        <文法符号3 name="type" isVn="true" noChildVn="false">
          <文法符号24 name="int" isVn="false">
            <value>int</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号24>
        </文法符号3>
        <文法符号44 name="idf" isVn="false">
          <value>foo</value>
          <line>1</line>
          <src>F:\compilertest\test.c</src>
        </文法符号44>
        <文法符号4 name="tplist" isVn="true" noChildVn="false">
          <文法符号26 name="(" isVn="false">
            <value>(</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号26>
          <文法符号5 name="tp" isVn="true" noChildVn="false">
            <文法符号3 name="type" isVn="true" noChildVn="false">
              <文法符号24 name="int" isVn="false">
                <value>int</value>
                <line>1</line>
                <src>F:\compilertest\test.c</src>
              </文法符号24>
            </文法符号3>
            <文法符号44 name="idf" isVn="false">
              <value>p</value>
              <line>1</line>
              <src>F:\compilertest\test.c</src>
            </文法符号44>
            <文法符号22 name="ltp" isVn="true" noChildVn="true" />
          </文法符号5>
          <文法符号27 name=")" isVn="false">
            <value>)</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号27>
        </文法符号4>
        <文法符号6 name="fbody" isVn="true" noChildVn="false">
          <文法符号29 name="{" isVn="false">
            <value>{</value>
            <line>1</line>
            <src>F:\compilertest\test.c</src>
          </文法符号29>
          <文法符号7 name="senlist" isVn="true" noChildVn="false">
            <文法符号8 name="sen" isVn="true" noChildVn="false">
              <文法符号17 name="declordef" isVn="true" noChildVn="false">
                <文法符号3 name="type" isVn="true" noChildVn="false">
                  <文法符号24 name="int" isVn="false">
                    <value>int</value>
                    <line>2</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号24>
                </文法符号3>
                <文法符号44 name="idf" isVn="false">
                  <value>i</value>
                  <line>2</line>
                  <src>F:\compilertest\test.c</src>
                </文法符号44>
                <文法符号18 name="T3" isVn="true" noChildVn="false">
                  <文法符号41 name="=" isVn="false">
                    <value>=</value>
                    <line>2</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号41>
                  <文法符号11 name="mov" isVn="true" noChildVn="false">
                    <文法符号45 name="const" isVn="false">
                      <value>0</value>
                      <line>2</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号45>
                    <文法符号12 name="op" isVn="true" noChildVn="true" />
                    <文法符号33 name=";" isVn="false">
                      <value>;</value>
                      <line>2</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号33>
                  </文法符号11>
                </文法符号18>
              </文法符号17>
            </文法符号8>
            <文法符号7 name="senlist" isVn="true" noChildVn="false">
              <文法符号8 name="sen" isVn="true" noChildVn="false">
                <文法符号19 name="if-else" isVn="true" noChildVn="false">
                  <文法符号31 name="if" isVn="false">
                    <value>if</value>
                    <line>4</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号31>
                  <文法符号26 name="(" isVn="false">
                    <value>(</value>
                    <line>4</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号26>
                  <文法符号13 name="val" isVn="true" noChildVn="false">
                    <文法符号44 name="idf" isVn="false">
                      <value>p</value>
                      <line>4</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号44>
                  </文法符号13>
                  <文法符号12 name="op" isVn="true" noChildVn="false">
                    <文法符号38 name="&lt;" isVn="false">
                      <value>&gt;</value>
                      <line>4</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号38>
                    <文法符号13 name="val" isVn="true" noChildVn="false">
                      <文法符号45 name="const" isVn="false">
                        <value>0</value>
                        <line>4</line>
                        <src>F:\compilertest\test.c</src>
                      </文法符号45>
                    </文法符号13>
                  </文法符号12>
                  <文法符号27 name=")" isVn="false">
                    <value>)</value>
                    <line>4</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号27>
                  <文法符号8 name="sen" isVn="true" noChildVn="false">
                    <文法符号29 name="{" isVn="false">
                      <value>{</value>
                      <line>4</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号29>
                    <文法符号7 name="senlist" isVn="true" noChildVn="false">
                      <文法符号8 name="sen" isVn="true" noChildVn="false">
                        <文法符号9 name="moveorop" isVn="true" noChildVn="false">
                          <文法符号44 name="idf" isVn="false">
                            <value>i</value>
                            <line>5</line>
                            <src>F:\compilertest\test.c</src>
                          </文法符号44>
                          <文法符号10 name="T2" isVn="true" noChildVn="false">
                            <文法符号41 name="=" isVn="false">
                              <value>=</value>
                              <line>5</line>
                              <src>F:\compilertest\test.c</src>
                            </文法符号41>
                            <文法符号11 name="mov" isVn="true" noChildVn="false">
                              <文法符号44 name="idf" isVn="false">
                                <value>i</value>
                                <line>5</line>
                                <src>F:\compilertest\test.c</src>
                              </文法符号44>
                              <文法符号14 name="opcall" isVn="true" noChildVn="false">
                                <文法符号12 name="op" isVn="true" noChildVn="false">
                                  <文法符号34 name="+" isVn="false">
                                    <value>+</value>
                                    <line>5</line>
                                    <src>F:\compilertest\test.c</src>
                                  </文法符号34>
                                  <文法符号13 name="val" isVn="true" noChildVn="false">
                                    <文法符号45 name="const" isVn="false">
                                      <value>1</value>
                                      <line>5</line>
                                      <src>F:\compilertest\test.c</src>
                                    </文法符号45>
                                  </文法符号13>
                                </文法符号12>
                              </文法符号14>
                              <文法符号33 name=";" isVn="false">
                                <value>;</value>
                                <line>5</line>
                                <src>F:\compilertest\test.c</src>
                              </文法符号33>
                            </文法符号11>
                          </文法符号10>
                        </文法符号9>
                      </文法符号8>
                      <文法符号7 name="senlist" isVn="true" noChildVn="true" />
                    </文法符号7>
                    <文法符号30 name="}" isVn="false">
                      <value>}</value>
                      <line>6</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号30>
                  </文法符号8>
                  <文法符号32 name="else" isVn="false">
                    <value>else</value>
                    <line>6</line>
                    <src>F:\compilertest\test.c</src>
                  </文法符号32>
                  <文法符号8 name="sen" isVn="true" noChildVn="false">
                    <文法符号29 name="{" isVn="false">
                      <value>{</value>
                      <line>6</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号29>
                    <文法符号7 name="senlist" isVn="true" noChildVn="false">
                      <文法符号8 name="sen" isVn="true" noChildVn="false">
                        <文法符号9 name="moveorop" isVn="true" noChildVn="false">
                          <文法符号44 name="idf" isVn="false">
                            <value>i</value>
                            <line>7</line>
                            <src>F:\compilertest\test.c</src>
                          </文法符号44>
                          <文法符号10 name="T2" isVn="true" noChildVn="false">
                            <文法符号41 name="=" isVn="false">
                              <value>=</value>
                              <line>7</line>
                              <src>F:\compilertest\test.c</src>
                            </文法符号41>
                            <文法符号11 name="mov" isVn="true" noChildVn="false">
                              <文法符号44 name="idf" isVn="false">
                                <value>i</value>
                                <line>7</line>
                                <src>F:\compilertest\test.c</src>
                              </文法符号44>
                              <文法符号14 name="opcall" isVn="true" noChildVn="false">
                                <文法符号12 name="op" isVn="true" noChildVn="false">
                                  <文法符号35 name="-" isVn="false">
                                    <value>-</value>
                                    <line>7</line>
                                    <src>F:\compilertest\test.c</src>
                                  </文法符号35>
                                  <文法符号13 name="val" isVn="true" noChildVn="false">
                                    <文法符号45 name="const" isVn="false">
                                      <value>1</value>
                                      <line>7</line>
                                      <src>F:\compilertest\test.c</src>
                                    </文法符号45>
                                  </文法符号13>
                                </文法符号12>
                              </文法符号14>
                              <文法符号33 name=";" isVn="false">
                                <value>;</value>
                                <line>7</line>
                                <src>F:\compilertest\test.c</src>
                              </文法符号33>
                            </文法符号11>
                          </文法符号10>
                        </文法符号9>
                      </文法符号8>
                      <文法符号7 name="senlist" isVn="true" noChildVn="true" />
                    </文法符号7>
                    <文法符号30 name="}" isVn="false">
                      <value>}</value>
                      <line>8</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号30>
                  </文法符号8>
                </文法符号19>
              </文法符号8>
              <文法符号7 name="senlist" isVn="true" noChildVn="true" />
            </文法符号7>
          </文法符号7>
          <文法符号30 name="}" isVn="false">
            <value>}</value>
            <line>9</line>
            <src>F:\compilertest\test.c</src>
          </文法符号30>
        </文法符号6>
      </文法符号2>
      <文法符号1 name="T1" isVn="true" noChildVn="false">
        <文法符号0 name="Pg" isVn="true" noChildVn="false">
          <文法符号2 name="fun" isVn="true" noChildVn="false">
            <文法符号3 name="type" isVn="true" noChildVn="false">
              <文法符号24 name="int" isVn="false">
                <value>int</value>
                <line>11</line>
                <src>F:\compilertest\test.c</src>
              </文法符号24>
            </文法符号3>
            <文法符号44 name="idf" isVn="false">
              <value>main</value>
              <line>11</line>
              <src>F:\compilertest\test.c</src>
            </文法符号44>
            <文法符号4 name="tplist" isVn="true" noChildVn="false">
              <文法符号26 name="(" isVn="false">
                <value>(</value>
                <line>11</line>
                <src>F:\compilertest\test.c</src>
              </文法符号26>
              <文法符号5 name="tp" isVn="true" noChildVn="true" />
              <文法符号27 name=")" isVn="false">
                <value>)</value>
                <line>11</line>
                <src>F:\compilertest\test.c</src>
              </文法符号27>
            </文法符号4>
            <文法符号6 name="fbody" isVn="true" noChildVn="false">
              <文法符号29 name="{" isVn="false">
                <value>{</value>
                <line>11</line>
                <src>F:\compilertest\test.c</src>
              </文法符号29>
              <文法符号7 name="senlist" isVn="true" noChildVn="false">
                <文法符号8 name="sen" isVn="true" noChildVn="false">
                  <文法符号9 name="moveorop" isVn="true" noChildVn="false">
                    <文法符号44 name="idf" isVn="false">
                      <value>foo</value>
                      <line>12</line>
                      <src>F:\compilertest\test.c</src>
                    </文法符号44>
                    <文法符号10 name="T2" isVn="true" noChildVn="false">
                      <文法符号21 name="call" isVn="true" noChildVn="false">
                        <文法符号15 name="plist" isVn="true" noChildVn="false">
                          <文法符号26 name="(" isVn="false">
                            <value>(</value>
                            <line>12</line>
                            <src>F:\compilertest\test.c</src>
                          </文法符号26>
                          <文法符号16 name="p" isVn="true" noChildVn="false">
                            <文法符号13 name="val" isVn="true" noChildVn="false">
                              <文法符号45 name="const" isVn="false">
                                <value>10</value>
                                <line>12</line>
                                <src>F:\compilertest\test.c</src>
                              </文法符号45>
                            </文法符号13>
                            <文法符号23 name="lp" isVn="true" noChildVn="true" />
                          </文法符号16>
                          <文法符号27 name=")" isVn="false">
                            <value>)</value>
                            <line>12</line>
                            <src>F:\compilertest\test.c</src>
                          </文法符号27>
                        </文法符号15>
                      </文法符号21>
                      <文法符号33 name=";" isVn="false">
                        <value>;</value>
                        <line>12</line>
                        <src>F:\compilertest\test.c</src>
                      </文法符号33>
                    </文法符号10>
                  </文法符号9>
                </文法符号8>
                <文法符号7 name="senlist" isVn="true" noChildVn="true" />
              </文法符号7>
              <文法符号30 name="}" isVn="false">
                <value>}</value>
                <line>13</line>
                <src>F:\compilertest\test.c</src>
              </文法符号30>
            </文法符号6>
          </文法符号2>
          <文法符号1 name="T1" isVn="true" noChildVn="true" />
        </文法符号0>
      </文法符号1>
    </文法符号0>
  </Pg>
</ParserTree>
```
限于篇幅，请自行进行更多测试，注意代码要在文法支持范围内
###4.	心得收获与体会
体会到了文法设计的重要性。熟悉了LL和LR分析方法，LR虽然分析能力较强，但难以手工实现，适合设计算法以文法作为输入得到LR(1)分析表,LL(1)虽然分析能力较弱于LR(1),但便于手工实现,在文法不是很复杂的情况下(例如对本实验的50个产生式的规模来说),很容易画出LL(1)分析表。


## 语义分析与中间代码生成实验报告

### 1.	实现的功能：
### 1.1	语义检查/语法制导翻译
支持的语义信息有：

> fun->type idf tplist fbody 将函数添加到符号表，并填充函数信息表
>
> tplist->( tp ) 将tplist的参数列表属性传递到tp,将形参加入符号表
>
> tp->type idf ltp 将tp的参数列表属性传递到子节点
>
> tp->e 无操作
>
> fbody->{ senlist } 将senlist的四元式添加到fbody中
>
> senlist->sen senlist 将子节点的四元式添加到父节点
>
> senlist->e 无操作
>
> sen->return mov 将sen的四元式序列属性传递到子节点
>
> sen->; 向sen四元式序列中添加一个空语句
>
> mov->idf opcall ; 根据idf和opcall信息添加四元式
>
> opcall->op 将op的算符和运算数属性传递给opcall
>
> op->+ val 将+和val的属性传递给op
>
> op->e 无操作
>
> val->idf 将idf名字信息传递给val
>
> val->const 将const值传递给val



###1.2	维护的符号表及标号表等信息
```java
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
```
###1.3	维护的标号表信息
```java
class Label {//标号表
	int id;//标号id
	boolean isdef;//定义否
	int addr;//地址，指向四元式标号
	String nameString;//标号名字
}
class LabelTable{//标号表
	ArrayList<Label> lbList;
}
```
###1.4	产生的四元式信息
```java
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
```
###1.5	语法树中新增的语义属性：
```java
//语义属性：
	public int val;//符号的值
	public int type;//符号的属性，对应文法符号id
	public int valType;//符号值的类型，整型等
	public ArrayList<Integer> plistType;//参数类型list
	public ArrayList<String> plistString;//参数名list
	public ArrayList<semanticAndInterCode.compiler.rmwang.FourElement> codeElements;//四元式list
	public String nameString;//传递上来的标识符名字，用作函数
	public int opType;//操作属性，0return，1+
	public String arg1;//操作数1
	public String arg2;//操作数2
	public String rlt;//操作数3
```
###1.6	支持的c语言特性：
识别函数，维护函数信息表，识别return语句，识别简单+运算表达式等
根据语法制导翻译语义信息，进行语义检查，生成四元式
1. 实现的关键技术
   2.1设计文法对应的属性信息，属性传递原则，语义动作等语义信息
   2.2设计数据结构维护符号表，标号表，函数信息表等信息
   2.3根据设计的语义信息，使用语法制导翻译对语法树一遍遍历，实现了语义分析和中间代码生成
   3.测试用例
   使用input下自带的测试用例进行测试
   ```C
   int main(int a, int b){//main function
    return a + b;
   }
   ```
   输出的四元式为：
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <IC name="test.ic.xml">
   <functions>
    <function>
      <quaternion result="T1" arg2="b" arg1="a" op="+" addr="1" />
      <quaternion result="" arg2="T1" arg1="" op="return" addr="2" />
    </function>
   </functions>
   </IC>
   ```
###4. 实验收获与心得体会
   熟悉了符号表，标号表等表格的作用，认识到了翻译过程中表格管理的重要性。认识到了文法符号的继承属性与综合属性的含义，熟悉了语法制导翻译的基本思想和实现方式，熟悉了使用四元式对源程序进行表达的中间代码形式。
   语义分析与中间代码生成在编译器工作流程中起着承上启下，至关重要的作用。语义信息的复杂与否也很大程度上决定了语义分析的实现难度。对于c语言，其文法提取左公因子时往往会造成语义分析的复杂性，因为添加了非终结符，将原来的某些产生式拆开了，所以非终结符的语义信息显得不是很“自然”和“直观”。文法和其语义的设计对于编程语言来说是至关重要的，这直接决定了语言的功能特性和结构特性。文法与语义的取舍也是很值得思考的问题，因为简化一方的实现很可能加大另一方的实现难度，有时候我们可以不按严格的LL(1)来设计文法，以便文法的语义信息显得直白，语法树的深度变小，易实现语义分析和中间代码生成，而我们付出的仅是语法分析时一些极小的回溯代价而已（回溯短）。

