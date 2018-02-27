package com.mastermycourse.controller;

import com.mastermycourse.ai.Polly;
import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.beans.ProgressBean;
import com.mastermycourse.database.TeacherDBCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Authors: James DeCarlo.
 *
 * This class handles parsing pdf from text to speech.
 */
@Controller
public class TextToSpeechParserController {

    @Autowired
    ApplicationContext ctx;

    /**
     * This method handles parsing the pdf to speed and redirects to the course creation page
     * @param request
     * @param response
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value = "/ParseTextToSpeech")
    public void parseTextToSpeech(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int courseId = ((CourseBean)request.getSession().getAttribute("course")).getCourseId();
        String courseName = ((CourseBean)request.getSession().getAttribute("course")).getCourseName();
        String userId = (String)request.getSession().getAttribute("userId");
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);

        List<Integer> contentModuleIds = tdbc.getCourseContentModuleIds(courseId);

        ProgressBean progressBean = new ProgressBean();
        progressBean.setMessage("Initializing");
        progressBean.setCourseId(courseId);
        progressBean.setPercentage(0);
        request.getServletContext().setAttribute("progress" + userId, progressBean);

        Runnable runnable = new TextToSpeechRunnable(contentModuleIds, tdbc, progressBean, courseName);
        Thread thread = new Thread(runnable);
        progressBean.setThread(thread);
        thread.start();

        response.sendRedirect("courseCreation.htm");
    }

    private class TextToSpeechRunnable implements Runnable{
        List<Integer> contentModuleIds;
        TeacherDBCommands tdbc;
        ProgressBean progressBean;
        String courseName;

        public TextToSpeechRunnable(List<Integer> contentModuleIds, TeacherDBCommands tdbc, ProgressBean progressBean, String courseName) {
            this.contentModuleIds = contentModuleIds;
            this.tdbc = tdbc;
            this.progressBean = progressBean;
            this.courseName = courseName;
        }

        @Override
        public void run() {
            Polly polly = new Polly();
            int i = 0;
            int totalPages = contentModuleIds.size();
            try {
                for(; i < contentModuleIds.size(); i++){
                    int contentModuleId = contentModuleIds.get(i);
                    progressBean.setMessage(courseName + " - Converting to Audio Page " + (i + 1) + " of " + totalPages);
                    double percentage = (double)(i + 1)/totalPages * 100;
                    progressBean.setPercentage((int)percentage);

                    String text = tdbc.getContentRawText(contentModuleId);
                    if(text.length() > 3000){
                        text = text.substring(0, 1500);
                    }
                    InputStream inputStream = polly.getTextToSpeech(text);
                    tdbc.insertPageAudio(inputStream, contentModuleId);
                }
                progressBean.setMessage("Completed Converting Book To Audio");
            } catch (SQLException ex){
                progressBean.setMessage("Page " + (i+1) + "Failed being retrieved from database " + ex.getMessage());
            } catch (IOException e) {
                progressBean.setMessage("Page " + (i+1) + "Failed being inserted to database " + e.getMessage());
            } catch (Exception e){
                progressBean.setMessage("Page " + (i+1) + "Failed Exception " + e.getMessage());
            } finally{
                polly.close();
                tdbc.closeConnection();
            }
        }
    }
}

