package com.freemind.timesheet.web.rest;

import com.freemind.timesheet.security.AuthoritiesConstants;
import com.freemind.timesheet.service.ReportService;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.dto.CustomerDTO;
import com.freemind.timesheet.service.dto.ReportDTO;
import com.freemind.timesheet.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Date;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReportRessource {
    private final Logger log = LoggerFactory.getLogger(ReportRessource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private static final String NAME = "report";

    private ReportService reportService;

    public ReportRessource(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/report")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\"+\"," +
        AuthoritiesConstants.CUSTOMER_ADMIN +
        "\"+\"," +
        AuthoritiesConstants.INSPECTOR +
        "\")"
    )
    //    public ResponseEntity  createFullReport(@Valid @RequestBody ReportDTO report, HttpServletResponse response) throws IOException {
    //        log.debug("REPORTDTO : {}", report);
    //        XSSFWorkbook wb = (XSSFWorkbook) this.reportService.makeFullReport(report);
    //        response.setHeader("Content-Disposition","attachment; filename="+createPath("test"));
    //        writeToOutputStream(response,wb);
    //        OutputStream out = response.getOutputStream();
    //        response.setContentType("application/vnd.ms-excel");
    ////        byte[] byteArray = ((HSSFWorkbook)wb).getBytes();
    ////        out.write(byteArray);
    ////        out.flush();
    ////        out.close();
    //        return ResponseEntity.ok().build();
    //    }
    //
    //    private String createPath(String word) {
    //        String file = System.getProperty("user.home");
    //        return file + "\\Downloads\\" + "timesheet " + " " + word + ".xlsx";
    //    }
    //    private void writeToOutputStream(HttpServletResponse response,XSSFWorkbook wb){
    //
    //        ServletOutputStream out ;
    //        try {
    //            out = response.getOutputStream();
    //            wb.write(out);
    //            wb.close();
    //            out.close();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }

    public void createFullReport(@Valid @RequestBody ReportDTO report, HttpServletResponse response) throws IOException {
        log.debug("REPORTDTO : {}", report);

        try {
            //            Workbook book = new XSSFWorkbook();
            byte[] output = this.reportService.makeFullReport(report);
            serveXlsxBrowser(response, output, "timesheet test.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void serveXlsxBrowser(HttpServletResponse response, byte[] file, String fileName) throws IOException {
        response.setContentType("Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.getOutputStream().write(file);
        response.getOutputStream().flush();
    }
    //    public void createFullReport(@Valid @RequestBody ReportDTO report, HttpServletResponse response) throws IOException {
    //        log.debug("REPORTDTO : {}", report);
    //
    //        File outputFile = this.reportService.makeFullReport(report);
    //
    //        log.debug("FILEEXIST:{}", outputFile.exists());
    //        log.debug("ISFILE:{}", outputFile.isFile());
    //        log.debug("ISFILE:{}", outputFile.getName());
    //
    //        FileInputStream stream = new FileInputStream(outputFile);
    //        response.setContentType("application/vnd.ms-excel");
    //        response.setHeader("Content-disposition", "attachment; filename=" + outputFile.getName());
    //        IOUtils.copy(stream, response.getOutputStream());
    //        stream.close();
    //    }
}
//}
//byte[] byteArray =  FileUtils.readFileToByteArray(outputFile);
//String contentType = outputFile.getName().contains(".xls")
//? "application/vnd.ms-excel.sheet.macroEnabled.12"
//: "application/vnd.ms-excel";
//this.reportService.makeFullReport(report);
//ResponseEntity<Boolean>
//return ResponseEntity
//.noContent()
//.headers(HeaderUtil.createEntityCreationAlert(applicationName, true, NAME, report.toString()))
//.build();
