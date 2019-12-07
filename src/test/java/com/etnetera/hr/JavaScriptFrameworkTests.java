package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Class used for Spring Boot/MVC based tests.
 * 
 * @author Etnetera
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkTests {

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
	public void frameworksTest() throws Exception {
		prepareData();

		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));
	}
	
	@Test
	public void addFrameworkInvalid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
		
		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].message", is("Size")));

		List<JavaScriptFramework> savedFrameworks = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
		Assert.assertTrue(savedFrameworks.isEmpty());
	}

	@Test
	public void addFrameworkValid() throws Exception {
		JavaScriptFramework framework = prepareValidFramework();

		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("Node.js")))
				.andExpect(jsonPath("$.deprecationDate", is("2020-12-24")))
				.andExpect(jsonPath("$.hypeLevel", is(90)));

		List<JavaScriptFramework> savedFrameworks = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
		Assert.assertSame(1 ,savedFrameworks.size());
	}

	@Test
	public void updateFrameworkValid() throws Exception{
		JavaScriptFramework framework = prepareValidFramework();
		long id = repository.save(framework).getId();

		framework.setHypeLevel(10);
		framework.setName("Node JS");
		framework.setDeprecationDate(LocalDate.parse("2020-12-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		mockMvc.perform(put("/frameworks/"+id).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Node JS")))
				.andExpect(jsonPath("$.deprecationDate", is("2020-12-31")))
				.andExpect(jsonPath("$.hypeLevel", is(10)));
	}

	@Test
	public void updateNonExistingFramework() throws Exception{
		prepareData();
		JavaScriptFramework framework = prepareValidFramework();

		mockMvc.perform(put("/frameworks/"+10).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isNotFound());

		List<JavaScriptFramework> savedFrameworks = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
		Assert.assertSame(2 ,savedFrameworks.size());
	}

	@Test
	public void updateFrameworkInvalidName() throws Exception{
		JavaScriptFramework framework = prepareValidFramework();
		long id = repository.save(framework).getId();

		framework.setName("");

		mockMvc.perform(put("/frameworks/"+id).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("name")))
				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
	}

	@Test
	public void updateFrameworkInvalidHypeLevel() throws Exception{
		JavaScriptFramework framework = prepareValidFramework();
		long id = repository.save(framework).getId();

		framework.setHypeLevel(-10);

		mockMvc.perform(put("/frameworks/"+id).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0].field", is("hypeLevel")))
				.andExpect(jsonPath("$.errors[0].message", is("Min")));
	}

	@Test
	public void deleteExistingFramework() throws Exception{
		prepareData();
		JavaScriptFramework framework = prepareValidFramework();
		long id = repository.save(framework).getId();

		mockMvc.perform(delete("/frameworks/"+id))
				.andExpect(status().isOk());

		Assert.assertFalse(repository.existsById(id));
		List<JavaScriptFramework> savedFrameworks = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
		Assert.assertSame(2 ,savedFrameworks.size());
	}

	@Test
	public void deleteNonExistingFramework() throws Exception{
		prepareData();

		mockMvc.perform(delete("/frameworks/"+10))
				.andExpect(status().isNotFound());

		List<JavaScriptFramework> savedFrameworks = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
		Assert.assertSame(2 ,savedFrameworks.size());
	}

	//VERSION

	@Test
	public void findFrameworkByName() throws Exception{
		prepareData();

		JavaScriptFramework node = new JavaScriptFramework("Node.js");
		JavaScriptFramework react2 = new JavaScriptFramework("React is the best.js");
		JavaScriptFramework reactBeta = new JavaScriptFramework("betaReact");

		repository.save(node);
		repository.save(react2);
		repository.save(reactBeta);

		mockMvc.perform(get("/frameworks/").param("name", "React"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")))
				.andExpect(jsonPath("$[1].name", is("React is the best.js")))
				.andExpect(jsonPath("$[2].name", is("betaReact")));
	}
}
