package com.freemind.timesheet.web.rest;

import com.freemind.timesheet.security.AuthoritiesConstants;
import com.freemind.timesheet.service.ReportService;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.dto.CustomerDTO;
import com.freemind.timesheet.service.dto.ReportDTO;
import com.freemind.timesheet.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200") //à checker
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
    public ResponseEntity<Resource> createFullReport(@Valid @RequestBody ReportDTO report, HttpServletRequest request)
        throws MalformedURLException {
        log.debug("REPORTDTO : {}", report);

        Resource resource = this.reportService.makeFullReport(report);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
    //    @GetMapping("/downloadFile/{fileName}")
    //    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws MalformedURLException {
    ////        Resource resource = fileUploadDownloadService.loadFileAsResource(fileName);
    //        // Try to determine file's content type
    //        String contentType = null;
    //        try {
    //            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    //        } catch (IOException ex) {
    //            logger.info("Could not determine file type.");
    //        }
    //        // Fallback to the default content type if type could not be determined
    //        if (contentType == null) {
    //            contentType = "application/octet-stream";
    //        }
    //        return ResponseEntity.ok()
    //                .contentType(MediaType.parseMediaType(contentType))
    //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
    //                .body(resource);
    //    }
}
