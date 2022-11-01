package com.aperalta;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;


class Book {
    private String title;
    private ArrayList<String> authors;
    private String excerpt;
    private ArrayList<String> pages;

    public Book() {
        authors = new ArrayList<>();
        pages = new ArrayList<>();
    }

    public Book(String title) {
        this.title = title;
        authors = new ArrayList<>();
        pages = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public ArrayList<String> getPages() {
        return pages;
    }

    public void setPages(ArrayList<String> pages) {
        this.pages = pages;
    }

    public void addPage(String page) {
//        System.out.println(page.length());
        this.pages.add(page);

    }

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public void authorsToString() {
        for (String author : this.authors) {
            System.out.println(author);
        }
    }

    public void pagesToString() {
        for (String page : this.pages) {
            System.out.println(page);
        }
    }

    public void createTitlePage() {
        String titlePage = "\n" + this.title + "\n" +
                "\n\nAuthors\n" + this.authors.toString() + "\n\n\n\n";
        this.pages.add(0, titlePage);
    }

    public String getPage(int index) {
        return (index < this.pages.size()) ? this.pages.get(index) : "Page Bot Found";
    }

    public int getNumberOfPages() {
        return this.pages.size();
    }
}

public class Main {

    static int bookCount = 0;

    public static void main(String[] args) {


        int numBooks = 50; // number of books to create
        int numAuthors = 3; // number of authors
        int numParagraphsPerPage = 6;
        int numPages = 10;
        String folder = "./books/"; //Existing folder at the base level of your project (/src for example)
        int[] wordsInTitle = new int[2];
        boolean isNumAuthorsFixed = true;

        Book book = new Book();

        // Assign Limits for number of words in Title
        wordsInTitle[0] = 1;
        wordsInTitle[1] = 5;

        for (int i = 0; i < numBooks; i++) {
            book = createBook(wordsInTitle, numAuthors, isNumAuthorsFixed, numParagraphsPerPage,numPages);
            exportBookPDF(book, folder);
            bookCount++;
        }

        System.out.println(numBooks + " LoremIpsum PDF Books Generated");
    }

    static void exportBookPDF(Book book, String folder) {
        //Generate unique filename based on book Title
        String fileName = book.getTitle().replace(" ", "_") + "-" + bookCount + ".pdf";
        if (fileName.length() > 23) {
            fileName = fileName.substring(0, 10) + "-" + bookCount + ".pdf";
        }
        fileName = folder + fileName;

        book.createTitlePage();


        // Create and Export PDF file using IText
        try {
            Document pdfDocument = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(fileName));
            pdfDocument.open();
            // Margins
            pdfDocument.setMarginMirroring(true);
            pdfDocument.setMargins(36, 72, 108, 180);
            pdfDocument.topMargin();

            // Font
            BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
            Font myFont = new Font(courier);

            // Font Styles
            Font boldFont = new Font();
            boldFont.setStyle(Font.BOLD);
            boldFont.setSize(10);

            myFont.setStyle(Font.NORMAL);
            myFont.setSize(9);

            pdfDocument.add(new com.itextpdf.text.Paragraph("\n"));
            for (int i = 0; i < book.getNumberOfPages(); i++) {
                com.itextpdf.text.Paragraph paragraph = new com.itextpdf.text.Paragraph(book.getPage(i));
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
                pdfDocument.add(paragraph);
            }
            pdfDocument.close();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
        // System.out.println(fileName);

        // Create and Export PDF file using PdfBox
        // Issues with a character encoding somewhere - leaving it as reference for the future
    /*
        PDDocument pdfDocument = new PDDocument();
        PDPage page = new PDPage();
        book.createTitlePage();

        for (int i = 0; i < book.getPages().size(); i++) {
            pdfDocument.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);

    //      contentStream.setFont(PDType1Font.COURIER, 12);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.beginText();
    //      contentStream.showText(book.getPages().get(i));
            contentStream.showText(book.getPage(i));
            contentStream.endText();
            contentStream.close();
        }

        pdfDocument.save(fileName);
        pdfDocument.close();
*/
    /*
        // Terminal Output for Testing

        System.out.println(book.getTitle());
        System.out.println();
        book.authorsToString();
//        System.out.println(book.getAuthors());
        System.out.println();
        System.out.println(book.getExcerpt());
        System.out.println();
        book.pagesToString();
//        System.out.println(book.getPages());
     */
    }

    static Book createBook(int[] titleLength, int numAuthors, boolean fixedNumAuthors, int numParagraphsPerPage, int numPages) {

        Lorem lorem = LoremIpsum.getInstance();

        Book book = new Book();
        book.setTitle(lorem.getTitle(titleLength[0], titleLength[1]));
        book.setExcerpt(lorem.getWords(55, 75));

        // Determines random number of Authors
        if (numAuthors > 1 && !fixedNumAuthors) {
            numAuthors = getRandomIntNumber(1, numAuthors);
        }

        // Number of Authors
        for (int j = 0; j < numAuthors; j++) {
            book.addAuthor(lorem.getFirstName() + " " + lorem.getLastName());
        }

        // Number of Pages
        for (int j = 0; j < numPages; j++) {
            book.addPage(lorem.getParagraphs(4, 5));
        }
        return book;
    }

    static int getRandomIntNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}

/*
==================================================================
Dummy PDF should contain

- Book Title
- Author(s) (default 1, max 4) - random, fixed
- Excerpt (1 paragraph)
- Book Content (Max 5 pages -4 paragraphs per page) - random, fixed

--------------------------------

- Generate random Title
- Determine number of authors -random
- Loop over authors
- Generate Excerpt
- Determine number of pages
- Generate number of pages
- Save Book

 ==================================================================
 */