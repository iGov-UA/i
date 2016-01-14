package org.igov.service.controller.interceptor;

import org.igov.service.controller.interceptor.JsonRequestDataResolver;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;

/**
 * User: goodg_000
 * Date: 25.08.2015
 * Time: 21:43
 */
public class JsonRequestDataResolverTest {

    private JSONParser parser = new JSONParser();

    @Ignore
    @Test
    public void testGetDataFromProperties() throws Exception {
        JSONObject requestObject = (JSONObject) parser.parse(IOUtils.toString(
                this.getClass().getResourceAsStream("SaveTaskRequest1.json"), "UTF-8"));

        Map<String, String> properties = JsonRequestDataResolver.getProperties(requestObject);
        assertThat(properties).hasSize(34);

        assertThat(JsonRequestDataResolver.getEmail(requestObject)).isEqualTo("al.dubilet@gmail.com");
    }

}
