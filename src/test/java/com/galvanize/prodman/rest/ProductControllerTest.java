package com.galvanize.prodman.rest;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.exception.InvalidCurrencyException;
import com.galvanize.prodman.exception.ProductNotFoundException;
import com.galvanize.prodman.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    private ProductService mockProductService;
    @InjectMocks
    private ProductController productController;

    private static final Integer PRODUCT_ID = 1;
    private static final Integer UNKNOWN_PRODUCT_ID = 500;

    private static final String PRODUCT_DESCRIPTION = "Example description";
    private static final String PRODUCT_NAME = "Example product name";
    private static final Integer PRODUCT_VERSION = 1;
    private static final Double PRODUCT_PRICE = 2.0;
    private static final Integer PRODUCT_VIEWS = 1;

    @Test
    void getProductWhenProductIdUnknown() {
        ResponseStatusException responseStatusException =
                assertThrows(ResponseStatusException.class, () ->
                        productController.getProduct(UNKNOWN_PRODUCT_ID, "USD"));
        String expectedMessage = String.format("404 NOT_FOUND \"Product with id: %s not found\"", UNKNOWN_PRODUCT_ID);
        assertEquals(expectedMessage, responseStatusException.getMessage());
    }

    @Test
    void getProductWhenCurrencyNotProvided() throws InvalidCurrencyException, ProductNotFoundException {
        Product product = setupProduct();
        when(mockProductService.get(PRODUCT_ID, "USD"))
                .thenReturn(product);
        Product p = productController.getProduct(PRODUCT_ID, null);
        assertEquals(PRODUCT_ID, p.getId());
    }

    @Test
    void getProductWhenCurrencyAbbreviationIsEUR() throws InvalidCurrencyException, ProductNotFoundException {
        Product product = setupProduct();
        when(mockProductService.get(PRODUCT_ID, "EUR"))
                .thenReturn(product);
        Product p = productController.getProduct(PRODUCT_ID, "EUR");
        assertEquals(PRODUCT_ID, p.getId());
    }

    @Test
    void addProduct() {
        Integer id = productController.addProduct("NAME", "DESCRIPTION", 2.5);
        assertEquals(0, id);
    }

    @Test
    void deleteProduct() {
        productController.deleteProduct(1);
    }

    private Product setupProduct() {
        Product product = new Product();
        product.setPrice(PRODUCT_PRICE);
        product.setName(PRODUCT_NAME);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setId(PRODUCT_ID);
        product.setVersion(PRODUCT_VERSION);
        product.setViews(PRODUCT_VIEWS);
        return product;
    }
}