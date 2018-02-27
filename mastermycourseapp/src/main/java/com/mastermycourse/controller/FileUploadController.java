package com.mastermycourse.controller;

import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.beans.UserFileBean;
import com.mastermycourse.s3.AWSS3;
import com.mastermycourse.settings.DirectorySettings;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authors: James DeCarlo, Zach Lerman.
 * This class provides methods that manage and control file uploading.
 * In specific this class provides method that allow PDFs to be uploaded, deleted and retrieved.
 */

@Controller
@MultipartConfig(location="/", fileSizeThreshold=1024*1024,
        maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class FileUploadController {

    Logger log = Logger.getLogger(FileUploadController.class.getName());

    /**
     * Handles the uploading of the pdf file that a course wants to use an redirects to the course creation page.
     * @param file
     * @param redirectAttributes is used to redirect flash attributes
     * @param request contains the session course
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value = "/PDFUpload", method = RequestMethod.POST)
    public String coursePDFFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) throws SQLException, IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            redirectAttributes.addFlashAttribute("isSuccess", false);
            return "redirect:/courseCreation.htm";
        }
        int courseId = ((CourseBean)request.getSession().getAttribute("course")).getCourseId();
        AWSS3 awss3 = new AWSS3();
        awss3.uploadCoursePdf(courseId, file.getInputStream(), file.getOriginalFilename(), file.getSize());
        awss3.closeConnection();
        return "redirect:/courseCreation.htm";
    }

    /**
     *
     * @param file
     * @param redirectAttributes
     * @param request
     * @return
     * @throws SQLException
     */

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) throws SQLException {
        String uploadDir = DirectorySettings.teachersDirectoryRoot;
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            redirectAttributes.addFlashAttribute("isSuccess", false);
            return "redirect:/courseCreation.htm";
        }

        try {
            int courseId = ((CourseBean)request.getSession().getAttribute("course")).getCourseId();
            uploadDir += (courseId + "/");

            File f = new File(request.getSession().getServletContext().getRealPath("/"), uploadDir);
            if(f.isDirectory()){
                redirectAttributes.addFlashAttribute("message", "Directory Exists");
            } else {
                redirectAttributes.addFlashAttribute("message", uploadDir);
                if(f.mkdirs()){
                    //redirectAttributes.addFlashAttribute("message", "Directory Created");
                } else {
                    redirectAttributes.addFlashAttribute("message", "Directory Creation Failed " + f.getAbsolutePath());
                }
            }

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(f.getAbsolutePath() + "/" + file.getOriginalFilename());

            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("isSuccess", true);
            //redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return "redirect:/courseCreation.htm";
    }

    /**
     * This method handles deleting a selected pdf file by the user and redirects to the course creation page.
     * @param request contains our session UserFileBean object. This object contains the pdf file we need to delete.
     * @return String which contains a redirect to the course creation page.
     */
    @RequestMapping(value = "/deletePdfFile")
    public String deletePdfFile(HttpServletRequest request){
        UserFileBean userFileBean = (UserFileBean)request.getSession().getAttribute("file");

        if(userFileBean != null && userFileBean.getExistingPDFFile() != null){
            userFileBean.getExistingPDFFile().delete();
        }
        return "redirect:/courseCreation.htm";
    }

    /**
     * This method is used to retrieve a pdf for a particular course.
     * @param request contains the session UserFileBean which has the pdf file associated with the course in the session.
     * @return ResponseEntity<byte[]> which contains the pdf that we want to retrieve.
     * @throws IOException
     */
    @RequestMapping(value="/getCoursePdf", method=RequestMethod.GET)
    public ResponseEntity<byte[]> getPDF1(HttpServletRequest request) throws IOException {
        File file = ((UserFileBean)request.getSession().getAttribute("file")).getExistingPDFFile();
        byte[] pdfBytes =  IOUtils.toByteArray(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = file.getName();

        headers.add("content-disposition", "inline;filename=" + filename);

        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        return response;
    }
}
