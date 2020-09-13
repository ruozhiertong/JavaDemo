package com.a51work6.qq.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;
import com.lxr.util.SocketUtil;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2020年9月9日 上午10:35:07 类说明
 */
public class AddFriendFrame extends JFrame {
	

	private JTextField txtFriendId;
	private JTextField txtMsg;	
	
	private String userId;
	private String userName;

	public AddFriendFrame(String userId, String userName) {
		
		this.userId = userId;
		this.userName = userName;
		
		double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		int frameWidth = 329;
		int frameHeight = 250;
		
		/// 初始化当前窗口
		setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource("/resource/img/QQ.png")));
		setTitle("添加好友");
		setResizable(false);
		getContentPane().setLayout(null);
		// 设置窗口大小
		setSize(frameWidth, frameHeight);
		// 计算窗口位于屏幕中心的坐标
		int x = (int) (screenWidth - frameWidth) / 2;
		int y = (int) (screenHeight - frameHeight) / 2;
		// 设置窗口位于屏幕中心
		setLocation(x, y);
		
		addWindowListener(new WindowAdapter() {
			// 单击窗口关闭按钮时调用
			public void windowClosing(WindowEvent e) {
				System.out.println("add friend out");
				dispose();
			}
		});

		getContentPane().add(getPaneLine());
		getContentPane().add(getButtonPanel());

		this.setVisible(true);
	}

	// 蓝线面板
	private JPanel getPaneLine() {

		JPanel paneLine = new JPanel();
		paneLine.setLayout(null);
		paneLine.setBounds(7, 54, 308, 118);// 无视父容器的layout，直接定位。而且是相对于父层容器的。
		// 边框颜色设置为蓝色
		paneLine.setBorder(BorderFactory.createLineBorder(new Color(102, 153, 255), 1));

		// 初始化【QQ号码↓】标签
		JLabel lblUserId = new JLabel();
		lblUserId.setText("QQ号码↓");
		lblUserId.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblUserId.setBounds(21, 8, 55, 18);
		paneLine.add(lblUserId);

		// 初始化【QQ号码】文本框
		this.txtFriendId = new JTextField();
		this.txtFriendId.setBounds(84, 8, 132, 18);
		paneLine.add(this.txtFriendId);
		
		// 初始化【附加消息】标签
		JLabel lgMsg = new JLabel();
		lgMsg.setText("附加消息");
		lgMsg.setFont(new Font("Dialog", Font.PLAIN, 12));
		lgMsg.setBounds(21, 38, 54, 18);
		paneLine.add(lgMsg);

		// 初始化【附加消息】文本框
		this.txtMsg = new JTextField();
		this.txtMsg.setBounds(84, 38, 132, 18);
		paneLine.add(this.txtMsg);

		return paneLine;
	}

	// 按钮面板
	private JPanel getButtonPanel() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setBounds(7, 178, 308, 50);

		// 初始化登录按钮
		JButton btnLogin = new JButton();
		btnLogin.setBounds(152, 6, 63, 19);
		btnLogin.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnLogin.setText("发送");
		buttonPanel.add(btnLogin);
		// 注册登录按钮事件监听器
		btnLogin.addActionListener(e -> {

			String friendId = this.txtFriendId.getText();
			String msg = this.txtMsg.getText();

			if(userId.equals(""))
				JOptionPane.showMessageDialog(null, "请检查用户名或密码");
			else
				addFriend(friendId, msg);
			//towait

		});

		// 初始化取消按钮
		JButton btnCancel = new JButton();
		btnCancel.setBounds(233, 6, 63, 19);
		btnCancel.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnCancel.setText("取消");
		buttonPanel.add(btnCancel);
		btnCancel.addActionListener(e -> {
			dispose();

		});
		return buttonPanel;

	}

	public void addFriend(String friendId, String msg) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("command", QQConstant.COMMAND_ADDFRI);
				jsonObj.put("user_id", userId);
				jsonObj.put("user_name", userName);
				jsonObj.put("friend_id", friendId);
				jsonObj.put("msg", msg);
				
				DatagramSocket ds = SocketUtil.UDPSend(QQConstant.SERVER_IP, QQConstant.SERVER_PORT, jsonObj.toString());
				if(ds == null) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, "发送失败");
						}
					});
					
					return ;
				}
				
				
				String recvStr = SocketUtil.UDPReceive(ds);
				
				if(recvStr == null) {
					SwingUtilities.invokeLater(()->{
						JOptionPane.showMessageDialog(null, "网络异常");
					});
					return;
				}
				JSONObject recvObj = new JSONObject(recvStr);
				int ret = (int)recvObj.get("result");
				if(ret == -1) {
					
					return;
				}
				
				SwingUtilities.invokeLater(()->{
					JOptionPane.showMessageDialog(null,"已发送", "发送", JOptionPane.PLAIN_MESSAGE);
					dispose();
				});
			}
		}).start();
	}

}
