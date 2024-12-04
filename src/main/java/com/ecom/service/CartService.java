package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.Cart;

import jakarta.servlet.http.HttpSession;

@Service
public interface CartService {
	public Cart saveCart(Integer productId, Integer userId);

	public List<Cart> getCartsByUser(Integer userId);

	public Integer getCountCart(Integer userId);

	public void updateQuantity(String sy, Integer cid, HttpSession session);

}
