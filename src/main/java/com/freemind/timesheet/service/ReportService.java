package com.freemind.timesheet.service;

import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.UserRepository;
import com.freemind.timesheet.web.rest.ReportRessource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
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

public class ReportService {
    private final Logger log = LoggerFactory.getLogger(ReportRessource.class);
    private final UserService userService;
    private final AppUserService appUserService;
    private final CompanyService companyService;
    private final JobService jobService;

    private static int CPTV; //cpt vertical (for row)
    private static int CPTH; //cpt horizontal(for cell)
    private static int CPTHMAX;

    public ReportService(CompanyService companyService, JobService jobService, AppUserService appUserService, UserService userService) {
        this.userService = userService;
        this.appUserService = appUserService;
        this.companyService = companyService;
        this.jobService = jobService;
    }

    public void makeFullReport(LocalDate date, long userId) { //first, for one month
        //number of column for the month
        CPTHMAX = date.lengthOfMonth();
        CPTH = 0;
        CPTV = 0;

        Workbook wb = new XSSFWorkbook();
        Map<String, CellStyle> styles = createStyles(wb);
        //Instantiate the excel. empty template

        //faire la première ligne.
        //0=>Company/ 1=>customer/ 2=>Project/ 3=> job => days of month

    }

    private void initiateDoc(Workbook wb, LocalDate date) {
        Sheet sheet = wb.createSheet("Timesheet");

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }

    private void setTitle(Workbook wb, Sheet sheet, Map<String, CellStyle> styles, LocalDate date) {
        Row titleRow = sheet.createRow(CPTH);
        CPTH++;
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Month Timesheet"); //user
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$Z$1"));
    }

    private void makeHeader(Workbook wb, Sheet sheet, Map<String, CellStyle> styles, LocalDate date) {
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

    private void SetDays(Workbook wb, Sheet sheet, Map<String, CellStyle> styles, Row headerRow, Cell headerCell, LocalDate date) {
        LocalDate dateCopy = date;
        for (int i = 0; i < CPTHMAX; i++) {
            headerCell = headerRow.createCell(CPTH);
            headerCell.setCellValue(new SimpleDateFormat("EEE").format(dateCopy));
            CPTH++;
            dateCopy.plusDays(1);
        }
        CPTV++;
    }

    private void populateWithEntity(Workbook wb, Sheet sheet, Map<String, CellStyle> styles, LocalDate date) {
        //employee name
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
