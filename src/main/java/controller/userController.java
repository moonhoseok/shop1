package controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.Item;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class userController {
	@Autowired
	private ShopService service;

	@GetMapping("*") // * : 설정되지 않은 모든요청시 호출되는 메서드
	public ModelAndView join () {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new User());
		return mav;
	}
	@PostMapping("join")
	public ModelAndView userAdd(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			// reject : global error에 추가
			bresult.reject("error.input.user");
			bresult.reject("error.input.check");
			return mav;
		}
		// 정상입력값 : 회원가입하기 => db의 useraccount 테이블에 저장
		try {
			service.userinsert(user);
			mav.addObject("user",user);
		} catch (DataIntegrityViolationException e) {
			// DataIntegrityViolationException : db등록시 Key값 중복 오류 발생
			// Duplicate entry 'adimin' for key 'PRIMARY'
			e.printStackTrace();
			bresult.reject("error.duplicate.user"); //global 오류 등록
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		mav.setViewName("redirect:login");
		return mav;
	}
	
	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			// reject : global error에 추가
			bresult.reject("error.login.input");
			return mav;
		}
		 // 1. userid 맞는 User를 db에서 조회하기
		try {
			User dbUser = service.selectUserOne(user.getUserid());
		} catch (EmptyResultDataAccessException e) { //조회된 데이터 없는 경우 발생
			e.printStackTrace();
			bresult.reject("error.login.id");//아이디를 확인하세요
			mav.getModel().putAll(bresult.getModel());
		}
		return mav;
	}
}
