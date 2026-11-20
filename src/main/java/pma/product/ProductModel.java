package pma.product;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidade JPA da tabela "product". Não é exposta na API: a conversão para DTO fica no ProductParser. */
@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {

  @Id
  @Column(name = "id_product", length = 36)
  private String id;

  @Column(name = "tx_name", nullable = false, length = 256)
  private String name;

  @Column(name = "price", nullable = false, precision = 19, scale = 2)
  private BigDecimal price;

  @Column(name = "tx_unit", nullable = false, length = 32)
  private String unit;

}
