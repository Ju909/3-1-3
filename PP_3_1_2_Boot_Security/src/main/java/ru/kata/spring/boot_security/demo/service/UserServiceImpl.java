package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final RoleDao roleDao;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.passwordEncoder = passwordEncoder;
		addDefault();
	}

	@Transactional
	@Override
	public void addUser(User user) {
		if (user.getRoles().isEmpty()) {
			user.setRoles(Collections.singleton(roleDao.findByName("ROLE_USER")));
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userDao.save(user);
	}

	@Transactional
	@Override
	public void addRole (Role role) {
		roleDao.save(role);
	}

	@Transactional
	@Override
	public void removeUserById(Long id) {
		userDao.deleteById(id);
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.findAll();
	}

	@Override
	public List<Role> getAllRoles() {
		return roleDao.findAll();
	}

	@Override
	public User getUserById (Long id) {
		return userDao.findById(id).get();
	}

	@Transactional
	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	public void addDefault() {
		Role roleUser = new Role(("ROLE_USER"));
		Role roleAdmin = new Role("ROLE_ADMIN");
		Set<Role> userRoles = new HashSet<Role>();
		Set<Role> adminRoles = new HashSet<Role>();
		userRoles.add(roleUser);
		adminRoles.add(roleAdmin);
		roleDao.save(roleUser);
		roleDao.save(roleAdmin);

		User admin = new User("Oleg", "Rogov", 25, "admin",  "325!");
		User user = new User("Sergey", "Sidorov", 40, "user", "112!");
		user.setRoles(userRoles);
		user.setRoles(adminRoles);
		userDao.save(user);
		userDao.save(admin);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
}
