package com.rahul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddingCssJsAndImages {

	@GetMapping(value = "/example")
	public String examples() {
		return "example";
	}

}
