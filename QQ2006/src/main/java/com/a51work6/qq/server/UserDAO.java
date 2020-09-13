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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

	// 查询所有用户信息
	public List<Map<String, String>> findAll() {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		// SQL语句
		String sql = "select user_id,user_pwd,user_name, user_icon from user";
		try (// 2.创建数据库连接
				Connection conn = DBHelper.getConnection();
				// 3. 创建语句对象
				PreparedStatement pstmt = conn.prepareStatement(sql);
				// 5. 执行查询
				ResultSet rs = pstmt.executeQuery();) {

			// 6. 遍历结果集
			while (rs.next()) {

				Map<String, String> row = new HashMap<String, String>();
				row.put("user_id", rs.getString("user_id"));
				row.put("user_name", rs.getString("user_name"));
				row.put("user_pwd", rs.getString("user_pwd"));
				row.put("user_icon", rs.getString("user_icon"));

				list.add(row);
			}

		} catch (SQLException e) {
		}

		return list;
	}

	// 按照主键查询
	public Map<String, String> findById(String id) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// SQL语句
		String sql = "select user_id,user_pwd,user_name, user_icon from user where user_id = ?";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象

			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, id);
			// 5. 执行查询（R）
			rs = pstmt.executeQuery();
			// 6. 遍历结果集
			if (rs.next()) {
				
				Map<String, String> row = new HashMap<String, String>();
				row.put("user_id", rs.getString("user_id"));
				row.put("user_name", rs.getString("user_name"));
				row.put("user_pwd", rs.getString("user_pwd"));
				row.put("user_icon", rs.getString("user_icon"));

				return row;
			}

		} catch (SQLException e) {
		} finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return null;
	}

	
	public boolean insertUser(String userId,String userPwd,String userName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = true;
		// SQL语句
		String sql = "insert into user(user_id,user_pwd,user_name,user_icon) values(?,?,?,?)";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象
			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, userId);
			pstmt.setString(2, userPwd);
			pstmt.setString(3, userName);
			pstmt.setString(4, "0"); //0默认的图标
			// 5. 执行查询（R）
			pstmt.execute();
		}catch (Exception e) {
			result = false;
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}
	
	// 查询好友 列表
	public List<Map<String, String>> findFriends(String id) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, String>> friends = new ArrayList<Map<String, String>>();
		// SQL语句
		String sql = "select user_id,user_pwd,user_name,user_icon FROM user " + " WHERE "
				+ "    user_id IN (select user_id2 as user_id  from friend where user_id1 = ?)"
				+ " OR user_id IN (select user_id1 as user_id  from friend where user_id2 = ?)";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象

			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, id);
			pstmt.setString(2, id);
			// 5. 执行查询（R）
			rs = pstmt.executeQuery();
			// 6. 遍历结果集
			while (rs.next()) {

				Map<String, String> row = new HashMap<String, String>();
				row.put("user_id", rs.getString("user_id"));
				row.put("user_name", rs.getString("user_name"));
				row.put("user_pwd", rs.getString("user_pwd"));
				row.put("user_icon", rs.getString("user_icon"));

				friends.add(row);
			}

		} catch (SQLException e) {
		} finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return friends;
	}

	
	
	// 查找离线历史信息
	public List<Map<String,String>> findAllOffMsg(String recv_id){
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, String>> msgs = new ArrayList<Map<String, String>>();
		// SQL语句
		String sql = "select * from offmsg where recv_id=?";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象

			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, recv_id);
			// 5. 执行查询（R）
			rs = pstmt.executeQuery();
			// 6. 遍历结果集
			while (rs.next()) {

				Map<String, String> row = new HashMap<String, String>();
				row.put("send_id", rs.getString("send_id"));
				row.put("msg", rs.getString("msg"));
				row.put("upd_date", rs.getString("upd_date"));

				msgs.add(row);
			}

		} catch (SQLException e) {
		} finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return msgs;
		
	}
	
	public void insertOffMsg(String send_id, String recv_id,String msg) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// SQL语句
		String sql = "insert into offmsg(msg,send_id,recv_id) values(?,?,?)";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象
			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, msg);
			pstmt.setString(2, send_id);
			pstmt.setString(3, recv_id);
			// 5. 执行查询（R）
			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public void deleteOffMsg(String recv_id) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// SQL语句
		String sql = "delete from offmsg where recv_id=?";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象
			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, recv_id);
			// 5. 执行查询（R）
			pstmt.execute();
			

		} catch (SQLException e) {
		} finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public void insertAddUser(String userId, String friendId,String msg) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// SQL语句
		String sql = "insert into adduser(user_id,friend_id,msg) values(?,?,?)";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象
			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, userId);
			pstmt.setString(2, friendId);
			pstmt.setString(3, msg);
			// 5. 执行查询（R）
			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	// 查找离线历史信息
		public List<Map<String,String>> findAllAddUser(String friendId){
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			List<Map<String, String>> msgs = new ArrayList<Map<String, String>>();
			// SQL语句
			String sql = "select * from adduser where friend_id=?";
			try {
				// 2.创建数据库连接
				conn = DBHelper.getConnection();
				// 3. 创建语句对象

				pstmt = conn.prepareStatement(sql);
				// 4. 绑定参数
				pstmt.setString(1, friendId);
				// 5. 执行查询（R）
				rs = pstmt.executeQuery();
				// 6. 遍历结果集
				while (rs.next()) {
					Map<String, String> row = new HashMap<String, String>();
					row.put("user_id", rs.getString("user_id"));
					row.put("friend_id", rs.getString("friend_id"));
					row.put("msg", rs.getString("msg"));
					row.put("upd_date", rs.getString("upd_date"));

					msgs.add(row);
				}

			} catch (SQLException e) {
			} finally { // 释放资源
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}

			return msgs;
			
		}
	
	public void deleteAddUser(String friendId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// SQL语句
		String sql = "delete from adduser where friend_id = ?";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象
			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, friendId);
			// 5. 执行查询（R）
			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public void insertFriend(String userId, String friendId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// SQL语句
		String sql = "insert into friend(user_id1,user_id2) values(?,?)";
		try {
			// 2.创建数据库连接
			conn = DBHelper.getConnection();
			// 3. 创建语句对象
			pstmt = conn.prepareStatement(sql);
			// 4. 绑定参数
			pstmt.setString(1, userId);
			pstmt.setString(2, friendId);
			// 5. 执行查询（R）
			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally { // 释放资源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
