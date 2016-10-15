package org.springfox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
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
import springfox.documentation.spring.web.plugins.Docket;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.File;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
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
	printDocs(ApplicationSwaggerConfig.EGOV_GROUP);
	printDocs(Docket.DEFAULT_GROUP_NAME);
    }

    private void printDocs(String group) {
	try {
	    String dirOut = (pathAsciidoсs + File.separator + group).toLowerCase().trim();
	    File file = new File(dirOut);
	    if (!file.exists()) {
		file.mkdirs();
	    }

	    MvcResult result = mockMvc.perform(get(pathApidoсs).accept(MediaType.APPLICATION_JSON).param("group", group)
		    .param("sLogin", "kermit").param("sPassword", "kermit")
		    .header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE=")).andReturn();
	    MockHttpServletResponse response = result.getResponse();
	    String swaggerJson = response.getContentAsString();
	    Swagger2MarkupConverter.fromString(swaggerJson).withMarkupLanguage(MarkupLanguage.ASCIIDOC)
		    .withPathsGroupedBy(GroupBy.TAGS).withDefinitionsOrderedBy(OrderBy.NATURAL).build()
		    .intoFolder(dirOut);
	} catch (Exception e) {
	    System.out.println("[WARNING] createSwaggerDocs = " + e.getMessage());
	}
    }

}
