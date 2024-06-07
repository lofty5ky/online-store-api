package me.dev.onlinestoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.constant.Constant;
import me.dev.onlinestoreapi.dto.ProductDTO;
import me.dev.onlinestoreapi.dto.ProductImageDTO;
import me.dev.onlinestoreapi.model.Product;
import me.dev.onlinestoreapi.model.ProductImage;
import me.dev.onlinestoreapi.response.ProductListResponse;
import me.dev.onlinestoreapi.response.ProductResponse;
import me.dev.onlinestoreapi.service.ProductService;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").ascending());

        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        List<ProductResponse> productResponseList = productPage.getContent();
        int totalPages = productPage.getTotalPages();

        return ResponseEntity.ok(ProductListResponse.builder()
                .products(productResponseList)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.from(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        try {
            // Split ids string to integer array
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .toList();

            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                        BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.addProduct(productDTO);

            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long productId,
                                          @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product existingProduct = productService.getProductById(productId);
            List<ProductImage> productImages = new ArrayList<>();
            files = files == null ? new ArrayList<>() : files;

            if (files.size() > Constant.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body("You cannot upload more than 5 images");
            }

            for (MultipartFile file : files) {
                if (file.getSize() == 0)
                    continue;

                // Check file size and format
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large! The maximum size is 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                // Save the file and update the thumbnail
                String fileName = storeFile(file);
                // Save to Product object in Database
                ProductImage productImage = productService.addProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(fileName)
                                .build());
                productImages.add(productImage);
            }
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image file format");
        }
        // Get the filename and clean the filename
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Use UUID to ensure file name is unique
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;

        // Directory used to save files
        Path uploadDir = Paths.get("uploads");

        // Check and create the directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Full path to the file
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        // Copy the file to the destination path
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imgName}")
    public ResponseEntity<?> viewImage(@PathVariable("imgName") String imageName) {
        try {
            Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else
                return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long productId,
                                           @Valid @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(productId, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully. id= " + productId);
    }
}
