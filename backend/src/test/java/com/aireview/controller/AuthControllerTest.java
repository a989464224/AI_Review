package com.aireview.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aireview.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

class AuthControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearUsers() {
        jdbcTemplate.execute("TRUNCATE TABLE `user`");
    }

    @Test
    void registerReturnsTokenAndStoresBcryptHash() throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(credentials("alice", "secret123", null)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.user.username").value("alice"))
            .andExpect(jsonPath("$.data.user.nickname").value("alice"))
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("secret123").doesNotContain("passwordHash");

        String storedHash = jdbcTemplate.queryForObject(
            "SELECT password_hash FROM `user` WHERE username = 'alice'", String.class);
        assertThat(storedHash).startsWith("$2").isNotEqualTo("secret123");
    }

    @Test
    void duplicateUsernameIsRejected() throws Exception {
        register("alice", "secret123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(credentials("alice", "another123", null)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void invalidRegistrationInputIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(credentials("ab", "123", null)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void loginAcceptsTheStoredPassword() throws Exception {
        register("alice", "secret123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(credentials("alice", "secret123", null)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.user.username").value("alice"))
            .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void loginRejectsWrongPasswordWithoutRevealingWhichFieldFailed() throws Exception {
        register("alice", "secret123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(credentials("alice", "wrong-password", null)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(credentials("nobody", "secret123", null)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void protectedEndpointRequiresAValidToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer not-a-jwt"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void meReturnsTheTokenOwner() throws Exception {
        String token = register("alice", "secret123");
        register("bob", "secret456");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.username").value("alice"));
    }

    private String register(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(credentials(username, password, null)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).path("data").path("token").asText();
    }

    private String credentials(String username, String password, String nickname) {
        ObjectNode node = objectMapper.createObjectNode()
            .put("username", username)
            .put("password", password);
        if (nickname != null) {
            node.put("nickname", nickname);
        }
        return node.toString();
    }
}
