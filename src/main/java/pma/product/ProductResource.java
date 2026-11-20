package pma.product;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Implementa o contrato ProductController: as rotas e o @Valid vêm das anotações da interface.
 * Só repassa para o ProductService e monta a resposta HTTP.
 */
@RestController
@RequiredArgsConstructor
public class ProductResource implements ProductController {

  private final ProductService productService;

  @Override
  public ResponseEntity<ProductOut> create(ProductIn in) {
    ProductOut out = productService.create(in);
    return ResponseEntity.created(URI.create("/products/" + out.id())).body(out);
  }

  @Override
  public ResponseEntity<List<ProductOut>> findAll() {
    return ResponseEntity.ok(productService.findAll());
  }

  @Override
  public ResponseEntity<ProductOut> findById(String id) {
    return ResponseEntity.ok(productService.findById(id));
  }

  @Override
  public ResponseEntity<Void> delete(String id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> healthCheck() {
    return ResponseEntity.ok().build();
  }

}
