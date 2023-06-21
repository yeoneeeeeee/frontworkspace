package com.kh.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCTemplate {

	/* 
	 * JDBC 과정중 반복적으로 쓰이는 구문들을 각각의 메소드로 정의해둘 돗
	 * "재사용"목적으로 공통 템플릿 작업을 진행
	 * 
	 * 이 클래스에서의 모든 메소드들은 다 static메소드로 만들것
	 * 
	 * 기존의 방식 : JDBC Driver 구문, 내가 접속할 url 정보 , 접속 계정 및 비밀번호
	 *             자바소스코드내에 명시적으로 작성함 => 정적 코드방식(하드코딩)
	 * - 문제점 : DBMS가 변경되거나, 접속할 URL, 계정명, 비밀번호가 변경되었으 경우
	 *           자바소스코드를 수정해줘야함.
	 * 수정된 내용을 반영시키고자한다면 프로그램을 재구동해야함 (사용자 입장에서도 프로그램 사용중
	 * 비 정상적으로 종료되었다가 다시 구동될수 있음)
	 *           * 유지보수에 불편하다
	 * - 해결     : DB관련된 정보들을 별도로 관리하는 외부 파일로 만들어서 관리
	 *             외부파일로 KEY에대한 VALUE를 읽어들여서 반영시킬것 => 동적코딩방식
	 * 
	 * */
	
	// 공통부분 뽑아내기
	// 1. DB와 접속된 Connection객체를 생성해서 반환시켜주는 메소드
	public static Connection getConnection() {
		// 동적 코딩방식을 적용하기위해서 Properties 객체를 생성한다.
		Properties prop = new Properties();
		
		
		
		// Connection 객체를 담을 그릇 생성
		Connection conn = null;
		
		// 연결시키기 => 1,2번 스텝 진행
		
		try {
			// prop으로부터 load메소드를 이용해서 driver.properties에 있는 정보를 읽어들임.
			prop.load(new FileInputStream("resources/driver.properties"));
			
			// prop로부터 getProperty함수를 이용해서 각 키값에 해당하는 밸류값을 뽑아서 배치
			Class.forName(prop.getProperty("driver"));
			
			conn = DriverManager.getConnection(prop.getProperty("url"),
												prop.getProperty("username"),
												prop.getProperty("password"));
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
