package com.example.onlineshop.product.service;

import com.example.onlineshop.product.dto.ProductDTO;
import com.example.onlineshop.product.dto.InventoryDTO;
import com.example.onlineshop.product.entity.Product;
import com.example.onlineshop.product.entity.SKU;
import com.example.onlineshop.product.entity.Inventory;
import com.example.onlineshop.product.repository.ProductRepository;
import com.example.onlineshop.product.repository.SKURepository;
import com.example.onlineshop.product.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    // Add a new product along with SKUs (inventory handling will be done separately)
    public void addProductWithSku(ProductDTO productDTO, List<String> skuCodes) {
        // Create the product
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        productRepository.save(product);

        // Add SKUs for the product
        for (String skuCode : skuCodes) {
            SKU sku = new SKU();
            sku.setProduct(product);
            sku.setSkuCode(skuCode);
            skuRepository.save(sku);
        }
    }

    // Update an existing product along with SKUs (inventory update is separate)
    public void updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        productRepository.save(product);

        // Update SKUs for the product (assuming new SKU codes are provided)
        skuRepository.deleteByProduct_ProductId(productId); // Delete existing SKUs associated with the product

        // Process SKU list from productDTO
        List<SKU> existingSkus = skuRepository.findByProduct(product); // Fetch all existing SKUs for the product from the DB

        // Stream through the list of SKU codes from the productDTO and save only the new ones
        productDTO.getSkuDTOs().stream()
                .filter(skuDTO -> existingSkus.stream().noneMatch(existingSku -> existingSku.getSkuCode().equals(skuDTO.getSkuCode())))
                .forEach(skuDTO -> {
                    SKU sku = new SKU();
                    sku.setProduct(product);
                    sku.setSkuCode(skuDTO.getSkuCode());
                    sku.setColor(skuDTO.getColor());
                    skuRepository.save(sku); // Save new SKU
                });
    }

    // Delete a product by ID
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // Get all products
    public List<ProductDTO> getAllProductsComplete() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // Get product by ID
    public ProductDTO getProductByIdComplete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductDTO(product);
    }

    // Get products by category
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory_Subcategory(category).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // Update inventory stock for a given SKU (separate inventory handling)
    public void updateInventoryStock(Long inventoryId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setStockAvailable(quantity);
        inventoryRepository.save(inventory);
    }

    // Add inventory for SKU separately (you can call this after adding SKUs)
    public void addInventoryForSku(Long skuId, Integer stockAvailable, Double price, Double cost) {
        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        Inventory inventory = new Inventory();
        inventory.setSku(sku);
        inventory.setStockAvailable(stockAvailable);
        inventory.setSellingPrice(price);
        inventory.setCostPrice(cost);
        inventoryRepository.save(inventory);
    }
}
