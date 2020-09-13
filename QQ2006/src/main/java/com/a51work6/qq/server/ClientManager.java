package com.a51work6.qq.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月13日 下午3:48:13
* 类说明
* 用于维护客户端用户的所有信息
*/
public class ClientManager {
	private ClientInfo userInfo;
	private List<String> friendList;//好友列表.
	private List<ClientInfo> chatUser; //聊天的用户
	
	public ClientManager(ClientInfo userInfo) {
		this.userInfo = userInfo;
		
		
	}
	
	

}
