package org.pk.library.view;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import org.pk.library.model.Book;

import java.util.Collections;

public class BookController {
    @FXML
    private MainController mainController;
    @FXML
    private JFXTextField findBookField;
    @FXML
    private JFXTextField bookTitleAddField;
    @FXML
    private JFXTextField bookIsbnAddField;
    @FXML
    private JFXTextField bookAuthorAddField;
    @FXML
    private JFXTextField bookPublisherAddField;
    @FXML
    private JFXTextField bookTitleUpdateField;
    @FXML
    private JFXTextField bookIsbnUpdateField;
    @FXML
    private JFXTextField bookAuthorUpdateField;
    @FXML
    public JFXTextField bookPublisherUpdateField;
    @FXML
    private JFXTreeTableView<Book> booksTableView;
    @FXML
    private JFXTreeTableColumn<Book, String> titleCol;
    @FXML
    private JFXTreeTableColumn<Book, String> isbnCol;
    @FXML
    private JFXTreeTableColumn<Book, String> publisherCol;
    @FXML
    private JFXTreeTableColumn<Book, String> authorCol;

    void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Inicializacja kolumn w tabeli z listą książek oraz wyszukiwarka książek
     */
    void initializeBookTableView(){

        titleCol = new JFXTreeTableColumn<>("Tytuł");
        titleCol.prefWidthProperty().bind(booksTableView.widthProperty().divide(4));
        titleCol.setResizable(false);
        titleCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
            if(titleCol.validateValue(param)) return new SimpleStringProperty(param.getValue().getValue().getTitle());
            else return titleCol.getComputedValue(param);
        });

        isbnCol = new JFXTreeTableColumn<>("ISBN");
        isbnCol.prefWidthProperty().bind(booksTableView.widthProperty().divide(4));
        isbnCol.setResizable(false);
        isbnCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
            if(isbnCol.validateValue(param)) return new SimpleStringProperty(param.getValue().getValue().getIsbn());
            else return isbnCol.getComputedValue(param);
        });

        authorCol = new JFXTreeTableColumn<>("Autor");
        authorCol.prefWidthProperty().bind(booksTableView.widthProperty().divide(4));
        authorCol.setResizable(false);
        authorCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
            if(authorCol.validateValue(param)) return new SimpleStringProperty(param.getValue().getValue().getAuthor());
            else return authorCol.getComputedValue(param);
        });

        publisherCol = new JFXTreeTableColumn<>("Wydawnictwo");
        publisherCol.prefWidthProperty().bind(booksTableView.widthProperty().divide(4));
        publisherCol.setResizable(false);
        publisherCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
            if(publisherCol.validateValue(param)) return new SimpleStringProperty(param.getValue().getValue().getPublisher());
            else return publisherCol.getComputedValue(param);
        });

        TreeItem<Book> booksTreeItem = new RecursiveTreeItem<>(FXCollections.observableArrayList(mainController.libraryController.getBooks()), RecursiveTreeObject::getChildren);
        booksTableView.setRoot(booksTreeItem);

        booksTableView.setShowRoot(false);
        booksTableView.setEditable(true);
        booksTableView.getColumns().setAll(titleCol,isbnCol,authorCol,publisherCol);

        findBookField.textProperty().addListener((o, oldVal, newVal) -> booksTableView.setPredicate(bookProp -> {
            final Book book = bookProp.getValue();
            String checkValue = newVal.trim().toLowerCase();
            return (book.getTitle().toLowerCase().contains(checkValue) ||
                    book.getPublisher().toLowerCase().contains(checkValue) ||
                    book.getAuthor().toLowerCase().contains(checkValue) ||
                    book.getIsbn().toLowerCase().contains(checkValue) ||
                    book.getBOOK_ID().toLowerCase().contains(checkValue));
        }));

        booksTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> changeUpdateBookForm());
}

    /**
     * Aktualizacja listy książęk
     */
    private void reloadBookTableView(){
        TreeItem<Book> booksTreeItem = new RecursiveTreeItem<>(FXCollections.observableArrayList(mainController.libraryController.getBooks()), RecursiveTreeObject::getChildren);
        booksTableView.setRoot(booksTreeItem);
    }

    /**
     * Dodawanie książki do struktury danych
     */
    @FXML
    private void addBook() {
        mainController.showInfoDialog(
                "Informacja",
                mainController.libraryController.addBook(
                        bookIsbnAddField.getText(),
                        bookTitleAddField.getText(),
                        bookAuthorAddField.getText(),
                        bookPublisherAddField.getText()
                )
        );
        reloadBookTableView();
    }

    /**
     * Aktualizacja książki w strukturze danych
     */
    @FXML
    private void updateBook() {
        mainController.showInfoDialog(
                "Informacja",
                mainController.libraryController.updateBook(
                        booksTableView.getSelectionModel().getSelectedItem().getValue(),
                        bookIsbnUpdateField.getText(),
                        bookTitleUpdateField.getText(),
                        bookAuthorUpdateField.getText(),
                        bookPublisherUpdateField.getText()
                )
        );
        reloadBookTableView();
    }

    /**
     * Usuwanie książki z struktury danych
     */
    @FXML
    private void deleteBook() {
        final Book bookToRemove = booksTableView.getSelectionModel().getSelectedItem().getValue();
        if(mainController.confirmDeletionBook(bookToRemove.getTitle())) {
            mainController.showInfoDialog(
                    "Informacja",
                    mainController.libraryController.deleteBook(bookToRemove)
            );
        }
        reloadBookTableView();
    }

    /**
     * Aktualizacja danych formularza po wybraniu ksiązki z listy
     */
    @FXML
    private void changeUpdateBookForm() {
        clearUpdateBookForm();
        if(!booksTableView.getSelectionModel().isEmpty()) {
            bookTitleUpdateField.setText(booksTableView.getSelectionModel().getSelectedItem().getValue().getTitle());
            bookPublisherUpdateField.setText(booksTableView.getSelectionModel().getSelectedItem().getValue().getPublisher());
            bookIsbnUpdateField.setText(booksTableView.getSelectionModel().getSelectedItem().getValue().getIsbn());
            bookAuthorUpdateField.setText(booksTableView.getSelectionModel().getSelectedItem().getValue().getAuthor());
        }
    }

    /**
     * Czyszczenie pól formularza dodawania książki
     */
    @FXML
    private void clearAddBookForm() {
        bookTitleAddField.clear();
        bookIsbnAddField.clear();
        bookAuthorAddField.clear();
        bookPublisherAddField.clear();
    }

    /**
     * Czyszczenie pól formularza aktualizacji książki
     */
    @FXML
    private void clearUpdateBookForm() {
        bookTitleUpdateField.clear();
        bookIsbnUpdateField.clear();
        bookAuthorUpdateField.clear();
        bookPublisherUpdateField.clear();
    }
}