package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/")
public class UserController {

	private final UserServiceImpl userService;

	public UserController(UserServiceImpl userService) {
		this.userService = userService;
	}

	@GetMapping("user")
	public String infoUsers(Principal principal, Model model) {
		User user= userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		return "user";
	}

	@GetMapping("admin")
	public String listUser(Model model) {
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		return "admin";
	}

	@GetMapping(value = "create")
	public String newUser(User user,Model model) {
		model.addAttribute(user);
		model.addAttribute("roles", userService.getAllRoles());
		return "create";
	}

	@PostMapping(value = "create")
	public String newUser(@Valid User user, BindingResult bindingResult, Model model) {
		model.addAttribute("roles", userService.getAllRoles());
		if (bindingResult.hasErrors()) {
			return  "create";
		}
		if (userService.findByUsername(user.getUsername()) != null) {
			bindingResult.addError(new FieldError("username", "username",String.format("username: \"%s\" занят!", user.getUsername())));
			return "create";
		}
		userService.addUser(user);
		return "redirect:/admin";
	}

	@GetMapping("/{id}/editUser")
	public String goPageEditUser(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("roles", userService.getAllRoles());
		return "update";
	}

	@PostMapping("/{id}/editUser")
	public String goPageUpdateUser(@Valid User user, BindingResult bindingResult, Model model) {
		model.addAttribute("roles", userService.getAllRoles());
		if (bindingResult.hasErrors()) {
			return "update";
		}
		User userDB = userService.findByUsername(user.getUsername());
		if (userDB.getId()!=user.getId() && userDB != null) {
			bindingResult.addError(new FieldError("username", "username",
					String.format("username: \"%s\" занят!", user.getUsername())));
			return "update";
		}
		userService.addUser(user);
		return "redirect:/admin";
	}

	@GetMapping("/{id}/delete")
	public String deleteUser(@PathVariable("id") Long id) {
		userService.removeUserById(id);
		return "redirect:/admin";
	}
}