package presenter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import models.Book;
import models.BibliographicCollection;
import models.Library;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import persistence.Persistence;
import views.View;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import javax.json.*;
import java.io.*;

public class BookPresenter {
    private Library library;
    private View view;
    private ArrayList<String> propertiesData;
    private Persistence persistence;

    private LocalDate lastCase1Execution;
    private LocalDate lastCase2Execution;

    public BookPresenter() {
        library = new Library();
        view = new View();
        persistence = new Persistence(library, view);
        propertiesData = persistence.readPropertiesFile();

    }

    public void run() {

        persistence.createPropertiesFile();
        persistence.deserializeLibrary();
        persistence.desserializarXML();
        persistence.serializeLibraryToJson(library);


        while (true) {
            if (adminPasswordAuthentication()) {
                mainMenu();
            } else {
                handleIncorrectCredentials();
            }
        }
    }

    public void mainMenu() {
        int option = 0;
        while (option != 23) {
            LocalDate currentTime = LocalDate.now();


            option = showMenu();
            switch (option) {
                case 1:
                    lastCase1Execution = currentTime;
                    createBibliographicCollection();
                    break;

                case 2:
                    lastCase2Execution = currentTime;
                    insertBook();
                    break;

                case 3:
                    editBook();
                    break;

                case 4:
                    eraseBook();
                    break;

                case 5:
                    searchBook();
                    break;

                case 6:
                    showAllBooks();
                    break;

                case 7:
                    assignBookToCollection();
                    break;

                case 8:
                    persistence.printBooksToFile();
                    break;

                case 9:
                    persistence.createCollectionFile();
                    break;

                case 10:
                    persistence.deleteCollectionFile();
                    break;

                case 11:
                    persistence.writeBinaryFile();
                    break;

                case 12:
                    persistence.readBinaryFile();
                    break;

                case 13:
                    persistence.writeSerializableLibrary();
                    break;

                case 14:
                    persistence.readLibraryXMLFile();
                    break;

                case 15:
                    persistence.writeLibraryXML();
                    break;

                case 16:
                    persistence.leerXMLFile();
                    break;
                case 17:
                    persistence.readAnyXMLFile();
                    break;
                case 18:
                    String collectionKnowledgeArea = view.readString("Enter Knowledge Area of the collection to serialize: ");
                    BibliographicCollection collectionToSerialize = library.getCollections().get(collectionKnowledgeArea);
                    if (collectionToSerialize != null) {
                        persistence.serializarXML(collectionToSerialize);
                    } else {
                        view.showMessage("Collection not found.");
                    }
                    break;

                case 19:
                    persistence.desserializarXML();
                    break;

                case 20:
                    persistence.serializeLibraryToJson(library);
                    break;

                case 21:
                    persistence.readSerializedJSONFile();
                    break;

                case 22:
                    String fileOption = view.readString("Por favor seleccione el tipo de archivo deseado para recibir la información \n1. XML\n2. JSON");

                    if (fileOption.equals("1")) {
                        persistence.webServiceRequest("xml");
                    } else if (fileOption.equals("2")) {
                        persistence.webServiceRequest("json");
                    } else {
                        view.showMessage("La opción no es correcta\n");
                    }
                    break;

                case 23:
                    persistence.readXMLWeatherProperties();
                    break;
                case 24:
                    persistence.readJSONWeatherProperties();
                case 25:
                    end();
                    break;
            }
        }
    }

    public int showMenu() {


        if (lastCase1Execution != null) {

            view.showMessage("Last bibliographic collection update " + lastCase1Execution);
        }
        if (lastCase2Execution != null) {

            view.showMessage("Last book addition update: " + lastCase2Execution);
        }

        int option = view.readInt("Main Menu\n1. Create Bibliographic Collection\n2. Insert Book\n3. Edit Book\n4. Erase Book\n5. Search Book\n6. Show All Books\n7. Assign Book to Collection\n8. Print Books to File\n9. Create Collection File\n10. Delete Collection File\n11. Create Binary File\n12. Read Binary File\n13. Create Serialized Library\n14. Read Library XML file\n15. Write XML Library\n16. Leer XML (CÓDIGO PROFESORA)\n17. Read any XML File\n18. Serialize XMl\n19. Deserialize XML\n20. Serialize JSON\n21. Read JSON\n22. Generate Files JSON or XMl\n23. ReadXML\n24. Read JSON\n25. End program");
        return option;
    }

    public void insertBook() {
        String ID = view.readString("Enter Book ID: ");
        String title = view.readString("Enter Book Title: ");
        String author = view.readString("Enter Book Author: ");
        String knowledgeArea = view.readString("Enter Knowledge Area: ");
        boolean state = view.readBoolean("Enter New Book State (stock). If available, enter 'true', if not" +
                "available, enter 'false': ");
        library.insertBook(ID, title, author, knowledgeArea, state);
    }

    public void editBook() {
        String ID = view.readString("Enter Book ID to Edit: ");
        if (library.searchBook(ID) != null) {
            String title = view.readString("Enter New Book Title: ");
            String author = view.readString("Enter New Book Author: ");
            String knowledgeArea = view.readString("Enter New Knowledge Area: ");
            boolean state = view.readBoolean("Enter New Book State (stock). If available, enter 'true', if not" +
                    "available, enter 'false': ");
            library.editBook(ID, title, author, knowledgeArea, state);
        } else {
            view.showMessage("Book not found.");
        }
    }

    public void eraseBook() {
        String ID = view.readString("Enter Book ID to Erase: ");
        library.eraseBook(ID);
    }

    public void searchBook() {
        String ID = view.readString("Enter Book ID to Search: ");
        Book foundBook = library.searchBook(ID);
        if (foundBook != null) {
            view.showMessageWithBookInfo("Book found:\n" + foundBook.toString());
        } else {
            view.showMessage("Book not found.");
        }
    }

    public void showAllBooks() {
        Map<String, Book> books = library.getBooks();
        if (books.isEmpty()) {
            view.showMessage("No books in the library.");
        } else {
            view.showMessage("List of Books:");
            for (Book book : books.values()) {
                view.showMessageWithBookInfo(book.toString());
            }
        }
    }

    public void createBibliographicCollection() {
        String knowledgeArea = view.readString("Enter Knowledge Area for the new collection: ");
        library.createBibliographicCollection(knowledgeArea);
    }

    public void assignBookToCollection() {
        String bookID = view.readString("Enter Book ID: ");
        String collectionKnowledgeArea = view.readString("Enter Knowledge Area of the target collection: ");
        library.assignBookToCollection(bookID, collectionKnowledgeArea);
    }



    public boolean adminPasswordAuthentication() {
        ArrayList<String> userEntry = new ArrayList<String>();
        userEntry.add(view.readString("Enter your username"));
        userEntry.add(view.readString("Enter your password"));

        if (userEntry.equals(propertiesData)) {
            return true;
        } else {
            return false;
        }
    }

    public void handleIncorrectCredentials() {
        int option = 0;
        while (option != 3) {
            option = view.readInt("Incorrect Credentials\n1. Try Again\n2. Exit Program\n3. Back to Main Menu");
            switch (option) {
                case 1:
                    if (adminPasswordAuthentication()) {
                        mainMenu();
                    } else {
                        view.showMessage("Incorrect Credentials");
                    }
                    break;

                case 2:
                    end();
                    break;

                case 3:
                    return;
            }
        }
    }






        public void end() {
        System.exit(0);
    }
}

