package com.kh.model.service;

import java.sql.Connection;
import java.util.ArrayList;

import com.kh.common.JDBCTemplate;
import com.kh.model.dao.MemberDao;
import com.kh.model.vo.Member;
import static com.kh.common.JDBCTemplate.*;

/*
 * Service : 기존의 DAO의 역할을 분담
 *           컨트롤러에서 서비스 호출후 서비스를 거쳐서 dao로 넘어갈것
 *           DAO호출시 커넥션 객체와 기존에 넘기고자 했던 매개변수를 같이 넘겨줌
 *           DAO가 작업이 끝나면 서비스에게 결과를 돌려주고 그에따른 트랜잭션 처리를 같이해줌
 *           => 서비스단을 추가함으로써 DAO에는 순수하게 SQL문을 처리하는 부분만 남게됨
 *  */
public class MemberService {
	
	public int insertMember(Member m) {
		// 먼저 Connection 객체 1),2)
		Connection conn = getConnection();
		
		// DAO 호출시 Connection 객체와 기존에 넘기고자했던 매개변수를 함께 전달
		int result = new MemberDao().insertMember(conn, m);
		
		// 트랜잭션처리. 6)
		if(result > 0) {
			commit(conn);
		}else {
			rollback(conn);
		}
		
		//Connection 객체 반납 7)
		close(conn);
		
		// 결과값을 컨트롤러에게 반환 8)
		return result;
	}
	
	public ArrayList<Member> selectAll(){
		// 1) Connection 객체 생성
		Connection conn = getConnection();
		
		//2) dao호출해서 리턴값 받기.
		ArrayList<Member> list = new MemberDao().selectAll(conn);
		
		//3) 닫기
		close(conn);
		
		//4) 리턴
		return list;
	}
	
	public Member selectByUserId(String userId) {
		
		//  1) Connection객체 생성
			Connection conn = getConnection();
		//  2) DAO호출 호출시 conn 객체와, 전달받은 값을 함께 넘겨주기
			Member m = new MemberDao().selectByUserId(conn, userId);
		//  3) conn 반납
			close(conn);
		//  4) 컨트롤러에게 전달받은값 리턴
			return m;
	}
	
	public ArrayList<Member> selectByUserName(String keyword){
		
		Connection conn = getConnection();
		
		ArrayList<Member> list = new MemberDao().selectByUserName(conn, keyword);
		
		close(conn);
		
		return list;
	}
	
	public int updateMember(Member m) {
		Connection conn = getConnection();
		
		int result = new MemberDao().updateMember(conn, m);
		
		if(result>0) {
			commit(conn);
		}else {
			rollback(conn);
		}
		
		close(conn);
		
		return result;
	}
	
	public int deleteMember(String userId, String userPwd) {
		Connection conn = getConnection();
		
		int result = new MemberDao().deleteMember(conn, userId, userPwd);
		
		if(result>0) {
			commit(conn);
		}else {
			rollback(conn);
		}
		
		close(conn);
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
