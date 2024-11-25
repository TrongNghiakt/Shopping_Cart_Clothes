package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;

@Service
public interface UserService {
	public UserDtls saveUser(UserDtls user);

	public UserDtls getUserByEmail(String email);

	public List<UserDtls> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);
}
