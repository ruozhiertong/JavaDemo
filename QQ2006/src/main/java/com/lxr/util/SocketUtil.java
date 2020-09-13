package com.lxr.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.plugins.bmp.BMPImageWriteParam;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月12日 下午9:23:08
* 类说明
*/
public class SocketUtil {
	
	/**
	 * 创建一个有超时机制的UDPsocket。
	 * 适合与简单了发包UDPSend 收包UDPReceive。
	 * @param serverName
	 * @param serverPort
	 * @param msg
	 * @return
	 */
	public static DatagramSocket UDPSend(String serverName, int serverPort, String msg) {
		DatagramSocket clientSocket = null;
		try {
			InetAddress serverAddr = InetAddress.getByName(serverName);
			clientSocket = new DatagramSocket(); //随机绑定端口
			clientSocket.setSoTimeout(5000);// 5s超时
			byte []b = msg.getBytes(); //如果msg过大，UDP 一个包可能装不下。需要进行多次发包。
			DatagramPacket dp = new DatagramPacket(b,0,b.length,serverAddr,serverPort);
			clientSocket.send(dp);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			clientSocket = null;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			clientSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			clientSocket = null;
		}
		return clientSocket;
		
	}
	/**
	 * 一次收包。 如果多次收包，其他方法。
	 * @param clientSocket
	 */
	public static String UDPReceive(DatagramSocket clientSocket) {
		
		String recStr = null;
		if(clientSocket == null)
			return recStr;
		
		byte []b = new byte[10240];//10k
		DatagramPacket dp = new DatagramPacket(b,10240);
		try {
			clientSocket.receive(dp);
			recStr = new String(dp.getData(),0, dp.getLength());
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recStr;
		
		
	}
	
	
	public static void TCPSend() {
		
	}
	public static void TCPReceive() {
		
	}

}
