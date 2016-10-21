
package org.igov.service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.model.object.ObjectCustoms;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@ActiveProfiles("default")
public class ObjectCustomsControllerTest 
{
    public static final String GET_OBJECTCUSTOMS = "/object/getObjectCustoms";
    public static final String SET_OBJECTCUSTOMS = "/object/setObjectCustoms";
    public static final String REMOVE_OBJECTCUSTOMS = "/object/removeObjectCustoms";
    public static final Integer HTTP_OK = 200;
    public static final Integer HTTP_FORBIDDEN = 403;
    public static final Integer HTTP_NOCONTENT = 204;
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    public static final String RECORD_NOT_FOUND = "record not found";
    public static final String DASH = "-";
   
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Integer HttpStatus = HTTP_OK;
    private Map<String, String> param_sIDUA;
    private Map<String, String> params_twoargs;
    private Map<String, String> param_sNameUA;
    private Map<String, String> param_sMeasure;
    private Map<String, String> params_no;
    private Map<String, String> params_nid;
    private Map<String, String> params_update;
    private Map<String, String> params_set;
    private Map<String, String> params_duplicate;
    private ObjectCustoms expObjectCustomsUpdate;
    private ObjectCustoms expObjectCustomsSet;
    private List<ObjectCustoms> pcode_list;

   
  
    @Before
    public void Init()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.params_duplicate = new HashMap<String, String>();
       
        this.param_sIDUA = new HashMap<String, String>();
        this.param_sIDUA.put("sID_UA", "0101");
        
        this.param_sNameUA = new HashMap<String, String>();
        this.param_sNameUA.put("sName_UA", "худоба");
       
        this.params_twoargs = new HashMap<String, String>();
        this.params_twoargs.put("sID_UA", "0101");
        this.params_twoargs.put("sName_UA", "Коні");
        
        this.param_sMeasure = new HashMap<String, String>();
        this.param_sMeasure.put("sMeasure_UA", "кг");
        
        this.params_no = new HashMap<String, String>();
        this.params_no.put("sID_UA", "333");
        this.params_no.put("sName_UA", "Металл");
        
        this.params_nid = new HashMap<String, String>();
        this.params_nid.put("nID", "1");
        
        this.params_update = new HashMap<String, String>();
        this.params_update.put("nID", "1");
        this.params_update.put("sID_UA", "3030 01 20 10");
        this.params_update.put("sName_UA", "Товарна позиція");
       
        this.expObjectCustomsUpdate = new ObjectCustoms();
        this.expObjectCustomsUpdate.setId(1L);
        this.expObjectCustomsUpdate.setsID_UA("3030 01 20 10");
        this.expObjectCustomsUpdate.setsName_UA("Товарна позиція");
        this.expObjectCustomsUpdate.setsMeasure_UA(DASH);
        
        this.params_set = new HashMap<String, String>();
        this.params_set.put("sID_UA", "4040 01 01 10");
        this.params_set.put("sName_UA", "Нова товарна позиція");
        this.params_set.put("sMeasure_UA", "кг");
        
        this.expObjectCustomsSet = new ObjectCustoms();
        this.expObjectCustomsSet.setId(101L);
        this.expObjectCustomsSet.setsID_UA("4040 01 01 10");
        this.expObjectCustomsSet.setsName_UA("Нова товарна позиція");
        this.expObjectCustomsSet.setsMeasure_UA("кг");
        
        
    }
    private List<ObjectCustoms> getObjectCustomsObjectsFromJsonArray(String strjson)
    {
       List<ObjectCustoms> list_pcode = new ArrayList<ObjectCustoms>();
       String subjson = strjson.substring(strjson.indexOf("[") + 1, strjson.indexOf("]"));
      
       String[] strarr = subjson.split("}");
       for(int i = 0; i < strarr.length; i++)
       {
           if(strarr[i].charAt(strarr[i].length()- 1) != '}')
           {
              String tempstr = strarr[i] + "}";
              strarr[i] = tempstr;
           }
           if(strarr[i].charAt(0) == ',')
           {
              String tempstr = strarr[i].substring(1);
              strarr[i] = tempstr;
           }
           
           ObjectCustoms pcode = JsonRestUtils.readObject(strarr[i], ObjectCustoms.class);
           list_pcode.add(pcode);
       }
       
       
       return list_pcode;
    }
    private MockHttpServletResponse getResponse(String url, Map<String, String> params) throws Exception
    {
         MockHttpServletRequestBuilder request = get(url);
         if(params != null && !params.isEmpty())
         {
             for(String key : params.keySet())
             {
                 request.param(key, params.get(key));
             }
         }
        ResultActions result = this.mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.status().is(this.HttpStatus));
        MvcResult mvcresult = result.andReturn();
        MockHttpServletResponse mockresponse = mvcresult.getResponse();
        
        return mockresponse;
         
    }
  
   @Test
    public void WithoutArgsShouldForbidden() throws Exception
    {
        //null args method getProductCodes
        this.HttpStatus = HTTP_FORBIDDEN;
        MockHttpServletResponse mockresponse = this.getResponse(GET_OBJECTCUSTOMS, null);
        String reason_error = mockresponse.getHeader("Reason");
        Assert.assertNotNull(reason_error);
       
        String jsonstring = mockresponse.getContentAsString();
        Assert.assertNotNull(jsonstring);
       
        //null args method setProductCode
        MockHttpServletResponse mockresponse_set = this.getResponse(SET_OBJECTCUSTOMS, null);
        String reason_error_set = mockresponse_set.getHeader("Reason");
        Assert.assertNotNull(reason_error_set);
       
        String jsonstring_set = mockresponse_set.getContentAsString();
        
        Assert.assertNotNull(jsonstring_set);
      //  Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"at least some parameters need to execute this service: nID, sID_UA, sName_UA\"}", jsonstring_set);
        
        //null args method removeProductCode
        MockHttpServletResponse mockresponse_remove = this.getResponse(REMOVE_OBJECTCUSTOMS, null);
        String reason_error_remove = mockresponse_remove.getHeader("Reason");
        Assert.assertNotNull(reason_error_remove);
       
        String jsonstring_remove = mockresponse_remove.getContentAsString();
        
        Assert.assertNotNull(jsonstring_remove);
       // Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"at least one parameter need to execute this service: nID, sID_UA\"}", jsonstring_remove);

    
        }
    
   
    @Test
    public void MethodsOnlyWithArg_nID() throws Exception
    {
       
        //method setProductCode with the only nID - must be error
        this.HttpStatus = HTTP_FORBIDDEN;
        Map<String, String> param_nID = new HashMap<String, String>();
        param_nID.put("nID", "1");
        MockHttpServletResponse mockresponse = this.getResponse(SET_OBJECTCUSTOMS, param_nID);
        String jsonstring = mockresponse.getContentAsString();
        
        Assert.assertNotNull(jsonstring);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"nID is the only param, it is necessary else sID_UA or/and sName_UA or/and sMeasure_UA\"}", jsonstring);
        
               
    }
    
   @Test
    public void getObjectCustomsWithArgs() throws Exception
    {
        //get item by only sID_UA format 0101 (getObjectCustoms)
        this.HttpStatus = HTTP_OK;
        MockHttpServletResponse mockresponse = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring = mockresponse.getContentAsString();
        this.pcode_list = this.getObjectCustomsObjectsFromJsonArray(jsonstring);
        Assert.assertNotNull(jsonstring);
        
        //get item by only sID_UA format 0101 01 (getObjectCustoms)
        this.HttpStatus = HTTP_OK;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101 10");
        MockHttpServletResponse mockresponse1 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring1 = mockresponse1.getContentAsString();
        this.pcode_list = this.getObjectCustomsObjectsFromJsonArray(jsonstring1);
        Assert.assertNotNull(jsonstring1);
        
        
        //get item by only sID_UA format 0101 01 01 (getObjectCustoms)
        this.HttpStatus = HTTP_OK;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101 10 10 00");
        MockHttpServletResponse mockresponse2 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring2 = mockresponse2.getContentAsString();
        this.pcode_list = this.getObjectCustomsObjectsFromJsonArray(jsonstring2);
        Assert.assertNotNull(jsonstring2);
        
        
        //get item by only sID_UA format 0101 01 01 01 (getObjectCustoms)
        this.HttpStatus = HTTP_NOCONTENT;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101 01 01 01");
        MockHttpServletResponse mockresponse3 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring3 = mockresponse3.getContentAsString();
        Assert.assertNotNull(jsonstring3);
        
        
       //get item by only sID_UA format incorrect 0101010101 — error (getObjectCustoms)
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101010101");
        MockHttpServletResponse mockresponse_formatincorrect = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring__formatincorrect = mockresponse_formatincorrect.getContentAsString();
        
        Assert.assertNotNull(jsonstring__formatincorrect);
        Assert.assertNotNull("{\"code\":\"BUSINESS_ERR\",\"message\":\"sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)\"}", jsonstring__formatincorrect);
      
        //get item by only sID_UA format incorrect 01010101 01 — error (getObjectCustoms)
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "01010101 01");
        MockHttpServletResponse mockresponse_formatincorrect1 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring__formatincorrect1 = mockresponse_formatincorrect1.getContentAsString();
        
        Assert.assertNotNull(jsonstring__formatincorrect1);
        Assert.assertNotNull("{\"code\":\"BUSINESS_ERR\",\"message\":\"sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)\"}", jsonstring__formatincorrect1);
      
        //get item by only sID_UA format incorrect 010101 01 01 — error (getObjectCustoms)
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "010101 01 01");
        MockHttpServletResponse mockresponse_formatincorrect2 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring__formatincorrect2 = mockresponse_formatincorrect2.getContentAsString();
        
        Assert.assertNotNull(jsonstring__formatincorrect2);
        Assert.assertNotNull("{\"code\":\"BUSINESS_ERR\",\"message\":\"sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)\"}", jsonstring__formatincorrect2);

        //get item by only sID_UA format incorrect 0101 01 01 01 01 — error (getObjectCustoms)
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101 01 01 01 01");
        MockHttpServletResponse mockresponse_formatincorrect3 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring__formatincorrect3 = mockresponse_formatincorrect3.getContentAsString();
        
        Assert.assertNotNull(jsonstring__formatincorrect3);
        Assert.assertNotNull("{\"code\":\"BUSINESS_ERR\",\"message\":\"sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)\"}", jsonstring__formatincorrect3);

        //get item by only sID_UA format incorrect 01 — error (getObjectCustoms)
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "01");
        MockHttpServletResponse mockresponse_formatincorrect4 = this.getResponse(GET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring__formatincorrect4 = mockresponse_formatincorrect4.getContentAsString();
        
        Assert.assertNotNull(jsonstring__formatincorrect4);
        Assert.assertNotNull("{\"code\":\"BUSINESS_ERR\",\"message\":\"sID_UA does not meet required format (0101 or 0101 01 or 0101 01 01 or 0101 01 01 01)\"}", jsonstring__formatincorrect4);

        
        //get item by only sName_UA - must be success
        this.HttpStatus = HTTP_OK; 
        MockHttpServletResponse mockresponse_name = this.getResponse(GET_OBJECTCUSTOMS, this.param_sNameUA);
        String jsonstring_name = mockresponse_name.getContentAsString();
        this.pcode_list = this.getObjectCustomsObjectsFromJsonArray(jsonstring_name);
        Assert.assertNotNull(jsonstring_name);
       
        //get items by two args sID_UA and sName_UA - must be success
        
        this.HttpStatus = HTTP_OK;
        MockHttpServletResponse mockresponse_both = this.getResponse(GET_OBJECTCUSTOMS, this.params_twoargs);
        String jsonstring_both = mockresponse_both.getContentAsString();
        this.pcode_list = this.getObjectCustomsObjectsFromJsonArray(jsonstring_both);
        Assert.assertNotNull(jsonstring_both);
        
        this.HttpStatus = HTTP_NOCONTENT;
        this.params_twoargs.clear();
        this.params_twoargs.put("sID_UA", "0101");
        this.params_twoargs.put("sName_UA", "Свині");
        MockHttpServletResponse mockresponse_both1 = this.getResponse(GET_OBJECTCUSTOMS, this.params_twoargs);
        String jsonstring_both1 = mockresponse_both1.getContentAsString();
        Assert.assertNotNull(jsonstring_both1);
       
      
    }
    
 
    
    @Test
    public void setObjectCustomsWithoutArgs() throws Exception
    {
        //set item without args — must be error
        this.HttpStatus = HTTP_FORBIDDEN;
        MockHttpServletResponse mockresponse = this.getResponse(SET_OBJECTCUSTOMS, null);
        String jsonstring = mockresponse.getContentAsString();
        
        Assert.assertNotNull(jsonstring);
      //  Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"at least some parameters need to execute this service: nID, sID_UA, sName_UA\"}", jsonstring);

    }
   
    @Test
    public void setProductCodeWithArgs() throws Exception
    {
      //set item only by nID — error 
       this.HttpStatus = HTTP_FORBIDDEN;
        MockHttpServletResponse mockresponse = this.getResponse(SET_OBJECTCUSTOMS, this.params_nid);
        String jsonstring = mockresponse.getContentAsString();
        Assert.assertNotNull(jsonstring);
       
        
        //set item only by sName_UA - must be error
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sNameUA.clear();
        this.param_sNameUA.put("sName_UA", "Рубероид");
        MockHttpServletResponse mockresponse_name = this.getResponse(SET_OBJECTCUSTOMS, this.param_sNameUA);
        String jsonstring_name = mockresponse_name.getContentAsString();
        
        Assert.assertNotNull(jsonstring_name);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object\"}", jsonstring_name);

       //set item only by sID_UA - must be error
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101");
        MockHttpServletResponse mockresponse_sid = this.getResponse(SET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring_sid = mockresponse_sid.getContentAsString();
        
        Assert.assertNotNull(jsonstring_sid);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object\"}", jsonstring_sid);
       
        //set item only by sMeasure_UA - must be error
        this.HttpStatus = HTTP_FORBIDDEN;
        MockHttpServletResponse mockresponse_measure = this.getResponse(SET_OBJECTCUSTOMS, this.param_sMeasure);
        String jsonstring_measure = mockresponse_measure.getContentAsString();
        
        Assert.assertNotNull(jsonstring_measure);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object\"}", jsonstring_measure);

        //check measure — error
        
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sMeasure.clear();
        this.param_sMeasure.put("sID_UA", "0101");
        this.param_sMeasure.put("sName_UA", "Рубероид");
        this.param_sMeasure.put("sMeasure_UA", "литр");
        MockHttpServletResponse mockresponse_measure1 = this.getResponse(SET_OBJECTCUSTOMS, this.param_sMeasure);
        String jsonstring_measure1 = mockresponse_measure1.getContentAsString();
        
        Assert.assertNotNull(jsonstring_measure1);

        //set item with two args sID_UA and sName_UA- must be error
       this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101");
        this.param_sIDUA.put("sName_UA", "Рубероид");
        MockHttpServletResponse mockresponse_name_sid = this.getResponse(SET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring_name_sid = mockresponse_name_sid.getContentAsString();
        
        Assert.assertNotNull(jsonstring_name_sid);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object\"}", jsonstring_name_sid);
       
        //set item with two args sID_UA and sMeasure_UA- must be error
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0101");
        this.param_sIDUA.put("sName_UA", "кг");
        MockHttpServletResponse mockresponse_measure_sid = this.getResponse(SET_OBJECTCUSTOMS, this.param_sIDUA);
        String jsonstring_measure_sid = mockresponse_measure_sid.getContentAsString();
        
        Assert.assertNotNull(jsonstring_measure_sid);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object\"}", jsonstring_measure_sid);

        //set item with two args sName_UA and sMeasure_UA- must be error
        this.param_sNameUA.clear();
        this.param_sNameUA.put("sName_UA", DASH);
        this.param_sNameUA.put("sMeasure_UA", DASH);
        MockHttpServletResponse mockresponse_measure_name = this.getResponse(SET_OBJECTCUSTOMS, this.param_sNameUA);
        String jsonstring_measure_name = mockresponse_measure_name.getContentAsString();
        
        Assert.assertNotNull(jsonstring_measure_name);
        Assert.assertEquals("{\"code\":\"BUSINESS_ERR\",\"message\":\"need sID_UA and sName_UA and sMeasure_UA if nID == null to insert new object\"}", jsonstring_measure_name);
        
    }
    
    @Test
    public void setObjectCustomsSaveOrUpdateUniqueAndSetDuplicate() throws Exception
    {
        //set duplicate sID_UA — must be error
        
        this.HttpStatus = HTTP_FORBIDDEN;
        this.params_duplicate.clear();
        this.params_duplicate.put("sID_UA", "0101");
        this.params_duplicate.put("sName_UA", "Руда");
        this.params_duplicate.put("sMeasure_UA", "кг");
        MockHttpServletResponse mockresponse_duplicate = this.getResponse(SET_OBJECTCUSTOMS, this.params_duplicate);
        String reason_error = mockresponse_duplicate.getHeader("Reason");
        Assert.assertNotNull(reason_error);
        
           
        //update with new unique data
      
       this.HttpStatus = HTTP_OK;
        
        MockHttpServletResponse mockresponse = this.getResponse(SET_OBJECTCUSTOMS, this.params_update);
        String jsonstring = mockresponse.getContentAsString();
        ObjectCustoms pcode_update = JsonRestUtils.readObject(jsonstring, ObjectCustoms.class);
        Assert.assertNotNull(jsonstring);
        Assert.assertEquals(this.expObjectCustomsUpdate, pcode_update);
        
        //set new data
       this.HttpStatus = HTTP_OK;
        MockHttpServletResponse mockresponse_set = this.getResponse(SET_OBJECTCUSTOMS, this.params_set);
        String jsonstring_set = mockresponse_set.getContentAsString();
        ObjectCustoms pcode_set = JsonRestUtils.readObject(jsonstring_set, ObjectCustoms.class);
        Assert.assertNotNull(jsonstring_set);
        Assert.assertEquals(this.expObjectCustomsSet, pcode_set);
        
            
    }
    
   @Test
    public void removeObjectCustomsTests() throws Exception
    {
        //no params pass into removeObjectCustoms - must be error
        this.HttpStatus = HTTP_FORBIDDEN;
        MockHttpServletResponse mockresponse = this.getResponse(REMOVE_OBJECTCUSTOMS, null);
        String jsonstring = mockresponse.getContentAsString();
        
        Assert.assertNotNull(jsonstring);
        
        //missed nID pass into removeObjectCustoms — must be error
      
        this.params_nid.clear();
        this.params_nid.put("nID", "98000");
        MockHttpServletResponse mockresponse1 = this.getResponse(REMOVE_OBJECTCUSTOMS, this.params_nid);
        String jsonstring1 = mockresponse1.getContentAsString();
        
        Assert.assertNotNull(jsonstring1);
        
          //method removeObjectCustoms with missed sID_UA -  must be error
        
        this.HttpStatus = HTTP_FORBIDDEN;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "9901");
        this.getResponse(REMOVE_OBJECTCUSTOMS, this.param_sIDUA);
      
        //method removeObjectCustoms with the only nID - must be success
        this.HttpStatus = HTTP_OK;
        this.params_nid.clear();
        this.params_nid.put("nID", "98");
        this.getResponse(REMOVE_OBJECTCUSTOMS, this.params_nid);
        
        //method removeObjectCustoms with sID_UA -  must be success
        this.HttpStatus = HTTP_OK;
        this.param_sIDUA.clear();
        this.param_sIDUA.put("sID_UA", "0102 90");
        this.getResponse(REMOVE_OBJECTCUSTOMS, this.param_sIDUA);
        
     }
   
    
}
