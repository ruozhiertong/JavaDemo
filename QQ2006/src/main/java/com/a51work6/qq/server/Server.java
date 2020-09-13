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

package com.a51work6.qq.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.a51work6.qq.constant.QQConstant;
import com.lxr.util.ImgUtil;

public class Server {

	private DatagramSocket socket;
	private byte[] buffer = new byte[20480]; //接收的buffer。

	// 所有已经登录的客户端信息. 客户端会有多个socket，所以用List去保存。而且List中第一个一般是客户端维持系统消息的socket。
	// 所以维护所有的客户端socket。
	//而且，如果是同一个对话的用户 只保留最新的socket，只有新的是有效的。
	//（只维护一个用户的聊天socket使得只能在单个用户使用。如果一个用户在多处同时登录就没办法，最新的才有效。）
	//（如果要支持多处登录，那么就要维护用户的ip 端口来确定用户。而且聊天的时候 是发送到多处登录的用户，而不是单单某一处的用户。）
	//（如果支持多处登录，还要维护socket的状态，要判断socket是否还有效／客户端没有正常退出聊天，就要删除该socket。单用户形式可以不用处理，因为就算出问题，也就一个socket无效，只要重新登录就覆盖了。而多用户如果不处理，会越来越多socket无效）
	//虽然利用客户端的ip+端口确定唯一的客户，但是如果客户端没有关闭就断掉，会导致服务端还一直维护着无效的客户端socket。
	//所以综上，反正只有一个有效的客户端，因此完全可以只维护一个客户端信息就好。
	//而且，如果要进行聊天，那发起聊天的用户 也要知道 要聊天用户的ip和端口，这显然也是不合适,不可行的。
	//private Map<String, List<ClientInfo>> clientMap = new HashMap<String, List<ClientInfo>>();
	//private Map<String, HashSet<ClientInfo>> clientMap = new HashMap<String, HashSet<ClientInfo>>();
	//因为HashSet无法获取第几个元素，所以采用LinkedHashSet。
	//有利有弊。 List 可以下标访问方便，但是有重复。 Set无重复，但是不可下标访问。
	private Map<String, LinkedHashSet<ClientInfo>> clientMap = new HashMap<String, LinkedHashSet<ClientInfo>>();

	// private static List<ClientInfo> clientList = new
	// CopyOnWriteArrayList<ClientInfo>();
	// 创建数据访问对象
	private UserDAO dao = new UserDAO();
	
	public static void main(String[] args) {
		int serverPort = QQConstant.SERVER_PORT;
		if (args.length == 1) {
			serverPort = Integer.parseInt(args[0]);
		}
		new Server().startServer(serverPort);
	}
	
	public Object getSetByIdx(LinkedHashSet set ,int idx) {
		if(set == null)
			return null;
		Iterator it = set.iterator();
		int i= 0;
		while(it.hasNext()) {
			Object c = it.next();
			if(i == idx)
				return c;
			i++;
		}
		return null;
	}
	
	public void startServer(int serverPort) {
		System.out.printf("服务器启动, 监听自己的端口%d...\n", serverPort);
		// 创建DatagramSocket对象，监听自己的端口7788
		try {
			socket = new DatagramSocket(serverPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 接收数据长度
			int len = packet.getLength();
			String recStr = new String(buffer, 0, len);
			// 从客户端传来的数据包中得到客户端地址
			InetAddress clientAddress = packet.getAddress();
			// 从客户端传来的数据包中得到客户端端口号
			int clientPort = packet.getPort();

			JSONObject recvJsonObj = new JSONObject(recStr);
			System.out.println("recv:" + recvJsonObj);

			int cmd = (int) recvJsonObj.get("command");
			
			if (cmd == QQConstant.COMMAND_LOGIN) {// 用户登录过程
				processLogin(recvJsonObj, clientAddress, clientPort);
			} else if (cmd == QQConstant.COMMAND_LOGOUT) {// 用户发送注销命令
				processLogout(recvJsonObj);
			} else if(cmd == QQConstant.COMMAND_CHAT) {
				processChat(recvJsonObj, clientAddress, clientPort);
			} else if(cmd == QQConstant.COMMAND_CHATOUT) {
				processChatOut(recvJsonObj);
			} else if(cmd == QQConstant.COMMAND_SENDMSG) {
				processSendMsg(recvJsonObj);
			}else if(cmd == QQConstant.COMMAND_REGISTER) {
				processRegister(recvJsonObj, clientAddress, clientPort);
			}else if(cmd == QQConstant.COMMAND_DOWNIMG) {
				processDownImg(recvJsonObj, clientAddress, clientPort);		
			}else if(cmd == QQConstant.COMMAND_ADDFRI) {
				processAddFriend(recvJsonObj, clientAddress, clientPort);
			}else if(cmd == QQConstant.COMMAND_BACKFRI) {
				processBackFriend(recvJsonObj, clientAddress, clientPort);
			}
		}
	}
		


/*
	// 获得用户在线状态
	private List<Map<String, String>> getUserOnlineStateList() {
		// 从数据库查询所有用户信息
		List<Map<String, String>> userList = dao.findAll();
		// 保存用户在线状态集合
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (Map<String, String> user : userList) {

			String userId = user.get("user_id");
			Map<String, String> map = new HashMap<String, String>();
			map.put("user_id", userId);
			// 默认离线
			map.put("online", "0");

			for (ClientInfo info : clientList) {
				// 如果clientList（已经登录的客户端信息）中有该用户，则该用户在线
				if (info.getUserId().equals(userId)) {
					// 设置为在线
					map.put("online", "1");
					break;
				}
			}
			list.add(map);
		}
		return list;
	}
	
*/
	
	private void broadcastMsg(String msg,String online,String userId, String userName,List<Map<String, String>> friends) {
		for (Map.Entry<String, LinkedHashSet<ClientInfo>> entry : clientMap.entrySet()) {
			//用户socket列表中的第一个。
			ClientInfo info = (ClientInfo)getSetByIdx(entry.getValue(),0);	
			// 给其他好友发送，当前用户上线消息
			if (!info.getUserId().equals(userId)) {
				//只给与之好友广播，而不是所有用户。
				boolean isFriend = false;
				for(Map<String,String>friend:friends) {
					if(friend.get("user_id").equals(info.getUserId())) {
						isFriend = true;
						break;
					}
				}
				if(!isFriend)
					continue;
				
				
				System.out.println("broadcastMsg,send:" + msg + "to "+ info.getUserId());

				JSONObject jsonObj = new JSONObject();
				if(online.equals("1"))
					jsonObj.put("command", QQConstant.COMMAND_LOGIN);
				else
					jsonObj.put("command", QQConstant.COMMAND_LOGOUT);

				jsonObj.put("user_id", userId);
				jsonObj.put("user_name", userName);
				jsonObj.put("online", online);
				jsonObj.put("msg", msg);

				byte[] b2 = jsonObj.toString().getBytes();
				DatagramPacket packet = new DatagramPacket(b2, b2.length, info.getAddress(), info.getPort());
				// 转发给好友
				try {
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	}

	
	private void processLogin(JSONObject recvObj, InetAddress clientAddress, int clientPort) {
		DatagramPacket packet;
		// 通过用户Id查询用户信息
		String userId = (String) recvObj.get("user_id");
		Map<String, String> user = dao.findById(userId);

		// 判断客户端发送过来的密码与数据库的密码是否一致
		if (user != null && recvObj.get("user_pwd").equals(user.get("user_pwd"))) {
			JSONObject sendJsonObj = new JSONObject(user);
			sendJsonObj.put("command", QQConstant.COMMAND_LOGIN);
			// 添加result:0键值对，0表示成功，-1表示失败
			sendJsonObj.put("result", 0);
			//sendJsonObj.put("user_name", user_name);

			ClientInfo cInfo = new ClientInfo(userId, null);
			cInfo.setUserName(user.get("user_name"));
			cInfo.setAddress(clientAddress);
			cInfo.setPort(clientPort);
			cInfo.setFriendId("-111");
			//if (null == clientMap.get(userId))
			clientMap.put(userId, new LinkedHashSet<ClientInfo>());//覆盖掉。也能避免客户端没有正常下线，旧的信息一直在服务端维持着。重新上线覆盖掉。
			clientMap.get(userId).add(cInfo);

			// 取出好友用户列表
			List<Map<String, String>> friends = dao.findFriends(userId);

			// 设置好友状态，更新friends集合，添加online字段
			for (Map<String, String> friend : friends) {
				// 添加好友状态 1在线 0离线
				friend.put("online", "0");
				String fid = friend.get("user_id");
				// 好友在clientList集合中存在，则在线
				for (String uid : clientMap.keySet()) {
					// 好友在线
					if (uid.equals(fid)) {
						// 更新好友状态 1在线 0离线
						friend.put("online", "1");
						break;
					}
				}
			}
			sendJsonObj.put("friends", friends);// 客户端根据这个更新好友信息

			
			//取出离线时收到的消息。
			List<Map<String, String>> offMsgs = dao.findAllOffMsg(userId);
			sendJsonObj.put("offMsgs", offMsgs);
			
			//取出离线时所有添加好友请求
			List<Map<String, String>> addFriends = dao.findAllAddUser(userId);
			sendJsonObj.put("adduser", addFriends);

			System.out.println("processLogin,send:" + sendJsonObj);

			//取完之后 删除离线消息
			dao.deleteOffMsg(userId);
			dao.deleteAddUser(userId);
			
			// 创建DatagramPacket对象，用于向客户端发送数据
			byte[] b = sendJsonObj.toString().getBytes();
			packet = new DatagramPacket(b, b.length, clientAddress, clientPort);
			try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			// 广播当前用户上线了,向用户的系统socket发送。
			broadcastMsg("上线了", "1", userId, user.get("user_name"),friends);

		} else {
			// 送失败消息
			JSONObject sendJsonObj = new JSONObject();
			sendJsonObj.put("command", QQConstant.COMMAND_LOGIN);
			sendJsonObj.put("result", -1);
			byte[] b = sendJsonObj.toString().getBytes();
			packet = new DatagramPacket(b, b.length, clientAddress, clientPort);
			// 向请求登录的客户端发送数据
			try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void processRegister(JSONObject recvObj, InetAddress clientAddress, int clientPort) {
		// 通过用户Id查询用户信息
		String userId = (String) recvObj.get("user_id");
		String userName = (String) recvObj.get("user_name");
		String userPwd = (String) recvObj.get("user_pwd");
		Map<String, String> user = dao.findById(userId);
		int ret;
		if(user != null) { //用户已存在
			ret = QQConstant.ERR_EXIST_USER;
		}else {
			if(dao.insertUser(userId, userPwd, userName))
				ret = QQConstant.ERR_OK;
			else
				ret = QQConstant.ERR_DATABASE; 
		}
		
		JSONObject sendJsonObj = new JSONObject();			
		sendJsonObj.put("result", ret);
		byte[] b = sendJsonObj.toString().getBytes();
		DatagramPacket packet = new DatagramPacket(b, b.length, clientAddress, clientPort);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void processLogout(JSONObject recvObj) {
		// 获得用户Id
		String userId = (String) recvObj.get("user_id");
		//String userName = clientMap.get(userId).get(0).getUserName();
		ClientInfo c = (ClientInfo)getSetByIdx(clientMap.get(userId), 0);
		String userName = c.getUserName();
		clientMap.remove(userId);
		// 向其他客户端广播该用户下线
		List<Map<String, String>> friends = dao.findFriends(userId);
		broadcastMsg("下线了","0", userId, userName,friends);	
	}
	//聊天应该是双向的。 所以只有双向都建立，才能正式聊天。
	private void processChat(JSONObject recvObj, InetAddress clientAddress, int clientPort) {
		// 获得用户Id
		String userId = (String) recvObj.get("user_id");
		String friendId = (String) recvObj.get("receive_user_id");
		
		ClientInfo info = new ClientInfo(userId,friendId);
		info.setAddress(clientAddress);
		info.setPort(clientPort);
			
		//根据userId friendId 来判断唯一型。
		clientMap.get(userId).add(info); //相同的话  会覆盖掉。 set是不重复的。
		System.out.println("processChat :" +userId + " and " + friendId);

	}
	
	private void processChatOut(JSONObject recvObj) {
		// 获得用户Id
		String userId = (String) recvObj.get("user_id");
		String friendId = (String) recvObj.get("receive_user_id");
		ClientInfo c = new ClientInfo(userId, friendId);
		
		clientMap.get(userId).remove(c);
		
		System.out.println("processChatOut :" +userId + " and " + friendId);
	
	}
	
	/**
	 * 服务端 获取消息，转发消息(转发给对方系统socket+聊天socket )
	 * @param recvObj
	 */
	private void processSendMsg(JSONObject recvObj) {
		// 获得用户Id
		String userId = (String) recvObj.get("user_id");
		String friendId = (String) recvObj.get("receive_user_id");
		String msg = recvObj.getString("msg");
		recvObj.put("command", QQConstant.COMMAND_SENDMSG);
		
		//转发。
		LinkedHashSet<ClientInfo> infos = clientMap.get(friendId);
		if(infos == null) { //用户离线
			//存入离线消息
			System.out.println("processSendMsg:" +friendId + "off" );
			dao.insertOffMsg(userId, friendId, msg);
			return ;
		}
		//系统消息。
		ClientInfo info0 = (ClientInfo)getSetByIdx(infos, 0);
		byte[] b2 = recvObj.toString().getBytes();
		System.out.println("processSendMsg send to sys:" + recvObj);

		DatagramPacket pack = new DatagramPacket(b2,b2.length,info0.getAddress(), info0.getPort());
		try {
			socket.send(pack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//聊天窗口
		Iterator<ClientInfo> it = infos.iterator();
		while(it.hasNext()) {
			ClientInfo info = it.next();
			if(info.getFriendId().equals(userId)) {
				System.out.println("processSendMsg send to userId:" + recvObj);
				pack = new DatagramPacket(b2,b2.length,info.getAddress(), info.getPort());
				try {
					socket.send(pack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}	
	}

	/**
	 * 开启下载图片线程
	 * @param recvJsonObj
	 * @param clientAddress
	 * @param clientPort
	 */
	private void processDownImg(JSONObject recvJsonObj, InetAddress clientAddress, int clientPort) {

		String imgURL = recvJsonObj.getString("img_url");
		String pattern = "[^:/]/"; // http://localhost:7788/path/xx.img
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(imgURL);
		System.out.println(m.matches());

		JSONObject sendJsonObj = new JSONObject();
		int startIdx = 0;
		boolean findFlag = false;
		while(m.find()) {
			findFlag = true;
			startIdx = m.start(); //取最后的。 取文件名。
		}
		if (findFlag) {			
			File f = new File(imgURL.substring(startIdx + 2));
			System.out.println(f.getPath());
			if(f.exists()) {
				int port = ImgUtil.DownImgServer(imgURL.substring(startIdx + 2));
				if (port == -1) {
					sendJsonObj.put("result", -1);
					sendJsonObj.put("msg", "path err");
				} else {
					sendJsonObj.put("result", 0);
					sendJsonObj.put("port", port);
				}
			}
			else
			{
				sendJsonObj.put("result", -1);
				sendJsonObj.put("msg", "file not found");
			}
		} else {
			sendJsonObj.put("result", -1);
			System.out.println("NO MATCH");
		}

		byte[] b = sendJsonObj.toString().getBytes();

		System.out.println(sendJsonObj.toString());
		
		try {
			socket.send(new DatagramPacket(b, 0, b.length, clientAddress, clientPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processAddFriend(JSONObject recvJsonObj, InetAddress clientAddress, int clientPort) {
		// 获得用户Id
		String userId = (String) recvJsonObj.get("user_id");
		String friendId = (String) recvJsonObj.get("friend_id");
		String msg = recvJsonObj.getString("msg");
		
		// 转发。
		LinkedHashSet<ClientInfo> infos = clientMap.get(friendId);
		if (infos == null) { // 用户离线
			// 存入离线消息
			System.out.println("processAddFriend:" + friendId + "off");
			dao.insertAddUser(userId, friendId, msg);
		}else {
			// 系统消息。
			ClientInfo info0 = (ClientInfo) getSetByIdx(infos, 0);
			byte[] b2 = recvJsonObj.toString().getBytes();
			DatagramPacket pack = new DatagramPacket(b2, b2.length, info0.getAddress(), info0.getPort());
			try {
				socket.send(pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JSONObject sendObj = new JSONObject();
		sendObj.put("result",0);
		byte b[] = sendObj.toString().getBytes();
		try {
			socket.send(new DatagramPacket(b, 0,b.length, clientAddress,clientPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processBackFriend(JSONObject recvJsonObj, InetAddress clientAddress, int clientPort) {
		// 获得用户Id
		String userId = (String) recvJsonObj.get("user_id");
		String friendId = (String) recvJsonObj.get("friend_id");
		int ret = recvJsonObj.getInt("response");
		
		if(ret == 0) {
			dao.insertFriend(userId, friendId);
		}
		
		//只是确认请求已收到。 实际没什么用，因为UDP可能存在丢包，导致这个未能被收到。所以并不能根据是否收到应该来判断请求发出去， 如果收到应答，可以证明请求到达，如果没收到，不能判断。
		JSONObject sendObj = new JSONObject();
		sendObj.put("result",0);
		byte b[] = sendObj.toString().getBytes();
		try {
			socket.send(new DatagramPacket(b, 0,b.length, clientAddress,clientPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
