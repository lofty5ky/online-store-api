package me.dev.onlinestoreapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dev.onlinestoreapi.constant.Constant;
import me.dev.onlinestoreapi.dto.ProductDTO;
import me.dev.onlinestoreapi.dto.ProductImageDTO;
import me.dev.onlinestoreapi.exception.InvalidParamException;
import me.dev.onlinestoreapi.exception.ResourceNotFoundException;
import me.dev.onlinestoreapi.model.Category;
import me.dev.onlinestoreapi.model.Product;
import me.dev.onlinestoreapi.model.ProductImage;
import me.dev.onlinestoreapi.repository.CategoryRepository;
import me.dev.onlinestoreapi.repository.ProductImageRepository;
import me.dev.onlinestoreapi.repository.ProductRepository;
import me.dev.onlinestoreapi.response.ProductResponse;
import me.dev.onlinestoreapi.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product addProduct(ProductDTO productDTO) {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find category with id=" + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();

        log.info("Product added");
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent())
            return optionalProduct.get();

        throw new ResourceNotFoundException("Cannot find product with id =" + productId);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        var productsPage = productRepository.searchProducts(keyword, categoryId, pageRequest);
        return productsPage.map(ProductResponse::from);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = getProductById(productId);
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setThumbnail(productDTO.getThumbnail());
        existingProduct.setDescription(productDTO.getDescription());

        if (productDTO.getCategoryId() != null) {
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find category with id=" + productDTO.getCategoryId()));
            existingProduct.setCategory(existingCategory);
        }
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Product with id = {} deleted", productId);
        productRepository.deleteById(productId);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage addProductImage(Long productId, ProductImageDTO productImageDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id=" + productId + " not found"));

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // Don't allow inserting more than 5 images
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= Constant.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Number of images in product cannot exceed "
                    + Constant.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }
}
