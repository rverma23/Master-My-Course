package com.mastermycourse.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Authors: James DeCarlo, Zach Lerman.
 *
 * This class is used to send out approval emails to students. Also the class is used to send e-mail notifications
 * about deletion of accounts and courses.
 *
 */
public class Email {

    /**
     * This method sends a welcome email to a student.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     * @param name the name of the student.
     */
    public static void sendStudentWelcomeEmail(final JavaMailSenderImpl mailSender, String email, String name){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("Welcome to MasterMyCourse.com");
        ms.setText("Hello " + name + ",\n" + "Welcome to our site we hope you enjoy our site!\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method sends a welcome email to a teacher.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     * @param name the name of the teacher.
     */
    public static void sendTeacherWelcomeEmail(final JavaMailSenderImpl mailSender, String email, String name){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("Welcome to MasterMyCourse.com");
        ms.setText("Hello " + name + ",\n" + "Welcome to our site our site! Your Teacher account is pending approval " +
                "you will be notified when you your account has been approved.\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method sends an approval email to a student.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     */
    public static void sendTeacherApprovalEmail(final JavaMailSenderImpl mailSender, String email){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teacher Account Info");
        ms.setText("Hello,\n" + "Your teacher account "+email+" has been approved.\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method sends a deletion of account email to a teacher.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     */
    public static void sendDeleteTeacherEmail(final JavaMailSenderImpl mailSender, String email){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teacher Account Info");
        ms.setText("Hello,\n" + "Your teacher account "+email+" has been denied and deleted.\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method sends a disablement of account email to a teacher.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     */
    public static void sendDisabledTeacherEmail(final JavaMailSenderImpl mailSender, String email){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teacher Account Info");
        ms.setText("Hello,\n" + "Your teacher account "+email+" has been disabled if you feel this " +
                "is an error reply to this email and we will look into the problem.\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method sends a student notifying them that they are a TA for a certain course.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     * @param courseName is the course name for which this account was enabled as a TA.
     */
    public static void sendAddTAEmail(final JavaMailSenderImpl mailSender, String email, String courseName){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teaching Assistant Account Info");
        ms.setText("Hello,\n\n" + "Your TA status has been enabled for email: "+email+" and course: "+courseName+"\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    public static void sendRemoveTAEmail(final JavaMailSenderImpl mailSender, String email, String courseName){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teaching Assistant Account Info");
        ms.setText("Hello,\n\n" + "Your TA status has been disabled for email: "+email+" and course: " + courseName+ "\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method notifies a teacher that their course is going to be disabled.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     * @param userName the name of the teacher.
     * @param courseName the name of the course being disabled.
     */
    public static void sendCourseDisabledEmail(final JavaMailSenderImpl mailSender, String email, String userName, String courseName){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teacher Account Info");
        ms.setText("Hello " + userName + ",\n" + "Your course "+courseName+" has been disabled. This could be because of copyright violations." +
                " If you have any further questions or you feel this is a mistake please contact us to resolve the issue.\n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }

    /**
     * This method notifies a teacher that their course is going to be enabled.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     * @param userName the name of the teacher.
     * @param courseName the name of the course being enabled.
     */
    public static void sendCourseEnabledEmail(final JavaMailSenderImpl mailSender, String email, String userName, String courseName, int courseCode){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teacher Account Info");
        String baseEmail = "Hello " + userName + ",\n" + "Your course "+ courseName +" has been enabled. Thank you for using our services.\n\n";

        if (courseCode > 0) {
            baseEmail += "Your course code is " + courseCode + ", provide this to your students to give them access to your course.";
        }
        baseEmail += "\n\n Thank you, \nMasterMyCourse.com";
        ms.setText(baseEmail);
        mailSender.send(ms);
    }

    /**
     * This method notifies a teacher that their course is going to be deleted.
     * @param mailSender is an instance JavaMailSenderImpl class used to send email via java.
     * @param email the email address to which the email is sent.
     * @param userName the name of the teacher.
     * @param courseName the name of the course being deleted.
     */
    public static void sendCourseDeletedEmail(final JavaMailSenderImpl mailSender, String email, String userName, String courseName){
        SimpleMailMessage ms = new SimpleMailMessage();
        ms.setFrom(mailSender.getJavaMailProperties().get("mail.smtp.email").toString());
        ms.setTo(email);
        ms.setSubject("MasterMyCourse.com Teacher Account Info");
        ms.setText("Hello " + userName + ",\n" + "Your course "+courseName+" has been deleted. This could be because of copyright violations." +
                " No further action is needed. To avoid this problem in the future make sure you have the authors permission before publishing. \n\n Thank you, \nMasterMyCourse.com");
        mailSender.send(ms);
    }
}
