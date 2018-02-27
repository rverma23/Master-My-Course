package com.mastermycourse.database;

import com.mastermycourse.json.JsonConverter;
import com.mastermycourse.pojos.*;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.plexus.util.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Authors: James DeCarlo, Rahul Verma and Zach Lerman.
 *
 * This class contains calls made to the db for Teacher related tasks.
 */
public class TeacherDBCommands extends StudentTeacherDBCommands {

    public TeacherDBCommands(ApplicationContext ctx) {
        super(ctx, "teacherDataSource");
    }

    /**
     * Inserts in the database new newly registered teacher
     *
     * @param email       is the email of the teacher
     * @param name        is the name of the teacher.
     * @param imageUrl    is the image url for the teachers image.
     * @param schoolName  is the name of the school the teacher belongs to
     * @param description is the description input by the teacher on registration
     * @return true if insert into db is success ful and false otherwise.
     * @throws SQLException
     */
    public boolean registerTeacher(String email, String name, String imageUrl, String schoolName, String description) throws SQLException {
        if (email == null || name == null || imageUrl == null || schoolName == null || description == null) {
            throw new IllegalArgumentException("All paramaters required");
        }

        String sql = "insert into mastermycourse.Users(email, name, status, imageUrl) values ('" + email + "', '" + name + "', 2 , '" + imageUrl + "')";
        PreparedStatement ps = connection.prepareStatement(sql);
        if (ps.executeUpdate() < 1) {
            return false;
        }

        sql = "Select U.id from mastermycourse.Users U where U.email = '" + email + "'";
        ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            return false;
        }

        sql = "INSERT INTO mastermycourse.Teachers (userId, schoolName, joinRequestDescription) " +
                "VALUES (?,?,?)";
        int id = rs.getInt("id");
        ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, schoolName);
        ps.setBytes(3, description.getBytes());

        if (ps.executeUpdate() > 0) {

            return true;
        }
        return false;
    }

    /**
     * query the db to check if a teacher account is approved.
     *
     * @param email the email of the teacher whose approval needs to be checked
     * @return true if approved and false otherwise.
     * @throws SQLException
     */
    public boolean isAccountApproved(String email) throws SQLException {
        String sql = "SELECT T.approved FROM mastermycourse.Users U, mastermycourse.Teachers T WHERE U.email = ? AND " +
                "U.id = T.userId";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("approved");
        }
        return false;
    }

    /**
     * insert into the db a newly created course
     *
     * @param email       is the email of the teacher who made the course
     * @param name        is the name of the course
     * @param description is the description of the course
     * @param isPublic    1 if course is public and 0 otherwise.
     * @return the course id if insert is successful and -1 otherwise.
     * @throws SQLException
     */
    public int createNewCourse(String email, String name, String description, boolean isPublic) throws SQLException {

        String sql = "insert INTO mastermycourse.Courses (name, description, isPublic, teacherId) " +
                "VALUES (?, ?, ?, (SELECT userId from mastermycourse.Users, mastermycourse.Teachers where id=userId and email=?))";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setBoolean(3, isPublic);
        ps.setString(4, email);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    /**
     * get the first course by a teacher and return the id of that course.
     *
     * @param email is the email of the teacher whos course we are searching
     * @return the id of the initial course, else return -1.
     * @throws SQLException
     */
    public int getInitialCourseId(String email) throws SQLException {
        String sql = "SELECT id from mastermycourse.Courses WHERE teacherId = (SELECT id FROM mastermycourse.Users WHERE email=?) LIMIT 1";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }

        return -1;
    }

    /**
     * get all courses by a teacher and return a map that maps the course name and the course id.
     *
     * @param email is the email of the teacher whos courses we are searching for
     * @return a map that maps course name and course id.
     * @throws SQLException
     */
    public Map<String, Integer> getCourses(String email) throws SQLException {
        String sql = "SELECT name, id from mastermycourse.Courses where teacherId = " +
                "(SELECT id from mastermycourse.Users where email=?) ORDER BY name DESC";
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
     * insert into the db a content module
     *
     * @param courseId     is the course id to which the content module belongs
     * @param chapterTitle is the chapter title of the content module
     * @param pageTitle    is the title of the page
     * @param style
     * @param body
     * @param rawText
     * @throws SQLException
     * @throws IOException
     */
    public void addContentModule(int courseId, String chapterTitle, String pageTitle, String style, BufferedImage body, String rawText) throws SQLException, IOException {
        String sql = "INSERT INTO mastermycourse.ContentModules (courseId, chapterTitle, title, style, body) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, courseId);
        ps.setString(2, chapterTitle);
        ps.setString(3, pageTitle);
        ps.setBytes(4, style.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(body, "png", baos);
        ps.setBytes(5, baos.toByteArray());
        ps.setQueryTimeout(120);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int contentModuleId = rs.getInt(1);
            sql = "INSERT INTO mastermycourse.ContentModuleAudio (contentModuleId, rawText) VALUES (?, ?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, contentModuleId);
            ps.setString(2, rawText);
            ps.setQueryTimeout(120);
            ps.executeUpdate();
        }
    }

    /**
     * delete all course content modules from a course
     *
     * @param courseId is the id of the course whose course content modules we are deleting
     * @throws SQLException
     */
    public void deleteCourseContentModules(int courseId) throws SQLException {
        String sql = "DELETE FROM mastermycourse.ContentModules WHERE courseId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.executeUpdate();
    }

    /**
     * query the db and return a course content outline
     *
     * @param courseId is the id of the course that we are querying
     * @return a list of Outline objects that hold the course content outline.
     * @throws SQLException
     */
    public List<Outline> getCourseOutline(int courseId) throws SQLException {
        String sql = "SELECT id, title, chapterTitle FROM mastermycourse.ContentModules WHERE courseId = ? ORDER BY id ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        List<Outline> outlines = new ArrayList<>();
        while (rs.next()) {
            Outline outline = new Outline();
            outline.setContentModuleId(rs.getInt("id"));
            outline.setChapterTitle(rs.getString("chapterTitle"));
            outline.setPageTitle(rs.getString("title"));
            outlines.add(outline);
        }
        return outlines;
    }

    /**
     * query and get the content module style
     *
     * @param contentModuleId is the id of the content module
     * @return a string containing the content module style
     * @throws SQLException
     */
    public String getContentModuleStyle(int contentModuleId) throws SQLException {
        String sql = "SELECT style FROM mastermycourse.ContentModules WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contentModuleId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Blob blob = rs.getBlob("style");
            return new String(blob.getBytes(1, (int) blob.length()));
        }
        return null;
    }


    /**
     * deletes a question from the db.
     *
     * @param column     is the column of the db table we query
     * @param courseId   is the course id the question belongs to
     * @param questionId is the question id
     * @throws SQLException
     */
    public void deleteQuestion(String column, int courseId, int questionId) throws SQLException {
        String sql = "DELETE FROM mastermycourse.QuizTestModuleQuestions WHERE courseId = ? AND " + column + "=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setInt(2, questionId);
        ps.executeUpdate();
        ps.close();
        connection.close();
    }

    /**
     * insert multiple choice question in db
     *
     * @param courseId       is the course id for the course that this question belongs to
     * @param prompt         the multiple choice question prompt
     * @param correctAnswer  the correct answer
     * @param wrong_answer_1 the wrong answer
     * @param wrong_answer_2 the wrong answer
     * @param wrong_answer_3 the wrong answer
     * @throws SQLException
     */
    public void addMultipleChoiceQuestion(int courseId, String prompt, String correctAnswer, String wrong_answer_1, String wrong_answer_2, String wrong_answer_3) throws SQLException {
        String sql = "INSERT into mastermycourse.MultipleChoiceModules (question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, prompt);
        ps.setString(2, correctAnswer);
        ps.setString(3, wrong_answer_1);
        ps.setString(4, wrong_answer_2);
        ps.setString(5, wrong_answer_3);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        int question_id = rs.getInt(1);
        sql = "INSERT INTO mastermycourse.QuizTestModuleQuestions (multipleChoiceModuleID, courseId) VALUES (?, ?)";
        ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, question_id);
        ps.setInt(2, courseId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    /**
     * insert a true/false question into the db
     *
     * @param courseId      the course id of the course for which this question is inserted
     * @param prompt        the question prompt
     * @param correctAnswer the correct answer
     * @throws SQLException
     */
    public void addTrueFalseQuestion(int courseId, String prompt, String correctAnswer) throws SQLException {
        String sql = "INSERT into mastermycourse.TrueFalseModule (question, answer) VALUES (?,?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, prompt);

        if (correctAnswer.equals("true")) {
            ps.setInt(2, 1);
        } else {
            ps.setInt(2, 0);
        }
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        int question_id = rs.getInt(1);
        sql = "INSERT INTO mastermycourse.QuizTestModuleQuestions (trueFalseModuleID, courseId) VALUES (?, ?)";
        ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, question_id);
        ps.setInt(2, courseId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    /**
     * insert an exact answer question into the db
     *
     * @param courseId      the course id of the course for which this question is inserted
     * @param prompt        the question prompt
     * @param correctAnswer the correct answer
     * @throws SQLException
     */
    public void addExactAnswerQuestion(int courseId, String prompt, String correctAnswer) throws SQLException {
        String sql = "INSERT into mastermycourse.ExactAnswerModule (question, answer) VALUES (?,?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, prompt);
        ps.setString(2, correctAnswer);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        int question_id = rs.getInt(1);
        sql = "INSERT INTO mastermycourse.QuizTestModuleQuestions (exactAnswerModuleID, courseId) VALUES (?, ?)";
        ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, question_id);
        ps.setInt(2, courseId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    /**
     * add code question into the db
     *
     * @param courseId          the course id of the course for which this question is inserted
     * @param prompt            the question prompt
     * @param testCases         the test cases for the code question.
     * @param programLanguageId id of the programming language
     * @param templateCode      template code for the question
     * @param expectedOutput    what the expected output should be
     * @throws SQLException
     */
    public void addCodeQuestion(int courseId, String prompt, String testCases, int programLanguageId, String templateCode, String expectedOutput) throws SQLException {
        String sql = "INSERT into mastermycourse.CodeQuestionModule (question, testCase, programLanguageId, templateCode, expectedOutput) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, prompt);
        ps.setString(2, testCases);
        ps.setInt(3, programLanguageId);
        ps.setString(4, templateCode);
        ps.setString(5, expectedOutput);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        int question_id = rs.getInt(1);
        sql = "INSERT INTO mastermycourse.QuizTestModuleQuestions (codeQuestionModuleID, courseId) VALUES (?, ?)";
        ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, question_id);
        ps.setInt(2, courseId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    /**
     * add an oversight question into the db
     *
     * @param courseId the course id of the course for which this question is inserted
     * @param prompt   the question prompt
     * @throws SQLException
     */
    public void addOversightQuestion(int courseId, String prompt) throws SQLException {
        String sql = "INSERT into mastermycourse.OversightQuestionModule (question) VALUES (?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, prompt);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        int question_id = rs.getInt(1);
        sql = "INSERT INTO mastermycourse.QuizTestModuleQuestions (oversightQuestionModule, courseId) VALUES (?, ?)";
        ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, question_id);
        ps.setInt(2, courseId);
        ps.executeUpdate();

        ps.close();
        connection.close();
    }

    /**
     * update multiple choice question in db
     *
     * @param id             is the multiple choice module id
     * @param questionPrompt the multiple choice question prompt
     * @param correct_answer the correct answer
     * @param wrong_answer_1 the wrong answer
     * @param wrong_answer_2 the wrong answer
     * @param wrong_answer_3 the wrong answer
     * @throws SQLException
     */
    public void updateMultipleChoiceQuestion(int id, String questionPrompt, String correct_answer, String wrong_answer_1, String wrong_answer_2, String wrong_answer_3) throws SQLException {
        String sql = "UPDATE mastermycourse.MultipleChoiceModules SET question=?, correctAnswer=?, wrongAnswer1=?, wrongAnswer2=?, wrongAnswer3=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, questionPrompt);
        ps.setString(2, correct_answer);
        ps.setString(3, wrong_answer_1);
        ps.setString(4, wrong_answer_2);
        ps.setString(5, wrong_answer_3);
        ps.setInt(6, id);
        ps.execute();
        ps.close();
        connection.close();
    }

    /**
     * update a true/false question into the db
     *
     * @param id             the id of the true false module
     * @param questionPrompt the question prompt
     * @param correct_answer the correct answer
     * @throws SQLException
     */
    public void updateTrueFalseQuestion(int id, String questionPrompt, String correct_answer) throws SQLException {
        String sql = "UPDATE mastermycourse.TrueFalseModule SET question=?, answer=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, questionPrompt);
        if (correct_answer.equals("true"))
            ps.setInt(2, 1);
        else
            ps.setInt(2, 0);
        ps.setInt(3, id);
        ps.execute();
        ps.close();
        connection.close();
    }

    /**
     * update an exact answer question into the db
     *
     * @param id             the id of exact answer module
     * @param questionPrompt the question prompt
     * @param correct_answer the correct answer
     * @throws SQLException
     */
    public void updateExactAnswerQuestion(int id, String questionPrompt, String correct_answer) throws SQLException {
        String sql = "UPDATE mastermycourse.ExactAnswerModule SET question=?, answer=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, questionPrompt);
        ps.setString(2, correct_answer);
        ps.setInt(3, id);
        ps.execute();
        ps.close();
        connection.close();
    }

    /**
     * update code question into the db
     *
     * @param id             the id of code question module
     * @param questionPrompt the question prompt
     * @param testCases      the test cases for the code question.
     * @param lang           id of the programming language
     * @param template       template code for the question
     * @param expectedOutput what the expected output should be
     * @throws SQLException
     */
    public void updateCodeQuestion(int id, String questionPrompt, String testCases, String expectedOutput, String template, int lang) throws SQLException {
        String sql = "UPDATE mastermycourse.CodeQuestionModule SET question=?, testCase=?, programLanguageId=?, templateCode=?, expectedOutput=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, questionPrompt);
        ps.setString(2, testCases);
        ps.setInt(3, lang);
        ps.setString(4, template);
        ps.setString(5, expectedOutput);
        ps.setInt(6, id);
        ps.execute();
        ps.close();
        connection.close();
    }

    /**
     * update an oversight question into the db
     *
     * @param id             the id of the oversight question module
     * @param questionPrompt the question prompt
     * @throws SQLException
     */
    public void updateOversightQuestion(int id, String questionPrompt) throws SQLException {
        String sql = "UPDATE mastermycourse.OversightQuestionModule SET question=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, questionPrompt);
        ps.setInt(2, id);
        ps.execute();
        ps.close();
        connection.close();
    }

    /**
     * query the db and get all quizzes from a course.
     *
     * @param courseId is the course id of the course whos queries we want.
     * @return an arraylist of all the quizzes in the course
     * @throws SQLException
     */
    public ArrayList<Quiz> getQuizzes(int courseId) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.QuizTestModule Where courseId=?";
        ArrayList<Quiz> quizzes = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Quiz quiz = new Quiz();
            int id = rs.getInt("id");
            quiz.setTitle(rs.getString("title"));
            quiz.setTeacherNotes(rs.getString("teacherNotes"));
            quiz.setId(id);
            Date date = rs.getDate("submissionDate");
            if (date == null) {
                quiz.setDate("");
            } else {
                quiz.setDate(date.toString());
            }
            // get the questions for this quiz
            sql = "SELECT * FROM mastermycourse.QuizTestOrder WHERE quizTestModuleId=? ORDER BY orderIndex";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                QuizTestOrder question = new QuizTestOrder();
                question.setTitle(rs2.getString("question"));
                question.setPoints(Integer.valueOf(rs2.getString("points")));
                question.setSubmissions(Integer.valueOf(rs2.getString("totalSubmissions")));
                question.setId(Integer.valueOf(rs2.getString("quizTestModuleId")));
                question.setQuestionId(Integer.valueOf(rs2.getString("quizTestModuleQuestionId")));
                quiz.getQuestions().add(question);
            }

            quizzes.add(quiz);
        }

        ps.execute();
        ps.close();
        connection.close();

        return quizzes;
    }

    /**
     * create a quiz test order and insert in to the db.
     *
     * @param quizId                   the id of the quiz
     * @param quizTestModuleQuestionId the question module id
     * @param order                    the order of the questions
     * @param totalSubmissions         the total number of submission for the quiz test order
     * @param points                   the total points
     * @param title                    the title of the quiz test order
     * @throws SQLException
     */
    public void createQuizTestOrder(int quizId, int quizTestModuleQuestionId, int order, int totalSubmissions, int points, String title) throws SQLException {
        String sql = "INSERT INTO mastermycourse.QuizTestOrder (quizTestModuleId, quizTestModuleQuestionId, orderIndex, totalSubmissions, points, question) VALUES(?,?,?,?,?,?);";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, quizId);
        ps.setInt(2, quizTestModuleQuestionId);
        ps.setInt(3, order);
        ps.setInt(4, totalSubmissions);
        ps.setInt(5, points);
        ps.setString(6, title);

        ps.execute();
        ps.close();
    }

    /**
     * This method removes a Quiz from the QuizTestModule table which maps quizzes to a course
     *
     * @param courseId the course id of the course whos quizzes we are deleting
     * @param id       the id of quiz test module
     * @throws SQLException
     */
    public void deleteQuiz(int courseId, int id) throws SQLException {
        String sql = "DELETE FROM mastermycourse.QuizTestModule WHERE courseId=? AND id=?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, courseId);
        ps.setInt(2, id);
        ps.execute();
        ps.close();
        connection.close();
    }

    /**
     * Returns the course code for a given course
     *
     * @param courseId the course Id of the current course
     * @return the course code, or 0 if there is none
     * @throws SQLException
     */
    public int getCourseCode(int courseId) throws SQLException {
        String sql = "SELECT courseCode FROM mastermycourse.Courses WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("courseCode");
        }

        return 0;
    }

    /**
     * update a quiztestmodule in the db
     *
     * @param id            the id of the quiz
     * @param title         the title of the quiz
     * @param date          the date of the quiz
     * @param questionArray the array containing all the questions in the quiz.
     * @param teacherNotes  all the notes the teacher left on the quiz
     * @throws SQLException
     * @throws JSONException
     */
    public void updateQuiz(int id, String title, Date date, JSONArray questionArray, String teacherNotes) throws SQLException, JSONException {
        String sql = "UPDATE mastermycourse.QuizTestModule SET title=?, teacherNotes=?, submissionDate=? where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, teacherNotes);
        if (date != null) {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            ps.setDate(3, sqlDate);
        } else {
            ps.setDate(3, null);
        }
        ps.setInt(4, id);
        ps.execute();

        sql = "DELETE FROM mastermycourse.QuizTestOrder WHERE quizTestModuleId=?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.execute();

        for (int i = 0; i < questionArray.length(); i++) {
            JSONObject jsonQuestion = questionArray.getJSONObject(i);
            int quizId = id;
            int questionId = jsonQuestion.getInt("moduleId");
            int points = jsonQuestion.getInt("points");
            int submissions = jsonQuestion.getInt("attempts");
            String question = jsonQuestion.getString("question");

            sql = "INSERT INTO mastermycourse.QuizTestOrder VALUES (?,?,?,?,?,?)";
            ps = connection.prepareStatement(sql);

            ps.setInt(1, quizId);
            ps.setInt(2, questionId);
            ps.setInt(3, i);
            ps.setInt(4, submissions);
            ps.setInt(5, points);
            ps.setString(6, question);
            ps.execute();
        }

        ps.close();
    }

    /**
     * create a quiztestmodule in the db
     *
     * @param courseId      the id of the course for which this quiz is being made
     * @param title         the title of the quiz
     * @param date          the date of the quiz
     * @param questionArray the array containing all the questions in the quiz.
     * @param teacherNotes  all the notes the teacher left on the quiz
     * @return the quiz id for the quiz that was made.
     * @throws SQLException
     * @throws JSONException
     */
    public int createQuiz(int courseId, String title, Date date, JSONArray questionArray, String teacherNotes) throws SQLException, JSONException {
        String sql = "INSERT INTO mastermycourse.QuizTestModule (title, courseId, teacherNotes, submissionDate) VALUES(?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, title);
        ps.setInt(2, courseId);
        ps.setString(3, teacherNotes);
        if (date != null) {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            ps.setDate(4, sqlDate);
        } else {
            ps.setDate(4, null);
        }
        ps.execute();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        int quiz_id = rs.getInt(1);
        // now loop through questions and insert them into the quiz

        for (int i = 0; i < questionArray.length(); i++) {
            String column, table;

            JSONObject question = questionArray.getJSONObject(i);
            String questionType = question.getString("question_type");
            int questionId = question.getInt("question_id");
            int points = question.getInt("points");
            int submissions = question.getInt("attempts");

            column = findColumn(questionType);
            sql = "SELECT id FROM mastermycourse.QuizTestModuleQuestions WHERE " + column + "=? AND courseId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, questionId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();
            rs.next();
            int quiz_test_id = rs.getInt(1);
            table = findTable(questionType);
            sql = "SELECT question FROM mastermycourse." + table + " where id=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, questionId);
            rs = ps.executeQuery();
            rs.next();
            String question_title = rs.getString("question");
            createQuizTestOrder(quiz_id, quiz_test_id, i, submissions, points, question_title);
        }

        return quiz_id;
    }

    /**
     * maps questionType to a column type
     *
     * @param questionType contains what kind of question type we want mapped to a column
     * @return the string column that was mapped.
     */
    public String findTable(String questionType) {
        String column;

        if (questionType.equals("multipleChoice")) {
            column = "MultipleChoiceModules";
        } else if (questionType.equals("trueFalse")) {
            column = "TrueFalseModule";
        } else if (questionType.equals("exactAnswer")) {
            column = "ExactAnswerModule";
        } else if (questionType.equals("oversight")) {
            column = "OversightQuestionModule";
        } else {
            column = "CodeQuestionModule";
        }

        return column;
    }

    /**
     * @param questionType
     * @return
     */
    public String findColumn(String questionType) {
        String column;

        if (questionType.equals("multipleChoice")) {
            column = "multipleChoiceModuleId";
        } else if (questionType.equals("trueFalse")) {
            column = "trueFalseModuleId";
        } else if (questionType.equals("exactAnswer")) {
            column = "exactAnswerModuleId";
        } else if (questionType.equals("oversight")) {
            column = "oversightQuestionModule";
        } else {
            column = "codeQuestionModuleId";
        }
        return column;
    }

    /**
     * gets the course name from the db
     *
     * @param courseId the id of the course whose name we want
     * @return the course name
     * @throws SQLException
     */
    public String getCourseName(int courseId) throws SQLException {
        String sql = "SELECT name FROM mastermycourse.Courses WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }

        return "";
    }

    /**
     * gets the list of all students in a course
     *
     * @param courseId the id of the course whose students we want
     * @return an array list of student in the course
     * @throws SQLException
     */
    public ArrayList<Student> getStudentList(int courseId) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.StudentCourses A, mastermycourse.Users B where A.userId = B.id AND courseId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        ArrayList<Student> studentList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("name");
            int id = rs.getInt("id");
            Student student = new Student();
            student.setName(name);
            student.setId(id);
            studentList.add(student);
        }
        ps.close();
        return studentList;
    }

    /**
     * insert a new course in the courses table in the db
     *
     * @param email       is the email of the teacher
     * @param name        the name of the course
     * @param description the description of the course
     * @param isPublic    if the course is public or not
     * @param courseCode  the course code
     * @return the id of the course if successfully inserted, else return -1
     * @throws SQLException
     */
    public int createNewCourse(String email, String name, String description, boolean isPublic, int courseCode) throws SQLException {
        String sql = "insert INTO mastermycourse.Courses (name, description, isPublic, courseCode, teacherId) " +
                "VALUES (?, ?, ?, ?, (SELECT userId from mastermycourse.Users, mastermycourse.Teachers where id=userId and email=?))";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        ps.setString(2, description);
        ps.setBoolean(3, isPublic);
        ps.setInt(4, courseCode);
        ps.setString(5, email);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    /**
     * delete a course from the db
     *
     * @param courseId the id of the course to delete
     * @throws SQLException
     */
    public void deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM mastermycourse.Courses WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.executeUpdate();
    }

    /**
     * query the db and get the student time metrics
     *
     * @param studentId the student id whose metrics we want
     * @param courseId  the course id for the course whose metrics we need
     * @return return an arraylist of the metrics we acquired
     * @throws SQLException
     */
    public ArrayList<ChapterMetrics> getStudentTimeMetrics(int studentId, int courseId) throws SQLException {
        String sql = "select id, title, chapterTitle, secondsSpentReadingContent " +
                "from mastermycourse.StudentContentMetrics, mastermycourse.ContentModules " +
                "where userId=? and courseId=? and mastermycourse.StudentContentMetrics.contentModuleId = id";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, studentId);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();

        ArrayList<ChapterMetrics> posTimes = createTimeMetricsTable(rs, true);
        ps.close();

        String sql2 = "select distinct id, title, chapterTitle " +
                "from mastermycourse.StudentContentMetrics, mastermycourse.ContentModules " +
                "where courseId=? ";
        PreparedStatement ps2 = connection.prepareStatement(sql2);
        ps2.setInt(1, courseId);
        ResultSet rs2 = ps2.executeQuery();

        ArrayList<ChapterMetrics> retTimes = createTimeMetricsTable(rs2, false);
        ps2.close();

        if (retTimes.size() == 0) {
            return retTimes;
        }

        for (ChapterMetrics c1 : retTimes) {
            for (ChapterMetrics c2 : posTimes) {
                if (c1.getChapterTitle().equals(c2.getChapterTitle())) {
                    for (PageMetrics pm : c1.getPm()) {
                        for (PageMetrics pm1 : c2.getPm()) {
                            if (pm.getId() == pm1.getId()) {
                                pm.setSeconds(pm1.getSeconds());
                            }
                        }
                    }
                }
            }
        }

        for (ChapterMetrics c : retTimes) {
            c.computeTotalTime();
        }

        return retTimes;

    }

    /**
     * create an arraylist of chaptermetrics from a result set.
     *
     * @param rs         the result set
     * @param isPositive a boolean to controll wether we want chapter metrics with positive time or not.
     * @return the arraylist of chapter metrics that was constructed.
     * @throws SQLException
     */
    public ArrayList<ChapterMetrics> createTimeMetricsTable(ResultSet rs, boolean isPositive) throws SQLException {
        ArrayList<ChapterMetrics> chaps = new ArrayList<ChapterMetrics>();
        boolean found = false;
        while (rs.next()) {

            int id = rs.getInt("id");
            String title = rs.getString("title");
            String chapterTitle = rs.getString("chapterTitle");
            int secondsSpentReading = 0;

            if (isPositive) {
                secondsSpentReading = rs.getInt("secondsSpentReadingContent");
            }

            for (ChapterMetrics c : chaps) {
                if (c.getChapterTitle().equals(chapterTitle)) {
                    found = true;
                    c.getPm().add(new PageMetrics(title, secondsSpentReading, id));
                }
            }

            if (!found) {
                ChapterMetrics c = new ChapterMetrics();
                c.setChapterTitle(chapterTitle);
                c.getPm().add(new PageMetrics(title, secondsSpentReading, id));
                chaps.add(c);
            } else {
                found = false;
            }
        }

        for (ChapterMetrics c : chaps) {
            c.computeTotalTime();
        }

        return chaps;
    }

    /**
     * query the db for student answers to a quiz and return an arraylist containing the student answers
     *
     * @param studentId is the id of the student
     * @param quizId    is the quiz id
     * @return an array list of the students answers
     * @throws SQLException
     */
    public ArrayList<StudentAnswer> getStudentAnswers(int studentId, int quizId) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.StudentAnswers where userId=? AND quizTestModuleId=?";
        ArrayList<StudentAnswer> studentAnswers = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, studentId);
        ps.setInt(2, quizId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int userId = rs.getInt("userId");
            int quizTestModuleQuestionId = rs.getInt("quizTestModuleQuestionId");
            String answer = rs.getString("answer");
            boolean isCorrect = rs.getBoolean("isCorrect");
            StudentAnswer studentAnswer = new StudentAnswer(userId, quizTestModuleQuestionId, answer, isCorrect);

            studentAnswers.add(studentAnswer);
        }

        ps.close();
        return studentAnswers;
    }

    /**
     * updates a student answer
     *
     * @param quizId       the id of the quiz
     * @param questionId   the id of the question in the quiz
     * @param studentId    the id of the student
     * @param teacherNotes the teachers notes on the answer
     * @param isCorrect    if the answer is correct or not.
     * @throws SQLException
     */
    public void updateStudentAnswer(int quizId, int questionId, int studentId, String teacherNotes, boolean isCorrect) throws SQLException {
        String sql = "UPDATE mastermycourse.StudentAnswers SET isCorrect=?, teacherComment=?, isGraded=? WHERE userID=? AND quizTestModuleId=? AND quizTestModuleQuestionId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, isCorrect);
        ps.setString(2, teacherNotes);
        ps.setInt(3, 1);
        ps.setInt(4, studentId);
        ps.setInt(5, quizId);
        ps.setInt(6, questionId);
        ps.execute();
        ps.close();
    }

    /**
     * @param studentId
     * @return
     * @throws SQLException
     */
    public ArrayList<Quiz> getStudentQuizzes(int studentId) throws SQLException {
        String sql = "select distinct title, id from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule where quizTestModuleId = id AND userId=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();

        ArrayList<Quiz> quizzes = createQuizzes(rs);

        ps.close();
        return quizzes;
    }

    /**
     * queries the db and returns all graded student quizzes
     *
     * @param studentId the id of the student whose quizzes we want
     * @param courseId  the id of the course the student is taking
     * @return an array list of graded quizzes constructed
     * @throws SQLException
     * @throws IOException
     */
    public ArrayList<Quiz> getGradedStudentQuizzes(int studentId, int courseId) throws SQLException, IOException {
        String sql = "select distinct title, id, points, isCorrect, mastermycourse.StudentAnswers.quizTestModuleQuestionId " +
                "from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule, mastermycourse.QuizTestOrder " +
                "where mastermycourse.StudentAnswers.quizTestModuleId = mastermycourse.QuizTestOrder.quizTestModuleId " +
                "AND QuizTestOrder.quizTestModuleId = id AND isGraded=? AND userId=? AND courseId = ? " +
                "and id not in( select id from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule where mastermycourse.StudentAnswers.quizTestModuleId = id and isGraded = 0)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, studentId);
        ps.setInt(3, courseId);
        ResultSet rs = ps.executeQuery();

        ArrayList<Quiz> quizzes = createQuizGradeTable(rs);
        ps.close();

        return quizzes;
    }

    /**
     * query the db and construct an arraylist containing all the quizzes taken by students in the the course
     *
     * @param courseId the id of the course that we querying
     * @return an arraylist of all the quizzes taken by the class
     * @throws SQLException
     * @throws IOException
     */
    public ArrayList<Quiz> getWholeClassQuizzesArrayList(int courseId) throws SQLException, IOException {
        String sql = "select distinct userId, title, id, points, isCorrect, mastermycourse.StudentAnswers.quizTestModuleQuestionId " +
                "from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule, mastermycourse.QuizTestOrder " +
                "where mastermycourse.StudentAnswers.quizTestModuleId = mastermycourse.QuizTestOrder.quizTestModuleId " +
                "AND QuizTestOrder.quizTestModuleId = id AND isGraded=? AND courseId = ? " +
                "and id not in( select id from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule where mastermycourse.StudentAnswers.quizTestModuleId = id and isGraded = 0)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();

        ArrayList<Quiz> quizzes = createQuizGradeTable(rs);
        return quizzes;
    }

    /**
     * query the db and construct an arraylist containing all the quizzes taken by students in the the course*
     *
     * @param courseId      the id of the course
     * @param wantHistogram is a boolean that decides if we want a histogram representing all the quiz grades.
     * @return a JSON string containing our quizzes data
     * @throws SQLException
     * @throws IOException
     */
    public String getWholeClassQuizzes(int courseId, boolean wantHistogram) throws SQLException, IOException {
        String sql = "select distinct userId, title, id, points, isCorrect, mastermycourse.StudentAnswers.quizTestModuleQuestionId " +
                "from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule, mastermycourse.QuizTestOrder " +
                "where mastermycourse.StudentAnswers.quizTestModuleId = mastermycourse.QuizTestOrder.quizTestModuleId " +
                "AND QuizTestOrder.quizTestModuleId = id AND isGraded=? AND courseId = ? " +
                "and id not in( select id from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule where mastermycourse.StudentAnswers.quizTestModuleId = id and isGraded = 0)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();

        ArrayList<Quiz> quizzes;
        ArrayList<QuizHistogram> hqs;

        String ret = null;
        if (!wantHistogram) {
            quizzes = createQuizGradeTable(rs);
            ret = (new JsonConverter()).convertQuizArrayListToJSON(quizzes);
        } else {
            hqs = createQuizHistogramTable(rs);
            ret = (new JsonConverter()).convertHistogramArrayListToJson(hqs);
        }
        ps.close();
        return ret;
    }

    /**
     * create a quizhistogram arraylist from our quizzes resultset
     *
     * @param rs is a resultset containing data about all the quizzes taken in a course
     * @return an arraylist of quizhistograms
     * @throws SQLException
     */
    public ArrayList<QuizHistogram> createQuizHistogramTable(ResultSet rs) throws SQLException {

        ArrayList<StudentQuiz> sqs = new ArrayList<>();
        ArrayList<QuizHistogram> qhs = new ArrayList<>();
        boolean found = false;

        while (rs.next()) {

            String quizTitle = rs.getString("title");
            int userId = rs.getInt("userId");
            int quizId = rs.getInt("id");
            int totalPoints = rs.getInt("points");
            int isCorrect = rs.getInt("isCorrect");
            int actualPoints = 0;

            if (isCorrect == 1) {
                actualPoints = actualPoints + totalPoints;
            }

            for (StudentQuiz sq : sqs) {
                if (sq.getQuiz().getId() == quizId) {
                    if (sq.getUserId() == userId) {
                        found = true;
                        sq.getQuiz().setTotalpoints(sq.getQuiz().getTotalpoints() + totalPoints);
                        sq.getQuiz().setActualpoints(sq.getQuiz().getActualpoints() + actualPoints);
                    }
                }

            }

            if (!found) {
                StudentQuiz studentQuiz = new StudentQuiz();
                studentQuiz.setUserId(userId);
                studentQuiz.getQuiz().setTitle(quizTitle);
                studentQuiz.getQuiz().setId(quizId);
                studentQuiz.getQuiz().setTotalpoints(totalPoints);
                studentQuiz.getQuiz().setActualpoints(actualPoints);
                sqs.add(studentQuiz);
            } else {
                found = false;
            }
        }

        for (StudentQuiz sq : sqs) {
            Quiz q = sq.getQuiz();
            double cGrade = (double) q.getActualpoints() / (double) q.getTotalpoints();
            cGrade = cGrade * 100;
            q.setComputedGrade(cGrade);
        }

        found = false;

        for (StudentQuiz sq : sqs) {

            for (QuizHistogram qh : qhs) {
                if (qh.getId() == sq.getQuiz().getId()) {
                    found = true;
                    double grade = sq.getQuiz().getComputedGrade();
                    int score = (int) Math.floor(grade);
                    score = score / 10;
                    score = (score == 10) ? score - 1 : score;
                    qh.getHistogram()[score] += 1;
                    qh.getAllGrades().add(grade);
                }
            }

            if (!found) {
                QuizHistogram qh = new QuizHistogram();
                qh.setTitle(sq.getQuiz().getTitle());
                qh.setId(sq.getQuiz().getId());
                double grade = sq.getQuiz().getComputedGrade();
                int score = (int) Math.floor(grade);
                score = score / 10;
                score = (score == 10) ? score - 1 : score;
                qh.getHistogram()[score] += 1;
                qh.getAllGrades().add(grade);
                qhs.add(qh);
            } else {
                found = false;
            }
        }

        return qhs;
    }

    /**
     * create an arraylist of quizzes. this arraylist contains data of all information of quizzes taken in a course
     *
     * @param rs is the ResultSet that contains information about quizzes taken in a course
     * @return an arraylist of quizzes that was constructed
     * @throws SQLException
     */
    public ArrayList<Quiz> createQuizGradeTable(ResultSet rs) throws SQLException {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        boolean found = false;

        while (rs.next()) {
            Quiz quiz = new Quiz();
            String quizTitle = rs.getString("title");
            int quizId = rs.getInt("id");
            int totalPoints = rs.getInt("points");
            int isCorrect = rs.getInt("isCorrect");
            int actualPoints = 0;

            if (isCorrect == 1) {
                actualPoints = actualPoints + totalPoints;
            }

            quiz.setTitle(quizTitle);
            quiz.setId(quizId);
            quiz.setTotalpoints(totalPoints);
            quiz.setActualpoints(actualPoints);

            for (Quiz q : quizzes) {
                if (q.getId() == quiz.getId()) {
                    found = true;
                    q.setTotalpoints(q.getTotalpoints() + quiz.getTotalpoints());
                    q.setActualpoints(q.getActualpoints() + quiz.getActualpoints());
                }
            }
            if (!found) {
                quizzes.add(quiz);
            } else {
                found = false;
            }
        }
        return quizzes;
    }

    /**
     * query the db and get all the ungraded quizzes for a student
     *
     * @param studentId is the student whose ungraded quizzes we want
     * @param courseId  if course id of the course the student belongs to
     * @return an arraylist of ungraded quizzes for a particular student.
     * @throws SQLException
     */
    public ArrayList<Quiz> getUngradedStudentQuizzes(int studentId, int courseId) throws SQLException {
        String sql = "select distinct title, id from mastermycourse.StudentAnswers, mastermycourse.QuizTestModule where quizTestModuleId = id AND isGraded=? AND userId=? AND courseId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 0);
        ps.setInt(2, studentId);
        ps.setInt(3, courseId);
        ResultSet rs = ps.executeQuery();

        ArrayList<Quiz> quizzes = createQuizzes(rs);

        ps.close();
        return quizzes;
    }

    /**
     * create an arraylist of quizzes and return it
     *
     * @param rs is the ResultSet that contains data about quizzes
     * @return the arraylist of quiz objects
     * @throws SQLException
     */
    public ArrayList<Quiz> createQuizzes(ResultSet rs) throws SQLException {
        ArrayList<Quiz> quizzes = new ArrayList<>();

        while (rs.next()) {
            Quiz quiz = new Quiz();
            String quizTitle = rs.getString("title");
            int quizId = rs.getInt("id");
            quiz.setTitle(quizTitle);
            quiz.setId(quizId);
            quizzes.add(quiz);
        }

        return quizzes;
    }

    /**
     * queries the db and gets the student id of a particular student
     *
     * @param email the email of the student we want to get the id of
     * @return the id of the student if found, else return -1
     * @throws SQLException
     */
    public int getStudentId(String email) throws SQLException {
        String sql = "SELECT id FROM mastermycourse.Users WHERE email=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }

        return -1;
    }

    /**
     * insert a TA in to the db
     *
     * @param name     the name of the TA
     * @param email    the email of the TA
     * @param courseId the course id of the course the TA belongs to
     * @return return a string "Success! TA has been added,green" if the insert is successful. else
     * return "An error occurred. This TA already exists.,red".
     * @throws SQLException
     */
    public String addTA(String name, String email, int courseId) throws SQLException {
        int studentId = getStudentId(email);
        PreparedStatement ps;

        if (studentId == -1) {
            // this student does not exist, add them to User table
            String sql = "INSERT INTO mastermycourse.Users (name, email, status) VALUES (?,?,?);";
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, 1); // all TA's are students
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                studentId = rs.getInt(1);
            }
        }

        try {
            String sql = "INSERT INTO mastermycourse.CourseTAs VALUES(?, ?);";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.execute();
            return "Success! TA has been added,green";
        } catch (MySQLIntegrityConstraintViolationException e) {
            // they already added this TA before, add handle code
            return "An error occurred. This TA already exists.,red";
        }
    }

    /**
     * Removes a student from a course, action triggered by a Teacher
     *
     * @param studentId of the student to be removed
     */
    public void removeStudentFromCourse(int studentId) throws SQLException {
        String sql = "DELETE FROM mastermycourse.StudentCourses where userId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, studentId);
        ps.execute();
        ps.close();
    }

    /**
     * Return all TAs related to the course
     *
     * @param courseId the id of the course the teacher is on
     * @return a list of TAs in that course
     */
    public ArrayList<TA> getTAs(int courseId) throws SQLException {
        String sql = "SELECT userId, name, email FROM mastermycourse.CourseTAs A, mastermycourse.Users B where B.id = A.userId AND A.courseId = ?";
        ArrayList<TA> taList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            TA ta = new TA();
            ta.setId(rs.getInt("userId"));
            ta.setName(rs.getString("name"));
            ta.setEmail(rs.getString("email"));
            taList.add(ta);
        }

        ps.close();
        return taList;
    }

    /**
     * Remove a TA from the db
     *
     * @param email the email of the TA that we want to remove.
     * @throws SQLException
     */
    public void removeTA(String email) throws SQLException {
        String sql = "SELECT id from mastermycourse.Users WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("id");
            sql = "DELETE FROM mastermycourse.CourseTAs WHERE userId=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.execute();
        }

        ps.close();
    }

    /**
     * query the db and get all the content module ids
     *
     * @param courseId the course id of the course whose content module ids we want
     * @return a list of ints that contain content module ids
     * @throws SQLException
     */
    public List<Integer> getCourseContentModuleIds(int courseId) throws SQLException {
        String sql = "SELECT id FROM mastermycourse.ContentModules WHERE courseId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        List<Integer> contentModuleIds = new ArrayList<>();
        while (rs.next()) {
            contentModuleIds.add(rs.getInt(1));
        }
        return contentModuleIds;
    }

    /**
     * insert page audio into the content module audio table in the db
     *
     * @param inputStream     the input stream whos bytes are input into the db
     * @param contentModuleId the content module id of the content module whos audio is being inserted
     * @throws SQLException
     * @throws IOException
     */
    public void insertPageAudio(InputStream inputStream, int contentModuleId) throws SQLException, IOException {
        String sql = "UPDATE mastermycourse.ContentModuleAudio SET audio=? WHERE contentModuleId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBytes(1, IOUtil.toByteArray(inputStream));
        ps.setInt(2, contentModuleId);
        ps.executeUpdate();
    }


    /**
     * Putting messaging board db stuff here
     */

    // Add a new post
    public void createNewPost(String message, String summary, int courseId) throws SQLException {
        String sql = "INSERT INTO mastermycourse.MessageBoard (message, summary, courseId, date) VALUES(?,?,?,?)";
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime()); // get current date
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, message);
        ps.setString(2, summary);
        ps.setInt(3, courseId);
        ps.setDate(4, date);
        ps.execute();
    }

    public ArrayList<Message> getMessages(int courseId) throws SQLException {
        String sql = "Select * From mastermycourse.MessageBoard Where courseId=?";
        ArrayList<Message> messages = new ArrayList<Message>();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Message message = new Message();
            message.setMessage(StringEscapeUtils.escapeEcmaScript(rs.getString("message")));
            message.setSummary(rs.getString("summary"));
            messages.add(message);
        }

        return messages;
    }
}