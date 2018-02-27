package com.mastermycourse.pojos;


import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Authors: Jose Rodriguez and James DeCarlo.
 *
 * Plain old Java Object to hold note retrieved from the database
 */
public class Note {
    String titleNote;
    String noteText;

    public String getTitleNote() {
        return titleNote;
    }

    public void setTitleNote(String titleNote) {
        this.titleNote = titleNote;
    }

    public String getNoteTextHtml() {

        return noteText.replace("\n", "<br>");
    }

    public void setnoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteEscapedJavascript(){
        return StringEscapeUtils.escapeEcmaScript(noteText);
    }

}
