package com.ecom.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {
		// Lấy danh sách giỏ hàng của người dùng
		List<Cart> carts = cartRepository.findByUserId(userid);

		// Danh sách lưu trữ tất cả đơn hàng (ProductOrder) để gửi email sau
		List<ProductOrder> productOrders = new ArrayList<>();

		for (Cart cart : carts) {
			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());

			// Thiết lập thông tin địa chỉ giao hàng
			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());

			order.setOrderAddress(address);

			// Lưu đơn hàng
			ProductOrder saveOrder = orderRepository.save(order);
			productOrders.add(saveOrder); // Thêm vào danh sách để gửi email sau

			// Cập nhật tồn kho sản phẩm và trạng thái
			Product product = cart.getProduct();
			product.setStock(product.getStock() - cart.getQuantity());

			// Nếu tồn kho không đủ, đánh dấu sản phẩm là không còn hoạt động
			if (product.getStock() < 0) {
				product.setIsActive(false);
			}

			productRepository.save(product);
		}

		// Gửi email sau khi lưu tất cả các đơn hàng
		if (!productOrders.isEmpty()) {
			commonUtil.sendMailForProductOrder(productOrders, "success"); // Gửi email cho tất cả các đơn hàng
		}

		// Xóa tất cả các sản phẩm trong giỏ hàng
		cartRepository.deleteAll(carts);
	}

	@Override
	public List<ProductOrder> getOrderByUser(Integer userId) {
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			ProductOrder productOrders = findById.get();
			productOrders.setStatus(status);
			ProductOrder updateOrder = orderRepository.save(productOrders);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);
	}

	@Override
	public ProductOrder getOrdersByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}

}
