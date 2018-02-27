package com.mastermycourse.database;

import com.mastermycourse.pojos.Question;
import com.mastermycourse.pojos.TeacherNote;
import com.mastermycourse.pojos.UpcomingQuiz;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authors: Zach Lerman and James DeCarlo.
 *
 * This class ocntains calls made to the DB for Question/Quiz related tasks.
 */
public class QuestionDBCommands {

    private DataSource dataSource;
    private Connection connection;
    private Logger log = Logger.getLogger(QuestionDBCommands.class.getName());

    public QuestionDBCommands(ApplicationContext ctx) {
        dataSource = (DataSource) ctx.getBean("teacherDataSource");
        connection = DataSourceUtils.getConnection(dataSource);
    }

    /**
     * Queries the DB for particular multiple choice question. Create a question object that contains the data of the multiple choice question
     * that was queried.
     *
     * @param questionId id of the question we are querying.
     * @param id         that we set for the question object.
     * @return the Question object we created.
     * @throws SQLException
     */
    public Question createMultipleChoiceQuestion(int questionId, int id) throws SQLException {
        String sql = "SELECT * From mastermycourse.MultipleChoiceModules Where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Question question = new Question();

        ps.setInt(1, questionId);
        ResultSet rs_question = ps.executeQuery();
        rs_question.next();

        String[] prompts = new String[4];
        prompts[0] = rs_question.getString("correctAnswer");
        prompts[1] = rs_question.getString("wrongAnswer1");
        prompts[2] = rs_question.getString("wrongAnswer2");
        prompts[3] = rs_question.getString("wrongAnswer3");

        question.setId(id);
        question.setQuestion_id(questionId);
        question.setQuestionType("multipleChoice");
        question.setQuestion(rs_question.getString("question"));
        question.setAnswer(rs_question.getString("correctAnswer"));
        question.setPrompts(prompts);
        return question;
    }

    /**
     * randomly arranges the multiple choice question choices.
     *
     * @param question is the multiple choice question whose choices we are randomly arranging.
     */
    public void scrambleMultipleChoiceQuestion(Question question) {
        List<Integer> indices = new ArrayList<Integer>();
        indices.add(0);
        indices.add(1);
        indices.add(2);
        indices.add(3);

        Collections.shuffle(indices);
        String[] prompts = question.getPrompts();
        String[] temp = new String[4];
        temp[0] = prompts[indices.get(0)];
        temp[1] = prompts[indices.get(1)];
        temp[2] = prompts[indices.get(2)];
        temp[3] = prompts[indices.get(3)];
        prompts = temp;
        question.setPrompts(temp);
    }

    /**
     * Queries the DB for particular true false question. Create a question object that contains the data of the true false question
     * that was queried.
     *
     * @param questionId id of the question we are querying.
     * @return the Question object we created.
     * @throws SQLException
     */
    public Question createTrueFalseQuestion(int questionId) throws SQLException {
        String sql = "SELECT * From mastermycourse.TrueFalseModule Where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Question question = new Question();

        ps.setInt(1, questionId);
        ResultSet rs_question = ps.executeQuery();
        rs_question.next();

        question.setQuestion_id(questionId);
        question.setQuestionType("trueFalse");
        question.setQuestion(rs_question.getString("question"));
        question.setAnswer(rs_question.getString("answer"));
        return question;
    }

    /**
     * Queries the DB for particular exact answer question. Create a question object that contains the data of the exact answer question
     * that was queried.
     *
     * @param questionId id of the question we are querying.
     * @return the Question object we created.
     * @throws SQLException
     */
    public Question createExactAnswerQuestion(int questionId) throws SQLException {
        String sql = "SELECT * From mastermycourse.ExactAnswerModule Where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Question question = new Question();

        ps.setInt(1, questionId);
        ResultSet rs_question = ps.executeQuery();
        rs_question.next();
        question.setQuestion_id(questionId);
        question.setQuestionType("exactAnswer");
        question.setQuestion(rs_question.getString("question"));
        question.setAnswer(rs_question.getString("answer"));
        return question;
    }

    /**
     * Queries the DB for particular oversight question. Create a question object that contains the data of the oversight question
     * that was queried.
     *
     * @param questionId id of the question we are querying.
     * @return the Question object we created.
     * @throws SQLException
     */
    public Question createOversightQuestion(int questionId) throws SQLException {
        String sql = "SELECT * From mastermycourse.OversightQuestionModule Where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Question question = new Question();

        ps.setInt(1, questionId);
        ResultSet rs_question = ps.executeQuery();
        rs_question.next();

        question.setQuestion_id(questionId);
        question.setQuestionType("oversight");
        question.setQuestion(rs_question.getString("question"));
        return question;
    }

    /**
     * Queries the DB for particular code question. Create a question object that contains the data of the code question
     * that was queried.
     *
     * @param questionId id of the question we are querying.
     * @return the Question object we created.
     * @throws SQLException
     */
    public Question createCodeQuestion(int questionId) throws SQLException {
        String sql = "SELECT * From mastermycourse.CodeQuestionModule Where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Question question = new Question();

        ps.setInt(1, questionId);
        ResultSet rs_question = ps.executeQuery();
        rs_question.next();

        String[] inputs = rs_question.getString("testCase").split(",");

        question.setQuestion_id(questionId);
        question.setQuestionType("code");
        question.setQuestion(rs_question.getString("question"));
        question.setAnswer(rs_question.getString("expectedOutput"));
        question.setTemplate_code(rs_question.getString("templateCode"));
        question.setLanguage(Integer.valueOf(rs_question.getString("programLanguageId")));
        question.setPrompts(inputs);

        return question;
    }

    /**
     * Queries the DB for quizzes and gets a list of teacher notes on a quiz.
     *
     * @param quizId is the quiz for whose notes we seek.
     * @return ArrayList<TeacherNote> is a list of notes we queried for.
     * @throws SQLException
     */
    public ArrayList<TeacherNote> getNotes(int quizId, int userId) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.StudentAnswers WHERE quizTestModuleId=? AND userId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();
        ArrayList<TeacherNote> notes = new ArrayList<TeacherNote>();

        while (rs.next()) {
            String comment = rs.getString("teacherComment");
            if (comment.length() > 1) {  // the teacher left a comment
                int questionId = rs.getInt("quizTestModuleQuestionId");
                sql = "SELECT * From mastermycourse.QuizTestModuleQuestions Where id=?";
                ps = connection.prepareStatement(sql);
                ps.setInt(1, questionId);
                ResultSet rs2 = ps.executeQuery();
                rs2.next();
                Question question = createQuestion(rs2);
                TeacherNote teacherNote = new TeacherNote();
                teacherNote.setIsCorrect(rs.getInt("isCorrect"));
                teacherNote.setQuestionPrompt(question.getQuestion());
                teacherNote.setTeacherComment(rs.getString("teacherComment"));
                notes.add(teacherNote);
            }
        }

        return notes;
    }

    /**
     * Queries the DB for quizzes in a particular course. Creates a list of upcoming quizzes.
     *
     * @param courseId the id of the course whose quizzes we are querying for.
     * @return ArrayList<UpcomingQuiz> is the list of upcoming quizzes we created.
     * @throws SQLException
     */
    public ArrayList<UpcomingQuiz> getUpcomingQuizzes(int courseId) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.QuizTestModule WHERE courseId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        ArrayList<UpcomingQuiz> upcomingQuizzes = new ArrayList<UpcomingQuiz>();
        Date date = new Date();

        while (rs.next()) {
           Date dueDate = rs.getDate("submissionDate");
           if (dueDate == null || dueDate.after(date)) {
               UpcomingQuiz upcomingQuiz = new UpcomingQuiz();
               upcomingQuiz.setTitle(rs.getString("title"));
               upcomingQuiz.setId(rs.getInt("id"));
               upcomingQuiz.setTeachersNote(rs.getString("teacherNotes"));
               if (dueDate != null) {
                   upcomingQuiz.setDate(dueDate.toString());
               } else {
                   upcomingQuiz.setDate("No due date");
               }
               upcomingQuizzes.add(upcomingQuiz);
           }
        }

        return upcomingQuizzes;
    }

    /**
     * Takes in a result set that contains information about a question.  Finds out what kind of question it is and calls
     * the appropriate function to create the question and returns it.
     *
     * @param rs is the ResultSet containing question information
     * @return Question is the question we created
     * @throws SQLException
     */
    public Question createQuestion(ResultSet rs) throws SQLException {
        Question question;
        int questionId;

        if ((questionId = rs.getInt("multipleChoiceModuleId")) != 0) {
            question = createMultipleChoiceQuestion(questionId, rs.getInt("id"));
        } else if ((questionId = rs.getInt("trueFalseModuleId")) != 0) {
            question = createTrueFalseQuestion(questionId);
        } else if ((questionId = rs.getInt("exactAnswerModuleId")) != 0) {
            question = createExactAnswerQuestion(questionId);
        } else if ((questionId = rs.getInt("oversightQuestionModule")) != 0) {
            question = createOversightQuestion(questionId);
        } else {
            question = createCodeQuestion(rs.getInt("codeQuestionModuleId"));
        }
        question.setId(rs.getInt("id"));

        return question;
    }

    /**
     * Takes in a result set that contains information about a question.  Finds out what kind of question it is and calls
     * the appropriate function to create the question. Associates a quiz to this question and returns the question object.
     *
     * @param rs     is the ResultSet containing question information
     * @param quizId is the quiz id to whom this question is associated.
     * @return Question is the question we created
     * @throws SQLException
     */
    public Question createQuizQuestion(ResultSet rs, int quizId) throws SQLException {
        Question question;
        int questionId;

        if ((questionId = rs.getInt("multipleChoiceModuleId")) != 0) {
            question = createMultipleChoiceQuestion(questionId, rs.getInt("id"));
        } else if ((questionId = rs.getInt("trueFalseModuleId")) != 0) {
            question = createTrueFalseQuestion(questionId);
        } else if ((questionId = rs.getInt("exactAnswerModuleId")) != 0) {
            question = createExactAnswerQuestion(questionId);
        } else if ((questionId = rs.getInt("oversightQuestionModule")) != 0) {
            question = createOversightQuestion(questionId);
        } else {
            question = createCodeQuestion(rs.getInt("codeQuestionModuleId"));
        }
        question.setId(rs.getInt("id"));
        addPointsandSubmissions(question, question.getId(), quizId);

        return question;
    }

    /**
     * Queries the DB for all the questions in a particular course. Creates an array list out of them and returns them.
     *
     * @param courseId is the course id for whome we are querying.
     * @return a list of questions for the course.
     * @throws SQLException
     */
    public ArrayList<Question> getQuestions(int courseId) throws SQLException {
        String sql = "SELECT * From mastermycourse.QuizTestModuleQuestions Where courseId=?";
        ArrayList<Question> questions = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            questions.add(createQuestion(rs));

        }

        return questions;
    }

    /**
     * Query the database for a particular question. Create a Question object for this queried question and return the Question object.
     *
     * @param questionId is the id of the question we are querying.
     * @return the Question object created.
     * @throws SQLException
     */
    public Question getQuestionById(int questionId) throws SQLException {
        String sql = "SELECT * From mastermycourse.QuizTestModuleQuestions Where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, questionId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        Question question = createQuestion(rs);
        return question;
    }

    /**
     * Queries the db for how many attempts were made on trying to answer a particular question in a quiz. Returns the number of attempts.
     *
     * @param quizId     is the quiz we are querying for.
     * @param questionId is the question we are querying for.
     * @return the number of attempts made on trying the answer the question we queried for.
     * @throws SQLException
     */
    public int getAttempts(int quizId, int questionId) throws SQLException {
        String sql = "SELECT totalSubmissions FROM mastermycourse.QuizTestOrder where quizTestModuleId=? AND quizTestModuleQuestionId=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ps.setInt(2, questionId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("totalSubmissions");
        }

        return -1;
    }

    /**
     * Adds the points and submissions fields to a question if application
     *
     * @param questionId the id of from the QuizTestOrder table to return
     * @throws SQLException
     */
    public void addPointsandSubmissions(Question question, int questionId, int quizId) throws SQLException {
        String sql = "SELECT * FROM mastermycourse.QuizTestOrder WHERE quizTestModuleId=? AND quizTestModuleQuestionId=? ORDER BY orderIndex";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ps.setInt(2, questionId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            question.setPoints(Integer.valueOf(rs.getString("points")));
            question.setSubmissions(Integer.valueOf(rs.getString("totalSubmissions")));
        }
    }

    /**
     * Query all the question in a particular quiz and return them as an array list of Question objects.
     *
     * @param quizId is the quiz id of the particular quiz we are querying for.
     * @return and array list of Question objects.
     * @throws SQLException
     */
    public ArrayList<Question> getQuizQuestions(int quizId) throws SQLException {
        // Will receive all the questions in this quiz in order
        String sql = "SELECT id, multipleChoiceModuleid, codeQuestionModuleId, trueFalseModuleId, exactAnswerModuleId, oversightQuestionModule FROM mastermycourse.QuizTestOrder A, mastermycourse.QuizTestModuleQuestions B " +
                "WHERE quizTestModuleId = ? AND B.id = A.quizTestModuleQuestionId ORDER BY orderIndex";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ResultSet rs = ps.executeQuery();

        ArrayList<Question> questions = new ArrayList<Question>();

        while (rs.next()) {
            // create question from question id
            Question question = createQuizQuestion(rs, quizId);
            if (rs.getInt("multipleChoiceModuleId") != 0) {
                scrambleMultipleChoiceQuestion(question);
            }

            questions.add(question);
        }

        return questions;
    }

    /**
     * Gets the quiz name for a particular quiz.
     *
     * @param quizId is the id of the quiz we are getting the quiz name of
     * @return the name of the quiz we queried for.
     * @throws SQLException
     */
    public String getQuizName(int quizId) throws SQLException {
        String sql = "SELECT title FROM mastermycourse.QuizTestModule where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quizId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("title");
    }

    /**
     * close connection to the db.
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
