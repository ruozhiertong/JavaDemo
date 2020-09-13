package com.a51work6.qq.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.a51work6.qq.client.Client;
import com.a51work6.qq.constant.QQConstant;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2020年9月8日 下午9:36:49 类说明
 */
public class Test2 {

	public static void main(String[] args) {
		
		
//		try {
//			FileOutputStream fos = new FileOutputStream(new File("res/hello.txt")); //不存在，会新建一个
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println();
//		
//		File f = new File("/res/hello.txt");
//		if(!f.exists())
//			try {
//				f.createNewFile();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		
		
		
		String url = "/resource/img/100200.jpg";
		
		String path = Test2.class.getResource("/").getPath() + url.substring(1);
		
		System.out.println(path);

		File f  = new File(path);
		
		System.out.println(f.exists());
		
		
		System.out.println(url.indexOf("*/"));
		String pattern = "[^:/]/$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(url);
		System.out.println(m.matches());
		
		while (m.find()) {
			System.out.println(m.groupCount());
			System.out.println(m.start());
			System.out.println(m.end());
			System.out.println("Found value: " + m.group());
		}
		
		
		
		

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			// socket.setSoTimeout(5000);
			// 打开窗口时 发送一次。
		} catch (Exception e) {
			// TODO: handle exception
		}

		RecvRunnable r = new RecvRunnable();
		r.socket = socket;
		new Thread(r).start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// r.runFlag = false;

		socket.close();
		socket = null;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		System.out.println("end main");

	}
}

class RecvRunnable implements Runnable {

	public DatagramSocket socket;

	public boolean runFlag = true;

	public byte[] buffer = new byte[10240];

	public InetAddress address;

	@Override
	public void run() {
		try {
			address = InetAddress.getByName(Client.serverIP);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		DatagramPacket packet;
		while (runFlag) {
			try {
				System.out.println("in run");
				/* 接收数据报 */
				packet = new DatagramPacket(buffer, buffer.length, address, Client.serverPort);
				// 开始接收
				socket.receive(packet); //阻塞。 当收到消息或socket关闭时会立即返回。
				// 接收数据长度
				int len = packet.getLength();
				String str = new String(buffer, 0, len);
				// 打印接收的数据
				System.out.printf("chatFrame 接收的消息：【%s】\n", str);

			} catch (Exception e) {
				System.out.println("run while(isRunning)");
				e.printStackTrace();
				runFlag = false;
			}
		}
	}
}
