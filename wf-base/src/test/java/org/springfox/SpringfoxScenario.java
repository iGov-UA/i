package org.springfox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.robwin.swagger2markup.GroupBy;
import io.github.robwin.swagger2markup.OrderBy;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringfoxConfiguration.class)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public class SpringfoxScenario {
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
	public static final String pathApidoсs = "/service/api-docs";
	public static final String pathAsciidoсs = "src/main/asciidoc";

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	public void createSwaggerDocs() throws Exception {
		try {
//			mockMvc.perform(get(pathApidoсs).accept(MediaType.APPLICATION_JSON).param("group", "All")
//					.param("sLogin", "kermit").param("sPassword", "kermit")
//					.header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE="))
//					.andDo(Swagger2MarkupResultHandler.outputDirectory(pathAsciidoсs).build())
//					.andExpect(status().isOk());
		    MvcResult result = mockMvc.perform(get(pathApidoсs).accept(MediaType.APPLICATION_JSON).param("group", "All")
			    .param("sLogin", "kermit").param("sPassword", "kermit")
			    .header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE=")).andReturn();
		    MockHttpServletResponse response = result.getResponse();
		    String swaggerJson = response.getContentAsString();
		    Swagger2MarkupConverter.fromString(swaggerJson).withMarkupLanguage(MarkupLanguage.ASCIIDOC)
			    .withPathsGroupedBy(GroupBy.TAGS).withDefinitionsOrderedBy(OrderBy.NATURAL).build()
			    .intoFolder(pathAsciidoсs);
		} catch (Exception e) {
			System.out.println("[WARNING] createSwaggerDocs = " + e.getMessage());
		}

	}

}
