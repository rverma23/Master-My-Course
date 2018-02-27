package com.mastermycourse.database;

import com.mastermycourse.pojos.Teacher;
import com.mastermycourse.pojos.TeacherCourse;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Authors: James DeCarlo and Zach Lerman.
 *
 * This class manages calls to the database for administration related tasks.
 */
public class AdminDBCommands {

    private DataSource dataSource;
    private Connection connection;

    public AdminDBCommands(ApplicationContext ctx) {
        if(ctx == null){
            throw new IllegalArgumentException("Ctx Null");
        }
        dataSource = (DataSource)ctx.getBean("administratorDataSource");
        connection = DataSourceUtils.getConnection(dataSource);
    }

    /**
     * Queries the DB and gets a list of all pending teachers.
     * @return List<Teacher> list of all pending teachers.
     * @throws SQLException
     */
    public List<Teacher> getPendingTeachers() throws SQLException{
        List<Teacher> pendingTeachers = new ArrayList<>();
        String sql = "Select * from mastermycourse.Users U, mastermycourse.Teachers T where T.userId = U.id and T.approved = false";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Teacher t = new Teacher();
            t.setName(rs.getString("name"));
            t.setEmail(rs.getString("email"));
            t.setImageUrl(rs.getString("imageUrl"));
            t.setSchool(rs.getString("schoolName"));
            Blob desc = rs.getBlob("joinRequestDescription");
            t.setDescription(new String(desc.getBytes(1, (int)desc.length())));
            pendingTeachers.add(t);
        }

        return pendingTeachers;
    }

    /**
     * Deletes a teacher from the DB
     * @param email is the email of the teacher to remove.
     * @throws SQLException
     */
    public void deleteTeacher(String email) throws SQLException {
        if(email == null)
            throw new IllegalArgumentException("Email Can't Be Null");
        String sql = "Delete from mastermycourse.Users where email = ?;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ps.executeUpdate();
    }

    /**
     * Sets approval for a teacher in DB.
     * @param email is the email of the teacher to approve.
     * @throws SQLException
     */
    public void approveTeacher(String email) throws SQLException {
        if(email == null)
            throw new IllegalArgumentException("Email Can't be null");
        String sql = "update mastermycourse.Teachers set approved = 1 where userId = (select id from mastermycourse.Users where email=?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,email);
        ps.executeUpdate();

    }

    /**
     * Disables a teacher and updates the DB to record this change
     * @param email is the email of the teacher to disable.
     * @throws SQLException
     */
    public void disableTeacher(String email) throws SQLException {
        if(email == null)
            throw new IllegalArgumentException("Email Can't be null");
        String sql = "update mastermycourse.Teachers set approved = 0 where userId = (select id from mastermycourse.Users where email=?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,email);
        ps.executeUpdate();
    }

    /**
     * Queries the DB to get all the courses.
     * @return List<TeacherCourse> list of all courses taught by some teacher.
     * @throws SQLException
     */
    public List<TeacherCourse> getAllCourses() throws SQLException {
        String sql = "SELECT C.id, U.name, C.name, C.description, C.enabled, C.isPublic, U.imageUrl " +
                "FROM mastermycourse.Users U, mastermycourse.Teachers T, mastermycourse.Courses C " +
                "WHERE U.id = T.userId and T.userId = C.teacherId and T.approved = TRUE";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<TeacherCourse> teacherCourses = new ArrayList<>();
        while (rs.next()){
            TeacherCourse teacherCourse = new TeacherCourse();
            teacherCourse.setCourseId(rs.getInt(1));
            teacherCourse.setTeacherName(rs.getString(2));
            teacherCourse.setName(rs.getString(3));
            teacherCourse.setDescription(rs.getString(4));
            teacherCourse.setEnabled(rs.getBoolean(5));
            teacherCourse.setPublic(rs.getBoolean(6));
            teacherCourse.setTeacherImage(rs.getString(7));
            teacherCourses.add(teacherCourse);
        }
        return teacherCourses;
    }

    /**
     * Queries the DB to get a list of all course taught by some teacher.
     * @param teacherId is the id of the teacher whose courses we want.
     * @return List<TeacherCourse> list of all courses taught the teacher in question.
     * @throws SQLException
     */
    public List<TeacherCourse> getTeacherCourses(int teacherId) throws SQLException {
        String sql = "SELECT C.id, U.name, C.name, C.description, C.enabled, C.isPublic, U.imageUrl " +
                "FROM mastermycourse.Users U, mastermycourse.Teachers T, mastermycourse.Courses C " +
                "WHERE U.id = T.userId and T.userId = C.teacherId and T.approved = TRUE and C.teacherId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, teacherId);
        ResultSet rs = ps.executeQuery();
        List<TeacherCourse> teacherCourses = new ArrayList<>();
        while (rs.next()){
            TeacherCourse teacherCourse = new TeacherCourse();
            teacherCourse.setCourseId(rs.getInt(1));
            teacherCourse.setTeacherName(rs.getString(2));
            teacherCourse.setName(rs.getString(3));
            teacherCourse.setDescription(rs.getString(4));
            teacherCourse.setEnabled(rs.getBoolean(5));
            teacherCourse.setPublic(rs.getBoolean(6));
            teacherCourse.setTeacherImage(rs.getString(7));
            teacherCourses.add(teacherCourse);
        }
        return teacherCourses;
    }

    /**
     * Queries the DB and gets a list of all approved teachers
     * @return List<Teacher> list of all approved teachers.
     * @throws SQLException
     */
    public List<Teacher> getApprovedTeachers() throws SQLException {
        String sql = "Select * from mastermycourse.Users U, mastermycourse.Teachers T where T.userId = U.id and T.approved = true";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<Teacher> teachers = new ArrayList<>();
        while (rs.next()){
            Teacher t = new Teacher();
            t.setName(rs.getString("name"));
            t.setEmail(rs.getString("email"));
            t.setImageUrl(rs.getString("imageUrl"));
            t.setSchool(rs.getString("schoolName"));
            Blob desc = rs.getBlob("joinRequestDescription");
            t.setDescription(new String(desc.getBytes(1, (int)desc.length())));
            t.setId(rs.getInt("id"));
            teachers.add(t);
        }
        return teachers;
    }

    /**
     * Update the DB to record the disabling of a course.
     * @param courseId is the course id of the course we are disabling.
     * @throws SQLException
     */
    public void disableCourse(int courseId) throws SQLException {
        String sql = "UPDATE mastermycourse.Courses SET enabled=FALSE WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,courseId);
        ps.executeUpdate();
    }

    /**
     * Update the DB to record the enabling of a course.
     * @param courseId is the course id of the course we are enabling.
     * @throws SQLException
     */
    public void enableCourse(int courseId) throws SQLException {
        String sql = "UPDATE mastermycourse.Courses SET enabled=TRUE WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,courseId);
        ps.executeUpdate();
    }

    /**
     * Delete a course of the DB
     * @param courseId is the course id of the course we are deleting.
     * @throws SQLException
     */
    public void deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM mastermycourse.Courses WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,courseId);
        ps.executeUpdate();
    }

    /**
     * Queries the DB to get the teacher for a particular course.
     * @param courseId is the course for whose teacher we are querying for.
     * @return Teacher is the teacher we queried for.
     * @throws SQLException
     */
    public Teacher getTeacherByCourseId(int courseId) throws SQLException {
        String sql = "SELECT email, name FROM mastermycourse.Users " +
                "WHERE id = (SELECT teacherId FROM mastermycourse.Courses WHERE id = ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,courseId);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            Teacher teacher = new Teacher();
            teacher.setEmail(rs.getString("email"));
            teacher.setName(rs.getString("name"));
            return teacher;
        }
        return null;
    }

    /**
     * Query the DB for the course code of a particular course
     * @param courseId is the course id of the course whose course code we are looking for.
     * @return int is the course code we queried for.
     * @throws SQLException
     */
    public int getCourseCode(int courseId) throws SQLException {
        String sql =  "SELECT courseCode FROM mastermycourse.Courses WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,courseId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int courseCode = rs.getInt("courseCode");
            return courseCode;
        }
        return 0;
    }

    /**
     * closes the connection to the DB.
     */
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
