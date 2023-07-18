package com.example.libarjava;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystemJavaFX extends Application {
    private Library library;
    private ListView<String> listView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        library = new Library();
        primaryStage.setTitle("Library System");


        listView = new ListView<>();
        Button addBookButton = new Button("Add Book");
        Button issueBookButton = new Button("Issue Book");
        Button returnBookButton = new Button("Return Book");
        Button displayBooksButton = new Button("Display Available Books");


        addBookButton.setOnAction(e -> handleAddBook());
        issueBookButton.setOnAction(e -> handleIssueBook());
        returnBookButton.setOnAction(e -> handleReturnBook());
        displayBooksButton.setOnAction(e -> handleDisplayBooks());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(listView, addBookButton, issueBookButton, returnBookButton, displayBooksButton);
        layout.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleAddBook() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Add Book");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter Book ID:");
        String idString = idDialog.showAndWait().orElse(null);
        if (idString != null) {
            try {
                int id = Integer.parseInt(idString);

                // Check for duplicate ID
                if (library.isBookIdExists(id)) {
                    showAlert("Duplicate ID", "Book with ID " + id + " already exists. Please enter a new ID.");
                    return;
                }

                TextInputDialog titleDialog = new TextInputDialog();
                titleDialog.setTitle("Add Book");
                titleDialog.setHeaderText(null);
                titleDialog.setContentText("Enter Book Title:");
                String title = titleDialog.showAndWait().orElse(null);

                TextInputDialog authorDialog = new TextInputDialog();
                authorDialog.setTitle("Add Book");
                authorDialog.setHeaderText(null);
                authorDialog.setContentText("Enter Book Author:");
                String author = authorDialog.showAndWait().orElse(null);

                TextInputDialog yearDialog = new TextInputDialog();
                yearDialog.setTitle("Add Book");
                yearDialog.setHeaderText(null);
                yearDialog.setContentText("Enter Publication Year:");
                String yearString = yearDialog.showAndWait().orElse(null);

                if (title != null && author != null && yearString != null) {
                    int year = Integer.parseInt(yearString);
                    LocalDateTime publicationTime = LocalDateTime.of(year, 1, 1, 0, 0);
                    library.addBook(new Book(id, title, author, publicationTime));
                    updateListView("Book added successfully: " + title);
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid ID/Year", "Please enter a valid integer for the book ID and publication year.");
            }
        }
    }

    private void handleIssueBook() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Issue Book");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter Book ID to issue:");
        String idString = idDialog.showAndWait().orElse(null);
        if (idString != null) {
            try {
                int id = Integer.parseInt(idString);
                library.issueBook(id);
                updateListView("Issued book with ID: " + id);
            } catch (NumberFormatException e) {
                showAlert("Invalid ID", "Please enter a valid integer for the book ID.");
            }
        }
    }

    private void handleReturnBook() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Return Book");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter Book ID to return:");
        String idString = idDialog.showAndWait().orElse(null);
        if (idString != null) {
            try {
                int id = Integer.parseInt(idString);
                library.returnBook(id);
                updateListView("Returned book with ID: " + id);
            } catch (NumberFormatException e) {
                showAlert("Invalid ID", "Please enter a valid integer for the book ID.");
            }
        }
    }

    private void handleDisplayBooks() {
        List<String> bookDetails = library.getAvailableBooksDetails();
        updateListView("Available Books:");
        updateListView(bookDetails);
    }

    private void updateListView(String message) {
        ObservableList<String> items = listView.getItems();
        items.add(message);
        listView.setItems(items);
    }

    private void updateListView(List<String> messages) {
        ObservableList<String> items = listView.getItems();
        items.addAll(messages);
        listView.setItems(items);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class Book {
        private int id;
        private String title;
        private String author;
        private boolean isAvailable;
        private LocalDateTime publicationTime;

        public Book(int id, String title, String author, LocalDateTime publicationTime) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.publicationTime = publicationTime;
            this.isAvailable = true;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public void setAvailable(boolean available) {
            isAvailable = available;
        }

        public LocalDateTime getPublicationTime() {
            return publicationTime;
        }
    }

    private class Library {
        private List<Book> books;

        public Library() {
            this.books = new ArrayList<>();
        }

        public void addBook(Book book) {
            books.add(book);
        }

        public void issueBook(int id) {
            for (Book book : books) {
                if (book.getId() == id && book.isAvailable()) {
                    book.setAvailable(false);
                    updateListView("Book issued successfully: " + book.getTitle());
                    return;
                }
            }
            updateListView("Book with ID " + id + " is not available for issuing.");
        }

        public void returnBook(int id) {
            for (Book book : books) {
                if (book.getId() == id && !book.isAvailable()) {
                    book.setAvailable(true);
                    updateListView("Book returned successfully: " + book.getTitle());
                    return;
                }
            }
            updateListView("Book with ID " + id + " is not available for returning.");
        }

        public List<String> getAvailableBooksDetails() {
            List<String> bookDetails = new ArrayList<>();
            for (Book book : books) {
                if (book.isAvailable()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String publicationTime = book.getPublicationTime().format(formatter);
                    String details = "ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Publication Year: " + book.getPublicationTime().getYear();
                    bookDetails.add(details);
                }
            }
            return bookDetails;
        }

        public boolean isBookIdExists(int id) {
            for (Book book : books) {
                if (book.getId() == id) {
                    return true;
                }
            }
            return false;
        }
    }
}
