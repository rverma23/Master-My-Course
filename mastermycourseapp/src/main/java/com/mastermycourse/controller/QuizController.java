package com.mastermycourse.controller;

import org.json.*;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.mastermycourse.beans.*;
import com.mastermycourse.database.StudentDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.Question;
import com.mastermycourse.pojos.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Zach Lerman.
 *
 * This class contains methods for both creating, updating and deleting quizzes as a professor,
 * as well as taking quizzes as a student and having the answers graded.
 */
@Controller
public class QuizController {
    @Autowired
    ApplicationContext ctx;

    /**
     * Initializes a quizBean for a student to take a quiz.
     */
    @RequestMapping(value = "/TakeQuiz", method = RequestMethod.POST)
    public void takeQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        QuizBean quizBean = (QuizBean) request.getSession().getAttribute("quiz");
        int quizId = Integer.valueOf(request.getParameter("quizId"));
        quizBean.setIndex(0); // start at question 1
        quizBean.setAttempts(-1); // -1 indicates need to get attempts
        quizBean.setQuizId(quizId);
        quizBean.getQuestions();
        response.sendRedirect("/quiz.htm");
    }

    /**
     *  Updates a CreateQuizBean and sets its question_list to question_list - quiz_questions and
     *  sets the edit quiz flag to true
     */
    @RequestMapping(value = "/EditQuiz", method = RequestMethod.POST)
    public void editQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        CreateQuizBean createQuiz = (CreateQuizBean) request.getSession().getAttribute("createQuiz");
        String title = request.getParameter("title");
        int quizId = Integer.valueOf(request.getParameter("quizId"));
        createQuiz.setEditQuiz(true); // when redirected to the quizCreation page it will now edit the quiz
        createQuiz.setQuizName(title);
        createQuiz.setDate(request.getParameter("date"));
        createQuiz.setTeacherNotes(request.getParameter("teacherNotes"));
        createQuiz.setQuizId(quizId);

        ArrayList<Question> questions = createQuiz.getQuizQuestions(quizId);
        ArrayList<Question> remainingQuestions = new ArrayList<Question>();

        for (int i = 0; i < createQuiz.getQuestion_list().size(); i++) {
            Question question = createQuiz.getQuestion_list().get(i);
            boolean isIn = check(question, questions);
            if (!isIn) {
                remainingQuestions.add(createQuiz.getQuestion_list().get(i));
            }
        }

        createQuiz.setQuestion_list(remainingQuestions);
        createQuiz.setQuiz_questions(questions);

        response.sendRedirect("/quizCreation.htm");
    }

    public boolean check(Question question, ArrayList<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            if (question.getId() == questions.get(i).getId())
                return true;
        }

        return false;
    }

    /**
     * This method posts to the hacker rank servers to compile code and run it against the provided test cases
     * @param source The students source code they typed for the quiz
     * @param lang An integer representing the language, e.g. 3 is Java
     * @param testCases A set of test cases that are entered as STDIN.
     * @return A JSON string from HackerRanks API.
     * @throws IOException
     */
    public String postHackerRank(String source, int lang, String testCases) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://api.hackerrank.com/checker/submission.json");
        String api_key = "hackerrank|2510175-1370|fd10ea91a161108e4ec91092acabfad8aa3046a7";

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("source", source));
        params.add(new BasicNameValuePair("lang", String.valueOf(lang)));
        params.add(new BasicNameValuePair("testcases", testCases));
        params.add(new BasicNameValuePair("api_key", api_key));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            InputStream inputStream = entity.getContent();
            try {
                result = CharStreams.toString(new InputStreamReader(
                        inputStream, Charsets.UTF_8));
            } finally {
                inputStream.close();
            }
        }

        return result;
    }


    /**
     * Evaluates a students response to a Coding question, it sends these properties into the
     * checkAnswer method which handles the logic for grading and seeing if they have any
     * attempts left if they answered incorrectly.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/AnswerCodeQuestion", method = RequestMethod.POST)
    public void answerCodeQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, JSONException {
        QuizBean quizBean = (QuizBean) request.getSession().getAttribute("quiz");
        StudentDBCommands sdbc = new StudentDBCommands(ctx);

        String email = String.valueOf(request.getSession().getAttribute("userEmail"));
        User user = sdbc.getUserByEmail(email);

        // assume correct until find a mistake
        boolean isCorrect = true;

        int index = quizBean.getIndex();
        Question question = quizBean.getQuestion_list().get(index);
        int language = question.getLanguage();
        String[] test_cases = question.getPrompts();

        String json_array = "[\"" + String.join("\",\"", test_cases) + "\"]";
        String studentCode = request.getParameter("student_code");

        JSONObject hr_response = null;
        JSONArray stdout = null;

        try {
            hr_response = new JSONObject(postHackerRank(studentCode, language, json_array));
            hr_response = hr_response.getJSONObject("result");
            stdout = hr_response.getJSONArray("stdout");
        } catch(Exception e) {
            isCorrect = false; // their code didn't even compile
        }

        if (isCorrect && stdout.length() > 0) {
            String[] expected_output = question.getAnswer().split(",");
            for (int i = 0; i < stdout.length(); i++) {
                String current_stdout = stdout.getString(i).replace("\n", "");
                String current_expected_output = expected_output[i];
                if (!current_stdout.equals(current_expected_output)) {
                    isCorrect = false;
                    break;
                }
            }
        } else {
            isCorrect = false;
        }

        checkAnswer(isCorrect, quizBean, index, "", user);

        if (quizBean.getIndex() == quizBean.getQuestion_list().size()) {
            response.sendRedirect("/quizSuccess.htm");
        } else {
            response.sendRedirect("/quiz.htm");
        }
    }

    /**
     * This method handles questions for all question types except for code questions,
     * it gets the properties from the form and submits them to the checkAnswer method
     * which handles the remaining logic for answering a question
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/AnswerQuestion", method = RequestMethod.POST)
    public void submitQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        QuizBean quizBean = (QuizBean) request.getSession().getAttribute("quiz");

        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String email = String.valueOf(request.getSession().getAttribute("userEmail"));
        User user = sdbc.getUserByEmail(email);
        int index = quizBean.getIndex();

        String studentAnswer = request.getParameter("question" + index);

        checkAnswer(true, quizBean, index, studentAnswer, user);

        if (quizBean.getIndex() == quizBean.getQuestion_list().size()) {
            response.sendRedirect("/quizSuccess.htm");
        } else {
            response.sendRedirect("/quiz.htm");
        }
    }

    /**
     * This method checks a students response to a question, if they get it write, it submits
     * the correct answer, if they get it wrong it decrements the number of attempts they have left
     * if they run out of attempts, the incorrect submission is entered, else it returns to the question
     * giving them another chance.
     * @param isCorrect whether the student got the answer correct
     * @param quizBean  the quizBean object
     * @param index the index of the question they are on in the list of questions
     * @param studentAnswer the answer the student submitted
     * @param user  the user object for getting their id
     * @throws SQLException
     */
    protected void checkAnswer(boolean isCorrect, QuizBean quizBean, int index, String studentAnswer, User user) throws SQLException {
        Question question = quizBean.getQuestion_list().get(index);
        String questionType = question.getQuestionType();
        StudentDBCommands sdbc = new StudentDBCommands(ctx);

        boolean isGraded = false;

        if (questionType.equals("oversight")) {
            // oversight questions always get added immediately
            isCorrect = false;
            isGraded = false;
            quizBean.setAttempts(-1); // reset attempts for next question
            quizBean.setIndex(quizBean.getIndex() + 1); // move on to next question
            sdbc.addStudentAnswer(user.getId(), quizBean.getQuizId(), question.getId(), studentAnswer, isCorrect, isGraded);
        }
        else {
            isGraded = true;
            if (!questionType.equals("code")) {
                isCorrect = studentAnswer.equals(question.getAnswer());
            }
            if (!isCorrect) {
                // they got it wrong, if they have more attempts return, else submit it as wrong
                quizBean.setAttempts(quizBean.getAttempts() - 1);
                if (quizBean.getAttempts() == 0) {
                    // they are out of attempts, submit their wrong answer
                    quizBean.setAttempts(-1); // reset attempts for next question
                    quizBean.setIndex(quizBean.getIndex() + 1); // move on to next question
                    sdbc.addStudentAnswer(user.getId(), quizBean.getQuizId(), question.getId(), studentAnswer, isCorrect, isGraded); // submit answer
                }
            } else {
                // they are correct, move on immediately
                quizBean.setAttempts(-1); // reset attempts for next question
                quizBean.setIndex(quizBean.getIndex() + 1); // move on to next question
                sdbc.addStudentAnswer(user.getId(), quizBean.getQuizId(), question.getId(), studentAnswer, isCorrect, isGraded); // submit correct answer
            }
        }
    }

    /**
     * This submits all questions a student answered in a quiz
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/SubmitQuiz", method = RequestMethod.POST)
    public void submitQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        QuizBean quizBean = (QuizBean) request.getSession().getAttribute("quiz");

        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String email = String.valueOf(request.getSession().getAttribute("userEmail"));
        User user = sdbc.getUserByEmail(email);

        ArrayList<Question> questions = quizBean.getQuestions();
        for (int i = 0; i < quizBean.getNumQuestions(); i++) {
            Question currentQuestion = questions.get(i);
            String questionType = currentQuestion.getQuestionType();
            String studentAnswer = request.getParameter("question" + i);

            boolean isCorrect;
            boolean isGraded;
            if (questionType.equals("oversight")) {
                isCorrect = false;
                isGraded = false;
            } else {
                isCorrect = studentAnswer.equals(currentQuestion.getAnswer());
                isGraded = true;
            }

            sdbc.addStudentAnswer(user.getId(), quizBean.getQuizId(), currentQuestion.getId(), studentAnswer, isCorrect, isGraded);
        }

        response.sendRedirect("/quizSuccess.htm");
    }

    /**
     * This Method redirects the user to the quizCreation.htm page after they click the create quiz button.
     * It also initializes the CreateQuizBean with the courseId and all the quizzes and questions in this course.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/GoToQuizCreation", method = RequestMethod.POST)
    public void goToQuizCreation(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        CreateQuizBean createQuiz = (CreateQuizBean)request.getSession().getAttribute("createQuiz");
        CourseBean courseBean = (CourseBean)request.getSession().getAttribute("course");
        createQuiz.setCourseId(courseBean.getCourseId());
        createQuiz.initQuestions();
        createQuiz.initQuizzes();
        createQuiz.setEditQuiz(false);
        response.sendRedirect("/quizCreation.htm");
    }

    /**
     * Creates a quiz with all the questions - and their point values and allowed attempts - to
     * the list of quizzes in this course.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/CreateQuiz", method = RequestMethod.POST)
    public void createQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, JSONException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        CourseBean courseBean = (CourseBean) request.getSession().getAttribute("course");
        int courseId = courseBean.getCourseId();

        String question_data = request.getParameter("quiz_questions_hidden");
        JSONArray questionArray = new JSONArray(question_data);

        String title = request.getParameter("quiz_name");

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        String dueDate = request.getParameter("dueDate");
        Date date;
        try {
            date = dateFormat.parse(dueDate);
        } catch (ParseException e) {
            date = null;
        }

        String teacherNotes = request.getParameter("teacherNotes");
        tdbc.createQuiz(courseId, title, date, questionArray, teacherNotes);

        CreateQuizBean createQuiz = (CreateQuizBean)request.getSession().getAttribute("createQuiz");
        createQuiz.initQuestions();
        createQuiz.initQuizzes();
        createQuiz.setEditQuiz(false);
        response.sendRedirect("/quizCreation.htm");
    }

    /**
     * This method deletes a quiz by removing it from a courses quiz list and from the chapter quiz modules
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/DeleteQuiz", method = RequestMethod.POST)
    public void deleteQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        CourseBean courseBean = (CourseBean) request.getSession().getAttribute("course");

        int quizTestModuleId = Integer.valueOf(request.getParameter("edit_quiz_id"));
        int courseId = courseBean.getCourseId();

        tdbc.deleteQuiz(courseId, quizTestModuleId);

        CreateQuizBean createQuiz = (CreateQuizBean)request.getSession().getAttribute("createQuiz");
        createQuiz.initQuestions();
        createQuiz.initQuizzes();
        createQuiz.setEditQuiz(false);
        response.sendRedirect("/quizCreation.htm");
    }

    /**
     * Updates a quiz/questions in the quiz based on the form input given by the professor
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/UpdateQuiz", method = RequestMethod.POST)
    public void updateQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, JSONException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        int quizTestModuleId = Integer.valueOf(request.getParameter("edit_quiz_id"));

        String question_data = request.getParameter("edit_quiz_questions_hidden");
        JSONArray questionArray = new JSONArray(question_data);

        String title = request.getParameter("quiz_name");
        String teacherNotes = request.getParameter("teacherNotes");

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        String dueDate = request.getParameter("dueDate");
        Date date;
        try {
            date = dateFormat.parse(dueDate);
        } catch (ParseException e) {
            try {
                date = dateFormat2.parse(dueDate);
            } catch(ParseException e2) {
                date = null;
            }
        }

        tdbc.updateQuiz(quizTestModuleId, title, date, questionArray, teacherNotes);

        CreateQuizBean createQuiz = (CreateQuizBean)request.getSession().getAttribute("createQuiz");
        createQuiz.initQuestions();
        createQuiz.initQuizzes();
        createQuiz.setEditQuiz(false);
        response.sendRedirect("/quizCreation.htm");
    }

    /**
     * This initializes the studentAnswerBean and redirects the teacher/TA to the gradeQuiz page
     * so they may grade that students quiz.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/GradeQuiz", method = RequestMethod.POST)
    public void gradeQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentAnswerBean studentAnswerBean = (StudentAnswerBean) request.getSession().getAttribute("studentAnswers");
        int quizId = Integer.valueOf(request.getParameter("quizId"));
        int studentId = Integer.valueOf(request.getParameter("studentId"));
        studentAnswerBean.setStudentId(studentId);
        studentAnswerBean.setQuizId(quizId);
        response.sendRedirect("/gradeQuiz.htm");
        return;
    }

    /**
     * This method occurs when the user clicks the cancel button on the quizCreation page, it resets the createQuiz
     * Bean and redirects them back to the page
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/CancelCreateQuiz", method = RequestMethod.POST)
    public void cancelCreateQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        CreateQuizBean createQuiz = (CreateQuizBean)request.getSession().getAttribute("createQuiz");
        createQuiz.initQuestions();
        createQuiz.initQuizzes();
        createQuiz.setEditQuiz(false);
        response.sendRedirect("/quizCreation.htm");
    }
}
