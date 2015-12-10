package org.springfox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import springfox.documentation.staticdocs.Swagger2MarkupResultHandler;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import static org.asciidoctor.Asciidoctor.Factory.create;
import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.Placement;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CentralSpringfoxConfiguration.class)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public class CentralSpringfoxScenario {
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
	public void createStaticDocs() throws Exception {
		try {
			mockMvc.perform(get(pathApidoсs).accept(MediaType.APPLICATION_JSON).param("group", "All")
					.param("sLogin", "kermit").param("sPassword", "kermit")
					.header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE="))
					.andDo(Swagger2MarkupResultHandler.outputDirectory(pathAsciidoсs).build())
					.andExpect(status().isOk());
		} catch (Exception e) {
			System.out.println("[WARNING] createSwaggerDocs = " + e.getMessage());
		}

		// createDocs("pdf");
		// createDocs("html5");
	}

	// private void createDocs(String backend) {
	// // создание docbook-документации
	// Attributes attributesDoc = new Attributes();
	// attributesDoc.setBackend(backend);
	// attributesDoc.setAnchors(true);
	// attributesDoc.setTableOfContents2(Placement.LEFT);
	// attributesDoc.setSectionNumbers(true);
	// attributesDoc.setCopyCss(true);
	//
	// Options optionsDoc = new Options();
	// optionsDoc.setAttributes(attributesDoc);
	// optionsDoc.setInPlace(true);
	//
	// Asciidoctor asciidoctorDoc = create();
	//
	// String[] result = asciidoctorDoc.convertDirectory(new
	// AsciiDocDirectoryWalker(pathAsciidoсs), optionsDoc);
	//
	// for (String html : result) {
	// System.out.println(html);
	// }
	//
	// }

}
