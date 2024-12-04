package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.OrderAddress;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductOrderRepository orderRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {

		return productRepository.findAll();
	}

	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}

		return false;
	}

	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {
		Product dbproduct = getProductById(product.getId());

		String imageName = image.isEmpty() ? dbproduct.getImage() : image.getOriginalFilename();

		dbproduct.setTitle(product.getTitle());
		dbproduct.setDescription(product.getDescription());
		dbproduct.setCategory(product.getCategory());
		dbproduct.setPrice(product.getPrice());
		dbproduct.setStock(product.getStock());
		dbproduct.setImage(imageName);
		dbproduct.setIsActive(product.getIsActive());
		dbproduct.setDiscount(product.getDiscount());

		Double discount = product.getPrice() * (product.getDiscount() / 100.0);
		Double discountPrice = product.getPrice() - discount;
		dbproduct.setDiscountPrice(discountPrice);

		Product saveProduct = productRepository.save(dbproduct);
		if (!ObjectUtils.isEmpty(saveProduct)) {
			if (!image.isEmpty()) {
				try {

					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
							+ image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return product;
		}
		return null;
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		} else {
			products = productRepository.findByCategory(category);
		}

		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
	}

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
	}

	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;
		if (ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} else {
			pageProduct = productRepository.findByCategory(pageable, category);
		}

		return pageProduct;
	}

	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
		Page<Product> pageProduct = null;
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		pageProduct = productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,
				ch, pageable);

		/*
		 * if (ObjectUtils.isEmpty(category)) { pageProduct =
		 * productRepository.findByIsActiveTrue(pageable); } else { pageProduct =
		 * productRepository.findByCategory(pageable, category); }
		 */

		return pageProduct;
	}

	@Override
	public List<ProductOrder> getBestSellingProducts(int limit) {
		Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("quantity")));
		Page<ProductOrder> productPage = orderRepository.findAll(pageable);
		return productPage.getContent();
	}

	@Override
	public void placeOrder(Integer productId, Integer quantity, String orderId, UserDtls user,
			OrderAddress orderAddress, String paymentType) {
		Product product = productRepository.findById(productId).orElse(null);

		if (product.getStock() >= quantity) {
			product.setStock(product.getStock() - quantity);

			if (product.getStock() < 0) {
				product.setIsActive(false);
			}

			productRepository.save(product);

		}

	}

}
