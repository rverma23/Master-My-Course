package com.mastermycourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.beans.ProgressBean;
import com.mastermycourse.beans.UserFileBean;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.OutlineKey;
import com.mastermycourse.pojos.TableOfContentPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Authors: James DeCarlo, Zach Lerman and Rahul Verma.
 *
 * This class contains the controls of parsing a pdf. It contains controls on parsing a pdf with an outline and
 * parsing a pdf without an outline.
 */
@Controller
public class PDFParserController {

    @Autowired
    ApplicationContext ctx;

    /**
     * Parses a pdf without outline into the database.  Afterwards, redirects the user to the courseCreation page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/ParsePDFRegX", method = RequestMethod.POST)
    public void parsePDFWithoutOutlineIntoDB(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UserFileBean fileBean = (UserFileBean) request.getSession().getAttribute("file");
        CourseBean courseBean = (CourseBean) request.getSession().getAttribute("course");
        String userId = request.getSession().getAttribute("userId").toString();
        int fileno = Integer.parseInt(request.getParameter("startPage"));
        int endfileno = Integer.parseInt(request.getParameter("endPage"));
        ProgressBean progressBean = new ProgressBean();
        progressBean.setMessage("Initializing");
        progressBean.setCourseId(courseBean.getCourseId());
        progressBean.setPercentage(0);
        request.getServletContext().setAttribute("progress" + userId, progressBean);

        ParsePDFNoOutlineRunnable runnable = new ParsePDFNoOutlineRunnable(fileBean.copy(), courseBean.getCourseId(), courseBean.getCourseName(), ctx, progressBean, fileno, endfileno);
        Thread thread = new Thread(runnable);
        progressBean.setThread(thread);
        thread.start();

        response.sendRedirect("/courseCreation.htm  ");
    }

    /**
     * Parses a pdf with outline into the database.  Afterwards, redirects the user to the courseCreation page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */

    @RequestMapping(value = "/ParsePDFOutline", method = RequestMethod.GET)
    public void parsePDFWithOutlineIntoDB(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UserFileBean fileBean = (UserFileBean)request.getSession().getAttribute("file");
        CourseBean courseBean = (CourseBean)request.getSession().getAttribute("course");
        String userId = request.getSession().getAttribute("userId").toString();

        if(fileBean == null || courseBean == null){
            throw new IllegalArgumentException("All Data Needed Beans Are Null");
        }
        if(courseBean.getCourseId() < 0){
            throw new IllegalArgumentException("Course Must Be initialized to current course");
        }
        if(!fileBean.getHasOutline()){
            throw new IllegalArgumentException("Pdf does not have an outline");
        }

        ProgressBean progressBean = new ProgressBean();
        progressBean.setMessage("Initializing");
        progressBean.setCourseId(courseBean.getCourseId());
        progressBean.setPercentage(0);
        request.getServletContext().setAttribute("progress" + userId, progressBean);


        ParsePDFOutlineRunnable runnable = new ParsePDFOutlineRunnable(fileBean.copy(), courseBean.getCourseId(), courseBean.getCourseName(), ctx, progressBean);
        Thread thread = new Thread(runnable);
        progressBean.setThread(thread);
        thread.start();

        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("status", "started");
        response.getWriter().write(node.toString());
    }

    /**
     * Gets the progress of the parsing that is being done.
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/Progress")
    public void getProgress(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = request.getSession().getAttribute("userId").toString();
        ProgressBean progressBean = (ProgressBean)request.getServletContext().getAttribute("progress" + userId);
        String message = "Initializing";
        float percentage = 0;
        if(progressBean != null){
            message = progressBean.getMessage();
            percentage = progressBean.getPercentage();
            if(!progressBean.getThread().isAlive()){
                request.getServletContext().removeAttribute("progress" + userId);
                percentage = 100;
            }
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("message", message);
        node.put("percent", percentage);
        out.write(node.toString());
    }

    /**
     * This class deals with parsing a pdf with an outline. It implements runnable.
     */
    private class ParsePDFOutlineRunnable implements Runnable{
        private UserFileBean fileBean;
        private ApplicationContext ctx;
        private ProgressBean progressBean;
        private int courseId;
        private String courseName;

        public ParsePDFOutlineRunnable(UserFileBean fileBean, int courseId, String courseName,ApplicationContext ctx, ProgressBean progressBean) {
            this.fileBean = fileBean;
            this.courseId = courseId;
            this.courseName = courseName;
            this.ctx = ctx;
            this.progressBean = progressBean;
        }

        @Override
        public void run() {
            List<OutlineKey> outlineKeys = fileBean.getOutlineKeys();
            String[] pageLabels = fileBean.getPDFPageLabels();
            String chapterTitle = "Cover";
            int outlineIndex = 0;
            OutlineKey outlineKey = outlineKeys.get(outlineIndex);

            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);

            // Remove old course content modules before inserting new ones
            try {
                tdbc.deleteCourseContentModules(courseId);
            } catch (SQLException e) {
                progressBean.setMessage("Error deleting old content");
                return;
            }

            try {
                int totalPages = fileBean.getTotalPages();
                int failCount = 0;
                int failSafe = (int)(1.2 * totalPages);
                int failSafeCount = 0;
                // Insert Page by page
                for(int i = 0; i < totalPages; i++){
                    failSafeCount++;
                    if(failSafeCount > failSafe){
                        throw new Exception("Fail safe timeout Please Try again");
                    }
                    progressBean.setMessage(courseName + " - Converting page " + (i + 1) + " of " + totalPages);
                    double percentage = (double)(i + 1)/totalPages * 100;
                    progressBean.setPercentage((int)percentage);
                    if(i == outlineKey.getPageIndex()){
                        chapterTitle = outlineKey.getChapter();
                        outlineIndex++;

                        if(outlineIndex < outlineKeys.size()){
                            outlineKey = outlineKeys.get(outlineIndex);
                        }
                    }

                    // Try reconnecting
                    try{
                        tdbc.addContentModule(courseId, chapterTitle, pageLabels[i], "", fileBean.getPDFPageImage(i), fileBean.getPDFPageText(i));
                        failCount = 0;
                    } catch (SQLException ex){
                        failCount++;
                        if(failCount > 20){
                            throw new SQLException("Sql connection failed multiple times");
                        }else{
                            progressBean.setMessage(courseName + " - Converting page " + (i + 1) + " of " + totalPages + " fail count " + failCount);
                            tdbc.closeConnection();
                            tdbc = new TeacherDBCommands(ctx);
                            i--;
                            continue;
                        }

                    }

                }
                progressBean.setMessage("Completed");
            } catch (SQLException ex){
                progressBean.setMessage(ex.getMessage());
            } catch (IOException ex2){
                progressBean.setMessage("Error IO Exception");
            } catch (Exception ex3){
                progressBean.setMessage(ex3.getMessage());
            } finally {
                tdbc.closeConnection();
            }

        }
    }

    /**
     * This class deals with parsing a pdf with no outline. This class implements runnable.
     */
    private class ParsePDFNoOutlineRunnable implements Runnable{
        private UserFileBean fileBean;
        private ApplicationContext ctx;
        private ProgressBean progressBean;
        private int startFileNo;
        private int endFileNo;
        private int courseId;
        private String courseName;

        public ParsePDFNoOutlineRunnable(UserFileBean fileBean, int courseId, String courseName, ApplicationContext ctx, ProgressBean progressBean, int startFileNo, int endFileNo) {
            this.fileBean = fileBean;
            this.courseId = courseId;
            this.courseName = courseName;
            this.ctx = ctx;
            this.progressBean = progressBean;
            this.startFileNo = startFileNo;
            this.endFileNo = endFileNo;
        }

        /**
         * This is the run method that run the parsing of the pdf without the outline.
         *
         */
        @Override
        public void run() {
            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
            int failCount = 0;
            try{
                // Remove old course content modules before inserting new ones
                tdbc.deleteCourseContentModules(courseId);

                List<TableOfContentPair> pairs = fileBean.parseTableOfContents(startFileNo,endFileNo);

                String chapterTitle = "Cover";
                int outlineIndex = 0;
                TableOfContentPair tableOfContentPair = pairs.get(outlineIndex);

                // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
                // Insert Page by page
                int totalPages = fileBean.getTotalPages();
                int failSafe = (int)(1.2 * totalPages);
                int failSafeCount = 0;
                for(int i = 0; i < totalPages; i++){
                    failSafeCount++;
                    if(failSafeCount > failSafe){
                        throw new Exception("Fail safe timeout Please Try again");
                    }


                    progressBean.setMessage(courseName + " - Converting page " + (i + 1) + " of " + totalPages);
                    double percentage = (double)(i + 1)/totalPages * 100;
                    progressBean.setPercentage((int)percentage);
                    if(i == tableOfContentPair.getPageNo()){

                        chapterTitle = tableOfContentPair.getName();
                        outlineIndex++;

                        if(outlineIndex < pairs.size()){
                            tableOfContentPair = pairs.get(outlineIndex);
                        }
                    }

                    try{
                        tdbc.addContentModule(courseId, chapterTitle, "Page " + (i+1), "", fileBean.getPDFPageImage(i), fileBean.getPDFPageText(i));
                        failCount = 0;
                    } catch (SQLException ex){
                        failCount++;
                        if(failCount > 20){
                            throw new SQLException("Sql connection failed multiple times");
                        }else{
                            tdbc.closeConnection();
                            tdbc = new TeacherDBCommands(ctx);
                            progressBean.setMessage(courseName + " - Converting page " + (i + 1) + " of " + totalPages + " Fail Count " + failCount);
                            i--;
                            continue;
                        }

                    }
                }
                progressBean.setMessage("Completed");
            }catch (SQLException ex){
                progressBean.setMessage(ex.getMessage());
            } catch (IOException ex2){
                progressBean.setMessage("Error IO Exception");
            } catch (Exception ex3){
                progressBean.setMessage(ex3.getMessage());
            } finally {
                tdbc.closeConnection();
            }

        }
    }
}


