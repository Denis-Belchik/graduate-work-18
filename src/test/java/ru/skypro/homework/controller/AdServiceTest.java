package ru.skypro.homework.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import ru.skypro.homework.HomeworkApplicationTests;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class AdServiceTest extends HomeworkApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithAnonymousUser
    @Sql(scripts = "/sql/add-ad.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-ad.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllAdsTest_OK() throws Exception {
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.results").isNotEmpty())
                .andDo(print());
    }

    @Test
    @Sql(scripts = "/sql/add-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-ad.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value="user@gmail.com", userDetailsServiceBeanName="userDetailsServiceImpl")
    void addAdTest_OK() throws Exception {

        JSONObject createOrUpdateAdDto = new JSONObject();
        createOrUpdateAdDto.put("title", "title test");
        createOrUpdateAdDto.put("price", 100);
        createOrUpdateAdDto.put("description", "description test");
        String json = createOrUpdateAdDto.toString();

        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);
        MockPart mockImage = new MockPart("image", "test_image.png", image.getBytes());
        mockImage.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);

        MockPart mockProperties = new MockPart("properties", json.getBytes());
        mockProperties.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        long id = userRepository.findUserByEmailIgnoreCase("user@gmail.com").get().getId();

        mockMvc.perform(multipart("/ads")
                        .part(mockImage, mockProperties))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").isNotEmpty())
                .andExpect(jsonPath("$.pk").isNumber())
                .andExpect(jsonPath("$.author").value(id))
                .andExpect(jsonPath("$.image").value("/ads/image/1"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.title").value("title test"));
    }

    @Test
    @WithMockUser(value = "user@gmail.com", authorities = "USER")
    @Sql(scripts = "/sql/add-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/add-ad.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-ad.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAdTest_OK() throws Exception {
        mockMvc.perform(get("/ads/1"))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.image").value(null))
                .andExpect(jsonPath("$.authorLastName").value("ln"))
                .andExpect(jsonPath("$.authorFirstName").value("fn"))
                .andExpect(jsonPath("$.phone").value("+78462265158"))
                .andExpect(jsonPath("$.price").value(123))
                .andExpect(jsonPath("$.description").value("description1"))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));
    }

}
