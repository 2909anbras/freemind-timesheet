package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.domain.User;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.CustomerRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.ProjectRepository;
import com.freemind.timesheet.repository.UserRepository;
import com.freemind.timesheet.web.rest.ReportRessource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportService {
    private final Logger log = LoggerFactory.getLogger(ReportRessource.class);
    private final UserRepository userRepository;
    private final AppUserRepository appUserRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final CustomerRepository customerRepository;
    private final ProjectRepository projectRepository;
    //    private  AppUser appUser;
    //    private Company company;
    //    private Customer customer;
    //    private Job job;
    private List<Performance> performances = new ArrayList<Performance>();
    private static int CPTV; //cpt vertical (for row)
    private static int CPTH; //cpt horizontal(for cell)
    private static int CPTHMAX; //total days of the month

    public ReportService(
        CompanyRepository companyRepository,
        JobRepository jobRepository,
        AppUserRepository appUserRepository,
        UserRepository userRepository,
        CustomerRepository customerRepository,
        ProjectRepository projectRepository
    ) {
        this.userRepository = userRepository;
        this.appUserRepository = appUserRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.customerRepository = customerRepository;
        this.projectRepository = projectRepository;
    }

    public void makeFullReport(LocalDate date, Long userId) throws IOException { //first, for one month
        //number of column for the month
        CPTHMAX = date.lengthOfMonth();
        CPTH = 0;
        CPTV = 0;

        Workbook wb = new XSSFWorkbook();
        Map<String, CellStyle> styles = createStyles(wb);
        initiateDoc(wb);
        Sheet sheet = wb.getSheet("Timesheet");
        setTitle(sheet, styles, date);
        makeHeader(sheet, styles);
        SetDays(styles, sheet.getRow(CPTV), date);
        //tout trier par ordre alphabétique par la suite cé po graf
        User user = userRepository.getOne(userId);
        fillSheet(wb, user, date);

        // Write the output to a file
        String file = "timesheet employee:" + user.getLogin() + " " + date.toString() + ".xls";
        if (wb instanceof XSSFWorkbook) file += "x";
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();
    }

    private void initiateDoc(Workbook wb) {
        Sheet sheet = wb.createSheet("Timesheet");

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }

    private void fillSheet(Workbook wb, User user, LocalDate date) {
        AppUser apUser = this.appUserRepository.getOne(user.getId());
        Row row = wb.getSheet("Timesheet").createRow(CPTV);
        printString(row, user.getLogin());
        Company company = apUser.getCompany();
        printString(row, company.getName());
        fillRows(row, company, user.getId(), date);
    }

    private void fillRows(Row row, Company company, Long userId, LocalDate date) {
        List<Customer> customers = customerRepository.findCustomersByUserId(userId, company.getId());
        for (Customer c : customers) {
            printString(row, c.getName());
            printProjects(projectRepository.findProjectsByCustomerAndUserId(userId, c.getId()), userId, row, date);
            CPTV++; //next cust
            CPTH = 1; //back to column c
        }
    }

    private void printProjects(List<Project> projects, Long userId, Row row, LocalDate date) {
        for (int i = 0; i < projects.size(); i++) {
            //			for(Project p:projects) {
            Project p = projects.get(i);
            printString(row, p.getName());
            printJobs(p.getJobsByUser(userId), userId, row, date);
            if (i + 1 <= projects.size()) {
                CPTV++; //next if last project dont do that.
                CPTH = 2;
            }
        }
    }

    private void printJobs(List<Job> jobs, Long userId, Row row, LocalDate date) {
        int i = 0;
        //			for(Job j:jobs) {
        for (int cptJ = 0; cptJ < jobs.size(); cptJ++) {
            Job j = jobs.get(cptJ);

            printString(row, j.getName());

            while (i < CPTHMAX) {
                Performance perf = j.getPerfByDateAndUserId(date, userId);
                if (perf == null) printString(row, "0"); else printString(row, perf.getHours().toString());
                i++;
            }

            if (cptJ + 1 <= jobs.size()) {
                CPTH = 3; //reviens au niveau des jobs names
                i = 0; //for the next perfs
                CPTV++; //new row, new job
            }
        }
    }

    private void printString(Row row, String name) {
        row.setHeightInPoints(35);
        Cell cell;
        cell = row.createCell(CPTH);
        cell.setCellValue(name);
        CPTH++;
    }

    private void setTitle(Sheet sheet, Map<String, CellStyle> styles, LocalDate date) {
        Row titleRow = sheet.createRow(CPTV);
        CPTV++;
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Month " + date.toString()); //user
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$Z$1"));
    }

    private void makeHeader(Sheet sheet, Map<String, CellStyle> styles) {
        Row headerRow = sheet.createRow(CPTV);
        headerRow.setHeightInPoints(40);
        Cell headerCell;
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue("Employee Name");
        CPTH++;
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue("Company Name");
        CPTH++;
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue("Customer Name");
        CPTH++;
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue("Project Name");
        CPTH++;
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue("Job Name");
        CPTH++;
    }

    private void SetDays(Map<String, CellStyle> styles, Row headerRow, LocalDate date) {
        LocalDate dateCopy = date;
        Cell headerCell;
        for (int i = 0; i < CPTHMAX; i++) {
            headerCell = headerRow.createCell(CPTH);
            headerCell.setCellValue(new SimpleDateFormat("EEE").format(dateCopy));
            CPTH++;
            dateCopy.plusDays(1);
        }
        CPTV++; //next row
        CPTH = 0;
    } //end first line

    private void populateWithEntity(Workbook wb, Map<String, CellStyle> styles, LocalDate date, Long userId) {
        //employee name
        Sheet sheet = wb.getSheet("Timesheet");
        Row headerRow = sheet.createRow(CPTV);
        headerRow.setHeightInPoints(30);
        Cell headerCell;
        //        String userName
        //company name
        //customer name
        //project name
        //job name
        //perf hours or 0
        //tot hours
    }

    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(titleFont);
        styles.put("title", style);

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short) 11);
        monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula_2", style);

        return styles;
    }
}
