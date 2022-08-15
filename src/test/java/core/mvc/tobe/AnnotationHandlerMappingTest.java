package core.mvc.tobe;

import core.db.DataBase;
import core.mvc.ModelAndView;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;

public class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;

    @BeforeEach
    public void setup() {
        handlerMapping = new AnnotationHandlerMapping("core.mvc.tobe");
        handlerMapping.initialize();
    }

    @Test
    void create_find() throws Exception {
        User user = new User("pobi", "password", "포비", "pobi@nextstep.camp");
        createUser(user);
        assertThat(DataBase.findUserById(user.getUserId())).isEqualTo(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        request.setParameter("userId", user.getUserId());
        MockHttpServletResponse response = new MockHttpServletResponse();
        ControllerExecutor execution = handlerMapping.getHandler(request);
        Map<String, Object> model = execution.execute(request, response).getModel();

        assertThat(model.get("user")).isEqualTo(user);
    }

    @DisplayName("RequestMethod 없을 경우 모든 method를 지원해야 한다.")
    @Test
    void testAllRequestMethod() {
        Arrays.stream(HttpMethod.values()).forEach(method -> {
            MockHttpServletRequest request = new MockHttpServletRequest(method.name(), "/testAllMethods");
            MockHttpServletResponse response = new MockHttpServletResponse();
            ControllerExecutor execution = handlerMapping.getHandler(request);

            try {
                Map<String, Object> model = execution.execute(request, response).getModel();
                assertThat(model.get("status")).isEqualTo("ok");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createUser(User user) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/users");
        request.setParameter("userId", user.getUserId());
        request.setParameter("password", user.getPassword());
        request.setParameter("name", user.getName());
        request.setParameter("email", user.getEmail());
        MockHttpServletResponse response = new MockHttpServletResponse();
        ControllerExecutor execution = handlerMapping.getHandler(request);
        execution.execute(request, response);
    }
}
