package com.a51work6.qq.client;

import java.awt.Button;
import java.awt.Toolkit;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;
import com.lxr.util.SocketUtil;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月3日 下午9:29:47
* 类说明: 系统消息弹窗。比如用户上线，弹窗提示。
*/
//public class MessageFrame extends JFrame implements Runnable{
public class MessageFrame extends JFrame{

	private String msg;
	private int frameType; // 消息提示0， 带操作1 确定取消。
	
	private String userId;
	private String friendId;
	
	private double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private int frameWidth = 250;
	private int frameHeight = 250;
	
	
	public MessageFrame(String msg, int type, String userId, String friendId) {
		setTitle("系统消息");
		// 初始化成员变量
		this.msg = msg;
		this.frameType = type;
		this.userId = userId;
		this.friendId = friendId;
		
		setResizable(false);
		//getContentPane().setLayout(null); //如果设置为null，那么容器中的组件的位置都要自己去设置，否则不知道位置在哪。

		// 设置Frame大小
		setSize(frameWidth, frameHeight);
		// 计算Frame位于屏幕中心的坐标
		int x = (int) (screenWidth - frameWidth);
		int y = (int) (screenHeight - frameHeight);
		// 设置Frame位于屏幕中心
		setLocation(x, y);
		
		JPanel panel = new JPanel();
		//panel.setBounds(0, 0, 200, 200);
		//panel.setLayout(null);
		JTextField tf = new JTextField(this.msg);
		//tf.setBounds(0, 0, 150, 150);
		panel.add(tf);
		if(frameType == 1) {
			JButton btnOk = new JButton("同意");
			btnOk.addActionListener((e)->{
				sendResponse(0);
			});
			
			JButton btnCancel = new JButton("拒绝");
			btnCancel.addActionListener((e)->{
				sendResponse(-1);
			});
			
			panel.add(btnOk);
			panel.add(btnCancel);

			
		}
		
		getContentPane().add(panel);
		
		this.setVisible(true);
		
		if(frameType == 0) {
			//定时器。 5s后自动关闭窗体
			Timer timer = new Timer();
			timer.schedule(new MyTask(this),5000); //5s后执行
			
			//new Thread(this).start();	
		}

	}


//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		while(true)
//			System.out.println("i am messageframe");
//	}
	
	
	
	public void sendResponse(int response) {
		
		new Thread(()->{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("command", QQConstant.COMMAND_BACKFRI);
			jsonObj.put("user_id", userId);
			jsonObj.put("friend_id", friendId);
			jsonObj.put("response",response);
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
			
		}).start();
		
	}
}

class MyTask extends TimerTask{

	private JFrame frame;
	public MyTask(JFrame frame) {
		this.frame = frame;
	}
	@Override
	public void run() {
		this.frame.dispose();
		cancel(); //要主动退出timer 负责线程会一直在
	}
	
}
