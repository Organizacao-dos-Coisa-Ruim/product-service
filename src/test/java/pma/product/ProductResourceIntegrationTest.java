package pma.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Testa as rotas de ponta a ponta: HTTP (MockMvc) → Service → JPA → H2 com as migrations do Flyway. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductResourceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void cleanDatabase() {
    productRepository.deleteAll();
  }

  @Test
  void createReturns201WithLocationAndPersists() throws Exception {
    create("{\"name\":\"  Tomato \",\"price\":10.12,\"unit\":\"kg\"}")
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value("Tomato"))
        .andExpect(jsonPath("$.price").value(10.12))
        .andExpect(jsonPath("$.unit").value("kg"));

    assertThat(productRepository.findAll()).hasSize(1);
    assertThat(productRepository.findAll().get(0).getPrice()).isEqualByComparingTo("10.12");
  }

  @Test
  void createWithInvalidFieldsReturns400() throws Exception {
    create("{\"price\":10.12,\"unit\":\"kg\"}").andExpect(status().isBadRequest());
    create("{\"name\":\" \",\"price\":10.12,\"unit\":\"kg\"}").andExpect(status().isBadRequest());
    create("{\"name\":\"Tomato\",\"unit\":\"kg\"}").andExpect(status().isBadRequest());
    create("{\"name\":\"Tomato\",\"price\":0,\"unit\":\"kg\"}").andExpect(status().isBadRequest());
    create("{\"name\":\"Tomato\",\"price\":-1,\"unit\":\"kg\"}").andExpect(status().isBadRequest());
    create("{\"name\":\"Tomato\",\"price\":10.123,\"unit\":\"kg\"}").andExpect(status().isBadRequest());
    create("{\"name\":\"Tomato\",\"price\":10.12}").andExpect(status().isBadRequest());
    create("{\"name\":\"" + "a".repeat(257) + "\",\"price\":10.12,\"unit\":\"kg\"}")
        .andExpect(status().isBadRequest());
    create("{\"name\":\"Tomato\",\"price\":10.12,\"unit\":\"" + "a".repeat(33) + "\"}")
        .andExpect(status().isBadRequest());

    assertThat(productRepository.count()).isZero();
  }

  @Test
  void findAllListsProductsOrderedByName() throws Exception {
    create("{\"name\":\"Tomato\",\"price\":10.12,\"unit\":\"kg\"}");
    create("{\"name\":\"Cheese\",\"price\":0.62,\"unit\":\"slice\"}");

    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Cheese"))
        .andExpect(jsonPath("$[1].name").value("Tomato"));
  }

  @Test
  void findAllWithoutProductsReturnsEmptyList() throws Exception {
    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void findByIdReturnsProductOr404() throws Exception {
    create("{\"name\":\"Tomato\",\"price\":10.12,\"unit\":\"kg\"}");
    String id = productRepository.findAll().get(0).getId();

    mockMvc.perform(get("/products/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Tomato"))
        .andExpect(jsonPath("$.price").value(10.12))
        .andExpect(jsonPath("$.unit").value("kg"));

    mockMvc.perform(get("/products/nao-existe"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteReturns204ThenProductIsGone() throws Exception {
    create("{\"name\":\"Tomato\",\"price\":10.12,\"unit\":\"kg\"}");
    String id = productRepository.findAll().get(0).getId();

    mockMvc.perform(delete("/products/" + id))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/products/" + id))
        .andExpect(status().isNotFound());
    mockMvc.perform(delete("/products/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  void healthCheckIsNotCapturedAsId() throws Exception {
    mockMvc.perform(get("/products/health-check"))
        .andExpect(status().isOk());
  }

  private ResultActions create(String json) throws Exception {
    return mockMvc.perform(post("/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));
  }

}
