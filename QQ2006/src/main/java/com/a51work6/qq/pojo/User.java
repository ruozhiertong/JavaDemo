package com.a51work6.qq.pojo;
/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月13日 下午4:06:26
* 类说明 . 与数据库对应的 实体。 这样做的好处就是能方便访问数据。而且不必去构造无谓的Map等。
*/
public class User {
	
	private String userId;
	private String userPwd;
	private String userName;
	private String userIcon;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	
	
	

}
