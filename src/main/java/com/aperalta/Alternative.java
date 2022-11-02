package com.aperalta;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Alternative {

    //TODO implement \r for new page in book
    static int bookCount = 1;

    public static void main(String[] args) {

        // ========== Book Setup ============== //
        int numBooks = 5; // number of books to create
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
        String jsonFilename = "fake-books-db-pdfbox.json";
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
        try (FileWriter file = new FileWriter(filename)) {
            file.write(booksList.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeJSON(String filename, JSONObject booksList) {
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
        String filename = book.getTitle().replace(" ", "_") + "-" + bookCount + ".pdf";
        if (filename.length() > 23) {
            filename = filename.substring(0, 10) + "-" + bookCount + ".pdf";
        }

        // Create and Export PDF file using IText
        try {
            PDDocument pdfDocument = new PDDocument();
            PDDocumentInformation pdd = new PDDocumentInformation();

            pdd.setTitle(book.getTitle());

            for (int i = 0; i < book.getNumberOfPages(); i++) {
                PDPage currentPage = new PDPage();
                PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, currentPage);
                contentStream.beginText();
                // try pagewidth 615 and height 815
                //Setting the position for the line
                contentStream.newLineAtOffset(05, 750);
                //Set Leading
                contentStream.setLeading(14.5f);
                //Setting up the Font and Font Size
                contentStream.setFont(PDType1Font.COURIER, 9 );
                //Write text to contentStream

                // Faster but doesnt break really long lines
                /*
                String[] lines = book.getPage(i).split("\\r?\\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }
*/

                // Generating Lines myself - less efficient, slower and flexible but better results so far
                ArrayList<String> pageLines = new ArrayList<>();
                pageLines = book.getPageLines(i, 90);

                for (String line : pageLines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }
//                PDFTextStripper textStripper = new PDFTextStripper();
//                textStripper.setLineSeparator("\n");

//                bookPage = bookPage.replace("\n", "").replace("\r", "");
//                contentStream.showText(bookPage);

//                contentStream.newLine(); //to add a new line

                contentStream.endText();
                contentStream.close();
                pdfDocument.addPage(currentPage);
            }



            pdfDocument.save(folder + filename);
            pdfDocument.close();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }

        return filename;
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

        book.createTitlePage(); //Used to add a Title + Authors page at the front of the Book

        return book;
    }

    static int getRandomIntNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
