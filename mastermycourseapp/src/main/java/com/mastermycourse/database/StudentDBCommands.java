package com.mastermycourse.database;

import com.mastermycourse.pojos.Note;
import com.mastermycourse.pojos.Outline;
import com.mastermycourse.pojos.TeacherCourse;
import com.mastermycourse.pojos.User;
import org.springframework.context.ApplicationContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Authors: James DeCarlo, Rahul Verma and Zach Lerman.
 *
 * This class contains calls made to the db for Student related tasks.
 */
public class StudentDBCommands extends StudentTeacherDBCommands {

    public StudentDBCommands(ApplicationContext ctx) {
        super(ctx, "studentDataSource");
    }

    /**
     * Inserts in the DB a newly register student.
     *
     * @param userEmail email of the student
     * @param userName  name of the student
     * @param imageUrl  image url of the student
     * @return true on success and false on failure to insert into DB.
     * @throws SQLException
     */
    public boolean registerStudent(String userEmail, String userName, String imageUrl) throws SQLException {
        if (userEmail == null || imageUrl == null || userName == null) {
            throw new IllegalArgumentException("All values must be initialized");
        }

        String sql = "insert into mastermycourse.Users(email, name, status, imageUrl) values ('" + userEmail + "', '" + userName + "', 1 , '" + imageUrl + "')";
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.execute();
    }

    /**
     * Query the db by email for a particular user. Create a User object, populate it with info from the query and return.
     *
     * @param email is email of the user we are querying the db for.
     * @return theUser object created.
     * @throws SQLException
     */
    public User getUserByEmail(String email) throws SQLException {
        if (email == null) {
            throw new IllegalArgumentException("Email Can't be null");
        }

        String sql = "Select * from mastermycourse.Users U where U.email = '" + email + "'";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setImageUrl(rs.getString("imageUrl"));
            user.setStatus(rs.getInt("status"));
            return user;
        }
        return null;
    }

    /**
     * Query the db for courses taken by a particular student.  Create a map that maps course name to course id for
     * the courses queried and return this map.
     *
     * @param email is the email of the student.
     * @return the map we created of course names and course ids.
     * @throws SQLException
     */
    public Map<String, Integer> getCourses(String email) throws SQLException {
        String sql = "SELECT C.name, C.id FROM mastermycourse.Users U, mastermycourse.StudentCourses S, mastermycourse.Courses C " +
                "WHERE U.email = ? AND U.id = S.userId AND S.courseId = C.id";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        Map<String, Integer> map = new HashMap<>();

        while (rs.next()) {
            map.put(rs.getString("name"), rs.getInt("id"));
        }

        return map;
    }

    /**
     * queries the db to check if a particular student is a TA. return 1 if the student is a TA and 0 if not.
     *
     * @param email is the email of the student we are querying for.
     * @return 1 if student is TA and 0 if not.
     * @throws SQLException
     */
    public int isTA(String email) throws SQLException {
        String sql = "select * from mastermycourse.CourseTAs C, mastermycourse.Users U WHERE C.userId = U.id AND U.email=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return 1;
        }
        return 0;
    }

    /**
     * Checks if a student is a TA of a particular course. If the student is a TA of the particular course return 1, return 0 if not.
     *
     * @param email    is the email of the student in question.
     * @param courseId is the course id of the course in question.
     * @return 1 if the student is a TA of the particular course, return 0 if not.
     * @throws SQLException
     */
    public int isTA(String email, int courseId) throws SQLException {
        String sql = "select * from mastermycourse.CourseTAs C, mastermycourse.Users U WHERE C.userId = U.id AND U.email=? AND C.courseId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return 1;
        }
        return 0;
    }

    /**
     * Queries the db for all courses a student is TAing.  Creates a map between these course titles and
     * the course ids. Returns the map created.
     *
     * @param email if the email of the student in question.
     * @return the map created between course names and course ids.
     * @throws SQLException
     */
    public Map<String, Integer> getTACourses(String email) throws SQLException {
        String sql = "select C2.id, C2.name from mastermycourse.CourseTAs C, mastermycourse.Users U, mastermycourse.Courses C2 WHERE C.userId = U.id AND C.courseId = C2.id AND email=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        Map<String, Integer> map = new HashMap<>();

        while (rs.next()) {
            map.put(rs.getString("name"), rs.getInt("id"));
        }

        return map;
    }

    /**
     * queries the db for content modules of a particular course. creates the course outline for the course.
     *
     * @param courseId the course id of the course we are querying for.
     * @return list of the outlines.
     * @throws SQLException
     */
    public List<Outline> getCourseOutline(int courseId) throws SQLException {
        String sql = "SELECT id, title, chapterTitle, quizId FROM mastermycourse.ContentModules WHERE courseId = ? ORDER BY id ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        List<Outline> outlines = new ArrayList<>();
        while (rs.next()) {
            Outline outline = new Outline();
            outline.setContentModuleId(rs.getInt("id"));
            outline.setChapterTitle(rs.getString("chapterTitle"));
            outline.setPageTitle(rs.getString("title"));
            outline.setQuizId(rs.getInt("quizId"));
            outlines.add(outline);
        }
        return outlines;
    }

    /**
     * Queries the db to find the courseId of the last course the student was on.
     *
     * @param email is the email of the student
     * @return the course id of the last course the student was on, if there are no courses return -1.
     * @throws SQLException
     */
    public int getInitialCourseId(String email) throws SQLException {
        String sql = "SELECT S.courseId, S.lastLoggedInTime FROM mastermycourse.StudentCourses S, mastermycourse.Users U " +
                "WHERE U.email = ? AND U.id = S.userId ORDER BY S.lastLoggedInTime DESC LIMIT 1";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int courseId = rs.getInt("courseId");
            if (courseId > 0) {
                return courseId;
            }
        }

        return -1;
    }

    /**
     * Query the db for courses by teacher. create a list of these TeacherCourse objects and return it.
     *
     * @param email email of the teacher.
     * @return the list of TeacherCourse objects
     * @throws SQLException
     */
    public List<TeacherCourse> getAllCoursesNotAlreadyIn(String email) throws SQLException {
        String sql = "SELECT C.id, U.name, C.name, C.description, C.enabled, C.isPublic, U.imageUrl " +
                "FROM mastermycourse.Users U, mastermycourse.Teachers T, mastermycourse.Courses C " +
                "WHERE U.id = T.userId and T.userId = C.teacherId and T.approved = TRUE AND C.enabled=TRUE AND C.isPublic=TRUE " +
                "AND C.id NOT IN (SELECT SC.courseId FROM mastermycourse.StudentCourses SC, mastermycourse.Users US where US.id = SC.userId and email=?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        List<TeacherCourse> teacherCourses = new ArrayList<>();
        while (rs.next()) {
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
     * Query the db for due date of a quiz. and return wether we are past due date or not.
     *
     * @param quizId the quiz id of the quiz whose due date we want to check.
     * @return 1 if we are past due date and return 0 if we are not past due date or there is not due date.
     * @throws SQLException
     */
    public int checkDueDate(int quizId) throws SQLException {
        String sql = "select submissionDate from mastermycourse.QuizTestModule WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Date date = new Date();
            Date dueDate = rs.getDate("submissionDate");
            if (dueDate == null) {
                return 0; // null indicates no due date.
            } else if (date.after(dueDate)) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * insert in to the db the joining of a student into a course.
     *
     * @param email    the email of the student joining the course
     * @param courseId the course id of the course being joined
     * @throws SQLException
     */
    public void joinCourse(String email, int courseId) throws SQLException {
        String sql = "INSERT INTO mastermycourse.StudentCourses (courseId, userId, approved) " +
                "VALUES (?,(SELECT id FROM mastermycourse.Users WHERE email=?) , TRUE)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setString(2, email);
        ps.executeUpdate();
    }

    /**
     * Query the db for the last pdf page a student viewed before logout and return the id of this content page. if non exists return id
     * of the first page of the pdf
     *
     * @param userId   is the id of the student
     * @param courseId is the id of the course
     * @return the id of the content module (pdf page) that the student viewed last before logout. If there isn't one, then return the id of
     * the first page.
     * @throws SQLException
     */
    public int getInitialCourseContentModuleId(int userId, int courseId) throws SQLException {
        String sql = "SELECT SCM.contentModuleId, SCM.timestamp FROM mastermycourse.StudentContentMetrics SCM, mastermycourse.ContentModules CM " +
                "WHERE SCM.contentModuleId = CM.id AND SCM.userId = ? AND CM.courseId = ? " +
                "ORDER BY SCM.timestamp DESC LIMIT 1";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("contentModuleId");
        }


        sql = "SELECT MIN(id) FROM mastermycourse.ContentModules WHERE courseId = ?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }


    /**
     * Query the db to see if the student took a quiz.
     *
     * @param quizId is the quiz we want to see if the student took.
     * @param email  is the email of the student.
     * @return 1 if the student took the quiz else return 0.
     * @throws SQLException
     */
    public int tookQuiz(int quizId, String email) throws SQLException {
        String sql = "select * from mastermycourse.StudentAnswers where quizTestModuleId=? AND userId = (SELECT id from mastermycourse.Users where email=?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ps.setString(2, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            // recorded the student answering the questions
            return 1;
        }
        return 0;
    }

    /**
     * Inserts a student answer to a question in the db.
     *
     * @param userId the id of the user that is submitting the answer.
     * @param quizId the id of the quiz the contains the question being answered
     * @param questionId the id of the question being answered
     * @param studentAnswer the answer given by the student
     * @param isCorrect whether the answer is correct or not
     * @param isGraded to mark if the question/quiz is graded.
     * @throws SQLException
     */
    public void addStudentAnswer(int userId, int quizId, int questionId, String studentAnswer, boolean isCorrect, boolean isGraded) throws SQLException {
        String sql = "INSERT INTO mastermycourse.StudentAnswers VALUES (?,?,?,?,?,?,?,?)";
        java.util.Date date = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, quizId);
        ps.setInt(3, questionId);
        ps.setDate(4, sqlDate);
        ps.setString(5, studentAnswer);
        ps.setBoolean(6, isCorrect);
        ps.setString(7, "");
        ps.setBoolean(8, isGraded);
        ps.execute();
        ps.close();
    }

    /**
     * Queries to see if the given course code is valid
     * @param courseCode the course code to test
     * @return the course id if it is valid else return -1.
     * @throws SQLException
     */
    public int validateCourseCode(int courseCode) throws SQLException {
        String sql = "SELECT id FROM mastermycourse.Courses where courseCode=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseCode);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int courseId = rs.getInt("id");
            return courseId;
        } else {
            return -1;
        }
    }

    /**
     * queries the db for content module audio.
     * @param contentModuleId is the id of the content module whose audio we want.
     * @return an array of bytes from the db call if the content module exists, else return byte[0].
     * @throws SQLException
     */
    public byte[] getAudio(int contentModuleId) throws SQLException {
        String sql = "SELECT audio FROM mastermycourse.ContentModuleAudio WHERE contentModuleId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contentModuleId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBytes(1);
        }

        return new byte[0];
    }

    /**
     * Queries the db to get a list of notes for a student taking a course.
     * @param courseId the id of the course whose notes are queried
     * @param userId the id of the user whose notes are queried
     * @return return the list of notes.
     * @throws SQLException
     */
    public List<Note> getNotes(int courseId, int userId) throws SQLException {
        String sql = "SELECT title, notes FROM mastermycourse.StudentNotes WHERE courseId = ? and userId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();

        List<Note> notes = new ArrayList<>();
        while (rs.next()) {
            Note note = new Note();
            note.setTitleNote(rs.getString("title"));
            note.setnoteText(rs.getString("notes"));
            notes.add(note);
        }
        return notes;
    }

    /**
     * Delete a note in the db
     * @param courseId id of the course the note belongs to
     * @param userId if of the user the note belongs to
     * @param title the title of the note.
     * @throws SQLException
     */
    public void deleteNote(int courseId, int userId, String title) throws SQLException {
        String sql = "DELETE FROM mastermycourse.StudentNotes WHERE courseId = ? AND userId = ? AND title = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setInt(2, userId);
        ps.setString(3, title);
        ps.executeUpdate();
    }

    /**
     * Add a note into the db
     * @param userId id of the user to whom the note belongs
     * @param courseId id of the course to whom the note belongs
     * @param title is the title of the note
     * @param note is the description of the note
     * @throws SQLException
     */
    public void addNote(int userId, int courseId, String title, String note) throws SQLException {
        String sql = "INSERT INTO mastermycourse.StudentNotes (userId, courseId, title, notes) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, courseId);
        ps.setString(3, title);
        ps.setString(4, note);
        ps.executeUpdate();
    }

    /**
     * Query the db and update a note in the db.
     * @param userId is the id of the user to whom the note belongs
     * @param courseId is the id of the course to whom the note belongs
     * @param title is the title of the note
     * @param note is the new description of the note.
     * @throws SQLException
     */
    public void updateNote(int userId, int courseId, String title, String note) throws SQLException {
        String sql = "UPDATE mastermycourse.StudentNotes SET notes = ? WHERE userId = ? AND courseId = ? AND title = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, note);
        ps.setInt(2, userId);
        ps.setInt(3, courseId);
        ps.setString(4, title);
        ps.executeUpdate();
    }

    /**
     * Queries the db to check if a note title exists.
     * @param userId is the id of the user whose note we want to check
     * @param courseId is the id of the course
     * @param title is the title of the note we want to check
     * @return return true if the note exists , else return false.
     * @throws SQLException
     */
    public boolean checkNoteTitleExists(int userId, int courseId, String title) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.StudentNotes WHERE userId = ? AND courseId = ? AND title = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, courseId);
        ps.setString(3, title);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return true;
        }
        return false;
    }

    /**
     * Update or insert the seconds the student spent on page also updates the timestamp this call was made
     *
     * @param userId
     * @param contentModuleId
     * @param seconds
     * @throws SQLException
     */
    public void insertUpdateStudentTimeOnPage(int userId, int contentModuleId, int seconds) throws SQLException {
        String sql = "INSERT INTO mastermycourse.StudentContentMetrics (userId, contentModuleId, secondsSpentReadingContent, timestamp)" +
                "VALUES (?, ?, ?, now())" +
                "ON DUPLICATE KEY UPDATE secondsSpentReadingContent = secondsSpentReadingContent + ?, timestamp = now()";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, contentModuleId);
        ps.setInt(3, seconds);
        ps.setInt(4, seconds);
        ps.executeUpdate();
    }


}
