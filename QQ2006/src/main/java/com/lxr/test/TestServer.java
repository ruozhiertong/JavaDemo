package com.lxr.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.a51work6.qq.constant.QQConstant;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月11日 下午3:36:04
* 类说明
* 测试UDP过程中的问题：丢包，乱序等。
*/
public class TestServer {
	
	public static void main(String[] args) {
		
		test();
	}
	
	/**
	 * UDP 丢包 乱序 测试。
	 */
	public static void test() {

		try {
			DatagramSocket socket = new DatagramSocket(QQConstant.SERVER_PORT);
			
			byte []b = new byte[1024];
			
			DatagramPacket dp = new DatagramPacket(b, 1024);
			
			int count =0;
			
			while(true) {
				
				socket.receive(dp);
				
				//System.out.println(dp.getData());
				//System.out.println(dp.getLength());
				String recStr = new String(dp.getData(), 0, dp.getLength());

				System.out.println(recStr);
				count++;
				Thread.sleep(20);
				
				if(recStr.equals("end"))
					break;
				
			}
			System.out.println(count);
			
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
