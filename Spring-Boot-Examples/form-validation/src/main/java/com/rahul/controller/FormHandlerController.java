package com.rahul.controller;

// Import Spring MVC annotations and validation classes
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.rahul.entity.LoginData;

// Import Jakarta Validation
import jakarta.validation.Valid;

// Mark this class as a Spring MVC controller
@Controller
public class FormHandlerController {

	// Handler for GET requests to "/formrequest"
	// This displays the form to the user
	@GetMapping("/formrequest")
	public String formValidation(Model model) {

		// Add an empty LoginData object to the model
		// Thymeleaf will use this to bind form fields
		model.addAttribute("LoginData", new LoginData());

		// Return the name of the Thymeleaf template to render (form.html)
		return "form";
	}

	// Handler for POST requests to "/process"
	// This processes the form submission
	@PostMapping("/process")
	public String processingForm(@Valid @ModelAttribute("LoginData") LoginData loginData, // Bind form data to LoginData
																							// and validate
			BindingResult result, // Holds validation results
			Model model) { // Used to pass attributes to the view

		// Print the submitted LoginData object to the console
		System.out.println(loginData);

		// Check if validation errors exist
		if (result.hasErrors()) {
			// Add the same LoginData object back to the model
			// So that the form can be re-rendered with previously entered values
			model.addAttribute("LoginData", loginData);

			// Print the validation errors to console for debugging
			System.out.println(result);

			// Return the form page again to show errors to the user
			return "form";
		}

		// If no validation errors, add the submitted data to the model
		// This allows the success page to display the submitted values
		model.addAttribute("LoginData", loginData);

		// Return the name of the success page template
		return "success";
	}
}
