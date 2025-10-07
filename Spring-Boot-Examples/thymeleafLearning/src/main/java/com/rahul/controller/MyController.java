package com.rahul.controller;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MyController {


    //Basic varible examples 
	@GetMapping(value="/about")
    public String about(Model model) {
        
    	model.addAttribute("name","Rahul");
        model.addAttribute("currentTime", new Date().toString());
        model.addAttribute("lastName","Wagh");
        
        return "about";  // Thymeleaf template name
    }
	
	
    // Example for iterating an loop   
    @GetMapping(value="/loop")
    public String ietrateLoop(Model model) {
        List<String> names = List.of("Rahul","Ram","Ratan","Raj");
    	model.addAttribute("names",names);
    	return "iterate";
    }
    
    // Exmaple of conditional statment
    @GetMapping(value="/conditional")
    public String conditional(Model model) {
    	model.addAttribute("isActive",true);
    	model.addAttribute("gender","F");
    	
    	List<Integer> numbers = List.of(111,112,12,14,15);
    	List<Integer> numbers1 = List.of();
    	model.addAttribute("numbers",numbers1);
		return "conditional";
    	
    }
    
    //Using an fragment tag
    @GetMapping(value="/fragment")
    public String fragmentController(Model model) {
    	return "fragment";
    }
    
    
    
    
    
}
