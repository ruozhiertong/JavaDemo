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

import java.net.InetAddress;

public class ClientInfo {
	// 用户Id
	private String userId;
	private String userName;
	// 客户端IP地址
	private InetAddress address;
	// 客户端端口号
	private int port;
	//与之聊天的好友id。
	private String friendId;
	
	/*
	byte b;
	boolean bo;
	char c;
	int i;
	short s;
	float f;
	double d;
	long l;
	
	String str;
	Object o;
	*/
	
	
	
	
	public ClientInfo(String userId, String friendId) {
		this.userId = userId;
		this.friendId = friendId;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + b;
		result = prime * result + (bo ? 1231 : 1237);
		result = prime * result + c;
		long temp;
		temp = Double.doubleToLongBits(d);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(f);
		result = prime * result + i;
		result = prime * result + (int) (l ^ (l >>> 32));
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + s;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		return result;
	}
	*/

	@Override
	public String toString() {
		return "ClientInfo [userId=" + userId + ", userName=" + userName + ", address=" + address + ", port=" + port
				+ ", friendId=" + friendId + "]";
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((friendId == null) ? 0 : friendId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientInfo other = (ClientInfo) obj;
		if (friendId == null) {
			if (other.friendId != null)
				return false;
		} else if (!friendId.equals(other.friendId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
	
	
	
	
	
}
