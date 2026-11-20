package pma.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

/**
 * Regras de negócio do Product. Erros viram status HTTP via ResponseStatusException.
 * A validação dos campos (obrigatórios, tamanho, preço positivo) é feita antes, pelo @Valid
 * do ProductController, e devolve 400 sem chegar aqui.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  @Transactional
  public ProductOut create(ProductIn in) {
    ProductModel model = ProductParser.to(in);
    model.setId(UUID.randomUUID().toString());
    return ProductParser.to(productRepository.save(model));
  }

  /** Ordenado por nome para a listagem sair sempre na mesma ordem. */
  @Transactional(readOnly = true)
  public List<ProductOut> findAll() {
    return productRepository.findAll(Sort.by("name")).stream()
        .map(ProductParser::to)
        .toList();
  }

  @Transactional(readOnly = true)
  public ProductOut findById(String id) {
    return productRepository.findById(id)
        .map(ProductParser::to)
        .orElseThrow(ProductService::notFound);
  }

  @Transactional
  public void delete(String id) {
    if (!productRepository.existsById(id)) {
      throw notFound();
    }
    productRepository.deleteById(id);
  }

  private static ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "produto não encontrado");
  }

}
