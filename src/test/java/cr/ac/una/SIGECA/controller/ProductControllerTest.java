package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.logic.LogicProduct;
import cr.ac.una.SIGECA.service.CourtService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ProductControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductController controller = new ProductController();
        LogicProduct logicProduct = Mockito.mock(LogicProduct.class);
        CourtService courtService = Mockito.mock(CourtService.class);

        Mockito.when(logicProduct.getProducts()).thenReturn(List.of());
        Mockito.when(courtService.listCourts()).thenReturn(List.of());

        ReflectionTestUtils.setField(controller, "logicProduct", logicProduct);
        ReflectionTestUtils.setField(controller, "courtServi", courtService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void adminListReturnsDashboardFragmentForAjaxRequests() throws Exception {
        mockMvc.perform(get("/products/admin/list")
                        .sessionAttr("usuarioLogueado", new User())
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list_product :: contenido"));
    }

    @Test
    void userListReturnsDashboardFragmentForAjaxRequests() throws Exception {
        mockMvc.perform(get("/products/user/list").header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product_client :: contenido"));
    }
}
