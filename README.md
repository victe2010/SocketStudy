android socket通信
================
## 1、`socket`的定义
* 即套接字，网络上的两个程序通过一个双向的通信连接实现数据的交换，
这个连接的一端称为一个socket，
是支持`TCP/IP`协议的网络通信的基本操作单元。
* Socket的使用可以基于TCP或者UDP协议。
## 2、`socket`使用方式
* 生成套接字，主要有3个参数：通信的目的IP地址、
使用的传输层协议(TCP或UDP)和使用的端口号。
Socket原意是“插座”。
通过将这3个参数结合起来，与一个“插座”Socket绑定，
应用层就可以和传输层通过套接字接口，区分来自不同应用程序进程或网络连接的通信，实现数据传输的并发服务。
## 3、端口号
* 端口号规定为16位，即允许一个ip主机最多65535（2的16次方）个不同的端口。
* 0~1023 ：分配给系统的端口
    > 最好是不要乱用
* 1024~49151:登记端口号，主要是让第三方应用使用
    > 但是必须在IANA（互联网数字分配机构）按照规定手续登记，
* 49152~65535：短暂端口号，是留给客户进程选择暂时使用，
一个进程使用完就可以供其他进程使用。
    > 在`Socket`使用时，可以用1024~65535的端口号
## 4、TCP协议
### ( 1 )定义
* Transmission Control Protocol，即传输控制协议，是一种传输层通信协议
    > 基于TCP的应用层协议有FTP、Telnet、SMTP、HTTP、POP3与DNS
### ( 2 )特点
* 面向连接、面向字节流、全双工通信、可靠
> `面向连接`：指的是要使用TCP传输数据，
必须先建立TCP连接，传输完成后释放连接，
就像打电话一样必须先拨号建立一条连接，打完后挂机释放连接。

>`全双工通信`：即一旦建立了TCP连接，
通信双方可以在任何时候都能发送数据。

>`可靠的`：指的是通过TCP连接传送的数据，
无差错，不丢失，不重复，并且按序到达。

> `面向字节流`：流，指的是流入到进程或从进程流出的字符序列。
简单来说，虽然有时候要传输的数据流太大，TCP报文长度有限制，
不能一次传输完，要把它分为好几个数据块，但是由于可靠性保证，
接收方可以按顺序接收数据块然后重新组成分块之前的数据流，
所以TCP看起来就像直接互相传输字节流一样，面向字节流。

### ( 3 )TCP连接(三次握手协议)
* TCP建立连接必须进行3次握手协议，分别是：

    `第一次握手`：建立连接。客户端发送连接请求报文段，将SYN位置为1，
    Sequence Number为x；然后，客户端进入SYN_SEND状态，等待服务器的确认。
    即客户端发送信息给服务器

    `第二次握手`：服务器收到客户端的SYN报文段，
    需要对这个SYN报文段进行确认（SYN+ACK）。
    即服务器收到连接信息后向客户端返回确认信息

    `第三次握手`：客户端收到服务器的（SYN+ACK）报文段，
    并向服务器发送ACK报文段(SYN+ACK+ACK)。
    即客户端收到确认信息后再次向服务器返回确认连接信息

        注意：三次握手期间任何一次未收到对面回复都要重发。
    最后一个确认报文段发送完毕以后，
    客户端和服务器端都进入已建立连接状态。

### ( 4 )为什么TCP建立连接需要三次握手？
* 防止服务器端因为接收了早已失效的连接请求报文从而一直等待客户端请求，
从而浪费资源,采用“三次握手”的办法可以防止该现象发生：
    > Client不会向Server的确认发出确认
  Server由于收不到确认，就知道Client并没有要求建立连接
  所以Server不会等待Client发送数据，资源就没有被浪费
### ( 5 )TCP断开链接（四次挥手协议）
`第一次挥手`：A发送释放信息到B；
（发出去之后，A->B发送数据这条路径就断了）

`第二次挥手`：B收到A的释放信息之后，回复确认释放的信息：
我同意你的释放连接请求

`第三次挥手`：B发送“请求释放连接“信息给A

`第四次挥手`：A收到B发送的信息后向B发送确认释放信息：
我同意你的释放连接请求

##### 为什么TCP释放连接需要四次挥手？
    为了保证双方都能通知对方“需要释放连接”，
    即在释放连接后都无法接收或发送消息给对方
##### 需要明确的是：
* TCP是全双工模式，这意味着是双向都可以发送、接收的

* 释放连接的定义是：双方都无法接收或发送消息给对方，是双向的

* 当主机1发出“释放连接请求”（FIN报文段）时，只是表示主机1已经没有数据要发送 / 数据已经全部发送完毕；
但是，这个时候主机1还是可以接受来自主机2的数据。
* 当主机2返回“确认释放连接”信息（ACK报文段）时，表示它已经知道主机1没有数据发送了
但此时主机2还是可以发送数据给主机1

* 当主机2也发送了FIN报文段时，即告诉主机1我也没有数据要发送了

此时，主机1和2已经无法进行通信：主机1无法发送数据给主机2，主机2也无法发送数据给主机1，此时，TCP的连接才算释放
## 5、UDP协议
### ( 1 )定义
* 即用户数据报协议，是一种传输层通信协议
    > 基于UDP的应用层协议有TFTP、SNMP与DNS。
### ( 2 )特点
* 无连接的、不可靠的、面向报文、没有拥塞控制

`无连接的`：和TCP要建立连接不同，
UDP传输数据不需要建立连接，就像写信，在信封写上收信人名称、
地址就可以交给邮局发送了，至于能不能送到，
就要看邮局的送信能力和送信过程的困难程度了。

`不可靠的`：因为UDP发出去的数据包发出去就不管了，不管它会不会到达，
所以很可能会出现丢包现象，使传输的数据出错。

`面向报文`：数据报文，就相当于一个数据包，应用层交给UDP多大的数据包，
UDP就照样发送，不会像TCP那样拆分。

`没有拥塞控制`：拥塞，是指到达通信子网中某一部分的分组数量过多，
使得该部分网络来不及处理，以致引起这部分乃至整个网络性能下降的现象，
严重时甚至会导致网络通信业务陷入停顿，即出现死锁现象，就像交通堵塞一样。TCP建立连接后如果发送的数据因为信道质量的原因不能到达目的地，它会不断重发，有可能导致越来越塞，所以需要一个复杂的原理来控制拥塞。而UDP就没有这个烦恼，发出去就不管了。

### ( 3 )应用场景
很多的实时应用（如IP电话、实时视频会议、某些多人同时在线游戏等）
要求源主机以很定的速率发送数据，
并且允许在网络发生拥塞时候丢失一些数据，
但是要求不能有太大的延时，UDP就刚好适合这种要求。


## 5、socket具体使用
服务端
```
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                System.out.print("有客户端连上了");
                ChatSocket chatSocket = new ChatSocket(socket);
                ThreadManager.getInstance().execute(chatSocket);
                ChatManager.getInstance().add(chatSocket);
            }
```

ChatSocket.class
```
package com.wls.socket;

import com.example.msit.study.Student;

import java.io.*;
import java.net.Socket;

/**
 * Created by 13526 on 2017/6/7.
 */
public class ChatSocket implements Runnable{
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public ChatSocket(Socket socket){
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //发送消息
    public void out(String mess){
        try {
            bufferedWriter.write(mess);
            bufferedWriter.write("\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null){

                    ChatManager.getInstance().publish(this,line);
              }
                bufferedReader.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            bufferedWriter.close();
            socket.close();
            ChatManager.getInstance().remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

客户端的使用
```
/*
*实例化socket
*/
 socket = new Socket("192.168.2.114",9999);
 /**
 *建立链接
 */
 socket.isConnected()
```


---------------------


         附：    FIN：结束标志    SYN：同步标志    ACK：确认标志