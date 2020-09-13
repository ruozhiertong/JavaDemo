/*
* Created by 智捷课堂
* 本书网站：http://www.zhijieketang.com/group/5
* 智捷课堂在线课堂：www.zhijieketang.com
* 智捷课堂微信公共号：zhijieketang
* 邮箱：eorient@sina.com
* Java读者服务QQ群：547370999
*
* 买《Java从小白到大牛》纸质版图书，送配套视频
*
* 【配套电子书】网址：
*       图灵社区：
*       http://www.ituring.com.cn/book/2480
*       百度阅读：
*       https://yuedu.baidu.com/ebook/7c1499987e192279168884868762caaedd33ba00 
*/


package com.a51work6.qq.client;

import java.io.IOException;
import java.net.DatagramSocket;

import com.a51work6.qq.constant.QQConstant;

public class Client {

	
	public static DatagramSocket socket;
	// 准备一个缓冲区
	public static byte []buffer = new byte[10240];//10k. 目前这个小程序，暂定最大的包10k。
	
	// 服务器端IP
	public static String serverIP = QQConstant.SERVER_IP;
	// 服务器端端口号
	public static int serverPort = QQConstant.SERVER_PORT;

	public static void main(String[] args) {

		if (args.length == 2) {
			serverIP = args[0];
			serverPort = Integer.parseInt(args[1]);
		}

		try {// 创建DatagramSocket对象，由系统分配可以使用的端口
			socket = new DatagramSocket();
			// 设置超时5秒，不再等待接收数据。 其实也没必要设置超时。 
			//但是因为在UI线程有receive，如果一直阻塞会导致UI卡死。所以要设置超时。
			//如果都只是放在工作线程中，那么一直等待也没问题。
			socket.setSoTimeout(5000);
			System.out.println("客户端运行...");
			LoginFrame frame = new LoginFrame();
			frame.setVisible(true);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
