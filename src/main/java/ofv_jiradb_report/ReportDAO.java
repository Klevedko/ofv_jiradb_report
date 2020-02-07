package ofv_jiradb_report;


import javax.sql.DataSource;

public interface ReportDAO {
    public void setDataSourceFFOMS(DataSource ds);
    public void setDataSourceOTV(DataSource ds);
    public void setDataSourceZABBIX(DataSource ds);
}