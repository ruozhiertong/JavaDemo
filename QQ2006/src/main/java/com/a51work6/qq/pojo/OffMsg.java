package com.a51work6.qq.pojo;

import java.sql.Date;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月13日 下午4:12:25
* 类说明
*/
public class OffMsg {
	
	private int id;
	private String msg;
	private String sendId;
	private String recvId;
	private Date updDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSendId() {
		return sendId;
	}
	public void setSendId(String sendId) {
		this.sendId = sendId;
	}
	public String getRecvId() {
		return recvId;
	}
	public void setRecvId(String recvId) {
		this.recvId = recvId;
	}
	public Date getUpdDate() {
		return updDate;
	}
	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

}
