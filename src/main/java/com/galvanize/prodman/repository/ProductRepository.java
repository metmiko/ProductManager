package com.galvanize.prodman.repository;

import com.galvanize.prodman.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Integer> {
}
