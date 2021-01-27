package com.freemind.timesheet.service;

import com.freemind.timesheet.config.ApplicationProperties;
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
import com.freemind.timesheet.service.dto.ReportDTO;
import com.freemind.timesheet.web.rest.ReportRessource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private List<Performance> performances = new ArrayList<Performance>();
    private static int CPTV; //cpt vertical (for row)
    private static int CPTH; //cpt horizontal(for cell)
    private static int CPTHMAX; //total days of the month

    @Autowired
    private ApplicationProperties applicationProperties;

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

    public File makeFullReport(ReportDTO report) {
        Workbook wb = new XSSFWorkbook();
        Map<String, CellStyle> styles = createStyles(wb);
        for (LocalDate date : report.getDates()) {
            LocalDate dateBis = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
            this.initSheetData(dateBis);
            String sheetName =
                "Timesheet" + date.format(DateTimeFormatter.ofPattern("MMMM")) + " " + date.format(DateTimeFormatter.ofPattern("y"));
            initiateDoc(wb, sheetName);
            Sheet sheet = wb.getSheet(sheetName);
            setTitle(sheet, styles, dateBis);
            makeHeader(sheet, styles);
            SetDays(styles, sheet.getRow(CPTV), date);
            fillByUsers(report, wb, date, styles, sheet);
        }
        // Write the output to a file
        String file = this.createPath("test");
        File f = this.fillFile(wb, file);
        return f;
    }

    private void initSheetData(LocalDate date) { //for each sheet
        CPTHMAX = date.lengthOfMonth();
        CPTH = 0;
        CPTV = 0;
    }

    private String createPath(String word) {
        String file = System.getProperty("user.home");
        return file + "\\Downloads\\" + "timesheet " + " " + word + ".xls";
    }

    private File fillFile(Workbook wb, String file) {
        if (wb instanceof XSSFWorkbook) file += "x";
        File f = null;
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            try {
                wb.write(out);
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new File(file);
    }

    private void initiateDoc(Workbook wb, String string) {
        Sheet sheet = wb.createSheet(string);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }

    private void fillByUsers(ReportDTO report, Workbook wb, LocalDate date, Map<String, CellStyle> styles, Sheet sheet) {
        Row row;
        List<Long> usersId = new ArrayList<Long>();
        usersId.addAll(report.getUsersId());
        for (int i = 0; i < usersId.size(); i++) {
            User user = userRepository.getOne(usersId.get(i));
            fillSheet(report, user, date, styles, sheet);

            if (i + 1 < usersId.size()) {
                if (sheet.getRow(CPTV + 1) == null && sheet.getRow(CPTV) == null) {
                    CPTV++; //next if last project dont do that.
                    row = sheet.createRow(CPTV);
                } else row = sheet.getRow(CPTV);
            }
        }
    }

    private void fillSheet(ReportDTO report, User user, LocalDate date, Map<String, CellStyle> styles, Sheet sheet) {
        LocalDate dateCopy = date;
        AppUser apUser = this.appUserRepository.getOne(user.getId());
        Row row = sheet.createRow(CPTV);
        Company company = apUser.getCompany();
        fillRows(sheet, row, report, company, user, dateCopy, styles); //t'être remettre date
    }

    private void fillRows(
        Sheet sheet,
        Row row,
        ReportDTO report,
        Company company,
        User user,
        LocalDate date,
        Map<String, CellStyle> styles
    ) {
        List<Customer> customers = customerRepository.findCustomersByUserId(user.getId(), company.getId());
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            CPTH = 0;
            printString(row, user.getLogin(), styles);
            CPTH++; //0=>1
            printString(row, company.getName(), styles);
            CPTH++; //1=>2
            printString(row, c.getName(), styles);
            CPTH++; //2=>3
            List<Project> projects = projectRepository.findProjectsByCustomerAndUserId(user.getId(), c.getId());
            projects = projects.stream().filter(e -> report.getProjectsId().contains(e.getId())).collect(Collectors.toList());

            if (projects.size() > 0) printProjects(sheet, report, projects, user, company, row, date, styles); else sheet.removeRow(row);
            if (i + 1 < customers.size()) {
                if (sheet.getRow(CPTV + 1) == null && sheet.getRow(CPTV) == null) {
                    CPTV++; //next if last project dont do that.
                    row = sheet.createRow(CPTV);
                } else row = sheet.getRow(CPTV);
            }
        }
    }

    private void printProjects(
        Sheet sheet,
        ReportDTO report,
        List<Project> projects,
        User user,
        Company company,
        Row row,
        LocalDate date,
        Map<String, CellStyle> styles
    ) {
        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);

            if (row.getCell(0) == null) {
                CPTH = 0;
                printString(row, user.getLogin(), styles);
                CPTH++; //0=>1
                printString(row, company.getName(), styles);
                CPTH++; //1=>2
                printString(row, p.getCustomer().getName(), styles);
                CPTH++; //2=>3
            }
            AppUser ap = appUserRepository.getOne(user.getId());
            printString(row, p.getName(), styles);
            CPTH++;
            List<Job> jobs = p.getJobsByUser(ap);
            jobs = jobs.stream().filter(e -> report.getJobsId().contains(e.getId())).collect(Collectors.toList());

            if (projects.size() > 0) printJobs(sheet, jobs, user, company, p, row, date, styles); else sheet.removeRow(row);

            if (i + 1 < projects.size()) {
                if (sheet.getRow(CPTV + 1) == null && sheet.getRow(CPTV) == null) {
                    CPTV++;
                    row = sheet.createRow(CPTV);
                } else row = sheet.getRow(CPTV);
            }
        }
    }

    private void printJobs(
        Sheet sheet,
        List<Job> jobs,
        User user,
        Company company,
        Project project,
        Row row,
        LocalDate date,
        Map<String, CellStyle> styles
    ) {
        LocalDate dateCopy = date;
        for (int i = 0; i < jobs.size(); i++) {
            if (row.getCell(0) == null) {
                CPTH = 0;
                printString(row, user.getLogin(), styles);
                CPTH++; //0=>1
                printString(row, company.getName(), styles);
                CPTH++; //1=>2
                printString(row, project.getCustomer().getName(), styles);
                CPTH++; //2=>3
                printString(row, project.getName(), styles);
                CPTH++; //3=>4
            }
            Job j = jobs.get(i);
            printString(row, j.getName(), styles);
            CPTH++;
            fillPerformances(company, user, j, dateCopy, row, sheet, styles);
            dateCopy = date;
            if (sheet.getRow(CPTV + 1) == null) CPTV++; //new row, new job
            else CPTV = CPTV + 2;
            row = sheet.createRow(CPTV);
        }
    }

    private void fillPerformances(
        Company company,
        User user,
        Job j,
        LocalDate dateCopy,
        Row row,
        Sheet sheet,
        Map<String, CellStyle> styles
    ) {
        int i = 0;
        int hours = 0;
        int totalHours = 0;

        while (i < CPTHMAX) {
            Performance perf = j.getPerfByDateAndUserId(dateCopy, user.getId());
            dateCopy = dateCopy.plusDays(1); //next day
            if (perf == null) hours = 0; else hours = perf.getHours();
            printString(row, hours + "", styles);

            totalHours += hours; //total for the end of the row
            if (perf != null && perf.getDescription() != null) {
                CPTV++;
                row = sheet.getRow(CPTV);
                if (row == null) {
                    row = sheet.createRow(CPTV);
                    int tmpCpth = CPTH;
                    CPTH = 0;
                    printString(row, user.getLogin(), styles);
                    CPTH++; //0=>1
                    printString(row, company.getName(), styles);
                    CPTH++; //1=>2
                    printString(row, j.getProject().getCustomer().getName(), styles);
                    CPTH++; //2=>3
                    printString(row, j.getProject().getName(), styles);
                    CPTH++; //3=>4
                    printString(row, j.getName(), styles);
                    CPTH++; //4=>5
                    CPTH = tmpCpth;
                }
                printString(row, perf.getDescription(), styles);
                CPTV--;
                row = sheet.getRow(CPTV);
            }
            CPTH++;
            i++;
        }
        printString(row, totalHours + "", styles);
    }

    private void printString(Row row, String name, Map<String, CellStyle> styles) {
        row.setHeightInPoints(35);
        Cell cell;
        cell = row.createCell(CPTH);
        cell.setCellValue(name);
        cell.setCellStyle(styles.get("cell"));
    }

    private void setTitle(Sheet sheet, Map<String, CellStyle> styles, LocalDate date) {
        Row titleRow = sheet.createRow(CPTV);
        CPTV++;
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(date.format(DateTimeFormatter.ofPattern("MMMM")) + " " + date.format(DateTimeFormatter.ofPattern("y")));
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$AK$1"));
    }

    private void printHeader(Row headerRow, String string, Map<String, CellStyle> styles) {
        Cell headerCell;
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue(string);
        headerCell.setCellStyle(styles.get("header"));
    }

    private void makeHeader(Sheet sheet, Map<String, CellStyle> styles) {
        Row headerRow = sheet.createRow(CPTV);
        headerRow.setHeightInPoints(40);
        this.printHeader(headerRow, "EMPLOYEE", styles);
        CPTH++;
        this.printHeader(headerRow, "COMPANY", styles);
        CPTH++;
        this.printHeader(headerRow, "CUSTOMER", styles);
        CPTH++;
        this.printHeader(headerRow, "PROJECT", styles);
        CPTH++;
        this.printHeader(headerRow, "JOB", styles);
        CPTH++;
    }

    private void SetDays(Map<String, CellStyle> styles, Row headerRow, LocalDate date) {
        LocalDate dateCopy = date;
        int total;

        Cell headerCell;
        for (int i = 0; i < CPTHMAX; i++) {
            headerCell = headerRow.createCell(CPTH);
            headerCell.setCellValue(dateCopy.format(DateTimeFormatter.ofPattern("EEE")));
            headerCell.setCellStyle(styles.get("header"));
            CPTH++;
            dateCopy = dateCopy.plusDays(1);
        }
        headerCell = headerRow.createCell(CPTH);
        headerCell.setCellValue("Total");
        headerCell.setCellStyle(styles.get("header"));
        CPTV++; //next row
        //        Row
        CPTH = 0;
    } //end first line

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
        monthFont.setFontHeightInPoints((short) 15);
        monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header", style);

        style = wb.createCellStyle();
        Font cellFont = wb.createFont();
        monthFont.setFontHeightInPoints((short) 13);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
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
