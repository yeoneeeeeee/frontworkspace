package com.kh.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.kh.common.JDBCTemplate.*;
import com.kh.model.vo.Member;


/* 
 * DAO (Data Access Object)
 * Controller를 통해서 호출
 * Controller에서 요청 받은 실질적인 기능을 수행하기 위해서
 * DB에 직접적으로 접근(Access)후 해당 sql문을 실행하고 결과를 돌려받기 => JDBC
 * */
public class MemberDao {
	
	/* 
	 * * JDBC용 객체
	 * - Connection : DB의 연결정보를 담고 있는 객체(IP주소, PORT번호, 계정명, 비밀번호)
	 * - (Prepared)Statement : 해당 db에 sql문을 전달하고 실행한 후 결과를 받아내는 객체
	 * - ResultSet : 만일 실행한 SQL문이 SELECT문일 경우 조회된 결과들이 담겨있는 객체.
	 * 
	 * ** PreparedStatement 특징 : SQL문을 바로 실행하지 않고 잠시 보관하는 개념
	 *                            미완성된 SQL문을 먼저 전달하고 실행하기전에 완성시킨다.
	 *                           => 미완성된 SQL문 만들기 (사용자가 입력한 값들이 들어가는
	 *                           공간에 ?(위치홀더)로 공간을 확보해둔다)
	 *                           각 위치홀더에 맞는 값들을 셋팅.
	 * ** Statement(부모)와 PreparedStatement(자식)관계이다.
	 * ** 차이점
	 * 1) Statement는 완성된 SQL문 , PrepraedStatement는 미완성된 SQL문
	 * 
	 * 2) Statement는 객체 생성시 stmt = conn.createStatement();
	 *   PreparedStatement는 객체 생성시 pstmt = conn.prepareStatement(sql);
	 *   
	 * 3) Statement로 SQL문 실행시  결과 = stmt.executeXXXX(sql);
	 *   PredparedStatement로 실행시 ? 로 표현된 빈 공간을 실제 값으로 채워주는 과정을 거쳐야한다.
	 *                              pstmt.setString(?위치, 실제값);
	 *                              pstmt.setInt(?위치, 실제값);
	 *                                결과 = pstmt.executeXXXXX();
	 * 
	 * 
	 * * JDBC 처리 순서
	 * 1) JDBC Driver 등록 : 해당 dbms가 제공하는 클래스 등록
	 * 2) Connection 생성  : 접속하고자하는 DB정보를 입력해서 DB에 접속하면서 생성
	 * 3_1) PreparedStatement  생성  : Connection 객체를 이용해서 생성
	 *                                (미완성된 sql문을 담은 상태로 생성)
	 * 3_2) 현재 미완성된 SQL문을 완성형태로 채우기
	 *      => 미완성된 경우에만 해당됨 / 완성된 경우에는 생략가능.
	 * 4) SQL문을 전달하면서 실행 : Statement객체를 이용해서 sql 문 실행
	 *     > SELECT문일 경우 - executeQuery() 메소드 사용
	 *     > 나머지 DML문일 경우 - executeUpdate() 메소드 사용
	 * 5) 결과값 받기
	 *     > SELECT 문일 경우 - ResultSet 객체로 받기(조회된 데이터들이 담겨있음) => 6_1)
	 *     > 나머지 DML문일 경우 - int형 변수로 받기(처리된 행의 갯수가 담겨있음  ) => 6_2)
	 * 
	 * 6_1) ResultSet 객체에 담긴 데이터들을 하나씩 뽑아서 VO 객체에 담기(반환된 데이터가 여러개
	 *      라면 ArrayList로 묶어서 관리)
	 * 6_2) 트랜잭션 처리(성공했다면 == 반환된 행의 갯수가 0이 아니라면 COMMIT, 실패했을시 ROLLBACK)
	 * 7) 다쓴 JDBC용 객체들을 반드시 자원반납해주기(close) => 생성된 순서의 역순으로 닫기
	 * 8) 결과값을 Controller로 반환해주기
	 *     > select문일 경우 6_1)에서 만들어진 결과
	 *     > 기타 DML문일 경우 - int현 값(처리된 행의 갯수)
	 * 
	 * * Statement 특징 : 완성된 sql문을 실행할수 있는 객체
	 * */
	
	/**
	 * 사용자가 회원 추가 요청시 입력했던 값을 가지고 INSERT문을 실행하는 메소드
	 * @param m : 사용자가 입력했던 아이디 ~ 취미까지의 값이 담겨있는 Member 객체
	 * @return : insert문을 실행한 결과 처리된 행의 갯수
	 */
	public int insertMember(Connection conn , Member m) { // INSERT문 => 처리된 행의 갯수를 반환, 트랜잭션처리
		
		//0) 필요한 변수들 셋팅
		int result = 0;// 처리된 결과를 담아줄 변수
		PreparedStatement pstmt = null; // SQL문 실행시킨 후 결과를 받기 위한 변수 
		
		// +필요한 변수 : 실행할 SQL문(완성된 형태로 준비) 
		//              끝에 세미콜론이 있으면 안됨
		/*
		 * INSERT INTO MEMBER
		 * VALUES(SEQ_USERNO.NEXTVAL, 'XXX','XXX','XXX','X',X,... , DEFAULT);
		 * 
		 *  */
		String sql = "INSERT INTO MEMBER "
				+ "VALUES(SEQ_USERNO.NEXTVAL,?,?,?,?,?,?,?,?,?,DEFAULT)";
		
		try {
			
			//3_1) PreparedStatement 객체 생성(SQL문을 미리 넘겨준다)
			pstmt = conn.prepareStatement(sql);
			
			//3_2) 미완성된 SQL문을 완성시켜주기. 
			//     pstmt.setXXX(?의 위치, 실제값);
			//  VALUES(SEQ_USERNO.NEXTVAL,?,?,?,?,?,?,?,?,?,DEFAULT)
			pstmt.setString(1, m.getUserId());
			pstmt.setString(2, m.getUserPwd());
			pstmt.setString(3, m.getUserName());
			pstmt.setString(4, m.getGender());
			pstmt.setInt(5, m.getAge());
			pstmt.setString(6, m.getEmail());
			pstmt.setString(7, m.getPhone());
			pstmt.setString(8, m.getAddress());
			pstmt.setString(9, m.getHobby());
			
			// 4, 5) DB에 완성된 SQL문을 전달하면서 실행 후 결과 받기
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//7) PreparedStatement 객체 반납.
			close(pstmt);
		}
		//8) 결과값 controller에게 반환
		return result;// 처리된 행의 갯수
	}
	
	/**
	 * 사용자가 회우너전체 요청시 select문을 실행해주는 메소드
	 * @return
	 */
	public ArrayList<Member> selectAll(Connection conn) { // SELECT => ResultSet형태 => ArrayList반환
		// 0) 필요한 변수들 셋팅
		// 조회된 결과를 뽑아서 담아줄 변수 => ArrayList 생성(여러 회원의 정보, 여러 행)
		ArrayList<Member> list = new ArrayList(); // 현재 텅빈 리스트
		
		// Connection , Statement, ResultSet => fianlly블록에서 자원반납하기 위해
		//                                      미리 try블럭 밖에다 변수 선언
		PreparedStatement pstmt  = null; // SQL문 실행후 결과값 돌려받기 위한 변수
		ResultSet rset = null;  // SELECT문이 실행된 조회결과값들이 처음에 담길 객체
		String sql = "SELECT * FROM MEMBER";
		
		try {
			//3) Statement 객체 생성
			pstmt = conn.prepareStatement(sql);
			
			// 4, 5) SQL문(SELECT)를 전달해서 실행 후 결과(ResultSet) 받기
			rset = pstmt.executeQuery();
			
			//6_1) 현재 조회결과가 담긴 ResultSet에서 한행씩 뽑아서 vo객체에 담기
			//rset.next() : 커서를 한줄 아래로 옮겨주고 해당 행이 존재할 경우 true 없다면 false.
			while(rset.next()) {
				// 현재 rset의 커서가 가리키고 있는 해당 행의 데이터를 하나씩 뽑아서 vo객체에 담기.
				Member m = new Member();
				
				// rset으로부터 어떤 칼럼에 해당하는 값을 뽑을건지 제시
				// => 컬럼명(대소문자 가리지 않음), 컬럼 순번
				//    권장사항 : 컬럼명 + 대문자로 쓰는것을 권장
				// rset.getInt(컬럼명 또는 순번) : int형으로 값을 뽑아올때
				// rset.getString(컬럼명 또는 순번) : String 형으로 뽑아올때
				// rset.getDate(컬럼명 또는 순번) : Date형 값을 뽑아낼때
				
				m.setUserNo(rset.getInt("USERNO"));
				m.setUserId(rset.getString("USERID"));
				m.setUserPwd(rset.getString(3));
				m.setUserName(rset.getString("USERNAME"));
				m.setGender(rset.getString("GENDER"));
				m.setAge(rset.getInt("AGE"));
				m.setEmail(rset.getString("EMAIL"));
				m.setPhone(rset.getString("PHONE"));
				m.setAddress(rset.getString("ADDRESS"));
				m.setHobby(rset.getString("HOBBY"));
				m.setEnrollDate(rset.getDate("ENROLLDATE"));
				/* 
				 * 한 행에 대한 모든 칼럼에 있는 값들을 Member VO객체의 각 필드에
				 * 담아주기.
				 * 
				 * 초기화시킨 VO객체를 List에 옮겨주기
				 * */
				
				list.add(m);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//7) 다쓴 JDBC용 객체 반납(생성된 순서의 역순으로)
			close(rset);
			close(pstmt);
		}
		return list;
		
	}
	
	public Member selectByUserId(Connection conn, String userId) {
		// SELECT문 => ResultSet => Member(1개의 행, 반복x)
		
		// 0) 필요한 변수 셋팅
		// 조회된 한 회원에 대한 정보를 담을 변수
		Member m = null;
		
		// JDBC관련된 객체들 선언
		PreparedStatement stmt  = null;
		ResultSet rset  = null;
		
		// 실행할 SQL문
		String sql = "SELECT * FROM MEMBER WHERE USERID = ?";
		
		try {
			//3) Statement 객체 생성
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			//SELECT * FROM MEMBER WHERE USERID = 'alsrudals'
			
			// 4, 5) SQL문(SELECT)를 전달해서 실행 후 결과(ResultSet) 받기
			rset = stmt.executeQuery();
			
			//6_1) 현재 조회결과가 담긴 Resultset에서 한행만 뽑아서 VO객체에 담기
			//     => ID검색은 한행만 조회될것이기 때문에
			if(rset.next()) {
				// 커서를 한행 움직여보고  조회결과가 있다면 true/ 없다면 false
				m = new Member();
				m.setUserNo(rset.getInt("USERNO"));
				m.setUserId(rset.getString("USERID"));
				m.setUserPwd(rset.getString(3));
				m.setUserName(rset.getString("USERNAME"));
				m.setGender(rset.getString("GENDER"));
				m.setAge(rset.getInt("AGE"));
				m.setEmail(rset.getString("EMAIL"));
				m.setPhone(rset.getString("PHONE"));
				m.setAddress(rset.getString("ADDRESS"));
				m.setHobby(rset.getString("HOBBY"));
				m.setEnrollDate(rset.getDate("ENROLLDATE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(stmt);
		}
		//8) 결과값 반환
		return m;
	}
	
	public ArrayList<Member> selectByUserName(Connection conn, String keyword){
		
		//0) 필요한 변수 셋팅
		ArrayList<Member> list = new ArrayList();
		
		//Connection conn = null;
		PreparedStatement stmt  = null;
		ResultSet rset  = null;
		
		String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE CONCAT(CONCAT('%', ?) ,'%')";
		//1) 문자열 앞뒤로 '%'붙여주기 "%" +keyword +"%";
		//2) 연결연산자활용 '%' || ? || '%'
		//3) 함수활용      CONCAT(CONCAT('%',?),'%')
		try {
			//1) JDBC 드라이버 등록
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//2) Connection 객체 생성( == DB와 연결시키겠다 --> URL,계정,비밀번호)
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
					//"JDBC","JDBC");
			
			//3) Statement 객체 생성
			stmt = conn.prepareStatement(sql);
			//SELECT * FROM MEMBER WHERE USERNAME LIKE '%'경민'%'
			stmt.setString(1, keyword);
			
			// 4, 5) SQL문(SELECT)를 전달해서 실행 후 결과(ResultSet) 받기
			rset = stmt.executeQuery();
			
			while(rset.next()) {
				Member m = new Member();
				m.setUserNo(rset.getInt("USERNO"));
				m.setUserId(rset.getString("USERID"));
				m.setUserPwd(rset.getString(3));
				m.setUserName(rset.getString("USERNAME"));
				m.setGender(rset.getString("GENDER"));
				m.setAge(rset.getInt("AGE"));
				m.setEmail(rset.getString("EMAIL"));
				m.setPhone(rset.getString("PHONE"));
				m.setAddress(rset.getString("ADDRESS"));
				m.setHobby(rset.getString("HOBBY"));
				m.setEnrollDate(rset.getDate("ENROLLDATE"));
				
				list.add(m);			
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			close(rset);
			close(stmt);
		}
		return list;
	}
	
	public int updateMember(Connection conn , Member m) {
		//UPDATE문 실행 => 처리된 행의 갯수(INT) => DML문호출시 트랜잭션처리필수!
		
		//0)
		int result = 0;
		
		//Connection conn = null;
		PreparedStatement stmt  = null;
		
		String sql = "UPDATE MEMBER "
			       + "SET USERPWD = ? ,"
			       +     "EMAIL = ?, "
			       +     "PHONE = ?, "
			       +     "ADDRESS= ? "
			       + "WHERE USERID = ?";
		
		try {
			//1) JDBC 드라이버 등록
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//2) Connection 객체 생성( == DB와 연결시키겠다 --> URL,계정,비밀번호)
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
					//"JDBC","JDBC");
			
			//3) Statement 객체 생성
			stmt = conn.prepareStatement(sql);
			
			stmt.setString(1, m.getUserPwd());
			stmt.setString(2, m.getEmail());
			stmt.setString(3, m.getPhone());
			stmt.setString(4, m.getAddress());
			stmt.setString(5, m.getUserId());
			
			//4) 5)
			result = stmt.executeUpdate();
			
			//6)
//			if(result > 0) {
//				conn.commit();
//			}else {
//				conn.rollback();
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(stmt);
//			try {
//				stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		}
		return result;
	}
	
	public int deleteMember(Connection conn, String userId, String userPwd) {
		//delete문 => 처리된 행의 갯수 => 트랜잭션
		int result = 0;
		
		//Connection conn = null;
		PreparedStatement stmt  = null;
		
		String sql = "DELETE FROM MEMBER "
				   + "WHERE USERID = ? AND USERPWD = ?";
		
		try {
			//1) JDBC 드라이버 등록
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//2) Connection 객체 생성( == DB와 연결시키겠다 --> URL,계정,비밀번호)
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
					//"JDBC","JDBC");
			
			//3) Statement 객체 생성
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, userPwd);
			
			//4) 5)
			result = stmt.executeUpdate();
//			if(result > 0) {
//				conn.commit();
//			}else {
//				conn.rollback();
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
//			try {
//				stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
			close(stmt);
		}
		return result;
		
	}
	
	
	
	
	
	
	

}
