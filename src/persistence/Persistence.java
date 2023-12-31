package persistence;

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
public class Persistence {
    private Library library;
    private View view;

    public Persistence(Library library, View view) {
        this.library = library;
        this.view = view;
    }

    public void printBooksToFile() {
        String fileName = view.readString("Enter the file name to save all books: ");
        String locationChoice = view.readString("Do you want to save the file in the project folder? (yes/no): ");

        if (locationChoice.equalsIgnoreCase("no")) {
            String customLocation = view.readString("Enter the full path where you want to save the file: ");
            fileName = customLocation + "\\" + fileName;
        } else {
            fileName = "src/data/" + fileName;
        }

        FileWriter fileWriter = null;
        PrintWriter printWriter = null;

        try {
            fileWriter = new FileWriter(fileName);
            printWriter = new PrintWriter(fileWriter);

            Map<String, Book> books = library.getBooks();
            for (Book book : books.values()) {
                printWriter.println("Book ID: " + book.getID());
                printWriter.println("Title: " + book.getTitle());
                printWriter.println("Author: " + book.getAuthor());
                printWriter.println("Knowledge Area: " + book.getKnowledgeArea());
                printWriter.println("State: " + book.isState());
                printWriter.println();
            }

            view.showMessage("All books have been printed to the file.");
        } catch (IOException e) {
            view.showMessage("Error writing to file: " + e.getMessage());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void createCollectionFile() {
        String fileName = view.readString("Enter the file name to save books by collection: ");
        String collectionKnowledgeArea = view.readString("Enter Knowledge Area of the collection: ");
        String locationChoice = view.readString("Do you want to save the file in the project folder? (yes/no): ");

        if (locationChoice.equalsIgnoreCase("no")) {
            String customLocation = view.readString("Enter the full path where you want to save the file: ");
            fileName = customLocation + "\\" + fileName;
        } else {
            fileName = "src/data/" + fileName;
        }

        FileWriter fileWriter = null;
        PrintWriter printWriter = null;

        try {
            fileWriter = new FileWriter(fileName);
            printWriter = new PrintWriter(fileWriter);

            Map<String, BibliographicCollection> collections = library.getCollections();
            if (collections.containsKey(collectionKnowledgeArea)) {
                BibliographicCollection collection = collections.get(collectionKnowledgeArea);
                printWriter.println("Bibliographic Collection Type: " + collection.getKnowledgeArea());

                List<Book> books = collection.getBooks();
                for (Book book : books) {
                    printWriter.println("Book ID: " + book.getID());
                    printWriter.println("Title: " + book.getTitle());
                    printWriter.println("Author: " + book.getAuthor());
                    printWriter.println("Knowledge Area: " + book.getKnowledgeArea());
                    printWriter.println("State: " + book.isState());
                    printWriter.println();
                }

                view.showMessage("Books in the specified collection have been printed to the file.");
            } else {
                view.showMessage("Collection not found.");
            }
        } catch (IOException e) {
            view.showMessage("Error writing to file: " + e.getMessage());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void deleteCollectionFile() {
        String fileName = view.readString("Enter the file name to delete: ");
        File fileToDelete = new File(fileName);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                view.showMessage("File has been deleted.");
            } else {
                view.showMessage("Error deleting the file.");
            }
        } else {
            view.showMessage("File not found.");
        }
    }

    public void createPropertiesFile() {
        String fileName = "src/data/" + "PropertiesFile";
        File file = new File(fileName);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            Properties prop = new Properties();

            prop.setProperty("user", "user123");
            prop.setProperty("password", "password123");
            prop.store(outputStream, "Setup File");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> readPropertiesFile() {
        String fileName = "src/data/" + "PropertiesFile";
        File file = new File(fileName);

        try {
            InputStream inputStream = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(inputStream);

            String user = prop.getProperty("user");
            String password = prop.getProperty("password");

            ArrayList<String> data = new ArrayList<String>();

            data.add(user);
            data.add(password);

            return data;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void writeBinaryFile() {
        String fileName = "src/data/" + "BinaryFile.bin";
        File file = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dataOutput = new DataOutputStream(fos);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fos);

            Map<String, Book> books = library.getBooks();
            Map<String, BibliographicCollection> collections = library.getCollections();

            dataOutput.writeInt(books.size());
            dataOutput.writeInt(collections.size());

            Map.Entry<String, Book> firstEntry = books.entrySet().iterator().next();
            String firstKey = firstEntry.getKey();
            Book foundBook = library.searchBook(firstKey);
            objectOutput.writeObject(foundBook);

        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Input output error");
            ex.printStackTrace();
        }
    }

    public void readBinaryFile() {
        String fileName = "src/data/" + "BinaryFile.bin";
        File file = new File(fileName);

        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream dataInput = new DataInputStream(fis);
            ObjectInputStream objectInput = new ObjectInputStream(fis);

            int numBooks = dataInput.readInt();
            int numCollections = dataInput.readInt();

            view.showMessage("Number of books: " + numBooks);
            view.showMessage("Number of collections: " + numCollections);

            Book readBook = (Book) objectInput.readObject();


            view.showMessage("1 Book Properties:");
            view.showMessage("ID: " + readBook.getID());
            view.showMessage("Title: " + readBook.getTitle());
            view.showMessage("Author: " + readBook.getAuthor());
            view.showMessage("Knowledge Area: " + readBook.getKnowledgeArea());
            view.showMessage("State: " + readBook.isState());

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeSerializableLibrary() {
        String fileName = "src/data/" + "SerializedLibrary.ser";
        File file = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fos);

            objectOutput.writeObject(library);

        } catch (FileNotFoundException ex) {
            view.showMessage("File not found");
            ex.printStackTrace();
        } catch (IOException ex) {
            view.showMessage("Input output error");
            ex.printStackTrace();
        }
    }

    public void deserializeLibrary() {
        String fileName = "src/data/" + "SerializedLibrary.ser";
        File file = new File(fileName);

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream objectInput = new ObjectInputStream(fis);

            if (objectInput != null) {
                try {
                    library = (Library) objectInput.readObject();
                } catch (EOFException eof) {
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void leerXMLFile() {
        File inputFile = new File("/Users/sebastiancanon/Documents/Uptc/Q2-2023/Programación2/Programs/Library/src/data/libros.xml");
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document documento = saxBuilder.build(inputFile);

            Element elementoInicial = documento.getRootElement();

            List<Element> listaLibros = elementoInicial.getChildren();

            for (int i = 0; i < listaLibros.size(); i++) {

                Element libro = listaLibros.get(i);

                view.showMessage("\nElemento :" + libro.getName());
                view.showMessage("Id: " + libro.getChild("id").getText());
                view.showMessage("Nombre: " + libro.getChild("titulo").getText());

                Element autores = libro.getChild("autores");

                view.showMessage("\nElemento :" + autores.getName());

                Attribute atributo = autores.getAttribute("numero");
                view.showMessage("Número de autores: " + atributo.getValue());
                List<Element> autoresLista = autores.getChildren("autor");
                for (int j = 0; j < autoresLista.size(); j++) {
                    Element autor = autoresLista.get(j);

                    view.showMessage("Nombre: " + autor.getChild("nombre").getText());
                }
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLibraryXML() {
        String fileName = "src/data/library.xml";
        File outputFile = new File(fileName);

        Element libraryElement = new Element("library");
        Document doc = new Document(libraryElement);


        Map<String, Book> books = library.getBooks();
        for (Book book : books.values()) {
            Element bookElement = new Element("book");

            Element idElement = new Element("id");
            idElement.setText(book.getID());
            bookElement.addContent(idElement);

            Element titleElement = new Element("title");
            titleElement.setText(book.getTitle());
            bookElement.addContent(titleElement);

            Element authorElement = new Element("author");
            authorElement.setText(book.getAuthor());
            bookElement.addContent(authorElement);

            Element knowledgeAreaElement = new Element("knowledgeArea");
            knowledgeAreaElement.setText(book.getKnowledgeArea());
            bookElement.addContent(knowledgeAreaElement);

            Element stateElement = new Element("state");
            stateElement.setText(String.valueOf(book.isState()));
            bookElement.addContent(stateElement);

            libraryElement.addContent(bookElement);
        }


        try {
            OutputStream outputStream = new FileOutputStream(outputFile);
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            xmlOutputter.output(doc, outputStream);
            outputStream.close();
            view.showMessage("File created");
        } catch (IOException e) {
            view.showMessage("Error writing file " + e.getMessage());
        }
    }

    public Document readXMLFile(String fileName) throws IOException, JDOMException {
        File inputFile = new File(fileName);
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(inputFile);
    }

    public void readLibraryXMLFile() {
        String fileName = "src/data/library.xml";
        try {
            Document document = readXMLFile(fileName);

            Element rootElement = document.getRootElement();
            List<Element> bookElements = rootElement.getChildren();

            for (Element bookElement : bookElements) {
                view.showMessage("\nElement: " + bookElement.getName());
                view.showMessage("ID: " + bookElement.getChildText("id"));
                view.showMessage("Title: " + bookElement.getChildText("title"));

            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readAnyXMLFile(Element element, int cont) {
        String sangria = "   ".repeat(cont);

        view.showMessage(sangria + " " + element.getName());
        if (element.getChildren().isEmpty()) {
            view.showMessage(sangria + "     " + element.getValue());
        }

        for (Element childElement : element.getChildren()) {
            readAnyXMLFile(childElement, cont + 1);
        }
    }

    public void readAnyXMLFile() {
        String fileName = "src/data/libros.xml";

        try {
            Document document = readXMLFile(fileName);
            Element rootElement = document.getRootElement();

            readAnyXMLFile(rootElement, 0);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public void serializarXML(BibliographicCollection c) {
        File archivo = new File("src/data/XMLSerialized.xml");

        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(archivo);
            Writer writer = new OutputStreamWriter(outFile, Charset.forName("UTF-8"));
            XStream st = new XStream(new DomDriver("UTF-8"));

            List<Book> libros = new ArrayList<Book>();

            for (Book b : c.getBooks()) {
                libros.add(b);
            }
            st.alias("book", Book.class);
            st.addPermission(AnyTypePermission.ANY);
            st.toXML(libros, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void desserializarXML() {
        File archivo = new File("src/data/XMLSerialized.xml");

        try {
            FileInputStream fis = new FileInputStream(archivo);
            XStream st = new XStream(new DomDriver("UTF-8"));
            st.alias("book", Book.class);
            st.addPermission(AnyTypePermission.ANY);

            Object o = st.fromXML(fis);
            List<Book> libros = (List<Book>) o;


            for (Book book : libros) {
                library.getBooks().put(book.getID(), book);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serializeLibraryToJson(Library library) {
        JsonObjectBuilder libraryObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder bookArrayBuilder = Json.createArrayBuilder();

        for (Book book : library.getBooks().values()) {
            JsonObjectBuilder bookObjectBuilder = Json.createObjectBuilder()
                    .add("ID", book.getID())
                    .add("title", book.getTitle())
                    .add("author", book.getAuthor())
                    .add("knowledgeArea", book.getKnowledgeArea())
                    .add("state", book.isState());
            bookArrayBuilder.add(bookObjectBuilder);
        }

        libraryObjectBuilder.add("book", bookArrayBuilder);

        JsonObject libraryJsonObject = libraryObjectBuilder.build();

        try {
            OutputStream outputStream = new FileOutputStream("src/data/library.json");
            JsonWriter jsonWriter = Json.createWriter(outputStream);

            jsonWriter.writeObject(libraryJsonObject);
            jsonWriter.close();
        } catch (IOException e) {
            view.showMessage("Error writing JSON file: " + e.getMessage());
        }
    }

    public void readSerializedJSONFile() {
        String fileName = "src/data/library.json";
        File archivo = new File(fileName);

        try {
            FileInputStream fis = new FileInputStream(archivo);
            JsonReader reader = Json.createReader(fis);

            JsonObject jsonObject = reader.readObject();
            reader.close();

            JsonArray bookArray = jsonObject.getJsonArray("book");


            for (JsonValue bookValue : bookArray) {
                JsonObject bookObject = (JsonObject) bookValue;
                String id = bookObject.getString("ID");
                String title = bookObject.getString("title");
                String author = bookObject.getString("author");
                String knowledgeArea = bookObject.getString("knowledgeArea");
                boolean state = bookObject.getBoolean("state");

                view.showMessage("Book ID: " + id);
                view.showMessage("Title: " + title);
                view.showMessage("Author: " + author);
                view.showMessage("Knowledge Area: " + knowledgeArea);
                view.showMessage("State: " + state);
                view.showMessage("");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void webServiceRequest(String fileType) {
        File file = new File("src/data/ServiceAPIConsumedInfo." + fileType);

        try {
            OutputStream outputStream = new FileOutputStream(file);

            String apiURL = "https://api.openweathermap.org/data/2.5/weather?lat=5,53&lon=-73,36&appid=4646c6d5517fb722cdacc1086919ccdc&mode=" + fileType + "&units=metric";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    outputStream.write(inputLine.getBytes());
                }
                in.close();
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readXMLWeatherProperties() {

        String fileName = "src/data/ServiceAPIConsumedInfo.xml";
        try {

            Document document = readXMLFile(fileName);

            Element rootElement = document.getRootElement();
            Element cityElement = rootElement.getChild("city");

            String city = cityElement.getAttributeValue("name");
            String country = cityElement.getChildText("country");

            Element temperatureElement = rootElement.getChild("temperature");
            String temperature = temperatureElement.getAttributeValue("value");

            Element windElement = rootElement.getChild("wind");
            String windSpeed = windElement.getChild("speed").getAttributeValue("value");
            String windDirection = windElement.getChild("direction").getAttributeValue("name");

            Element precipitationElement = rootElement.getChild("precipitation");
            String precipitationMode = precipitationElement.getAttributeValue("mode");

            view.showMessage("Lugar: " + city + ", " + country);
            view.showMessage("temperatura hoy " + temperature + "°C");
            view.showMessage("direccion y velocidad de viento: " + windSpeed + " metros por segundo m/s " + windDirection);
            view.showMessage("precipitaciones: " + precipitationMode);


        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readJSONWeatherProperties(){
        String fileName = "src/data/ServiceAPIConsumedInfo.json";
        File archivo = new File(fileName);

        try {
            FileInputStream fis = new FileInputStream(archivo);
            JsonReader reader = Json.createReader(fis);

            JsonObject jsonObject = reader.readObject();
            reader.close();

            JsonObject coord = jsonObject.getJsonObject("coord");
            JsonObject main = jsonObject.getJsonObject("main");
            JsonValue weatherValue = jsonObject.getJsonArray("weather").get(0);
            JsonObject weatherObject = (JsonObject) weatherValue;

            String city = jsonObject.getString("name") + ", " + jsonObject.getJsonObject("sys").getString("country");
            String weatherDescription = weatherObject.getString("description");
            double temperatureFeelsLike = main.getJsonNumber("feels_like").doubleValue();
            double maxTemperature = main.getJsonNumber("temp_max").doubleValue();

            view.showMessage("lugar: " + city);
            view.showMessage("clima hoy " + weatherDescription);
            view.showMessage("sensacion térmica " + temperatureFeelsLike);
            view.showMessage("tempeeratura maxima: " + maxTemperature);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void end() {
        System.exit(0);
    }
}

