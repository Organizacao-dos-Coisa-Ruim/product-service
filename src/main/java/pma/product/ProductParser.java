package pma.product;

/** Conversão entre a entidade (ProductModel) e os DTOs (ProductIn / ProductOut). */
public class ProductParser {

  private ProductParser() {
  }

  /** O id não vem do cliente: quem gera é o ProductService. */
  public static ProductModel to(ProductIn in) {
    return ProductModel.builder()
        .name(in.name().trim())
        .price(in.price())
        .unit(in.unit().trim())
        .build();
  }

  public static ProductOut to(ProductModel model) {
    return ProductOut.builder()
        .id(model.getId())
        .name(model.getName())
        .price(model.getPrice())
        .unit(model.getUnit())
        .build();
  }

}
