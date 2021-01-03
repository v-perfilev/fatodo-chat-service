package com.persoff68.fatodo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FatodoMessageServiceApplicationTests {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void contextLoads() throws Exception {
        FatodoMessageServiceApplication.main(new String[]{});
        mvc.perform(get("/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testWrongPath() throws Exception {
        mvc.perform(get("/wrong-path"))
                .andExpect(status().isNotFound());
    }

}
