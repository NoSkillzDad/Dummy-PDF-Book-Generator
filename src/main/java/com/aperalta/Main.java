package com.aperalta;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


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

    static int bookCount = 1;

    public static void main(String[] args) {


        // ========== Book Setup ============== //
        int numBooks = 15; // number of books to create
        int numAuthors = 3; // number of authors
        int numParagraphsPerPage = 6; // Paragraphs are of variable size. This is an estimate for lorem ipsum
        int numPages = 10; // Estimated number of pages you want in the book (Final number could be + or -
        String folder = "./books/"; //Existing folder at the base level of your project (/src for example) where books are gonna be exported
        boolean isNumAuthorsFixed = true; // if you want numAuthors to be the that for all the books. if false, then a random number between 1 and numAuthors will be used
        int[] wordsInTitle = new int[2]; // Defined below

        // Assign Limits for number of words in Title
        wordsInTitle[0] = 1; // Lower limit
        wordsInTitle[1] = 5; // Upper limit

        // ============= JSON Setup ================== //
        String jsonFilename = "fake-books-db.json";
        boolean exportForJsonServer = true; // set to true if exported json file is to be used with json-server, otherwise, false.
        JSONArray booksList = new JSONArray();

        Book book = new Book();

        for (int i = 0; i < numBooks; i++) {

            // cover some but not all of input setting up errors.
            if (numAuthors < 1 || numBooks < 1 || numParagraphsPerPage < 1 || numPages < 1) System.exit(1);

            book = createBook(wordsInTitle, numAuthors, isNumAuthorsFixed, numParagraphsPerPage,numPages);
            String filename = exportBookPDF(book, folder);


            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", bookCount);
            jsonObject.put("filename", filename);
            jsonObject.put("title",book.getTitle());
            jsonObject.put("authors", book.getAuthors());
            jsonObject.put("excerpt", book.getExcerpt());

            booksList.add(jsonObject);

            bookCount++;
        }

        if (exportForJsonServer) {
            JSONObject jsonObjectJS = new JSONObject();
            jsonObjectJS.put("books", booksList);
            writeJSON(jsonFilename, jsonObjectJS);
        } else {
            writeJSON(jsonFilename, booksList);
        }

        System.out.println(numBooks + " Lorem Ipsum PDF books and 'fake-books-db.json' generated");
    }

    private static void writeJSON(String filename, JSONArray booksList) {
//        try (FileWriter file = new FileWriter("fake-books-db.json")) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(booksList.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeJSON(String filename, JSONObject booksList) {
//        try (FileWriter file = new FileWriter("fake-books-db.json")) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(booksList.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    static void exportBookPDF(Book book, String folder) {
    static String exportBookPDF(Book book, String folder) {
        //Generate unique filename based on book Title
        String fileName = book.getTitle().replace(" ", "_") + "-" + bookCount + ".pdf";
        if (fileName.length() > 23) {
            fileName = fileName.substring(0, 10) + "-" + bookCount + ".pdf";
        }
        // Moved the folder part to the PdfWriter itself to keep filename "clean" to use it in JSON below
//        fileName = folder + fileName;

        book.createTitlePage(); //Used to add a Title + Authors page at the front of the Book

        // Create and Export PDF file using IText
        try {
            Document pdfDocument = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, new FileOutputStream(folder + fileName));
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

        return fileName;
//        finally {
            //add book entry to json file
            /* JSON format
            {
            "Title": "<Book.title>",
            "Author": [
                <Book.author1>,
                <Book.authors>,
                <Book.author3>
            ],
            "Excerpt": "<Book.excerpt>",
            "Filename": "<Filename>"
            }

             */
//        }
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