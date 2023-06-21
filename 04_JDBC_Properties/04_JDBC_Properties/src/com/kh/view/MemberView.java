package com.kh.view;

import java.util.ArrayList;
import java.util.Scanner;

import com.kh.controller.MemberController;
import com.kh.model.vo.Member;

/* 
 * View : 사용자가 보게될 시각전인 요소를 담당(화면 => 입력,출력)
 *      ex) html페이지
 * */

public class MemberView {

	// Scanner 객체 생성
	private Scanner sc = new Scanner(System.in);
	
	// MemberController 객체 생성
	private MemberController mc = new MemberController();
	
	// alt + shift +j : 메소드 설명 주석 단축키(매개변수와 반환형과 함께 자동완성됨) : 파란색주석,
	//                  매서드설명만 달아주면됨.
	/**
	 * 사용자가 보게될 첫 화면(메인화면)
	 */
	public void mainMenu() {
		while(true) {
			System.out.println("***** 회원 관리 프로그램 *****");
			System.out.println("1. 회원 추가");
			System.out.println("2. 회원 전체 조회");
			System.out.println("3. 회원 아이디로 검색");
			System.out.println("4. 회원 이름 키워드 검색");
			System.out.println("5. 회원 정보 변경");
			System.out.println("6. 회원 탈퇴");
			System.out.println("0. 프로그램 종료");
			System.out.println("----------------------------------------");
			System.out.print("이용할 메뉴 선택 : ");
			int menu = sc.nextInt();
			sc.nextLine();
			
			switch(menu) {
			case 1 : insertMember(); break;
			case 2 : selectAll(); break;
			case 3 : selectByUserId(); break;
			case 4 : selectByUserName(); break;
			case 5 : updateMember(); break; 
			case 6 : deleteMember(); break;
			case 0 : System.out.println("프로그램을 종료합니다..."); return;
			default: System.out.println("잘못입력하셨습니다. 다시 입력해주세요");
			}
		}
	}
	
	/**
	 * 회원 추가용 화면
	 * 추가하고자 하는 회원의 정보를 입력받아서 추가 요청할 수 있는 화면.
	 */
	public void insertMember() {
		System.out.println("----- 회원 추가 -----");
		
		// 입력
		System.out.print("아이디 : ");
		String userId = sc.nextLine();
		
		System.out.print("비밀번호 : ");
		String userPwd = sc.nextLine();
		
		System.out.print("이름 : ");
		String userName = sc.nextLine();
		
		System.out.print("성별 (M/F): ");
		String gender = String.valueOf(sc.nextLine().toUpperCase().charAt(0));
		
		System.out.print("나이 : ");
		int age = Integer.parseInt(sc.nextLine());
		
		System.out.print("이메일 : ");
		String email = sc.nextLine();
		
		System.out.print("휴대폰번호 (숫자만) : ");
		String phone = sc.nextLine();
		
		System.out.print("주소 : ");
		String address = sc.nextLine();
		
		System.out.print("취미 (,로 공백없이 나열) : ");
		String hobby = sc.nextLine();
		
		Member m = new Member(userId, userPwd, userName, gender, age, email, phone, address, hobby);
		
		// 입력받은 정보를 넘겨서 회원 추가 요청 => Controller의 어떤 메소드를 호출하겠다
		mc.insertMember(m);
	}
	
	/**
	 * 회원 전체 조회를 할 수 있는 화면
	 */
	public void selectAll() {
		System.out.println("----- 회원 전체 조회 -----");
		
		// 회원 전체 조회 요청
		mc.selectAll();
	}
	
	/**
	 * 사용자에게 검색할 회원의 아이디를 입력받은 후 조회 요청하는 메소드
	 */
	public void selectByUserId() {
		
		System.out.println("----- 회원 아이디로 검색 -----");
		
		System.out.print("검색할 회원의 아이디 : ");
		String userId = sc.nextLine(); 
		
		// 입력한 아이디를 회원 아이디 검색 요청시 같이 넘김
		mc.selectByUserId(userId);
	}
	
	public void selectByUserName() {
		System.out.println("----- 회원 이름 키워드 검색 -----");
		
		System.out.print("회원 이름 키워드 입력 : ");
		String keyword = sc.nextLine();
		
		mc.selectByUserName(keyword);
	}
	
	/**
	 * 사용자에게 변결할 회원의 아이디, 변경할 정보들(비번, 이메일, 휴대폰 ,주소) 입력을 받은 후
	 * 변경 요청하는 메소드
	 */
	public void updateMember() {
		System.out.println("----- 회원정보 변경 -----");
		
		System.out.print("변경할 회원의 아이디 : ");
		String userId = sc.nextLine();
		
		//-----------------------------------
		System.out.print("변경할 비밀번호 : ");
		String userPwd = sc.nextLine();
		
		System.out.print("변경할 이메일 : ");
		String email = sc.nextLine();
		
		System.out.print("변경할 휴대폰번호(숫자만) : ");
		String phone = sc.nextLine();
		
		System.out.print("변경할 주소 : ");
		String address = sc.nextLine();
		
		mc.updateMember(userId, userPwd, email, phone, address);
	}
	
	/**
	 * 사용자에게 탈퇴할 회원의 아이디 + 비밀번호를 입력받은후 삭제요청하는 메소드
	 */
	public void deleteMember() {
		
		System.out.println("----- 회원탈퇴 -----");
		
		System.out.print("탈퇴할 회원 ID :");
		String userId = sc.nextLine();
		
		System.out.print("탈퇴할 회원 PASSWORD :");
		String userPwd = sc.nextLine();
				
		mc.deleteMember(userId,userPwd);
	}
	
	/**
	 * 서비스 요청 성공시 사용자가 보게될 화면
	 * @param message : 성공 메세지
	 */
	public void displaySuccess(String message) {
		System.out.println("\n서비스 요청 성공 : "+message);
	}
	
	/**
	 * 서비스 요청 실패 시 사용자가 보게될 화면
	 * @param message : 실패 메세지
	 */
	public void displayFail(String message) {
		System.out.println("\n서비스 요청 실패 : "+message);
	}
	
	/**
	 * 조회 서비스 요청 시 조회결과가 없을 때 보게될 화면
	 * @param message : 사용자에게 보여질 메세지
	 */
	public void displayNodata(String message) {
		System.out.println(message);
	}
	
	/**
	 * 조회 서비스 요청시 "여러 행"이 조회된 결과를 받아서 보게될 화면
	 * @param list : 여러행이 조회된 결과
	 */
	public void displayList(ArrayList<Member> list) {
		System.out.println("\n조회된 데이터는 "+ list.size()+"건 입니다.\n");
		
		for(Member m : list) {
			System.out.println(m);
		}
	}
	
	public void displayOne(Member m) {
		System.out.println("\n조회된 데이터는 다음과 같습니다.");
		
		System.out.println(m);
	}
	
}




