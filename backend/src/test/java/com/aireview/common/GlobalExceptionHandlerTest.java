package com.aireview.common;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ValidationProbeController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void invalidInputUsesTheApiEnvelope() throws Exception {
        mockMvc.perform(post("/test/validation").contentType(APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }
}

@RestController
class ValidationProbeController {
    @PostMapping("/test/validation")
    Result<Void> validate(@Valid @RequestBody ValidationProbe request) {
        return Result.ok(null);
    }
}

record ValidationProbe(@NotBlank(message = "名称不能为空") String name) {
}
