package com.mastermycourse.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.mastermycourse.pojos.S3File;
import com.mastermycourse.settings.AWSS3Credentials;
import com.mastermycourse.settings.DirectorySettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: James DeCarlo.
 *
 * Used for access to AWS S3 Bucket for file storage
 */
public class AWSS3 {

    AmazonS3 s3;

    /**
     * Creates a new AWSS3 instance you must close the closeConnection method when finished with this object.
     */
    public AWSS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWSS3Credentials.accessKey, AWSS3Credentials.secretKey);

        s3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(new ClientConfiguration())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.US_EAST_1).build();
    }

    /**
     * Uploads the course pdf
     * @param courseId the course id of the course
     * @param inputStream the file input stream
     * @param fileName the file name
     * @param fileLength the file length
     */
    public void upload(int courseId, InputStream inputStream, String fileName, long fileLength){
        String key = DirectorySettings.teachersDirectoryRoot + courseId + '/' + fileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileLength);
        s3.putObject(new PutObjectRequest(AWSS3Credentials.bucket, key, inputStream, objectMetadata));
    }

    /**
     * Uploads the course pdf
     * @param courseId the course id of the course
     * @param inputStream the file input stream
     * @param fileName the file name
     * @param fileLength the file length
     */
    public void uploadCoursePdf(int courseId, InputStream inputStream, String fileName, long fileLength){

        String key = DirectorySettings.coursesPdfsDirectoryRoot + courseId + '/' + fileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileLength);
        s3.putObject(new PutObjectRequest(AWSS3Credentials.bucket, key, inputStream, objectMetadata));
    }

    /**
     * List all the files of the given course Id
     * @param courseId the course id of the course you want to list the files for
     * @return a list collection of S3File objects containing the necessary information
     */
    public List<S3File> listFiles(int courseId){
        String key = DirectorySettings.teachersDirectoryRoot + courseId + '/';
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(AWSS3Credentials.bucket)
                .withPrefix(key));

        List<S3File> files = new ArrayList<>();
        for(S3ObjectSummary summary : objectListing.getObjectSummaries()){
            S3File file = new S3File();
            file.setFileName(summary.getKey().substring(key.length()));
            file.setFileSize(summary.getSize());
            file.setLastModified(summary.getLastModified());
            files.add(file);
        }
        return files;
    }


    /**
     * Get the current course PDF file
     * @param courseId the course Id of the course
     * @return the pdf file temporary file.
     * @throws IOException
     */
    public File getCoursePdf(int courseId) throws IOException {
        String key = DirectorySettings.coursesPdfsDirectoryRoot + courseId + '/';
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(AWSS3Credentials.bucket)
                .withPrefix(key));

        if(objectListing.getObjectSummaries().size() > 0){
            String fileKey = objectListing.getObjectSummaries().get(0).getKey();
            S3Object s3Object = s3.getObject(new GetObjectRequest(AWSS3Credentials.bucket, fileKey));
            InputStream inputStream = s3Object.getObjectContent();
            String fileName = fileKey.substring(key.length());
            String prefix = fileName.substring(0, fileName.lastIndexOf('.'));
            String suffix = fileName.substring(fileName.lastIndexOf('.'));
            File file = File.createTempFile(courseId +"_"+prefix, suffix);
            file.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            org.apache.commons.io.IOUtils.copy(inputStream, fileOutputStream);
            return file;
        }
        return null;
    }

    /**
     * Downloads the file for the given course id
     * @param courseId the course id of the course the file is being downloaded from
     * @param fileName the filename you want to download
     * @return the InputStream of the file download
     */
    public InputStream download(int courseId, String fileName){
        String key = DirectorySettings.teachersDirectoryRoot + courseId + '/' + fileName;
        S3Object object = s3.getObject(new GetObjectRequest(AWSS3Credentials.bucket, key));
        return object.getObjectContent();
    }

    /**
     * Deletes the filename in the course id given
     * @param courseId the course id of the file you want to delete
     * @param fileName the filename you want to delete.
     */
    public void delete(int courseId, String fileName){
        String key = DirectorySettings.teachersDirectoryRoot + courseId + '/' + fileName;
        s3.deleteObject(AWSS3Credentials.bucket, key);
    }

    /**
     * Closes the connection to S3 must be called once at the end of object use.
     */
    public void closeConnection(){
        s3.shutdown();
    }
}
