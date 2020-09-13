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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.json.JSONArray;
import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;

public class ChatFrame extends JFrame {

	//public volatile boolean isRunning = true;

	// 当前用户Id
	public String userId;
	public String userName;
	// 聊天好友用户Id
	public String friendUserId;
	// 聊天好友用户名
	public String friendUserName;

	// 获得当前屏幕的高和宽
	private double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

	// 登录窗口宽和高
	private int frameWidth = 345;
	private int frameHeight = 310;

	// 查看消息文本区
	public JTextArea txtMainInfo;
	// 发送消息文本区
	public JTextArea txtInfo;
	// 消息日志
	public StringBuffer infoLog;

	// 好友列表Frame
	public FriendsFrame friendsFrame;
	
	public InetAddress address;

	// 对话框维护的socket。 socket会在两个线程中使用：UI线程中发送消息，runnable线程中接收。
	public volatile DatagramSocket socket;
	// 接收消息线程。
	public ChatRunnable recvRunnable;
	
	// 准备一个缓冲区 用于接收
	//public byte[] buffer = new byte[10240];

	// 日期格式化
	public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ChatFrame(FriendsFrame friendsFrame, Map<String, String> user, Map<String, String> friend) {

		// 初始化成员变量
		this.friendsFrame = friendsFrame;

		this.userId = user.get("user_id");
		this.userName = user.get("user_name");
		String userIcon = user.get("user_icon");

		this.friendUserId = friend.get("user_id");
		this.friendUserName = friend.get("user_name");
		
		try {
			address = InetAddress.getByName(Client.serverIP);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// 初始化查看消息面板
		getContentPane().add(getPanLine1());
		// 初始化发送消息面板
		JPanel sendMsgPanel = getPanLine2();
		getContentPane().add(sendMsgPanel);

		/// 初始化当前Frame
		String iconFile = String.format("/resource/img/%s.jpg", userIcon);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource(iconFile)));
		String title = String.format("%s与%s聊天中...", user.get("user_name"), friendUserName);
		setTitle(title);
		setResizable(false);
		getContentPane().setLayout(null);

		// 设置Frame大小
		setSize(frameWidth, frameHeight);
		// 计算Frame位于屏幕中心的坐标
		int x = (int) (screenWidth - frameWidth) / 2;
		int y = (int) (screenHeight - frameHeight) / 2;
		// 设置Frame位于屏幕中心
		setLocation(x, y);

		// 注册窗口事件
		addWindowListener(new WindowAdapter() {
			// 单击窗口关闭按钮时调用
			public void windowClosing(WindowEvent e) {
//				isRunning = false;
//				setVisible(false);
				setShow(false);
				// 重启好友列表线程
				// friendsFrame.resetThread();
			}
		});

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出应用程序

	}

	// 查看消息面板
	private JPanel getPanLine1() {

		txtMainInfo = new JTextArea();
		txtMainInfo.setEditable(false);

//		List<Map<String,String>> offmsgs = (List<Map<String, String>>) friendsFrame.user.get("offMsgs");
//
//		if(offmsgs != null) {
//			
//			for(Map<String, String> msg : offmsgs) {
//				if(msg.get("send_id").equals(friendUserId)) {
//					String info = String.format("#%s#" + "\n" + "%s对您说：%s\n", msg.get("upd_date"), friendUserName,msg.get("msg"));
//					infoLog.append(info);
//				}
//			}
//			
//			txtMainInfo.setText(infoLog.toString());
//			
//		}
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 5, 320, 200);
		scrollPane.setViewportView(txtMainInfo);

		JPanel panLine1 = new JPanel();
		panLine1.setLayout(null);
		panLine1.setBounds(new Rectangle(5, 5, 330, 210));
		panLine1.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
		panLine1.add(scrollPane);

		return panLine1;
	}

	// 发送消息面板
	private JPanel getPanLine2() {

		JPanel panLine2 = new JPanel();
		panLine2.setLayout(null);
		panLine2.setBounds(5, 220, 330, 50);
		panLine2.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
		panLine2.add(getSendButton());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 5, 222, 40);
		panLine2.add(scrollPane);

		txtInfo = new JTextArea();
		scrollPane.setViewportView(txtInfo);
				
		txtInfo.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				//System.out.println(e.getKeyChar());
				//System.out.println(e.getKeyCode());
				//if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if(e.getKeyChar() == '\n') {
					String msg = txtInfo.getText();
					sendMessage(msg.substring(0,msg.length()-1));//-1去除最后的enter建
					txtInfo.setText("");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});

		return panLine2;
	}

	private JButton getSendButton() {

		JButton button = new JButton("发送");
		button.setBounds(232, 10, 90, 30);
		button.addActionListener(e -> {
			sendMessage(txtInfo.getText());
			txtInfo.setText("");
		});
		return button;
	}

	private void sendMessage(String msg) {
		if(msg == null || msg.equals("")) {
			return;
		}

		// 获得当前时间，并格式化
		String date = dateFormat.format(new Date());

		String info = String.format("#%s#" + "\n" + "您对%s说：%s", date, friendUserName, msg);
		infoLog.append(info).append('\n');
		txtMainInfo.setText(infoLog.toString());

		Map<String, String> message = new HashMap<String, String>();
		message.put("receive_user_id", friendUserId);
		message.put("user_id", userId);
		message.put("user_name", userName);
		message.put("msg", msg);

		JSONObject jsonObj = new JSONObject(message);
		jsonObj.put("command", QQConstant.COMMAND_SENDMSG);

		try {
			InetAddress address = InetAddress.getByName(Client.serverIP);
			/* 发送数据报 */
			byte[] b = jsonObj.toString().getBytes();
			DatagramPacket packet = new DatagramPacket(b, b.length, address, Client.serverPort);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("发送消息失败");
		}

	}

	public void setShow(boolean flag) {

		if (flag && !this.isVisible()) {// [start]
			startChat();			
			int x = (int) (screenWidth - frameWidth) / 2;
			int y = (int) (screenHeight - frameHeight) / 2;
			// 设置Frame位于屏幕中心
			setLocation(x, y);

			infoLog = new StringBuffer();

			byte[] buf = new byte[1024];
			int n = 0;
			try {
				File file = new File(userId + "-" + friendUserId + ".txt");
				if (file.exists()) {
					InputStream is = new FileInputStream(file);
					while (true) {
						n = is.read(buf);
						if (n <= 0)
							break;
						infoLog.append(new String(buf, 0, n));
					}
					txtMainInfo.setText(infoLog.toString());

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//setVisible(flag);

			recvRunnable = new ChatRunnable(this);
			new Thread(recvRunnable, userId + "-" + friendUserId).start();
			
		} // [end]
		else if (flag == false && this.isVisible()) {// [start]
			//注意，线程可能没有立即结束，因为线程中run可能会有等待，导致没有结束。
			//如果没有立即结束，可能会导致新建线程创建的socket 和旧线程中的socket不一致，旧线程中可能会将新线程关闭掉。
			//所以， 对于socket的创建和结束，可以都放到UI线程中去处理。
			//就算放到UI线程中创建，也有可能会有问题。 就是旧线程中还没结束(因为等待)，而又新建了socket,导致旧线程中用到了新的socket，接收了新的信息。 
			//如果能让线程中用到的是拷贝副本就好了，不过socket好像没有clone。
			//不过，socket在关闭的时候，并不是一直等待的，会立即返回。所以也就不存在上述问题：旧线程用了新socket。
			recvRunnable.setRunFlag(false);
			OutputStream os;
			try {
				File file = new File(userId + "-" + friendUserId + ".txt");
				file.createNewFile(); // if file already exists will do nothing
				os = new FileOutputStream(file);
				os.write(infoLog.toString().getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//setVisible(flag);
			endChat();
		} // [end]
		
		setVisible(flag);
	}
	
	public void startChat() {
		// 因为这个对话框并不是完全关闭，只是设为不可见。因此socket放在runnable中创建，每次可见时，新建socket。
		try {
			socket = new DatagramSocket();
			//socket.setSoTimeout(5000); //无需设置，让其一直等待就行。
			// 打开窗口时 发送一次。
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("command", QQConstant.COMMAND_CHAT);
			jsonObj.put("user_id", userId);
			jsonObj.put("receive_user_id", friendUserId);
			byte[] b;
			DatagramPacket packet;
			b = jsonObj.toString().getBytes();
			// 创建DatagramPacket对象
			packet = new DatagramPacket(b, b.length, address, Client.serverPort);
			// 发送
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("startChat err");
			e.printStackTrace();
		}
	}
	public void endChat() {
		// 每次结束时 发送关闭，并关闭socket。
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("command", QQConstant.COMMAND_CHATOUT);
		jsonObj.put("user_id", userId);
		jsonObj.put("receive_user_id", friendUserId);
		byte[] b = jsonObj.toString().getBytes();

		DatagramPacket packet;

		try {
			// 创建DatagramPacket对象
			packet = new DatagramPacket(b, b.length, address, Client.serverPort);
			// 发送
			socket.send(packet);
			System.out.println(socket);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		socket.close(); // 关闭之后该socket就不可用了。 服务端往这个客户端socket发送会接收不到。
		socket = null;
	}
}

class ChatRunnable implements Runnable {

	private ChatFrame chatFrame;
	
	private boolean runFlag;
	
	public ChatFrame getChatFrame() {
		return chatFrame;
	}

	public void setChatFrame(ChatFrame chatFrame) {
		this.chatFrame = chatFrame;
	}

	public boolean isRunFlag() {
		return runFlag;
	}

	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}

	public ChatRunnable(ChatFrame chatFrame) {
		this.chatFrame = chatFrame;
		this.runFlag = true;
	}

	@Override
	public void run() {
		DatagramPacket packet;
		JSONObject jsonObj;
		// 准备一个缓冲区 用于接收
		byte[] buffer = new byte[10240];
		while (runFlag) {
			try {
				/* 接收数据报 */
				packet = new DatagramPacket(buffer, buffer.length, chatFrame.address, Client.serverPort);
				// 开始接收
				chatFrame.socket.receive(packet); //阻塞等待。直到收到包或socket被关闭。
				
				// 接收数据长度
				int len = packet.getLength();
				String str = new String(buffer, 0, len);

				// 打印接收的数据
				System.out.printf("chatFrame 接收的消息：【%s】\n", str);
				jsonObj = new JSONObject(str);

				// 获得当前时间，并格式化
				String date = chatFrame.dateFormat.format(new Date());
				String message = (String) jsonObj.get("msg");

				String info = String.format("#%s#" + "\n" + "%s对您说：%s", date, chatFrame.friendUserName, message);
				chatFrame.infoLog.append(info).append('\n');

				chatFrame.txtMainInfo.setText(chatFrame.infoLog.toString());
				chatFrame.txtMainInfo.setCaretPosition(chatFrame.txtMainInfo.getDocument().getLength());

			} catch (Exception e) {
				System.out.println("run while(isRunning)");
				System.out.println(chatFrame.socket);
				e.printStackTrace();
			}
		}
	}

}
