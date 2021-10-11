package com.galvanize.prodman.service;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.exception.InvalidCurrencyException;
import com.galvanize.prodman.exception.ProductNotFoundException;
import com.galvanize.prodman.model.FxResponse;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private FxService mockFxService;
    @InjectMocks
    private ProductService service;

    private static final String PRODUCT_DESCRIPTION = "Example description";
    private static final String PRODUCT_NAME = "Example product name";
    private static final Integer PRODUCT_VERSION = 1;
    private static final Double PRODUCT_PRICE = 2.0;
    private static final Integer PRODUCT_VIEWS = 1;
    private static final Double USD_FX_RATE = 1.0;
    private static final Double CAD_FX_RATE = 1.25;
    private static final Double EUR_FX_RATE = 1.65;
    private static final String UNSUPPORTED_CURRENCY_ABBREVIATION = "PLN";
    private static final String VALID_CURRENCY_ABBREVIATIONS = "USD,CAD,EUR,GBP";
    private static final Integer NON_EXISTING_PRODUCT_ID = 500;

    @Test
    public void create() {
        Product product = setupProduct();
        when(mockProductRepository.save(any(Product.class)))
                .thenReturn(product);
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName(PRODUCT_NAME);
        newProduct.setPrice(PRODUCT_PRICE);
        newProduct.setDescription(PRODUCT_DESCRIPTION);
        newProduct.setId(5);
        Integer returnedId = service.create(newProduct);
        assertEquals(1, returnedId);
    }

    @Test
    void delete() {
        service.delete(1);
    }

    @Test
    public void testGetWhenUsdProvidedAsCurrency() throws InvalidCurrencyException, ProductNotFoundException {
        when(mockFxService.getSupportedCurrencies()).thenReturn(VALID_CURRENCY_ABBREVIATIONS);
        when(mockProductRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(setupProduct()));
        Product returnedProduct = service.get(1, "USD");
        assertEquals(returnedProduct.getName(), PRODUCT_NAME);
        assertEquals(PRODUCT_PRICE * USD_FX_RATE, returnedProduct.getPrice());
    }

    @Test
    public void testGetWhenUsdProvidedAsCurrencyForNonExistentId() throws InvalidCurrencyException, ProductNotFoundException {
        when(mockFxService.getSupportedCurrencies()).thenReturn(VALID_CURRENCY_ABBREVIATIONS);
        when(mockProductRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> service.get(NON_EXISTING_PRODUCT_ID, "USD"));
        String expectedMessage = String.format("Product with id: %s does not exist.", NON_EXISTING_PRODUCT_ID);
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testGetWhenEurProvidedAsCurrency() throws InvalidCurrencyException, ProductNotFoundException {
        Product product = setupProduct();
        when(mockProductRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(product));
        when(mockProductRepository.save(any(Product.class)))
                .thenReturn(product);
        setupFxService();
        Product returnedProduct = service.get(1, "EUR");
        assertEquals(returnedProduct.getName(), PRODUCT_NAME);
        assertEquals(PRODUCT_PRICE * EUR_FX_RATE, returnedProduct.getPrice());
    }

    @Test
    public void testGetWhenCadProvidedAsCurrency() throws InvalidCurrencyException, ProductNotFoundException {
        Product product = setupProduct();
        when(mockProductRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(product));
        when(mockProductRepository.save(any(Product.class)))
                .thenReturn(product);
        setupFxService();
        Product returnedProduct = service.get(1, "CAD");
        assertEquals(returnedProduct.getName(), PRODUCT_NAME);
        assertEquals(PRODUCT_PRICE * CAD_FX_RATE, returnedProduct.getPrice());
    }

    @Test
    public void testGetWhenUnsupportedCurrencyAbbreviationProvided() throws InvalidCurrencyException {
        when(mockFxService.getSupportedCurrencies()).thenReturn(VALID_CURRENCY_ABBREVIATIONS);
        InvalidCurrencyException exception = assertThrows(InvalidCurrencyException.class,
                () -> service.get(1, UNSUPPORTED_CURRENCY_ABBREVIATION));
        assertEquals(InvalidCurrencyException.class, exception.getClass());
        String expectedMessage = String.format("Invalid currency abbreviation: %s", UNSUPPORTED_CURRENCY_ABBREVIATION);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private Product setupProduct() {
        Product product = new Product();
        product.setPrice(PRODUCT_PRICE);
        product.setName(PRODUCT_NAME);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setId(1);
        product.setVersion(PRODUCT_VERSION);
        product.setViews(PRODUCT_VIEWS);
        return product;
    }

    private Map<String, Double> setupFxQuotes() {
        Map<String, Double> quotes = new HashMap<>();
        quotes.put("USDUSD", USD_FX_RATE);
        quotes.put("USDCAD", CAD_FX_RATE);
        quotes.put("USDEUR", EUR_FX_RATE);
        return quotes;
    }

    private void setupFxService() {
        FxResponse fxResponse = new FxResponse();
        Map<String, Double> quotes = setupFxQuotes();
        fxResponse.setQuotes(quotes);
        when(mockFxService.getQuotes()).thenReturn(fxResponse);
        when(mockFxService.getSupportedCurrencies()).thenReturn(VALID_CURRENCY_ABBREVIATIONS);
    }
}