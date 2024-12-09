package com.ecom.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecom.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
	List<ProductOrder> findByUserId(Integer userId);

	ProductOrder findByOrderId(String orderId);

	Page<ProductOrder> findAll(Pageable pageable);

	List<ProductOrder> findByProductId(Integer id);

	List<ProductOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

	List<ProductOrder> findByStatus(String status);

	List<ProductOrder> findByStatusAndOrderDateBetween(String status, LocalDate startDate, LocalDate endDate);

	// Tính tổng doanh thu
	@Query("SELECT SUM(po.price * po.quantity) FROM ProductOrder po WHERE po.status = 'Delivered'")
	Double calculateTotalRevenue();

	// Tính doanh thu theo ngày
	@Query("SELECT SUM(po.price * po.quantity) FROM ProductOrder po WHERE po.orderDate = :date AND po.status = 'Delivered'")
	Double calculateDailyRevenue(@Param("date") LocalDate date);

	// Tính doanh thu theo phạm vi ngày
	@Query("SELECT SUM(po.price * po.quantity) FROM ProductOrder po WHERE po.orderDate BETWEEN :startDate AND :endDate AND po.status = 'Delivered'")
	Double calculateRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
