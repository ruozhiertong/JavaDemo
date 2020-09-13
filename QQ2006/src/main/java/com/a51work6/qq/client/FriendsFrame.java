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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;
import com.lxr.util.DownCallBack;
import com.lxr.util.ImgUtil;

public class FriendsFrame extends JFrame implements Runnable {

	// 线程运行状态
	//private boolean isRunning = true;
	// 用户信息
	public Map user;
	// 好友列表
	private List<Map<String, String>> friends;
	
	private Map<String, String> friendIdName = new HashMap<String, String>(); //id 对应的name。
	// 好友标签控件列表
	private List<JLabel> lblFriendList;
	
	//聊天对话框。
	private Map<String,ChatFrame> chatFrames = new HashMap<String , ChatFrame>();

	// 获得当前屏幕的宽
	private double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

	// 登录窗口宽和高
	private int frameWidth = 260;
	private int frameHeight = 600;

	public FriendsFrame(Map user) {
		setTitle("QQ2006");

		// 初始化成员变量
		this.user = user;
		
		
		/// 初始化用户列表
		this.friends = (List<Map<String, String>>) user.get("friends");

		// 设置布局
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);

		String userId = (String) user.get("user_id");
		String userName = (String) user.get("user_name");
		String userIcon = (String) user.get("user_icon");

		JLabel iconLable = new JLabel(userName);
		//iconLable.setHorizontalAlignment(SwingConstants.CENTER);
		String iconFile = String.format("/resource/img/%s.jpg", userIcon);
		String path = FriendsFrame.class.getResource("/").getPath() + iconFile.substring(1);
		File f = new File(path);
		if(!f.exists()) {
			ImgUtil.DownImg(iconFile, new DownCallBack() {
				
				@Override
				public void callBack(int ret) {
					System.out.println("callBack" + ret);
					if(ret == 0) {
						SwingUtilities.invokeLater(()->{
							iconLable.setIcon(new ImageIcon(FriendsFrame.class.getResource(iconFile)));
						});
					}	
				}
			});
		}
		else
			iconLable.setIcon(new ImageIcon(FriendsFrame.class.getResource(iconFile)));
		JPanel headPanel = new JPanel();
		
		JButton addFriend = new JButton("添加好友");
		addFriend.addActionListener(e->{
			new AddFriendFrame(userId, userName);
		});
		JButton userInfo  = new JButton("编辑");
		headPanel.add(iconLable);
		headPanel.add(userInfo);
		headPanel.add(addFriend);
		
		
		getContentPane().add(headPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.blue, 1));

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel listPanel = new JPanel();
		scrollPane.setViewportView(listPanel);
		listPanel.setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel("我的好友");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		listPanel.add(label, BorderLayout.NORTH);

		// 好友列表面板
		JPanel friendListPanel = new JPanel();
		listPanel.add(friendListPanel);
		friendListPanel.setLayout(new GridLayout(10, 0, 0, 5)); //固定行数。其实可以在refresh时动态变化行数。

		lblFriendList = new ArrayList<JLabel>();
		// 初始化好友列表
		for (int i = 0; i < friends.size(); i++) {
			Map<String, String> friend = this.friends.get(i);
			String friendUserId = friend.get("user_id");
			String friendUserName = friend.get("user_name");
			String friendUserIcon = friend.get("user_icon");
			// 获得好友在线状态
			String friendUserOnline = friend.get("online");

			friendIdName.put(friendUserId, friendUserName);
			
			JLabel lblFriend = new JLabel(friendUserName);
			lblFriend.setToolTipText(friendUserId);
			String friendIconFile = String.format("/resource/img/%s.jpg", friendUserIcon);
			path = FriendsFrame.class.getResource("/").getPath() + friendIconFile.substring(1);
			f = new File(path);
			if(!f.exists()) {
				ImgUtil.DownImg(friendIconFile, new DownCallBack() {
					
					@Override
					public void callBack(int ret) {
						System.out.println("callBack" + ret);
						if(ret == 0) {
							SwingUtilities.invokeLater(()->{
								lblFriend.setIcon(new ImageIcon(FriendsFrame.class.getClass().getResource(friendIconFile)));
							});
						}	
					}
				});
			}
			else
				lblFriend.setIcon(new ImageIcon(FriendsFrame.class.getClass().getResource(friendIconFile)));
			
			//lblFriend.setIcon(new ImageIcon(FriendsFrame.class.getClass().getResource(friendIconFile)));
			// 在线设置可用，离线设置不可用
			if (friendUserOnline.equals("0")) {
				lblFriend.setEnabled(false);
			} else {
				lblFriend.setEnabled(true);
			}

			lblFriend.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// 用户图标双击鼠标时显示对话框. 如果窗体存在不应该再弹新的窗体，而是应该show出来。
					if (e.getClickCount() == 2) {
						if(chatFrames.get(friendUserId) == null) {
							ChatFrame chatFrame = new ChatFrame(FriendsFrame.this, user, friend);
							//chatFrame.setVisible(true);
							chatFrame.setShow(true);
							chatFrames.put(friendUserId, chatFrame);
						}
						else{
							chatFrames.get(friendUserId).setShow(true);
						}
						
						//isRunning = false;
					}
				}
			});
			// 添加到列表集合
			lblFriendList.add(lblFriend);
			// 添加到面板
			friendListPanel.add(lblFriend);
		}
		
		//取出离线消息。
		List<Map<String,String>> offmsgs = (List<Map<String, String>>) user.get("offMsgs");
		if(offmsgs != null) {
			for(Map<String, String> msg : offmsgs) {
				try {
					File file = new File(user.get("user_id") +"-" + msg.get("send_id")+".txt");
					file.createNewFile(); // if file already exists will do nothing 
					String info = String.format("#%s#" + "\n" + "%s对您说：%s\n", msg.get("upd_date"),friendIdName.get(msg.get("send_id")),msg.get("msg"));
					OutputStream os =new FileOutputStream(file, true);
					os.write(info.getBytes());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}			
		}
		
		// 取出离线消息。
		List<Map<String, String>> addUsers = (List<Map<String, String>>) user.get("adduser");
		if (offmsgs != null) {
			for (Map<String, String> addUser : addUsers) {
				new MessageFrame(addUser.get("msg"), 1, addUser.get("friend_id"), addUser.get("user_id"));
			}
		}
		

		/// 初始化当前Frame
		setBounds((int) screenWidth - 300, 10, frameWidth, frameHeight);
		setIconImage(Toolkit.getDefaultToolkit().getImage(FriendsFrame.class.getResource("/resource/img/QQ.png")));

		// 注册窗口事件
		addWindowListener(new WindowAdapter() {
			// 单击窗口关闭按钮时调用
			public void windowClosing(WindowEvent e) {
                // 当前用户下线
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("command", QQConstant.COMMAND_LOGOUT);
				jsonObj.put("user_id", userId);
				byte[] b = jsonObj.toString().getBytes();

				InetAddress address;
				try {
					address = InetAddress.getByName(Client.serverIP);
					// 创建DatagramPacket对象
					DatagramPacket packet = new DatagramPacket(b, b.length, address, Client.serverPort);
					// 发送
					Client.socket.send(packet);
				} catch (IOException e1) {
				}

				// 退出系统
				System.exit(0);
			}
		});

		// 启动接收消息子线程
		resetThread();
	}

	//这个线程用于接收系统消息等。 只会接收到 login logout sendMsg消息。
	@Override
	public void run() {
	
		while (true) {//isRunning

			try {
				InetAddress address = InetAddress.getByName(Client.serverIP);
				/* 接收数据报 */
				DatagramPacket packet = new DatagramPacket(Client.buffer, Client.buffer.length, address, Client.serverPort);
				// 开始接收
				Client.socket.receive(packet);
				// 接收数据长度
				int len = packet.getLength();
				String str = new String(Client.buffer, 0, len);

				System.out.println("客户端 系统消息：  " + str);

				JSONObject jsonObj = new JSONObject(str);
				String userId = (String) jsonObj.get("user_id");
				int command = jsonObj.getInt("command");
				
				if(command == QQConstant.COMMAND_SENDMSG) {
					if(chatFrames.get(userId) == null || !chatFrames.get(userId).isVisible()){
						String msg =(String) jsonObj.get("user_name") +  (String)jsonObj.get("msg");					
						new MessageFrame(msg,0,null,null);
						
						//同时保存未读消息。 在打开对话框时显示。
						File file = new File(user.get("user_id") +"-" + userId+".txt");
						file.createNewFile(); // if file already exists will do nothing 
						
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String date = dateFormat.format(new Date());
						String info = String.format("#%s#" + "\n" + "%s对您说：%s\n", date,jsonObj.get("user_name"),jsonObj.get("msg"));
						OutputStream os =new FileOutputStream(file, true);
						os.write(info.getBytes());
					}
				}else if(command == QQConstant.COMMAND_LOGIN || command == QQConstant.COMMAND_LOGOUT){
					String online = (String) jsonObj.get("online");
					// 刷新好友列表
					refreshFriendList(userId, online);
					
					String msg =(String) jsonObj.get("user_name") +  (String)jsonObj.get("msg");					
					new MessageFrame(msg,0,null,null);
				} else if(command == QQConstant.COMMAND_ADDFRI) {
					String friendId = (String)jsonObj.get("user_id");
					String msg = "好友请求：" + jsonObj.getString("user_name") + " " + jsonObj.get("msg") ; 
					new MessageFrame(msg,1,(String)user.get("user_id"),friendId);
				}

			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	// 刷新好友列表
	public void refreshFriendList(String userId, String online) {
		// 初始化好友列表
		for (JLabel lblFriend : lblFriendList) {
			// 判断用户Id是否一致
			if (userId.equals(lblFriend.getToolTipText())) {
				if (online.equals("1")) {
					lblFriend.setEnabled(true);
				} else {
					lblFriend.setEnabled(false);
				}
			}
		}
	}

	// 重新启动接收消息子线程
	public void resetThread() {
		//isRunning = true;
		// 接收消息子线程
		Thread receiveMessageThread = new Thread(this,(String)user.get("user_id"));
		// 启动接收消息线程
		receiveMessageThread.start();
	}
}
