package com.ecom.service.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.repository.ProductOrderRepository;

@Service
public class RevenueService {

	@Autowired
	private ProductOrderRepository productOrderRepository;

	// Tính tổng doanh thu
	public Double getTotalRevenue() {
		return productOrderRepository.calculateTotalRevenue();
	}

	// Tính doanh thu cho một ngày
	public Double getDailyRevenue(LocalDate date) {
		return productOrderRepository.calculateDailyRevenue(date);
	}

	// Tính doanh thu trong phạm vi ngày
	public Double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
		return productOrderRepository.calculateRevenueByDateRange(startDate, endDate);
	}
}
