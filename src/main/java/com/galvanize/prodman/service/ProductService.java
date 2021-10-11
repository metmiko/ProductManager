package com.galvanize.prodman.service;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.exception.InvalidCurrencyException;
import com.galvanize.prodman.exception.ProductNotFoundException;
import com.galvanize.prodman.model.FxResponse;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class ProductService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private final ProductRepository productRepository;
    private final FxService fxService;

    public ProductService(final ProductRepository productRepository, final FxService fxService) {
        this.productRepository = productRepository;
        this.fxService = fxService;
    }

    public Integer create(final ProductDTO productDTO) {
        LOG.info("Creating new product");
        final Product product = new Product();
        mapToEntity(productDTO, product);
        LOG.info("Saving product: {}", product.toString());
        return productRepository.save(product).getId();
    }

    public void delete(final Integer id) {
        LOG.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
        LOG.info("Product deleted");
    }

    public Product get(final Integer id, final String currencyAbbreviation) throws InvalidCurrencyException, ProductNotFoundException {
        LOG.info("Processing GET method with variables - id: {}, currencyAbbrev: {}", id, currencyAbbreviation);

        List<String> supportedCurrencies = getSupportedCurrencies();
        if (!supportedCurrencies.contains(currencyAbbreviation)) {
            String messageException = String.format("Invalid currency abbreviation: %s", currencyAbbreviation);
            throw new InvalidCurrencyException(messageException, 400);
        }

        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {

            Product product = productOptional.get();
            updateViewsCounter(product);

            if (!currencyAbbreviation.equals("USD")) {
                LOG.info("Fetching current currency exchange rate for: {}", currencyAbbreviation);
                String currencyAbbreviationKeyName = getCurrencyAbbreviationKey(currencyAbbreviation);
                Double currencyQuote = getCurrentCurrencyQuote(currencyAbbreviationKeyName);
                LOG.info("Currency rate for {}: {}", currencyAbbreviation, currencyQuote);
                Double priceAfterConversion = product.getPrice() * currencyQuote;
                LOG.info("Price after conversion: {}", priceAfterConversion);
                product.setPrice(priceAfterConversion);
            }
            return product;
        } else {
            String errorMessage = String.format("Product with id: %s does not exist.", id);
            LOG.error(errorMessage);
            throw new ProductNotFoundException(errorMessage, 404);
        }
    }

    private Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setViews(0);
        product.setDeleted(false);
        return product;
    }

    private String getCurrencyAbbreviationKey(String currencyAbbreviation) {
        return new StringBuilder("USD")
                .append(currencyAbbreviation)
                .toString();
    }

    private Double getCurrentCurrencyQuote(final String currencyAbbreviationKeyName) {
        FxResponse fxResponse = fxService.getQuotes();
        Double currencyQuote = fxResponse.getQuotes().get(currencyAbbreviationKeyName);
        return currencyQuote;
    }

    private void updateViewsCounter(Product product) {
        LOG.info("Updating views counter");
        Integer currentViews = product.getViews();
        LOG.info("Current views: {}", currentViews);
        product.setViews(currentViews + 1);
        LOG.info("Updating DB entry with: {}", product.getViews());
        productRepository.save(product);
    }

    private List<String> getSupportedCurrencies() {
        List<String> supportedCurrencies = new ArrayList<>();
        Collections.addAll(supportedCurrencies, fxService.getSupportedCurrencies().split(","));
        return supportedCurrencies;
    }
}
