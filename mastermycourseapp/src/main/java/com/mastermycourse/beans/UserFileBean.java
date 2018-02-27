package com.mastermycourse.beans;

import com.mastermycourse.pdf.PDF;
import com.mastermycourse.pojos.OutlineKey;
import com.mastermycourse.pojos.TableOfContentPair;
import com.mastermycourse.settings.DirectorySettings;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authors: James DeCarlo, Zach Lerman, and Rahul Verma
 *
 * Session Scoped bean to handle pdf file upload status and parsing for the teacher course creation page.
 * ApplicationContext, userId, directoryRoot and course need to be set for this bean to work properly.
 * Implements Serializable and ApplicationContextAware
 *
 */
public class UserFileBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;
    private File filePDF;
    private String userId;
    private String course;
    private int courseId;
    private String directoryRoot;
    private int pageNumber = 0;
    private Logger log = Logger.getLogger(UserFileBean.class.getName());


    /**
     * Creates a deep copy of the UserFileBean
     * @return UserFileBean deep copy.
     */
    public UserFileBean copy(){
        UserFileBean fileBean = new UserFileBean();
        fileBean.setApplicationContext(this.ctx);
        fileBean.setUserId(this.userId);
        fileBean.setCourse(this.course);
        fileBean.setDirectoryRoot(this.directoryRoot);
        fileBean.setPageNumber(this.pageNumber);
        return fileBean;
    }


    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDirectoryRoot(String directoryRoot){
        this.directoryRoot = directoryRoot;
    }

    public String getPDFFileUrl(){
        return DirectorySettings.teachersDirectoryRoot.substring(2) +  course + "/" + getPDFFileName();
    }

    public String convertPDFToBase64(){
        try {
            return DatatypeConverter.printBase64Binary(org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(getExistingPDFFile())));
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public void setFilePDF(File filePDF) {
        this.filePDF = filePDF;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /*
    public File getExistingPDFFile() {

        if(filePDF == null){
            AWSS3 awss3 = new AWSS3();
            try {
                filePDF = awss3.getCoursePdf(courseId);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(),e);
            } finally {
                awss3.closeConnection();
            }
        }
        return filePDF;
    }*/


    /**
    * This method returns an existing pdf file.
    *
    * @return File existing file.
    */
    public File getExistingPDFFile(){

        File file = new File(directoryRoot + "/" + DirectorySettings.teachersDirectoryRoot  + course);

        if (file.isDirectory()){
            for (File f: file.listFiles()){
                if(f.getName().endsWith(".pdf")){
                    return new File(file, f.getName());
                }
            }
        }
        return null;
    }

    /**
     * Returns the name of an existing pdf file.
     * @return String file name of existing file.
     * */

    public String getPDFFileName(){
        File file = getExistingPDFFile();
        if(file != null){
            return file.getName();
        }
        return null;
    }

    /**
     * Returns the text of a page given by an index.
     * @param index is the index of the page whose text we want.
     * @return String file name of existing file.
     * */

    public String getPDFPageText(int index) throws IOException {
        PDF pdf = new PDF(getExistingPDFFile());
        String text = pdf.ripPDFtoString(index +1, index + 1);
        pdf.close();
        return text;
    }

    /**
     * Returns the buffered image of the pdf request by index
     * @param index is the index of the page whose buffered image we are requesting
     * @return BufferedImage the image of the pdf page
     * */

    public BufferedImage getPDFPageImage(int index) throws IOException {
        PDF pdf = new PDF(getExistingPDFFile());
        BufferedImage image = pdf.ripPDFtoImage(index);
        pdf.close();
        return image;
    }

    /**
     * returns the page labels
     * @return String[] is an array of strings containing the page labels.
     * */

    public String[] getPDFPageLabels(){
        try {
            if(getExistingPDFFile() == null){
                return null;
            }
            PDF pdf = new PDF(getExistingPDFFile());
            String[] labels = pdf.getPageLabels();
            pdf.close();
            return labels;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } catch (NullPointerException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Finds whether the existing pdf has an outline or not
     * @return boolean true if the existing pdf has an outline , false if it does not have outline.
     * */


    public boolean getHasOutline(){
        try {
            PDF pdf = new PDF(getExistingPDFFile());
            boolean hasOutline = pdf.getHasOutline();
            pdf.close();
            return hasOutline;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the total pages in the existing pdf file.
     * @return int of total pages in the existing pdf file.
     * */


    public int getTotalPages() throws IOException {
        PDF pdf = new PDF(getExistingPDFFile());
        int totalPages = pdf.totalNumberOfPages();
        pdf.close();
        return totalPages;
    }

    /**
     * Returns the outline keys of the existing pdf file
     * @return List<OutlineKey> of outline keys in the existing pdf file.
     * */


    public List<OutlineKey> getOutlineKeys(){
        if(getExistingPDFFile() == null){
            return null;
        }

        try {
            PDF pdf = new PDF(getExistingPDFFile());
            List<OutlineKey> outlineKeys = pdf.getOutlineKeys();
            pdf.close();
            return outlineKeys;
        } catch (InvalidPasswordException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns the Table Of Content Pair pairs of the existing pdf file
     * @return List<TableOfContentPair> of table of content pairs in the existing pdf file.
     * */

    public List<TableOfContentPair> parseTableOfContents(int startPage, int endPage){
        //parse code here.
        if (getExistingPDFFile() == null)
            return null;

        try {
            PDF pdf = new PDF(getExistingPDFFile());
            List<TableOfContentPair> pairs = pdf.getTableOfContentsPairs(startPage, endPage);
            pdf.close();
            return pairs;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

}


