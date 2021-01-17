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

    public void makeMonthReport(LocalDate oldDate, Long userId) { //fullMonthReport for on user
        LocalDate date = LocalDate.of(oldDate.getYear(), oldDate.getMonthValue(), 1);
        this.initSheetData(date);

        Workbook wb = new XSSFWorkbook();
        Map<String, CellStyle> styles = createStyles(wb);

        initiateDoc(wb);
        Sheet sheet = wb.getSheet("Timesheet");
        setTitle(sheet, styles, date);
        makeHeader(sheet, styles);
        SetDays(styles, sheet.getRow(CPTV), date);
        //tout trier par ordre alphabétique par la suite
        User user = userRepository.getOne(userId);
        fillSheet(wb, user, date, styles);
        // Write the output to a file
        String file = this.createMonthPath(user.getLogin(), date.toString());
        this.fillFile(wb, file);
    }

    public void makeFullReport(ReportDTO report) {
        //1 page/mois => nom de la page= mois+année (for nbr de mois => les mois triés à l'avance)
        //fill template (title, header
        // fill data
        //create file en dehors du for
    }

    private void initSheetData(LocalDate date) { //for each sheet
        CPTHMAX = date.lengthOfMonth();
        CPTH = 0;
        CPTV = 0;
    }

    private String createMonthPath(String userName, String date) {
        String file = System.getProperty("user.home");
        return file + "\\Downloads\\" + "timesheet " + userName + " " + date + ".xls";
    }

    private void fillFile(Workbook wb, String file) {
        if (wb instanceof XSSFWorkbook) file += "x";
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            log.debug("FILE:{}", file.toString());
            try {
                log.debug("Inside");
                wb.write(out);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initiateDoc(Workbook wb) {
        Sheet sheet = wb.createSheet("Timesheet");
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }

    private void fillSheet(Workbook wb, User user, LocalDate date, Map<String, CellStyle> styles) {
        LocalDate dateCopy = date;
        AppUser apUser = this.appUserRepository.getOne(user.getId());
        Row row = wb.getSheet("Timesheet").createRow(CPTV);
        Sheet sheet = wb.getSheet("Timesheet");
        printString(row, user.getLogin(), styles);
        CPTH++;
        Company company = apUser.getCompany();
        printString(row, company.getName(), styles);
        CPTH++;
        fillRows(sheet, row, company, user.getId(), date, styles);
    }

    private void fillRows(Sheet sheet, Row row, Company company, Long userId, LocalDate date, Map<String, CellStyle> styles) {
        List<Customer> customers = customerRepository.findCustomersByUserId(userId, company.getId());
        for (Customer c : customers) {
            printString(row, c.getName(), styles);
            CPTH++;
            printProjects(sheet, projectRepository.findProjectsByCustomerAndUserId(userId, c.getId()), userId, row, date, styles);
            CPTV++; //next cust
            row = sheet.createRow(CPTV);
            CPTH = 2; //back to column c
        }
    }

    private void printProjects(Sheet sheet, List<Project> projects, Long userId, Row row, LocalDate date, Map<String, CellStyle> styles) {
        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            printString(row, p.getName(), styles);
            CPTH++;
            printJobs(sheet, p.getJobsByUser(userId), userId, row, date, styles);
            if (i + 1 < projects.size()) {
                if (sheet.getRow(CPTV + 1) == null) {
                    CPTV++; //next if last project dont do that.
                    row = sheet.createRow(CPTV);
                } else row = sheet.getRow(CPTV);

                CPTH = 3;
            }
        }
    }

    private void printJobs(Sheet sheet, List<Job> jobs, Long userId, Row row, LocalDate date, Map<String, CellStyle> styles) {
        LocalDate dateCopy = date;
        int i = 0;
        int hours = 0;
        int totalHours = 0;
        for (int cptJ = 0; cptJ < jobs.size(); cptJ++) {
            Job j = jobs.get(cptJ);
            log.debug("JOB:{}", j);
            printString(row, j.getName(), styles);
            CPTH++;
            while (i < CPTHMAX) {
                log.debug("CPTH: {}", CPTH);
                Performance perf = j.getPerfByDateAndUserId(dateCopy, userId); //get the perf

                dateCopy = dateCopy.plusDays(1); //next day

                if (perf == null) hours = 0; else hours = perf.getHours();

                log.debug("Performance:{}", perf);

                printString(row, hours + "", styles);

                totalHours += hours; //total for the end of the row

                if (perf != null && perf.getDescription() != "") {
                    log.debug("CPTV AVANT:{}", row);
                    row = sheet.getRow(CPTV + 1);
                    log.debug("description:{}", perf.getDescription());
                    if (row == null) {
                        log.debug("CREATE ROW");
                        row = sheet.createRow(CPTV + 1);
                    }
                    log.debug("CPTV après:{}", row);
                    printString(row, perf.getDescription(), styles);
                    row = sheet.getRow(CPTV);
                }
                CPTH++;
                i++;
            }
            printString(row, totalHours + "", styles);
            totalHours = 0;
            if (cptJ < jobs.size()) {
                dateCopy = date;
                CPTH = 4; //reviens au niveau des jobs names
                i = 0; //for the next perfs
                log.debug("ROW EXISTS?:{}", sheet.getRow(CPTV + 1) != null);

                if (sheet.getRow(CPTV + 1) == null) CPTV++; //new row, new job
                else CPTV += 2;

                row = sheet.createRow(CPTV);
                log.debug("NEW JOB ROW");
            }
        }
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
        this.printHeader(headerRow, "PROJECTS", styles);
        CPTH++;
        this.printHeader(headerRow, "JOBS", styles);
        CPTH++;
        //        headerRow.setHeightInPoints(40);
        //        Cell headerCell;
        //        headerCell = headerRow.createCell(CPTH);
        //        headerCell.setCellValue("EMPLOYEE ");
        //        headerCell.setCellStyle(styles.get("header"));
        //        CPTH++;
        //        headerCell = headerRow.createCell(CPTH);
        //        headerCell.setCellValue("COMPANY ");
        //        headerCell.setCellStyle(styles.get("header"));
        //
        //        CPTH++;
        //        headerCell = headerRow.createCell(CPTH);
        //        headerCell.setCellValue("CUSTOMERS ");
        //        headerCell.setCellStyle(styles.get("header"));
        //
        //        CPTH++;
        //        headerCell = headerRow.createCell(CPTH);
        //        headerCell.setCellValue("PROJECTS ");
        //        headerCell.setCellStyle(styles.get("header"));
        //
        //        CPTH++;
        //        headerCell = headerRow.createCell(CPTH);
        //        headerCell.setCellValue("JOBS ");
        //        headerCell.setCellStyle(styles.get("header"));

        //        CPTH++;
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

    private void populateWithEntity(Workbook wb, Map<String, CellStyle> styles, LocalDate date, Long userId) {
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
