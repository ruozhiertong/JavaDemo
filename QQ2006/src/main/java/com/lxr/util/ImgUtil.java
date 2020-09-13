package com.lxr.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONObject;

import com.a51work6.qq.client.FriendsFrame;
import com.a51work6.qq.constant.QQConstant;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月12日 上午10:12:52
* 类说明
*/
public class ImgUtil {
	
	
	/**
	 * 使用TCP 进行下载。
	 * @param imgURL
	 */
	public static void DownImg(String fileName, DownCallBack callBack) {
		new Thread(() -> {
			
			InetAddress addr = null;
			try {
				addr = InetAddress.getByName(QQConstant.SERVER_IP);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JSONObject sendObj = new JSONObject();
			sendObj.put("command", QQConstant.COMMAND_DOWNIMG);
			sendObj.put("img_url", fileName);

			byte b[];
			b = sendObj.toString().getBytes();
			try {
				ds.send(new DatagramPacket(b,0 ,b.length,addr, QQConstant.SERVER_PORT));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			b = new byte[1024];
			DatagramPacket dp = new DatagramPacket(b,1024);
			try {
				ds.receive(dp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String recvStr = new String(dp.getData(),0,dp.getLength());
			
			JSONObject recvObj = new JSONObject(recvStr);
			
			if((int)recvObj.get("result") == -1) {
				callBack.callBack(-1);
			}
			else
			{
				int port = (int)recvObj.get("port");
				Socket clientSocket = null;
				InputStream inFromServer =null;
				FileOutputStream fos =null;
				try {
					clientSocket = new Socket(addr, port);
					inFromServer = clientSocket.getInputStream();
					int n;
					System.out.println(fileName);
					System.out.println(ImgUtil.class.getResource("").getPath());
					System.out.println(FriendsFrame.class.getResource("").getPath());
					System.out.println(FriendsFrame.class.getResource("/").getPath());
					//getResource。绝对路径，是对于classes下的。 相对路径是，是对于当前类 xx.class下的。
					// File 的相对路径，是相对于当前工程下的。 绝对路径是系统下的。
					System.out.println(FriendsFrame.class.getResource("/resource/img/100.jpg"));
					System.out.println(FriendsFrame.class.getResource("resource/img/100.jpg"));
					System.out.println(ImgUtil.class.getResource(fileName)); //不存在，返回null。
					//System.out.println(ImgUtil.class.getResource(fileName).getPath());
					//File f = new File(ImgUtil.class.getResource(fileName).getPath());
					File f = new File(ImgUtil.class.getResource("/").getPath() + fileName.substring(1));
					if(!f.exists())
						f.createNewFile();

					fos = new FileOutputStream(f);

					while ((n = inFromServer.read(b)) != -1) {
						fos.write(b, 0,n);
					}
					callBack.callBack(0);
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callBack.callBack(-1);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callBack.callBack(-1);

				}finally {
					if(fos!=null)
						try {
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if(inFromServer != null)
						try {
							inFromServer.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if(clientSocket != null)
						try {
							clientSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
			}	

		},"ImgClient").start();

	}
	
	
	public static int DownImgServer(String imgURL) {
		
		ServerSocket serverSocket;
		int port;
		try {
			serverSocket = new ServerSocket(0); //自动分配端口
			port = serverSocket.getLocalPort();
			System.out.println(port);
			//serverSocket.setSoTimeout(10000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		
		new Thread(()->{
			System.out.println("等待远程连接，端口号为：" + serverSocket.getLocalPort() + "...");
			Socket client;
			try {
				client = serverSocket.accept();
				System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
				//DataInputStream in = new DataInputStream(client.getInputStream());
				//System.out.println(in.readUTF());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				
				FileInputStream fio = new FileInputStream(imgURL);
			
				byte [] b = new byte[1024];
			
				int n;
				
				while((n= fio.read(b)) != -1) {
					out.write(b, 0, n);
				}				
				client.close();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		},"ImgServer").start();
		
		return port;
		
	}

}
