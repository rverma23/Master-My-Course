package com.mastermycourse.database;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Authors: James DeCarlo, Rahul Verma and Zach Lerman.
 *
 * This class contains calls made to the db for Student and Teacher related tasks.
 */
public class StudentTeacherDBCommands {

    protected DataSource dataSource;
    protected Connection connection;

    protected StudentTeacherDBCommands(ApplicationContext ctx, String beanDataSource) {
        dataSource = (DataSource) ctx.getBean(beanDataSource);
        connection = DataSourceUtils.getConnection(dataSource);
    }

    /**
     * queries the db by course id for a course name.
     * @param id is the id of the course whose name we want.
     * @return the course name if it exists, else return null.
     * @throws SQLException
     */
    public String getCourseName(int id) throws SQLException {
        String sql = "SELECT name from mastermycourse.Courses where id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
        return null;
    }

    /**
     * Queries the db for a content module and returns its byte[] if it exits
     * @param contentModuleId the id of the content module who we are querying for.
     * @return byte[] of the content module if it exits, else return null.
     * @throws SQLException
     */
    public byte[] getContentModuleHtml(int contentModuleId) throws SQLException {
        String sql = "SELECT body FROM mastermycourse.ContentModules WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contentModuleId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Blob blob = rs.getBlob("body");
            return blob.getBytes(1, (int) blob.length());
        }
        return null;
    }

    /**
     * Queries the db to get the previous content module if there is one..
     * @param courseId the id of the course whose content module we are searching
     * @param currentContentModuleId the content module id of the current content module.
     * @return the previous content module id if there is one, else return the current content module id.
     * @throws SQLException
     */
    public int getPreviousCourseModuleId(int courseId, int currentContentModuleId) throws SQLException {
        String sql = "SELECT MAX(id) FROM mastermycourse.ContentModules WHERE courseId = ? and id < ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setInt(2, currentContentModuleId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int ret = rs.getInt(1);
            if (ret > 0) {
                return ret;
            }
        }
        return currentContentModuleId;
    }

    /**
     * Queries the db to get the next content module if there is one..
     * @param courseId the id of the course whose content module we are searching
     * @param currentContentModuleId the content module id of the current content module.
     * @return the next content module id if there is one, else return the current content module id.
     * @throws SQLException
     */
    public int getNextCourseModuleId(int courseId, int currentContentModuleId) throws SQLException {
        String sql = "SELECT MIN(id) FROM mastermycourse.ContentModules WHERE courseId = ? and id > ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setInt(2, currentContentModuleId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int ret = rs.getInt(1);
            if (ret > 0) {
                return ret;
            }
        }
        return currentContentModuleId;
    }

    /**
     * Queries the db and gets the raw text for a content module.
     * @param contentModuleId id of the content module whose raw text we want.
     * @return the raw text of the content module searched if it exits, else return null.
     * @throws SQLException
     */
    public String getContentRawText(int contentModuleId) throws SQLException {
        String sql = "SELECT rawText FROM mastermycourse.ContentModuleAudio WHERE contentModuleId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contentModuleId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }

    /**
     * Queries db to check if course is enabled.
     * @param courseId is the course id of the course we want to check
     * @return true if the course is enabled, else return false.
     * @throws SQLException
     */
    public boolean isCourseEnabled(int courseId) throws SQLException {
        String sql = "SELECT enabled FROM mastermycourse.Courses WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("enabled");
        }
        return false;
    }

    /**
     * closes the connection to the db.
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            // Do nothing
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}
