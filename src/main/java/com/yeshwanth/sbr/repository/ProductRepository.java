package com.yeshwanth.sbr.repository;

import com.yeshwanth.sbr.model.Cart;
import com.yeshwanth.sbr.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    
    Product findByCode(String code);

}
