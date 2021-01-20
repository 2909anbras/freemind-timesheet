package com.freemind.timesheet.web.rest;

import com.freemind.timesheet.security.AuthoritiesConstants;
import com.freemind.timesheet.service.ReportService;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.dto.CustomerDTO;
import com.freemind.timesheet.service.dto.ReportDTO;
import com.freemind.timesheet.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Date;
import javax.validation.Valid;
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

    @PostMapping("/report/monthReport/{userId}")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\"+\"," +
        AuthoritiesConstants.CUSTOMER_ADMIN +
        "\"+\"," +
        AuthoritiesConstants.INSPECTOR +
        "\")"
    )
    public ResponseEntity<Boolean> createMonthReport(@PathVariable Long userId, @Valid @RequestBody LocalDate date)
        throws URISyntaxException, IOException {
        log.debug("REPORT DATE : {}", date);

        this.reportService.makeMonthReport(date, userId);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, NAME, userId.toString()))
            .build();
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
    public ResponseEntity<Boolean> createFullReport(@Valid @RequestBody ReportDTO report) {
        log.debug("REPORTDTO : {}", report);

        this.reportService.makeFullReport(report);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, NAME, report.toString()))
            .build();
    }
}
