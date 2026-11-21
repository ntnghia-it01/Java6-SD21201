package com.fpoly.java6.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.java6.beans.ProductBean;
import com.fpoly.java6.entities.CategoryEntity;
import com.fpoly.java6.entities.ProductEntity;
import com.fpoly.java6.enums.ProductStatus;
import com.fpoly.java6.jpas.CategoryJPA;
import com.fpoly.java6.jpas.ProductJPA;
import com.fpoly.java6.response.CategoryResponse;
import com.fpoly.java6.response.ProductResponse;

@Service
public class ProductService {

	@Autowired
	private CategoryJPA categoryJPA;
	
	@Autowired
	private ProductJPA productJPA;
	
	public void addProduct(ProductBean bean) {
//		Kiểm tra danh mục của sản phẩm có tồn tại không?
//		Do client gửi lên catId => Có khả năng danh mục không có trong DB
		
		Optional<CategoryEntity> catOptional = categoryJPA.findById(bean.getCatId());
		
		if(!catOptional.isPresent()) {
			throw new RuntimeException("Danh mục không tồn tại");
		}
		
//		Dùng base64 để nhận ảnh 
//		base64:
//		- Là một chuỗi 
//		- Định dạng: data:image/<Kiểu ảnh>,<Mã base64>
//		Kiểm tra định dạng:
//		- chuỗi phải bắt đầu bằng data:image/**
//		- Kiểu ảnh: png, jpg, jpge, webp
		
		if(!bean.getImage().startsWith("data:image/")) {
			throw new RuntimeException("File tải lên phải là ảnh");
		}
		
//		- Cắt chuỗi thành mảng từ dấu , => ["data:image/<Kiểu ảnh>", "<Mã base64>"]
//		- "data:image/<Kiểu ảnh>" => Cắt thành 1 mảng từ dấu / => ["data:image", "<Kiểu ảnh>"]
		String extend = bean.getImage().split(",")[0].split(";")[0].split("/")[1];
		
		String types[] = {"png", "jpg", "jpeg", "webp"};
//		Chuyển mảng về thành array list
		List<String> typeArray = Arrays.asList(types);
		
		System.out.println(extend);
		
		if(!typeArray.contains(extend)) {
			throw new RuntimeException("Ảnh không đúng định dạng");
		}
		
		byte image[] = Base64.getDecoder().decode(bean.getImage().split(",")[1]);
		
		String imageName = "";
		
		try {
			String UPLOAD_DIR = "images-upload";
			Files.createDirectories(Paths.get(UPLOAD_DIR));

			imageName = String.format("%d.%s", new Date().getTime(), extend); 
			Path targetPath = Paths.get(UPLOAD_DIR, imageName);

			Files.write(targetPath, image);

		} catch (IOException e) {
			throw new RuntimeException("Lưu ảnh thất bại");
		}
		
//		- Chuyển base64 => byte[] 
//		- Kiểm tra độ lớn của file => Giới hạn 5MB
//		
//		Lưu ảnh vào folder để có tên ảnh
//		Lưu thông tin sản phẩm vào db
		
		ProductEntity productEntity = new ProductEntity();
		productEntity.setName(bean.getName());
		productEntity.setDesc(bean.getDesc());
		productEntity.setPrice(bean.getPrice());
		productEntity.setQuantity(bean.getQuantity());
		productEntity.setStatus(bean.getStatus().getValue());
		productEntity.setImage(imageName);
		productEntity.setCategoryEntity(catOptional.get());
		
		productJPA.save(productEntity);
		
	}
	
//	Khai báo throws để biết khi gọi phương thức updateProduct
//	Bắt buộc phải nằm trong try catch
	public void updateProduct(ProductBean bean, int id) throws RuntimeException, SQLException{
		
		Optional<ProductEntity> prodOptional = productJPA.findById(id);
		
		if(!prodOptional.isPresent()) {
			throw new RuntimeException("Sản phẩm không tồn tại");
		}
		
		Optional<CategoryEntity> catOptional = categoryJPA.findById(bean.getCatId());
		
		if(!catOptional.isPresent()) {
			throw new RuntimeException("Danh mục không tồn tại");
		}
		
		if(!bean.getImage().startsWith("data:image/")) {
			throw new RuntimeException("File tải lên phải là ảnh");
		}
		
		String extend = bean.getImage().split(",")[0].split(";")[0].split("/")[1];
		
		String types[] = {"png", "jpg", "jpeg", "webp"};
		List<String> typeArray = Arrays.asList(types);
		
		System.out.println(extend);
		
		if(!typeArray.contains(extend)) {
			throw new RuntimeException("Ảnh không đúng định dạng");
		}
		
		byte image[] = Base64.getDecoder().decode(bean.getImage().split(",")[1]);
		
		String imageName = "";
		
		try {
			String UPLOAD_DIR = "images-upload";
			Files.createDirectories(Paths.get(UPLOAD_DIR));

			imageName = String.format("%d.%s", new Date().getTime(), extend); 
			Path targetPath = Paths.get(UPLOAD_DIR, imageName);

			Files.write(targetPath, image);

		} catch (IOException e) {
			throw new RuntimeException("Lưu ảnh thất bại");
		}
		
		ProductEntity productEntity = new ProductEntity();
		productEntity.setId(id);
		productEntity.setName(bean.getName());
		productEntity.setDesc(bean.getDesc());
		productEntity.setPrice(bean.getPrice());
		productEntity.setQuantity(bean.getQuantity());
		productEntity.setStatus(bean.getStatus().getValue());
		productEntity.setImage(imageName);
		productEntity.setCategoryEntity(catOptional.get());
		
		productJPA.save(productEntity);
		
	}
	
	public List<ProductResponse> getProducts(){
		List<ProductEntity> prodEntities = productJPA.findAll();
		
		List<ProductResponse> prodResponses = new ArrayList<ProductResponse>();
		
		for(ProductEntity prodEntity : prodEntities) {
			prodResponses.add(convertEntityToResponse(prodEntity));
		}
		
		return prodResponses;
	} 
	
	public List<ProductResponse> getProductsPublic(){
		List<ProductEntity> prodEntities = productJPA.getListByStatusTrue();
		
		List<ProductResponse> prodResponses = new ArrayList<ProductResponse>();
		
		for(ProductEntity prodEntity : prodEntities) {
			prodResponses.add(convertEntityToResponse(prodEntity));
		}
		
		return prodResponses;
	} 
	
	public ProductResponse getProductDetail(int id) {
		Optional<ProductEntity> prodOptional = productJPA.findById(id);
		
		if(!prodOptional.isPresent()) {
			throw new RuntimeException("Sản phẩm không tồn tại");
		}
		
		return convertEntityToResponse(prodOptional.get());
	}
	
	public void updateProductStatus(int id, int status) {
		try {
			Optional<ProductEntity> prodOptional = productJPA.findById(id);
			
			if(!prodOptional.isPresent()) {
				throw new RuntimeException("Sản phẩm không tồn tại");
			}
			
			ProductEntity productEntity = prodOptional.get();
			productEntity.setStatus(status);
			productJPA.save(productEntity);
		}catch(Exception e) {
			throw new RuntimeException("Có lỗi khi cập nhật trạng thái");
		}
	}
	
//	DTO
	public static ProductResponse convertEntityToResponse(ProductEntity productEntity) {
		ProductResponse productResponse = new ProductResponse();
		productResponse.setName(productEntity.getName());
		productResponse.setDesc(productEntity.getDesc());
		productResponse.setPrice(productEntity.getPrice());
		productResponse.setQuantity(productEntity.getQuantity());
		productResponse.setStatus(productEntity.getStatus());
		productResponse.setImage(String.format("/images-upload/%s", productEntity.getImage()));
		productResponse.setCategory(CategoryService
				.convertEntityToResponse(productEntity.getCategoryEntity()));
		productResponse.setStatusString(ProductStatus
				.getProductStatus(productEntity.getStatus()).getDesc());
		return productResponse;
	}
}
