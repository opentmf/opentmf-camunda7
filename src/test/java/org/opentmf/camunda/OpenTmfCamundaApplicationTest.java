package org.opentmf.camunda;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class OpenTmfCamundaApplicationTest {

  @Test
  void mainDelegatesToSpringApplicationRun() {
    try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
      OpenTmfCamundaApplication.main(new String[] {"--spring.main.web-application-type=none"});
      springApplication.verify(
          () ->
              SpringApplication.run(
                  OpenTmfCamundaApplication.class, "--spring.main.web-application-type=none"));
    }
  }
}
