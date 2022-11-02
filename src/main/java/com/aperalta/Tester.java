package com.aperalta;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Tester {

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

            book = createBook(wordsInTitle, numAuthors, isNumAuthorsFixed, numParagraphsPerPage, numPages);
            String filename = exportBookPDF(book, folder);


            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", bookCount);
            jsonObject.put("filename", filename);
            jsonObject.put("title", book.getTitle());
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
        for (int i = 0; i < book.getNumberOfPages(); i++) {

            ArrayList<String> pageLines = new ArrayList<>();
            pageLines = book.getPageLines(i, 78);

            for (String line : pageLines) {
                System.out.println(line);
                System.out.println("\n -artificial new line \n");
            }
        }
        return "Done";
    }

        static Book createBook ( int[] titleLength, int numAuthors, boolean fixedNumAuthors, int numParagraphsPerPage,
        int numPages){

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

        static int getRandomIntNumber ( int min, int max){
            Random random = new Random();
            return random.nextInt(max - min) + min;
        }
    }
