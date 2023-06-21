package com.kh.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTemplate {

	/* 
	 * JDBC 과정중 반복적으로 쓰이는 구문들을 각각의 메소드로 정의해둘 돗
	 * "재사용"목적으로 공통 템플릿 작업을 진행
	 * 
	 * 이 클래스에서의 모든 메소드들은 다 static메소드로 만들것
	 *  
	 * */
	// 공통부분 뽑아내기
	// 1. DB와 접속된 Connection객체를 생성해서 반환시켜주는 메소드
	public static Connection getConnection() {
		
		// Connection 객체를 담을 그릇 생성
		Connection conn = null;
		
		// 연결시키기 => 1,2번 스텝 진행
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
					"JDBC","JDBC");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Connection 객체 반환
		return conn;
	}
	
	//2. 전달받은 JDBC용 객체를 반납시켜주는 메소드 (객체별로)
	//2_1) Connection객체를 전달받아서 반납시켜주는 메소드
	public static void close(Connection conn) {
		
		try {
			if(conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//2_2 Statement 객체를 전달받아서 반납시켜주는 메소드(오버로딩적용)
	//    => 다형성으로 인해 PreparedStatement 객체 또한 매개변수로 전달이 가능함.(UpCasting)
	public static void close(Statement stmt) {
		try {
			if(stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//2_3) ResultSet 객체를 전달받아서 반납시켜주는 메소드(오버로딩)
	public static void close(ResultSet rset) {
		
		try {
			if(rset != null && !rset.isClosed()) rset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 3. 전달받은 Connection객체를 가지고 트랜잭션처리를 해주는 메소드
	// 3_1) commit메소드
	public static void commit(Connection conn) {
		try {
			if(conn != null && !conn.isClosed())
				conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//3_2 rollback
	public static void rollback(Connection conn) {
		try {
			if(conn != null && !conn.isClosed())
				conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
}
