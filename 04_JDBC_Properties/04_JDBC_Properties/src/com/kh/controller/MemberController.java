package com.kh.controller;

import java.util.ArrayList;

import com.kh.model.dao.MemberDao;
import com.kh.model.service.MemberService;
import com.kh.model.vo.Member;
import com.kh.view.MemberView;

/* 
 * Controller : View를 통해서 요청(request)한 기능을 처리하는 담당
 *              해당 메소드로 전달된 데이터들을 가공처리(VO 객체로 래핑)한후
 *              Service메소드 호출시 전달
 *              Service로부터 반환받은 결과에 따라 사용자가 보게될 화면을 지정 => 응답(response)
 * */
public class MemberController {
	
	private MemberService ms = new MemberService();
	
	/**
	 * 사용자의 회원 추가 요청을 처리해주는 메소드
	 * @param m : 회원의 정보가 담긴 객체
	 */
	public void insertMember(Member m) {
		
		// 1. 전달받은 userId, userName,userPwd... 정보를 가지고 Member 객체에 담기
		
		// 2. Dao의 insertMember 메소드 호출
		int result = ms.insertMember(m);
		
		// result = 0 ==> 데이터 추가 실패
		// result = 1 ==> 데이터 삽입 성공
		
		// 3. 결과값에 의해서 사용자가 보게될 화면(response)를 지정
		if(result > 0) { // 성공시
			// 성공메세지를 띄워주는 화면 호출
			new MemberView().displaySuccess("회원 추가 성공");
		}else { // 실패시
			// 실패메세지를 띄워주는 화면 호출
			new MemberView().displayFail("회원 추가 실패");
		}
	}
	
	/**
	 * 사용자의 회원 전체 조회 요청을 처리해주는 메소드
	 */
	public void selectAll() {
		
		// 결과값을 담을 변수
		// SELECT -> ResultSet -> ArrayList<Member>
		
		ArrayList<Member> list = ms.selectAll();
		
		// 조회 결과가 있는지 없는지 판단한 후 사용자가 보게될 응답화면을 지정
		if(list.isEmpty()) { // 텅 빈 리스트일 경우 => 조회결과가 없음
			new MemberView().displayNodata("전체 조회 결과가 없습니다.");
		}else { // 조회가 되었을 경우 => 조회결과 있음
			new MemberView().displayList(list);
		}
		
	}
	
	/**
	 * 사용자의 아이디로 검색 요청을 처리해주는 메소드
	 * @param userId : 사용자가 입력했던 검색하고자 하는 아이디
	 * @author 민경민
	 */
	public void selectByUserId(String userId) {
		
		// 결과값을 담을 변수
		// SELECT => RESULTSET => Member
		Member m = ms.selectByUserId(userId);
		
		//조회 결과가 있는지 없는지 판단한후 사용자가 보게될 화면을 지정
		if(m == null) { // 조회결과가 없는경우
			new MemberView().displayNodata(userId+"에 해당하는 검색 결과가 없습니다..");
		}else { // 조회결과가 있을경우
			new MemberView().displayOne(m);
		}
	}
	
	public void selectByUserName(String keyword) {
		
		ArrayList<Member> list = ms.selectByUserName(keyword);
		
		if(list.isEmpty()) {
			new MemberView().displayNodata(keyword+"에 대한 검색 결과가 없습니다.");
		}else {
			new MemberView().displayList(list);
		}
		
	}
	
	public void updateMember(String userId, String userPwd, String email, String phone, String address) {
		// 넘겨받은값을가지고 가공처리(VO객체로 만들기) 완료
		Member m = new Member();
		m.setUserId(userId);
		m.setAddress(address);
		m.setEmail(email);
		m.setPhone(phone);
		m.setUserPwd(userPwd);
		
		int result = ms.updateMember(m);
		
		if(result > 0) {
			new MemberView().displaySuccess("회원 정보 변경 성공");
		}else {
			new MemberView().displayFail("회원 정보 변경 실패");
		}		
	}
	
	public void deleteMember(String userId, String userPwd) {
		
		int result = ms.deleteMember(userId, userPwd);
		
		if(result > 0) {
			new MemberView().displaySuccess("회원 탈퇴 성공");
		}else {
			new MemberView().displayFail("회원 탈퇴 실패");
		}	
	}
	
	
	
	
	
}



