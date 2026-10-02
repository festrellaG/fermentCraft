package com.test.canonical.fermentcraft.service;

import com.test.canonical.fermentcraft.dto.PageResultDTO;
import com.test.canonical.fermentcraft.dto.ProductBatchRequestDTO;
import com.test.canonical.fermentcraft.dto.ProductBatchResponseDTO;
import com.test.canonical.fermentcraft.entity.ProductBatch;
import com.test.canonical.fermentcraft.exception.ResourceNotFoundException;
import com.test.canonical.fermentcraft.mapper.ProductBatchMapper;
import com.test.canonical.fermentcraft.repository.ProductBatchRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Objects;

@ApplicationScoped
public class ProductBatchService {

    private final ProductBatchRepository repository;
    private final ProductBatchMapper mapper;

    @Inject
    public ProductBatchService(ProductBatchRepository repository, ProductBatchMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PageResultDTO<ProductBatchResponseDTO> listBatches(int page, int size, String category) {
        if (page < 0) {
            throw new IllegalArgumentException("page no puede ser negativo");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size debe ser mayor que cero");
        }

        var query = repository.findActiveByCategory(category, Page.of(page, size));
        var content = query.list().stream()
                .map(mapper::toDTO)
                .toList();
        return PageResultDTO.of(content, page, size, query.count());
    }

    public ProductBatchResponseDTO getById(Long id) {
        return mapper.toDTO(findActiveById(id));
    }

    @Transactional
    public ProductBatchResponseDTO create(ProductBatchRequestDTO dto) {
        Objects.requireNonNull(dto, "dto no puede ser null");
        ProductBatch entity = mapper.toEntity(dto);
        repository.persist(entity);
        return mapper.toDTO(entity);
    }

    @Transactional
    public ProductBatchResponseDTO update(Long id, ProductBatchRequestDTO dto) {
        Objects.requireNonNull(dto, "dto no puede ser null");
        ProductBatch entity = findActiveById(id);
        mapper.updateEntityFromDTO(entity, dto);
        return mapper.toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        findActiveById(id).setActive(false);
    }

    private ProductBatch findActiveById(Long id) {
        Objects.requireNonNull(id, "id no puede ser null");
        return repository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un lote activo con ID: " + id
                ));
    }
}
