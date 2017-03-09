package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.igov.io.GeneralConfig;
import org.igov.service.ECPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = { "ECPCommonController -- наложение электронной подписи" })
@Controller
@RequestMapping(value = "/ecp")
public class ECPCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(ECPCommonController.class);

    @Autowired
    ECPService ecpService;
    
    @Autowired
    GeneralConfig generalConfig;

    @ApiOperation(value = "/apply", notes = "##### Файл передается в теле запроса (POST) с именем параметра file. Примеры:\n"
            + "https://alpha.test.igov.org.ua/wf/service/ecp/apply")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<byte[]> applyDigitalSignature(
    		@RequestParam(required = true, value = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
    	LOG.info("Uploaded file with filename : " + file.getOriginalFilename());
    	byte[] res = ecpService.signFile(file.getBytes());

    	HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("charset", "utf-8");
        responseHeaders.setContentType(MediaType.valueOf(file.getContentType()));
        responseHeaders.setContentLength(res.length);
        responseHeaders.set("Content-disposition", "attachment; filename=" + file.getOriginalFilename());
        
        LOG.info("Original file size:" + file.getSize() + " final file size:" + res.length + " content type:" + file.getContentType());
    	
    	return new ResponseEntity<byte[]>(res, responseHeaders, HttpStatus.OK);
    }
    
}			
