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

@Api(tags = { "ECPController -- наложение электронной подписи" })
@Controller
@RequestMapping(value = "/ecp")
public class ECPController {

    private static final Logger LOG = LoggerFactory.getLogger(ECPController.class);

    @Autowired
    ECPService ecpService;
    
    @Autowired
    GeneralConfig generalConfig;

    @ApiOperation(value = "/apply", notes = "##### Метод для наложения цифровой подписи к файлу\n")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public
    ResponseEntity<byte[]> applyDigitalSignature(
    		@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
    	LOG.info("Uploaded file with filename : " + file.getName());
    	byte[] res = ecpService.signFile(file.getBytes());

    	HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("charset", "utf-8");
        responseHeaders.setContentType(MediaType.valueOf(file.getContentType()));
        responseHeaders.setContentLength(res.length);
        responseHeaders.set("Content-disposition", "attachment; filename=" + file.getName());
    	
    	return new ResponseEntity<byte[]>(res, responseHeaders, HttpStatus.OK);
    }
    
}			
