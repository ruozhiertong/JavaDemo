package com.a51work6.qq.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2020年9月9日 上午10:35:07 类说明
 */
public class RegisterFrame extends LoginBaseFrame {

	// 密码确认
	private JPasswordField txtPwdConfirm = null;
	private JTextField txtUserName;

	private LoginFrame loginFrame;

	public RegisterFrame(LoginFrame loginFrame) {

		super();

		this.loginFrame = loginFrame;
		// 注册窗口事件. 无法覆盖,添加到事件链中。先添加的先执行，所以会先执行父类的。所以将父类的移除，让子类自己去做。
		super.addWindowListener(new WindowAdapter() {
			// 单击窗口关闭按钮时调用
			public void windowClosing(WindowEvent e) {
				System.out.println("register out");
				closeFrame();
			}
		});

		getContentPane().add(getPaneLine());
		getContentPane().add(getButtonPanel());

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
		this.txtUserId = new JTextField();
		this.txtUserId.setBounds(84, 8, 132, 18);
		paneLine.add(this.txtUserId);
		
		// 初始化【用户名】标签
		JLabel lblUserName = new JLabel();
		lblUserName.setText("QQ用户名");
		lblUserName.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblUserName.setBounds(21, 38, 54, 18);
		paneLine.add(lblUserName);

		// 初始化【用户名】文本框
		this.txtUserName = new JTextField();
		this.txtUserName.setBounds(84, 38, 132, 18);
		paneLine.add(this.txtUserName);

		// 初始化【QQ密码】标签
		JLabel lblUserPwd = new JLabel();
		lblUserPwd.setText("QQ密码");
		lblUserPwd.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblUserPwd.setBounds(21, 68, 54, 18);
		paneLine.add(lblUserPwd);

		// 初始化【QQ密码】密码框
		this.txtUserPwd = new JPasswordField();
		this.txtUserPwd.setBounds(84, 68, 132, 18);
		paneLine.add(this.txtUserPwd);

		// 初始化【确认密码】标签
		JLabel lblPwdConfirm = new JLabel();
		lblPwdConfirm.setText("确认密码");
		lblPwdConfirm.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblPwdConfirm.setBounds(21, 98, 54, 18);
		paneLine.add(lblPwdConfirm);

		// 初始化【确认密码】密码框
		this.txtPwdConfirm = new JPasswordField();
		this.txtPwdConfirm.setBounds(84, 98, 132, 18);
		paneLine.add(this.txtPwdConfirm);
		
		

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
		btnLogin.setText("注册");
		buttonPanel.add(btnLogin);
		// 注册登录按钮事件监听器
		btnLogin.addActionListener(e -> {

			String userId = this.txtUserId.getText();
			String userName = this.txtUserName.getText();
			String userPwd = new String(this.txtUserPwd.getPassword());
			String passConfirm = new String(this.txtPwdConfirm.getPassword());

			if(userId.equals("") ||userName.equals("") ||userPwd.equals("")||!userPwd.equals(passConfirm))
				JOptionPane.showMessageDialog(null, "请检查用户名或密码");
			else
				register(userId, userPwd,userName);
			//towait

		});

		// 初始化取消按钮
		JButton btnCancel = new JButton();
		btnCancel.setBounds(233, 6, 63, 19);
		btnCancel.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnCancel.setText("取消");
		buttonPanel.add(btnCancel);
		btnCancel.addActionListener(e -> {
			closeFrame();

		});
		return buttonPanel;

	}

	public void register(String userId, String pass,String userName) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				InetAddress address;

				try {
					address = InetAddress.getByName(Client.serverIP);

					JSONObject jsonObj = new JSONObject();
					jsonObj.put("command", QQConstant.COMMAND_REGISTER);
					jsonObj.put("user_id", userId);
					jsonObj.put("user_pwd", pass);
					jsonObj.put("user_name", userName);
					// 字节数组
					byte[] b = jsonObj.toString().getBytes();
					// 创建DatagramPacket对象
					DatagramPacket packet = new DatagramPacket(b, b.length, address, Client.serverPort);
					// 发送
					Client.socket.send(packet);

					/* 接收数据报 */
					packet = new DatagramPacket(Client.buffer, Client.buffer.length, address, Client.serverPort);
					Client.socket.receive(packet);
					// 接收数据长度
					int len = packet.getLength();
					String str = new String(Client.buffer, 0, len);
					System.out.println("receivedjsonObj = " + str);
					JSONObject receivedjsonObj = new JSONObject(str);
					if ((Integer) receivedjsonObj.get("result") == 0) {
						JOptionPane.showMessageDialog(null,"注册成功，跳转登录界面", "注册成功", JOptionPane.PLAIN_MESSAGE);
						closeFrame();
					}
					else
					{
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, "注册失败");
							}
						});
					}

				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.out.println("主机名异常");

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, "主机名异常");

						}
					});

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("socket 异常");
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, "socket 异常 连接超时");

						}
					});
				}
			}
		}).start();
	}
	
	public void closeFrame() {
		loginFrame.setVisible(true);
		dispose();
	}

}
