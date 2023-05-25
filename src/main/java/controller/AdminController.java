package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.ShopService;
import logic.User;

/*
 * AdminController 의 모든 메서드들은 반드시 관리자로 로그인 해야만 실행가능함.
 * AOP 설정 : AdminLoginAspect 클래스. adminCheck 메서드
 * 	1. 로그아웃상태 : 로그인하세요 login페이지로 이동
 * 	2. 관리자 로그인이 아닌 경우 : 관리자만 가능한 거래입니다. mypage로이동
 */
@Controller
@RequestMapping("admin")
public class AdminController {
	@Autowired
	private ShopService service;
	
	@GetMapping("*") // * : 설정되지 않은 모든요청시 호출되는 메서드
	public ModelAndView join () {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new User());
		return mav;
	}
}
