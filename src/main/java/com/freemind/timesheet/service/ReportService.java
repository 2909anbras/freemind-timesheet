package com.freemind.timesheet.service;

import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.UserRepository;
import com.freemind.timesheet.web.rest.ReportRessource;
import java.time.LocalDate;
import java.util.Date;
import javax.validation.Valid;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportService {
    private final Logger log = LoggerFactory.getLogger(ReportRessource.class);
    private final UserService userService;
    private final AppUserService appUserService;
    private final CompanyService companyService;
    private final JobService jobService;

    public ReportService(CompanyService companyService, JobService jobService, AppUserService appUserService, UserService userService) {
        this.userService = userService;
        this.appUserService = appUserService;
        this.companyService = companyService;
        this.jobService = jobService;
    }

    public void makeFullReport(@Valid LocalDate date, long userId) { //first for one month
        int cptV = date.lengthOfMonth(); //number of column for the month
        int cptH = 0;
        Workbook wb;
        //Instantiate the excel. empty template

        //faire la première ligne.
        //0=>Company/ 1=>customer/ 2=>Project/ 3=> job => days of month

    }

    private void initiateDoc() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Timesheet");
    }

    private void makeHeader() {}
}
