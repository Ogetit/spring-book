package com.github.core.ssologin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SsoLogin {
	/**
	 * 根据ssouserid免登陆访问，需要实现此方法
	 * 即根据传入的id取用户，不用密码验证登陆
	 * @param ssouserid//登录人id
	 * @param request
	 * @param response [参数说明]
	 * 
	 * @return void [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	void login(String ssouserid, HttpServletRequest request, HttpServletResponse response);
}
