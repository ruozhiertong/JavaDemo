package com.a51work6.qq.constant;
/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月4日 上午11:16:48
* 类说明
*/
public class QQConstant {
	
	//命令代码
	public static final int COMMAND_REGISTER = 100; // 注册命令
	public static final int COMMAND_LOGIN = 101; // 登录命令
	public static final int COMMAND_LOGOUT = 102; // 注销命令
	public static final int COMMAND_ADDFRI = 103; // 添加好友
	public static final int COMMAND_BACKFRI = 104; // 好友申请 回复


	public static final int COMMAND_CHAT = 201; //新建聊天
	public static final int COMMAND_CHATOUT = 202; //聊天退出
	public static final int COMMAND_SENDMSG = 301; // 发消息命令
	public static final int COMMAND_DOWNIMG = 401; // 下载消息

	
	
	
	
	//error code
	public static final int ERR_OK = 0;
	public static final int ERR_EXIST_USER = -100; // 用户已存在
	public static final int ERR_DATABASE = -500; // 数据库操作错误

	
	
	public static final String SERVER_IP = "127.0.0.1";
	public static final int SERVER_PORT = 7788;
	
	public static final int IMGDOWN_PORT = 7799;

}
