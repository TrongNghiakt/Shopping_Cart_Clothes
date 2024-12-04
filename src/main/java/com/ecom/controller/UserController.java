package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);

	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {

		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed!");
		} else {
			session.setAttribute("succMsg", "Product added to cart!");
		}

		return "redirect:/product/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		if (!carts.isEmpty()) {

			Double totalOrderPrice = carts.stream().mapToDouble(cart -> cart.getTotalPrice()).sum();

			String formattedTotalPrice = String.format("%.2f", totalOrderPrice);
			m.addAttribute("totalOrderPrice", formattedTotalPrice);
		} else {
			session.setAttribute("errorMsg", "Your shopping cart is empty. Please add more products.");
		}

		return "/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid, HttpSession session) {
		cartService.updateQuantity(sy, cid, session);

		String errorMsg = (String) session.getAttribute("errorMsg");
		if (errorMsg != null) {
			return "redirect:/user/cart";
		}

		return "redirect:/user/cart";
	}

	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);

		return userDtls;
	}

	// Code Order

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		if (carts.isEmpty()) {
			m.addAttribute("errorMsg", "You must select at least one product to proceed with payment.");
			return "redirect:/user/cart";
		}

		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 5 + 10;
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";
	}

	@PostMapping("/save-order")
	public String savePage(@ModelAttribute OrderRequest request, Principal p) throws Exception {
		// System.out.println(request);

		UserDtls user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrderByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		return "/user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status updated!");
		} else {
			session.setAttribute("errorMsg", "Status not updated!");
		}

		return "redirect:/user/user-orders";
	}

	@GetMapping("/profile")
	public String profile() {

		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);

		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated!");
		} else {
			session.setAttribute("succMsg", "Profile updated!");
		}

		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String confirmPassword,
			@RequestParam String currentPassword, Principal p, HttpSession session) {
		if (!newPassword.equals(confirmPassword)) {
			session.setAttribute("errorMsg", "New Password and Confirm Password do not match!");
			return "redirect:/user/profile";
		}

		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);
		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated successfully!");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password Incorrect!");
		}

		return "redirect:/user/profile";
	}

}
