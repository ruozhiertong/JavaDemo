package com.lxr.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.a51work6.qq.client.Client;
import com.a51work6.qq.constant.QQConstant;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月11日 下午3:36:50
* 类说明
*/
public class TestClient {

	public static void main(String[] args) {
		
		test();
	}
	
	
	/**
	 * UDP丢包 乱序测试。
	 * 
	 */
	public static void test() {
		try {
			DatagramSocket socket = new DatagramSocket(); //端口由系统分配
			
			byte []b;
			
			DatagramPacket pd; //发送的包要指定目标地址 端口等
			int no = 1;
			String str = "hello";
			InetAddress address = InetAddress.getByName(QQConstant.SERVER_IP);
			while(true) {
				b = (str + no).getBytes();
				System.out.println(str+no);
				pd = new DatagramPacket(b, b.length, address, QQConstant.SERVER_PORT);
				socket.send(pd);
				no++;
				if(no > 20000)
					break;
			}
			b = "end".getBytes();
			pd = new DatagramPacket(b, b.length, address, QQConstant.SERVER_PORT);
			socket.send(pd);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
