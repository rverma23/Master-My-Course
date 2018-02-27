package com.mastermycourse.controller;

import com.mastermycourse.beans.StudentCourseBean;
import com.mastermycourse.database.StudentDBCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Author: James DeCarlo.
 *
 * This class contains the controls for the audio.
 * More specifically this class has all the text-to-speech controls.
 */

@Controller
public class AudioController {

    @Autowired
    ApplicationContext ctx;

    @RequestMapping(value = "/TextToSpeech")
    public void textToSpeech(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StudentCourseBean courseBean = (StudentCourseBean)request.getSession().getAttribute("course");

        ServletOutputStream stream = null;
        try {
            stream = response.getOutputStream();
            //set response headers
            response.setContentType("audio/mpeg");

            response.addHeader("Content-Disposition", "attachment; filename=Voice");

            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            byte[] audioBytes = sdbc.getAudio(courseBean.getContentModuleId());

            //read from the file; write to the ServletOutputStream
            for(int i = 0; i < audioBytes.length; i++){
                stream.write(audioBytes[i]);
            }
        } catch (IOException ioe) {
            throw new ServletException(ioe.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stream != null){
                stream.close();
            }
        }
    }
}
