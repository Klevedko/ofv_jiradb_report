package ofv_jiradb_report;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ofv_jiradb_report.Templates.ReportJDBCTemplateFOMS;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static String path = "C:/1/";

    public void main(String F_project_name, String F_start_date, String F_end_date, String[] F_assignee_to_users, long ts) {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cacerts");
        System.setProperty("javax.net.ssl.trustStore", resourceStream.toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        ReportJDBCTemplateFOMS studentJDBCTemplateFOMS = (ReportJDBCTemplateFOMS) context.getBean(F_project_name);
        List<Map<String, Object>> x = studentJDBCTemplateFOMS.getFOMSreport( F_assignee_to_users, F_project_name, F_start_date, F_end_date);
        beginInsert(x, ts);
    }

    public void beginInsert(List<Map<String, Object>> obj, long ts) {
        try {
            logger.info("begininsert");
            System.out.println("begin insert");
            new File(path).mkdirs();
            HSSFWorkbook wb = new HSSFWorkbook();
            String SheetName;
            SheetName = "Period";
            String output = path + ts + ".xls";
            Sheet list = wb.createSheet(SheetName);
            int rowIndex = 0;
            FileOutputStream fileout;
            fileout = new FileOutputStream(output);
            Set<Map.Entry<String, Object>> entryHeader = obj.get(0).entrySet();
            Row row = list.createRow(rowIndex);
            int a=0;
            for (Map.Entry<String, Object> ent : entryHeader) {
                row.createCell(a).setCellValue(ent.getKey());
                a++;
            }
            rowIndex++;
            for (Map<String, Object> map : obj) {
                row = list.createRow(rowIndex++);
                int i = 0;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() != null)
                        row.createCell(i).setCellValue(entry.getValue().toString());
                    i++;
                }
            }
            wb.write(fileout);
            fileout.close();
            logger.info("end insert");
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
    }
}