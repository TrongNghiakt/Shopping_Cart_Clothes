package com.ecom.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.service.impl.RevenueService;

@Controller
public class RevenueController {

	@Autowired
	private RevenueService revenueService;

	// Hiển thị tổng doanh thu
	@GetMapping("/admin/revenue")
	public String getTotalRevenue(Model model) {
		Double totalRevenue = revenueService.getTotalRevenue();
		model.addAttribute("totalRevenue", totalRevenue);
		return "admin/revenue"; // trả về trang hiển thị doanh thu
	}

	// Hiển thị doanh thu cho một ngày
	@GetMapping("/admin/revenue/daily")
	public String getDailyRevenue(@RequestParam("date") String dateString, Model model) {
		LocalDate date = LocalDate.parse(dateString);
		Double dailyRevenue = revenueService.getDailyRevenue(date);
		model.addAttribute("dailyRevenue", dailyRevenue);
		model.addAttribute("date", date);
		return "admin/revenue"; // trả về trang hiển thị doanh thu
	}

	// Hiển thị doanh thu theo phạm vi ngày
	@GetMapping("/admin/revenue/range")
	public String getRevenueByDateRange(@RequestParam("startDate") String startDateString,
			@RequestParam("endDate") String endDateString, Model model) {
		LocalDate startDate = LocalDate.parse(startDateString);
		LocalDate endDate = LocalDate.parse(endDateString);
		Double revenueByDateRange = revenueService.getRevenueByDateRange(startDate, endDate);
		model.addAttribute("revenueByDateRange", revenueByDateRange);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		return "admin/revenue"; // trả về trang hiển thị doanh thu
	}
}
