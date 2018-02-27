package com.mastermycourse.pdf;

import com.mastermycourse.pojos.OutlineKey;
import com.mastermycourse.pojos.TableOfContentPair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Authors: James DeCarlo and Zach Lerman.
 *
 * Class is used to open a pdf document and perform all applicable tasks to this application once you are
 * completed with this class you must close() the document by calling the close method in this class.
 */
public class PDF {
    PDDocument pdDocument;
    PDFRenderer renderer;

    /**
     * Create a New PDF object. method close() must be called when completed with this object.
     * @param file The PDF file to be opened
     * @throws IOException
     */
    public PDF(File file) throws IOException{
        pdDocument = PDDocument.load(file);
        renderer = new PDFRenderer(pdDocument);
    }

    /**
     * Creates string from the startPage to the endPage of a pdf.
     * @param startPage the start page to start the pdf to string conversion
     * @param endPage the last page of the pdf to string conversion.
     * @return String the string that contains all the pdf converted pages.
     */
    public String ripPDFtoString(int startPage, int endPage) throws IOException{
        String string;
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        pdfTextStripper.setStartPage(startPage);
        pdfTextStripper.setEndPage(endPage);
        string = pdfTextStripper.getText(pdDocument);
        return string;
    }

    /**
     * Creates a BufferedImage for the given pdf page.
     * @param page the start page to start the pdf to string conversion
     * @return BufferedImage the string that contains all the pdf converted pages.
     */
    public BufferedImage ripPDFtoImage(int page) throws IOException {
        BufferedImage image = renderer.renderImageWithDPI(page, 150, ImageType.RGB);
        return image;
    }

    /**
     * Gets the page labels for each page.
     * @return String[] array of page labels created.
     */
    public String[] getPageLabels() throws IOException {
        String[] labels = pdDocument.getDocumentCatalog().getPageLabels().getLabelsByPageIndices();
        return labels;
    }

    /**
     * Finds and returns the total number of pages in the pdf document.
     * @return int that is the total number of pages for the pdf document.
     */
    public int totalNumberOfPages() throws IOException{
        return pdDocument.getNumberOfPages();
    }

    /**
     * This method finds out if the pdf in question has an outline.
     * @return true if an outline exists, else return false.
     */
    public boolean getHasOutline(){
        try{
            PDDocumentOutline outline = pdDocument.getDocumentCatalog().getDocumentOutline();
            if(outline == null){
                return false;
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Finds and returns a list of the outline key for a pdf with an outline.
     * @return List<OutlineKey> of outline keys from the pdf with outline.
     */
    public List<OutlineKey> getOutlineKeys() throws IOException {
        PDDocumentOutline outline = pdDocument.getDocumentCatalog().getDocumentOutline();
        PDOutlineItem chapter = outline.getFirstChild();

        List<OutlineKey> outlineKeys = new ArrayList<>();
        while (chapter != null){
            String title = chapter.getTitle();
            int pageIndex = pdDocument.getDocumentCatalog().getPages().indexOf(chapter.findDestinationPage(pdDocument));
            OutlineKey outlineKey = new OutlineKey(pageIndex, title);
            outlineKeys.add(outlineKey);
            chapter = chapter.getNextSibling();
        }
        return outlineKeys;
    }

    /**
     * Finds and returns a list of table of content pairs for a pdf without an outline.
     * @return List<OutlineKey> of table of content pairs for a pdf without an outline.
     */
    public List<TableOfContentPair> getTableOfContentsPairs(int startPage, int endPage) throws IOException {
        PDPage current; // current page
        int page_number = startPage; // current page number

        List<TableOfContentPair> pairs = new ArrayList<TableOfContentPair>();
        // while more pages
        while ((current = pdDocument.getPage(page_number)) != null) {
            if (page_number > endPage)
                break;

            // check if it is a new chapter
            String p = ripPDFtoString(page_number, page_number);
            String lines[] = p.split("\\r?\\n");
            String regex = "^(\\d+)(\\s+)(.*)(\\s+)(\\d+)";
            String regex2 = "^(\\w)(\\s+)(.*)(\\s+)(\\d+)";
            for (String line : lines){
                if(line.matches(regex)||line.matches(regex2)){
                    String splitLine[] = line.split("\\s");
                    String title = "";
                    int pageNum = 0;
                    for (int i = 0; i < splitLine.length;i++){
                        if(i==splitLine.length-1){
                            pageNum = Integer.parseInt(splitLine[i]);
                        }
                        else{
                            title = title + splitLine[i] + " ";
                        }
                    }
                    TableOfContentPair pair = new TableOfContentPair(title, pageNum);
                    pairs.add(pair);
                }
            }
            page_number++;
        }
        return pairs;
    }

    /**
     * This method is used to close the pdf document.
     */
    public void close(){
        try {
            pdDocument.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
