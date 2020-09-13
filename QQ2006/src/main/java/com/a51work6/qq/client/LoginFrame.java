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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;

public class LoginFrame extends LoginBaseFrame {

	

	public LoginFrame() {

		super();
		
		// 注册窗口事件
		addWindowListener(new WindowAdapter() {
			// 单击窗口关闭按钮时调用
			public void windowClosing(WindowEvent e) {
				// 退出系统
				System.out.println("system out");

				System.exit(0);
			}
		});
		
		//setDefaultCloseOperation(EXIT_ON_CLOSE); //一样效果
		
		// 添加蓝线面板
		getContentPane().add(getPaneLine());

		// 按钮面板
		getContentPane().add(getButtonPanel());

	}

	// 客户端向服务器发送登录请求
	public Map<String, Object> login(String userId, String password) {
		InetAddress address;

		try {
			address = InetAddress.getByName(Client.serverIP);

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("command", QQConstant.COMMAND_LOGIN);
			jsonObj.put("user_id", userId);
			jsonObj.put("user_pwd", password);
			// 字节数组
			byte[] b = jsonObj.toString().getBytes();
			// 创建DatagramPacket对象
			DatagramPacket packet = new DatagramPacket(b, b.length, address, Client.serverPort);
			// 发送
			Client.socket.send(packet);

			//注意，刚开是登录 接收到的消息可能会很大。 会包含离线时的各种消息。
			/* 接收数据报 */
			packet = new DatagramPacket(Client.buffer, Client.buffer.length, address, Client.serverPort);
			Client.socket.receive(packet);
			// 接收数据长度
			int len = packet.getLength();
			String str = new String(Client.buffer, 0, len);
			System.out.println("receivedjsonObj = " + str);
			JSONObject receivedjsonObj = new JSONObject(str);

			if ((Integer) receivedjsonObj.get("result") == 0) {
				Map<String,Object> user = receivedjsonObj.toMap();
				return user;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 蓝线面板
	private JPanel getPaneLine() {

		JPanel paneLine = new JPanel();
		paneLine.setLayout(null);
		paneLine.setBounds(7, 54, 308, 118);//无视父容器的layout，直接定位。而且是相对于父层容器的。
		// 边框颜色设置为蓝色
		paneLine.setBorder(BorderFactory.createLineBorder(new Color(102, 153, 255), 1));

		// 初始化【忘记密码？】标签
		JLabel lblHelp = new JLabel();
		lblHelp.setBounds(227, 47, 67, 21);
		lblHelp.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblHelp.setForeground(new Color(51, 51, 255));
		lblHelp.setText("忘记密码？");
		paneLine.add(lblHelp);

		// 初始化【QQ密码】标签
		JLabel lblUserPwd = new JLabel();
		lblUserPwd.setText("QQ密码");
		lblUserPwd.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblUserPwd.setBounds(21, 48, 54, 18);
		paneLine.add(lblUserPwd);

		// 初始化【QQ号码↓】标签
		JLabel lblUserId = new JLabel();
		lblUserId.setText("QQ号码↓");
		lblUserId.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblUserId.setBounds(21, 14, 55, 18);
		paneLine.add(lblUserId);

		// 初始化【QQ号码】文本框

		this.txtUserId = new JTextField();
		this.txtUserId.setBounds(84, 14, 132, 18);
		paneLine.add(this.txtUserId);

		// 初始化【QQ密码】密码框
		this.txtUserPwd = new JPasswordField();
		this.txtUserPwd.setBounds(84, 48, 132, 18);
		paneLine.add(this.txtUserPwd);

		// 初始化【自动登录】复选框
		JCheckBox chbAutoLogin = new JCheckBox();
		chbAutoLogin.setText("自动登录");
		chbAutoLogin.setFont(new Font("Dialog", Font.PLAIN, 12));
		chbAutoLogin.setBounds(79, 77, 73, 19);
		paneLine.add(chbAutoLogin);

		// 初始化【隐身登录】复选框
		JCheckBox chbHideLogin = new JCheckBox();
		chbHideLogin.setText("隐身登录");
		chbHideLogin.setFont(new Font("Dialog", Font.PLAIN, 12));
		chbHideLogin.setBounds(155, 77, 73, 19);
		paneLine.add(chbHideLogin);

		return paneLine;
	}

	//按钮面板
	private JPanel getButtonPanel() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setBounds(7, 178, 308, 50);
		
		
		// 初始化【申请号码↓】按钮
		JButton btnSetup = new JButton();
		btnSetup.setBounds(14, 6, 99, 22);
		btnSetup.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnSetup.setText("申请号码↓");
		buttonPanel.add(btnSetup);
		btnSetup.addActionListener(e->{
			this.setVisible(false);
			new RegisterFrame(this).setVisible(true);;
		});
		
		
		// 初始化登录按钮
		JButton btnLogin = new JButton();
		btnLogin.setBounds(152, 6, 63, 19);
		btnLogin.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnLogin.setText("登录");
		buttonPanel.add(btnLogin);
		// 注册登录按钮事件监听器
		btnLogin.addActionListener(e -> {

			// 先进行用户输入验证，验证通过再登录
			String userId = txtUserId.getText();
			String password = new String(txtUserPwd.getPassword());

			Map user = null;
			// 防止UI卡死，耗时工作(如网络操作等)应该在工作线程中处理。
			// 如果需要等待工作线程，那么UI应该进行loading，等待。 等工作线程返回后解除loading。
			// 注意这时应该要有时间限制，不然也会一直卡在loading。（这个其实和放在UI线程中差不多，只是这个防止了UI卡死）
			// 放在工作线程中，是为了防止UI卡死。 如果有超时机制，放在UI线程中，UI最后也能反应，只是会期间是卡死的。 而放在工作线程中，
			// UI不是卡死的，可以响应。
			// 如果UI要同步耗时的工作，即也要等待耗时工作结束后进行下一步，那么放在UI线程中和放在工作线程中有什么区别？ 主要的意义是防止卡死。
			// 其次 如果UI是阻塞等待工作线程的话，一定要最长时间限制，不能无限制等待工作线程，工作线程最好要有超时操作，不然UI一直在等待 (虽然不是卡死的)。

			// 这里是小型程序，而且有了超时机制，UI不会一直卡死， 所以直接放在UI线程中也算可以。
			user = login(userId, password);

			if (user != null) {
				// 登录成功调转界面
				System.out.println("登录成功调转界面");
				// 设置登录窗口可见
				this.setVisible(false);
				FriendsFrame frame = new FriendsFrame(user);
				frame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "您QQ号码或密码不正确");
			}

		});

		// 初始化取消按钮
		JButton btnCancel = new JButton();
		btnCancel.setBounds(233, 6, 63, 19);
		btnCancel.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnCancel.setText("取消");
		buttonPanel.add(btnCancel);
		btnCancel.addActionListener(e -> {
			// 退出系统
			System.exit(0);
		});

	
		
		return buttonPanel;

	}

}
