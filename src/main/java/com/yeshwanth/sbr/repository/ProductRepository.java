package com.yeshwanth.sbr.repository;

import com.yeshwanth.sbr.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ProductRepository extends JpaRepository<Product, String> {
    
    Product findByCode(String code);

}
