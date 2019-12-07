package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.data.Version;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VersionTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JavaScriptFrameworkRepository repository;

    private void prepareData() throws Exception {
        JavaScriptFramework react = new JavaScriptFramework("ReactJS");
        JavaScriptFramework vue = new JavaScriptFramework("Vue.js");

        repository.save(react);
        repository.save(vue);
    }

    private JavaScriptFramework prepareValidFramework(){
        JavaScriptFramework framework = new JavaScriptFramework("Node.js");
        framework.setDeprecationDate(LocalDate.of(2020, 12, 24));
        framework.setHypeLevel(90);

        return framework;
    }

    @Test
    public void addValidVersions() throws Exception{
        JavaScriptFramework framework = prepareValidFramework();
        long id = repository.save(framework).getId();

        Version one = new Version("1.0.1", LocalDate.of(2020, 12, 5));
        Version two = new Version("1.0.2", LocalDate.of(2020, 12, 24));
        mockMvc.perform(post("/frameworks/"+id+"/addVersion").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(one)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versions", hasSize(1)))
                .andExpect(jsonPath("$.versions[0].name", is("1.0.1")));

        mockMvc.perform(post("/frameworks/"+id+"/addVersion").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(two)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versions", hasSize(2)))
                .andExpect(jsonPath("$.versions[0].name", is("1.0.1")))
                .andExpect(jsonPath("$.versions[1].name", is("1.0.2")));
    }

    @Test
    public void addInvalidVersionName() throws Exception{
        JavaScriptFramework framework = prepareValidFramework();
        long id = repository.save(framework).getId();

        Version emptyName = new Version("", LocalDate.of(2020, 12, 24));
        Version longName = new Version("very long version name will not be accepted", LocalDate.of(2020, 12, 24));

        mockMvc.perform(post("/frameworks/"+id+"/addVersion").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(emptyName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));

        mockMvc.perform(post("/frameworks/"+id+"/addVersion").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(longName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("Size")));

    }

    @Test
    public void addInvalidVersionReleaseDate() throws Exception{
        JavaScriptFramework framework = prepareValidFramework();
        long id = repository.save(framework).getId();

        Version emptyName = new Version("1.0.1", null);

        mockMvc.perform(post("/frameworks/"+id+"/addVersion").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(emptyName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("releaseDate")))
                .andExpect(jsonPath("$.errors[0].message", is("NotNull")));
    }

    @Test
    public void addVersionToNonExistingFramework() throws Exception{
        prepareData();

        Version one = new Version("1.0.1", LocalDate.of(2020, 12, 5));

        mockMvc.perform(post("/frameworks/"+10+"/addVersion").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(one)))
                .andExpect(status().isNotFound());

    }



}
