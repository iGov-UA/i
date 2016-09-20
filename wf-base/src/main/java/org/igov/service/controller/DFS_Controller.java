package org.igov.service.controller;

import com.google.common.io.Files;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.fs.FileSystemData;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.util.swind.jaxb.DBody;
import org.igov.util.swind.jaxb.DHead;
import org.igov.util.swind.jaxb.DeclarContent;
import org.igov.util.swind.jaxb.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"DFS_Controller -- интеграция с дфс"})
@Controller
@RequestMapping(value = "/dfs")
public class DFS_Controller {

    private static final Logger LOG = LoggerFactory.getLogger(DFS_Controller.class);

    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    @ApiOperation(value = "/putAttachmentToRedis_new", notes = "##### Контроллер платежей. Регистрация проведенного платежа - по колбэку от платежной системы\n")
    @RequestMapping(value = {"/putAttachmentToRedis_new"}, method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    byte[] putAttachmentToRedis_new(@RequestBody(required = false) Map<String, String> data,
            @RequestParam(required = false) String sPathFile,
            HttpServletResponse httpResponse) throws Exception {
        String declarContent = null;
        //генерируем хмл в виде файла и проставляем значения в него. отдаем на клиента
        LOG.info("data: " + data);
        //считываем контент файла
        //вставляем значения в него
        File file = FileSystemData.getFile(FileSystemData.SUB_PATH_XML, sPathFile);
        declarContent = Files.toString(file, Charset.defaultCharset());
        LOG.info("Created document with customer info to embedd to message: {}", declarContent);
        String regex, replacement;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            regex = "<" + entry.getKey().trim() + ">";
            replacement = regex + entry.getValue();
            declarContent = declarContent.replaceAll(regex, replacement);
        }
        //запись контента в xml файл
        MultipartFile multipartFile = new ByteArrayMultipartFile(declarContent.getBytes(),
                "SwinEd", "SwinEd", "application/xml");
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + multipartFile.getName());
        httpResponse.setHeader("Content-Type", multipartFile.getContentType());
        httpResponse.setContentLength(declarContent.getBytes().length);
        String key = oBytesDataInmemoryStorage.putBytes(AbstractModelTask
                .multipartFileToByteArray(multipartFile, multipartFile.getOriginalFilename())
                .toByteArray());
        return declarContent.getBytes();
        //return declarContent.getBytes();
    }

    @ApiOperation(value = "/putAttachmentToRedis", notes = "##### Контроллер платежей. Регистрация проведенного платежа - по колбэку от платежной системы\n")
    @RequestMapping(value = {"/putAttachmentToRedis"}, method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    String putAttachmentToRedis(@RequestBody(required = false) Map<String, String> data,
            HttpServletResponse httpResponse) throws Exception {
        //генерируем хмл в виде файла и проставляем значения в него. отдаем на клиента
        LOG.info("data: " + data);
        DBody body = new DBody();
        ObjectFactory factory = new ObjectFactory();
        body.setHLNAME(data.get("hlname"));
        body.setHPNAME(data.get("hpname"));
        body.setHFNAME(factory.createDBodyHFNAME(data.get("hfname")));
        body.setHTIN(data.get("htin"));
        body.setHEMAIL(data.get("hemail"));
        body.setHPASS(factory.createDBodyHPASS(data.get("hpass")));
        body.setHPASSDATE(factory.createDBodyHPASSDATE(data.get("hpassDate")));
        body.setHPASSISS(factory.createDBodyHPASS(data.get("hpassiss")));
        body.setHSTREET(data.get("hstreet"));
        body.setHBUILD(data.get("hbuild"));
        body.setHAPT(factory.createDBodyHAPT(data.get("hapart")));
        body.setHCOUNTRY(data.get("country"));
        body.setR01G01(Integer.valueOf(data.get("kvStart")));
        body.setR01G02(Integer.valueOf(data.get("yStart")));
        body.setR02G01(Integer.valueOf(data.get("kvEnd")));
        body.setR02G02(Integer.valueOf(data.get("yEnd")));

        DHead head = new DHead();
        head.setTIN(null);
        head.setLINKEDDOCS(factory.createDHeadLINKEDDOCS(null));
        head.setCDOC(null);
        head.setCDOCSUB(null);
        head.setCDOCVER(null);
        head.setCDOCTYPE(null);
        head.setCDOCCNT(null);
        head.setCREG(null);
        head.setCRAJ(null);
        head.setPERIODMONTH(null);
        head.setPERIODTYPE(null);
        head.setPERIODYEAR(null);
        head.setCSTIORIG(null);
        head.setCDOCSTAN(null);
        head.setDFILL(null);
        head.setSOFTWARE(null);
        head.setTIN(null);

        DeclarContent declar = new DeclarContent();
        declar.setDECLARHEAD(head);
        declar.setDECLARBODY(body);

        StringWriter sw = new StringWriter();
        JAXBContext jc = JAXBContext.newInstance(DeclarContent.class);
        Marshaller m = jc.createMarshaller();
        m.marshal(declar, sw);

        String declarContent = sw.toString();
        LOG.info("Created document with customer info to embedd to message: {}", declarContent);
        //запись контента в xml файл
        MultipartFile file = new ByteArrayMultipartFile(declarContent.getBytes(),
                "SwinEd", "SwinEd", "application/xml");
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + file.getName());
        httpResponse.setHeader("Content-Type", file.getContentType());
        httpResponse.setContentLength(declarContent.getBytes().length);
        String key = oBytesDataInmemoryStorage.putBytes(AbstractModelTask
                .multipartFileToByteArray(file, file.getOriginalFilename())
                .toByteArray());
        return key + " " + declarContent;
        //return declarContent.getBytes();
    }

}
