package com.galvanize.prodman.rest;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.exception.InvalidCurrencyException;
import com.galvanize.prodman.exception.ProductNotFoundException;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value = "/api/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(final ProductService productService) { this.productService = productService; }


    @GetMapping(value = {"product/{id}", "product/{id}/{currencyAbbreviation}"})
    Product getProduct(@PathVariable final Integer id,
                       @PathVariable(required = false) final String currencyAbbreviation) throws InvalidCurrencyException, ProductNotFoundException {
        Product result;
        try {
            if (currencyAbbreviation == null) {
                result = productService.get(id, "USD");
            } else {
                result = productService.get(id, currencyAbbreviation);
            }

            if (result == null) {
                String errorMessage = String.format("Product with id: %s not found", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
            } else {
                return result;
            }
        } catch(InvalidCurrencyException | ProductNotFoundException exception) {
            throw exception;
        }
    }

    @PostMapping("product")
    Integer addProduct(@RequestParam String productName, @RequestParam String description, @RequestParam Double price) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(productName);
        productDTO.setDescription(description);
        productDTO.setPrice(price);
        return productService.create(productDTO);
    }

    @DeleteMapping("product")
    void deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
    }
}
