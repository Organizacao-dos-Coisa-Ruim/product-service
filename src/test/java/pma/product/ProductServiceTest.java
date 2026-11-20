package pma.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Testes unitários do ProductService, com o repositório mockado (sem Spring e sem banco). */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  void createGeneratesIdAndTrimsFields() {
    when(productRepository.save(any(ProductModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ProductOut out = productService.create(new ProductIn(" Tomato ", new BigDecimal("10.12"), " kg "));

    assertThat(out.id()).isNotBlank();
    assertThat(out.name()).isEqualTo("Tomato");
    assertThat(out.price()).isEqualByComparingTo("10.12");
    assertThat(out.unit()).isEqualTo("kg");
  }

  @Test
  void findByIdUnknownIs404() {
    when(productRepository.findById("x")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productService.findById("x"))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(error -> ((ResponseStatusException) error).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteUnknownIs404AndDeletesNothing() {
    when(productRepository.existsById("x")).thenReturn(false);

    assertThatThrownBy(() -> productService.delete("x"))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(error -> ((ResponseStatusException) error).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
    verify(productRepository, never()).deleteById(any());
  }

}
