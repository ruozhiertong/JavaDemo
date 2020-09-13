package com.a51work6.qq.client;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2020年9月9日 上午11:08:21 类说明
 */
public class LoginBaseFrame extends JFrame {

	// private Client client;
	// 获得当前屏幕的宽和高
	public double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	// 登录窗口宽和高
	public int frameWidth = 329;
	public int frameHeight = 250;

	// QQ号码文本框
	public JTextField txtUserId = null;
	// QQ密码框
	public JPasswordField txtUserPwd = null;

	public LoginBaseFrame() {
		JLabel lblImage = new JLabel();
		lblImage.setIcon(new ImageIcon(LoginFrame.class.getResource("/resource/img/QQll.JPG")));
		lblImage.setBounds(0, 0, 325, 48);
		getContentPane().add(lblImage);

		/// 初始化当前窗口
		setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource("/resource/img/QQ.png")));
		setTitle("QQ登录");
		setResizable(false);
		getContentPane().setLayout(null);
		// 设置窗口大小
		setSize(frameWidth, frameHeight);
		// 计算窗口位于屏幕中心的坐标
		int x = (int) (screenWidth - frameWidth) / 2;
		int y = (int) (screenHeight - frameHeight) / 2;
		// 设置窗口位于屏幕中心
		setLocation(x, y);


	}

}
