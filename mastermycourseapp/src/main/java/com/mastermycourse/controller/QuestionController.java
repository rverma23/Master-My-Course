package com.mastermycourse.controller;

import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.beans.CreateQuizBean;
import com.mastermycourse.database.TeacherDBCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Author: Zach Lerman.
 *
 * This class contains methods for creating, updating and deleting Questions. These Questions
 * can later be added to quizzes that can be assigned to students.
 */
@Controller
public class QuestionController {
    @Autowired
    ApplicationContext ctx;

    /**
     * This method gets form parameters and adds a new type of question for all questions except code questions
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/SubmitQuestion", method = RequestMethod.POST)
    public void submitQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        CourseBean courseBean = (CourseBean) request.getSession().getAttribute("course");
        int courseId = courseBean.getCourseId();

        String questionType = request.getParameter("hidden_question_type");
        String questionPrompt = request.getParameter("questionPrompt");

        if (questionType.equals("multipleChoice")) {
            String correct_answer = request.getParameter("mult_radio");
            String wrong_answer_1;
            String wrong_answer_2;
            String wrong_answer_3;

            if (correct_answer.equals("a")) {
                correct_answer = request.getParameter("q1_prompt");
                wrong_answer_1 = request.getParameter("q2_prompt");
                wrong_answer_2 = request.getParameter("q3_prompt");
                wrong_answer_3 = request.getParameter("q4_prompt");
            } else if (correct_answer.equals("b")) {
                correct_answer = request.getParameter("q2_prompt");
                wrong_answer_1 = request.getParameter("q1_prompt");
                wrong_answer_2 = request.getParameter("q3_prompt");
                wrong_answer_3 = request.getParameter("q4_prompt");
            } else if (correct_answer.equals("c")) {
                correct_answer = request.getParameter("q3_prompt");
                wrong_answer_1 = request.getParameter("q1_prompt");
                wrong_answer_2 = request.getParameter("q2_prompt");
                wrong_answer_3 = request.getParameter("q4_prompt");
            } else {
                correct_answer = request.getParameter("q4_prompt");
                wrong_answer_1 = request.getParameter("q1_prompt");
                wrong_answer_2 = request.getParameter("q2_prompt");
                wrong_answer_3 = request.getParameter("q3_prompt");
            }

            tdbc.addMultipleChoiceQuestion(courseId, questionPrompt, correct_answer, wrong_answer_1, wrong_answer_2, wrong_answer_3);
        } else if (questionType.equals("trueFalse")) {
            String correct_answer = request.getParameter("tf_radio");
            tdbc.addTrueFalseQuestion(courseId, questionPrompt, correct_answer);
        } else if (questionType.equals("exactAnswer")) {
            String correct_answer = request.getParameter("a_exact");
            tdbc.addExactAnswerQuestion(courseId, questionPrompt, correct_answer);
        } else {
            tdbc.addOversightQuestion(courseId, questionPrompt);
        }

        update_questions((CreateQuizBean)request.getSession().getAttribute("createQuiz"));
        response.sendRedirect("/quizCreation.htm");
    }


    /**
     * Add a code question, due to the complexities of code question creation it receives a separate method.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/CreateCodeQuestion", method = RequestMethod.POST)
    public void addCodeQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        CourseBean courseBean = (CourseBean) request.getSession().getAttribute("course");
        int courseId = courseBean.getCourseId();

        // 1) Get Question Prompt
        String question = request.getParameter("question");

        // 2) Get the language value
        int language = Integer.valueOf(request.getParameter("language"));

        // 3) Get the template source code
        String template_code = request.getParameter("template_code");

        // 4) Get the stdin/stdout
        String stdin_values = request.getParameter("stdin");
        String stdout_values = request.getParameter("stdout");

        tdbc.addCodeQuestion(courseId, question, stdin_values, language, template_code, stdout_values);

        update_questions((CreateQuizBean)request.getSession().getAttribute("createQuiz"));
        response.sendRedirect("/quizCreation.htm");
    }


    /**
     * Deletes a single question given the question Id
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/DeleteQuestion", method = RequestMethod.POST)
    public void deleteQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        String questionType = request.getParameter("question_type");
        CourseBean courseBean = (CourseBean) request.getSession().getAttribute("course");
        int questionId = Integer.parseInt(request.getParameter("question_id"));
        String column = tdbc.findColumn(questionType);
        tdbc.deleteQuestion(column, courseBean.getCourseId(), questionId);

        update_questions((CreateQuizBean)request.getSession().getAttribute("createQuiz"));
        response.sendRedirect("/quizCreation.htm");
    }

    /**
     * Updates a question's fields given by the form inputs sent by a teacher
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/UpdateQuestion", method = RequestMethod.POST)
    public void updateQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);

        String questionType = request.getParameter("question_type");
        int questionId = Integer.parseInt(request.getParameter("question_id"));

        String questionPrompt = request.getParameter("questionPrompt");

        if (questionType.equals("multipleChoice")) {
            String correct_answer = request.getParameter("mult_radio");
            String wrong_answer_1;
            String wrong_answer_2;
            String wrong_answer_3;

            if (correct_answer.equals("a")) {
                correct_answer = request.getParameter("q1_prompt");
                wrong_answer_1 = request.getParameter("q2_prompt");
                wrong_answer_2 = request.getParameter("q3_prompt");
                wrong_answer_3 = request.getParameter("q4_prompt");
            } else if (correct_answer.equals("b")) {
                correct_answer = request.getParameter("q2_prompt");
                wrong_answer_1 = request.getParameter("q1_prompt");
                wrong_answer_2 = request.getParameter("q3_prompt");
                wrong_answer_3 = request.getParameter("q4_prompt");
            } else if (correct_answer.equals("c")) {
                correct_answer = request.getParameter("q3_prompt");
                wrong_answer_1 = request.getParameter("q1_prompt");
                wrong_answer_2 = request.getParameter("q2_prompt");
                wrong_answer_3 = request.getParameter("q4_prompt");
            } else {
                correct_answer = request.getParameter("q4_prompt");
                wrong_answer_1 = request.getParameter("q1_prompt");
                wrong_answer_2 = request.getParameter("q2_prompt");
                wrong_answer_3 = request.getParameter("q3_prompt");
            }

            tdbc.updateMultipleChoiceQuestion(questionId, questionPrompt, correct_answer, wrong_answer_1, wrong_answer_2, wrong_answer_3);
        } else if (questionType.equals("trueFalse")) {
            String correct_answer = request.getParameter("tf_radio");
            tdbc.updateTrueFalseQuestion(questionId, questionPrompt, correct_answer);
        } else if (questionType.equals("exactAnswer")) {
            String correct_answer = request.getParameter("a_exact");
            tdbc.updateExactAnswerQuestion(questionId, questionPrompt, correct_answer);
        } else if (questionType.equals("oversight")) {
            tdbc.updateOversightQuestion(questionId, questionPrompt);
        } else {
            String testCases = request.getParameter("stdin");
            String expectedOutput = request.getParameter("stdout");
            int lang = Integer.valueOf(request.getParameter("language"));
            String template = request.getParameter("code_template");
            tdbc.updateCodeQuestion(questionId, questionPrompt, testCases, expectedOutput, template, lang);
        }

        update_questions((CreateQuizBean)request.getSession().getAttribute("createQuiz"));
        response.sendRedirect("/quizCreation.htm");
    }

    /**
     * Initializes a createQuiz bean's question list. This list is the questions that are available
     * to a teacher in creating a quiz.
     * @param createQuiz The createQuiz bean is the main bean used in quizCreation.jsp
     * @throws SQLException
     */
    private void update_questions(CreateQuizBean createQuiz) throws SQLException {
        createQuiz.initQuestions();
    }

    /**
     * Message Board functions, move to MessageBoardController, wasn't working for some reason there.
     */

    /**
     * Add new post to course message board list
     */
    @RequestMapping(value = "/CreateNewPost", method = RequestMethod.POST)
    public void createNewPost(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);

        int courseId = Integer.valueOf(request.getParameter("courseIdHidden"));
        String message = request.getParameter("editor");
        String summary = request.getParameter("summary");

        tdbc.createNewPost(message, summary, courseId);
        response.sendRedirect("/messageBoard.htm");
    }
}