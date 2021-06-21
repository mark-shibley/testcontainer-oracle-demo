package com.ms.oracle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class MvcHelper {
    public static MvcResult doPost(MockMvc mockMvc, String url, Object obj) throws Exception {
        return mockMvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(obj)))
                .andReturn();
    }

    public static MvcResult doGet(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(
                get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public static <T> T deserializeObject (MvcResult result, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(result.getResponse().getContentAsString(), clazz);
    }

    public static <T> List<T> deserializeIntoList(String json, Class<T> clazz) throws IOException {
        CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, clazz);
        return new ObjectMapper().readValue(json, collectionType);
    }
}
