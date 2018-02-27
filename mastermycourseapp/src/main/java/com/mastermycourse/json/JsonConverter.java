package com.mastermycourse.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastermycourse.pojos.ChapterMetrics;
import com.mastermycourse.pojos.Quiz;
import com.mastermycourse.pojos.QuizHistogram;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Authors: Rahul Verma and Zach Lerman.
 *
 * This class has helper methods to convert various objects found in the POJOs folder into JSON
 */
public class JsonConverter {

    /**
     * Convert Quiz objects to JSON string
     * @param quizzes
     * @return String representation of the JSON
     * @throws IOException
     */
    public String convertQuizArrayListToJSON(ArrayList<Quiz> quizzes) throws IOException {
        final StringWriter sw = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(sw, quizzes);
        return sw.toString();
    }

    /**
     * Convert QuizHistogram objects to JSON string
     * @param hqs
     * @return String representation of the JSON
     * @throws IOException
     */
    public String convertHistogramArrayListToJson(ArrayList<QuizHistogram> hqs) throws IOException{
        final StringWriter sw = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(sw, hqs);
        return sw.toString();
    }

    /**
     * Convert ChapterMetrics objects to JSON string
     * @param chp
     * @return String representation of the JSON
     * @throws IOException
     */
    public String convertChapterMetricsmArrayListToJson(ArrayList<ChapterMetrics> chp) throws IOException{
        final StringWriter sw = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(sw, chp);
        return sw.toString();
    }

    /**
     * Convert arraylist objects to JSON string
     * @param objects
     * @return String representation of the JSON
     * @throws IOException
     */

    public static String convertToJson(ArrayList objects) throws IOException {
        final StringWriter sw = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(sw, objects);
        return sw.toString();
    }

}
