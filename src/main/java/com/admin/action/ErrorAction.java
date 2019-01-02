package com.admin.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import chok.devwork.BaseController;

@Scope("prototype")
@Controller
@RequestMapping("/error")
public class ErrorAction extends BaseController<Object>
{
	@RequestMapping("/401")
	public String error401 ()
	{
		put("msg", req.getAttribute("msg"));
		return "html/error/401";
	}
}
