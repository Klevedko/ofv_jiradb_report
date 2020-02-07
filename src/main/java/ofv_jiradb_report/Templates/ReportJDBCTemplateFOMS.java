package ofv_jiradb_report.Templates;

import ofv_jiradb_report.ReportDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ReportJDBCTemplateFOMS implements ReportDAO {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataSource dataSourceFFOMS;
    private DataSource dataSourceERZ;
    private DataSource dataSourceZABBIX;
    private NamedParameterJdbcTemplate jdbcTemplateObject;

    public void setDataSourceFFOMS(DataSource dataSourceFFOMS) {
        this.dataSourceFFOMS = dataSourceFFOMS;
        this.jdbcTemplateObject = new NamedParameterJdbcTemplate(dataSourceFFOMS);
    }

    public void setDataSourceOTV(DataSource dataSourceERZ) {
        this.dataSourceERZ = dataSourceERZ;
        this.jdbcTemplateObject = new NamedParameterJdbcTemplate(dataSourceERZ);
    }

    public void setDataSourceZABBIX(DataSource dataSourceZABBIX) {
        this.dataSourceZABBIX = dataSourceZABBIX;
        this.jdbcTemplateObject = new NamedParameterJdbcTemplate(dataSourceZABBIX);
    }

    public List<Map<String, Object>> getFOMSreport(String[] F_assignee_to_users, String F_project_name, String date_from, String date_to) {
        try {
            System.out.println("getFOMSreport");
            String sql = readQuery(F_project_name);
            String[] stringArr = new String[]{""};
            if (F_assignee_to_users.length != 0)
                stringArr = F_assignee_to_users;
            List<String> intList = Arrays.asList(stringArr);
            return jdbcTemplateObject.query(sql,
                    new MapSqlParameterSource().addValue("tags", intList)
                            .addValue("dateFrom", date_from)
                            .addValue("dateTo", date_to)
                           .addValue("assignee_filter", F_assignee_to_users.length != 0)
                    , new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));
        } catch (Exception x) {
            System.out.println(x);
            throw new RuntimeException("Failed to convert String to Invoice: ");
        }
    }

    public String readQuery(String F_project_name) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(F_project_name + ".txt")) {
            System.out.println("readQuery");
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br2 = new BufferedReader(isr);
            String line2;
            String sql = "";
            while ((line2 = br2.readLine()) != null)
                sql += line2 + '\n';
            isr.close();
            br2.close();
            return sql;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}