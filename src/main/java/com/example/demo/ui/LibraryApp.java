package com.example.demo.ui;

import com.example.demo.DemoApplication;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LibraryApp extends Application {

    private ConfigurableApplicationContext springContext;
    private StudentService studentService;
    private BookService bookService;
    private BorrowService borrowService;
    private LibrarianService librarianService;
    private DepartmentService departmentService;

    private static final String SIDEBAR_COLOR = "#1E293B";
    private static final String BG_COLOR = "#F8FAFC";
    private static final String CARD_COLOR = "#FFFFFF";
    private static final String ACCENT_COLOR = "#3B82F6";
    private static final String SUCCESS_COLOR = "#10B981";
    private static final String DANGER_COLOR = "#EF4444";
    private static final String TEXT_DARK = "#334155";

    private BorderPane root;
    private StackPane contentArea;

    private VBox studentView, bookView, borrowView, departmentView, librarianView;
    private ComboBox<Department> studentDeptCombo;
    private ComboBox<Student> borrowStudentCombo;
    private ComboBox<Book> borrowBookCombo;
    private ComboBox<Librarian> borrowLibCombo;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(DemoApplication.class).run();
        studentService = springContext.getBean(StudentService.class);
        bookService = springContext.getBean(BookService.class);
        borrowService = springContext.getBean(BorrowService.class);
        librarianService = springContext.getBean(LibrarianService.class);
        departmentService = springContext.getBean(DepartmentService.class);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System 2026");
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));

        studentView = createStudentView();
        bookView = createBookView();
        borrowView = createBorrowView();
        departmentView = createDepartmentView();
        librarianView = createLibrarianView();

        root.setLeft(createSidebar());
        root.setCenter(contentArea);
        switchView(borrowView, "Borrow");

        Scene scene = new Scene(root, 1150, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    private boolean isValidInput(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumeric(String str) {
        if (str == null) return false;
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_COLOR + ";");
        sidebar.setPadding(new Insets(20, 0, 20, 0));
        Label logo = new Label("📚 LibraryOS");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);
        logo.setPadding(new Insets(0, 0, 40, 20));
        sidebar.getChildren().addAll(logo,
                createNavButton("🔄    Borrow Operations", borrowView, "Borrow"),
                createNavButton("📖    Books Database", bookView, "Books"),
                createNavButton("👨‍🎓    Students List", studentView, "Students"),
                createNavButton("🏛️ Departments", departmentView, "Departments"),
                createNavButton("👤    Librarians", librarianView, "Librarians"));
        return sidebar;
    }

    private Button createNavButton(String text, Node view, String viewName) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(15, 20, 15, 20));
        btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-cursor: hand;"));
        btn.setOnAction(e -> switchView(view, viewName));
        return btn;
    }

    private void switchView(Node view, String viewName) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
        if (viewName.equals("Students")) {
            studentDeptCombo.getItems().setAll(departmentService.getAllDepartments());
        } else if (viewName.equals("Borrow")) {
            borrowStudentCombo.getItems().setAll(studentService.getAllStudents());
            borrowBookCombo.getItems().setAll(bookService.getAllBooks());
            borrowLibCombo.getItems().setAll(librarianService.getAllLibrarians());
            borrowStudentCombo.getSelectionModel().clearSelection();
            borrowBookCombo.getSelectionModel().clearSelection();
            borrowLibCombo.getSelectionModel().clearSelection();
        }
    }

    private VBox createCard(String titleText) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 12; -fx-padding: 25;");
        DropShadow shadow = new DropShadow(15, Color.rgb(0, 0, 0, 0.08));
        shadow.setOffsetY(4);
        card.setEffect(shadow);
        if (titleText != null) {
            Label title = new Label(titleText);
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
            title.setTextFill(Color.web(TEXT_DARK));
            card.getChildren().addAll(title, new Separator());
        }
        return card;
    }

    private Button createActionBtn(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand;");
        return btn;
    }

    private VBox createStudentView() {
        VBox layout = new VBox(25);

        VBox formCard = createCard("Add New Student");
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);

        TextField fName = new TextField(); fName.setPromptText("First Name");
        TextField lName = new TextField(); lName.setPromptText("Last Name");
        TextField email = new TextField(); email.setPromptText("example@mail.com");
        TextField limit = new TextField(); limit.setPromptText("Max Borrow Limit (e.g. 5)");

        fName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                fName.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });

        lName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                lName.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });

        limit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                limit.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        studentDeptCombo = new ComboBox<>();
        studentDeptCombo.setPromptText("Select Department");
        studentDeptCombo.setMaxWidth(Double.MAX_VALUE);
        studentDeptCombo.setConverter(new javafx.util.StringConverter<Department>() {
            @Override public String toString(Department d) { return d == null ? "" : d.getDeptName(); }
            @Override public Department fromString(String string) { return null; }
        });

        grid.addRow(0, new Label("First Name:"), fName, new Label("Last Name:"), lName);
        grid.addRow(1, new Label("Email:"), email, new Label("Borrow Limit:"), limit);
        grid.addRow(2, new Label("Department:"), studentDeptCombo);

        Button addBtn = createActionBtn("Register Student", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Student Directory");
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(0, 0, 10, 0));

        Button vipBtn = new Button("⭐ VIP Students");
        vipBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        Button allBtn = new Button("Show All");

        filterBar.getChildren().addAll(vipBtn, allBtn);

        TableView<Student> table = new TableView<>();
        setupStudentTable(table);
        refreshStudentTable(table);

        vipBtn.setOnAction(e -> table.getItems().setAll(studentService.getVipStudents(5)));
        allBtn.setOnAction(e -> refreshStudentTable(table));

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(filterBar, table);
        layout.getChildren().addAll(formCard, tableCard);

        addBtn.setOnAction(e -> {
            String firstName = fName.getText().trim();
            String lastName = lName.getText().trim();
            String emailAddr = email.getText().trim();
            String limitStr = limit.getText().trim();
            Department dept = studentDeptCombo.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || emailAddr.isEmpty() || limitStr.isEmpty()) {
                showAlert("Missing Data", "Please fill in all student details.", Alert.AlertType.WARNING);
                return;
            }

            if (!emailAddr.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Invalid Email", "Please enter a valid email format (e.g., name@domain.com).", Alert.AlertType.WARNING);
                return;
            }

            if (dept == null) {
                showAlert("Department Required", "Please assign the student to a department.", Alert.AlertType.WARNING);
                return;
            }

            try {
                int maxLimit = Integer.parseInt(limitStr);
                if (maxLimit <= 0 || maxLimit > 20) {
                    showAlert("Limit Error", "Borrow limit must be between 1 and 20.", Alert.AlertType.WARNING);
                    return;
                }

                Student s = new Student();
                s.setFirstname(firstName);
                s.setLastname(lastName);
                s.setEmail(emailAddr);
                s.setMaxBorrowLimit(maxLimit);
                s.setDepartment(dept);

                studentService.addStudent(s);
                refreshStudentTable(table);

                fName.clear(); lName.clear(); email.clear(); limit.clear();
                studentDeptCombo.getSelectionModel().clearSelection();
                showAlert("Success", "Student registered successfully!", Alert.AlertType.INFORMATION);

            } catch (Exception ex) {
                showAlert("Error", "Could not save student: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        return layout;
    }

    private void setupStudentTable(TableView<Student> table) {
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> fNameCol = new TableColumn<>("First Name");
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));

        TableColumn<Student, String> lNameCol = new TableColumn<>("Last Name");
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));


        TableColumn<Student, Integer> limitCol = new TableColumn<>("Borrow Limit");
        limitCol.setCellValueFactory(new PropertyValueFactory<>("maxBorrowLimit"));


        table.getColumns().setAll(idCol, fNameCol, lNameCol, emailCol, limitCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem update = new MenuItem("Update Name");
            update.setOnAction(e -> showUpdateStudentDialog(row.getItem(), table));
            MenuItem delete = new MenuItem("Delete Student"); delete.setStyle("-fx-text-fill: red;");
            delete.setOnAction(e -> { studentService.deleteStudent(row.getItem().getStudentId()); refreshStudentTable(table); });
            menu.getItems().addAll(update, delete);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
            return row;
        });
    }
    private void showUpdateStudentDialog(Student s, TableView<Student> t) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Student Profile & Borrowing History");
        ButtonType upBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(upBtn, ButtonType.CANCEL);

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setPrefWidth(500);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10); editGrid.setVgap(10);
        TextField fn = new TextField(s.getFirstname());
        TextField ln = new TextField(s.getLastname());
        TextField em = new TextField(s.getEmail());
        editGrid.addRow(0, new Label("First Name:"), fn);
        editGrid.addRow(1, new Label("Last Name:"), ln);
        editGrid.addRow(2, new Label("Email:"), em);

        Label historyTitle = new Label("📚 Borrowing History:");
        historyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");

        TableView<Borrow> historyTable = new TableView<>();

        TableColumn<Borrow, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBook() != null ? cd.getValue().getBook().getTitle() : "N/A"));

        TableColumn<Borrow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd").format(cd.getValue().getBorrowDate())));

        TableColumn<Borrow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));

        historyTable.getColumns().addAll(bookCol, dateCol, statusCol);

        if (s.getBorrowHistory() != null) {
            historyTable.getItems().setAll(s.getBorrowHistory());
        }
        historyTable.setPrefHeight(200);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainLayout.getChildren().addAll(new Label("Update Basic Info:"), editGrid, new Separator(), historyTitle, historyTable);
        d.getDialogPane().setContent(mainLayout);

        d.showAndWait().ifPresent(r -> {
            if (r == upBtn) {
                s.setFirstname(fn.getText());
                s.setLastname(ln.getText());
                s.setEmail(em.getText());
                studentService.updateStudent(s.getStudentId(), s);
                refreshStudentTable(t);
            }
        });
    }

    private void refreshStudentTable(TableView<Student> t) { if(studentService != null) t.getItems().setAll(studentService.getAllStudents()); }


    private VBox createBookView() {
        VBox layout = new VBox(25);

        VBox formCard = createCard("Catalog New Book");
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);

        TextField title = new TextField(); title.setPromptText("Enter Book Title");
        TextField author = new TextField(); author.setPromptText("Enter Author Name");
        TextField cat = new TextField(); cat.setPromptText("e.g. Science, History");
        TextField copies = new TextField(); copies.setPromptText("Number of copies");

        copies.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                copies.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        grid.addRow(0, new Label("Title:"), title, new Label("Author:"), author);
        grid.addRow(1, new Label("Category:"), cat, new Label("Total Copies:"), copies);

        Button addBtn = createActionBtn("Add Book to Library", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Library Inventory Management");
        CheckBox availableOnly = new CheckBox("Display In-Stock Only (Copies > 0)");
        availableOnly.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        TableView<Book> table = new TableView<>();
        setupBookTable(table);
        refreshBookTable(table);

        availableOnly.setOnAction(e -> {
            if(availableOnly.isSelected()) table.getItems().setAll(bookService.getAvailableBooksOnly());
            else refreshBookTable(table);
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(availableOnly, table);
        layout.getChildren().addAll(formCard, tableCard);

        addBtn.setOnAction(e -> {
            if (title.getText().trim().isEmpty() || author.getText().trim().isEmpty() ||
                    cat.getText().trim().isEmpty() || copies.getText().trim().isEmpty()) {

                showAlert("Validation Missing", "All fields must be filled before saving!", Alert.AlertType.WARNING);
                return;
            }

            int numCopies;
            try {
                numCopies = Integer.parseInt(copies.getText());
                if (numCopies <= 0) {
                    showAlert("Invalid Data", "Total copies must be at least 1.", Alert.AlertType.WARNING);
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid whole number for copies.", Alert.AlertType.ERROR);
                return;
            }

            try {
                Book b = new Book();
                b.setTitle(title.getText().trim());
                b.setAuthor(author.getText().trim());
                b.setCategory(cat.getText().trim());
                b.setTotalCopies(numCopies);
                b.setAvailableCopies(numCopies);

                bookService.addBook(b);
                refreshBookTable(table);

                title.clear(); author.clear(); cat.clear(); copies.clear();
                showAlert("Success", "Book has been cataloged successfully!", Alert.AlertType.INFORMATION);

            } catch (Exception ex) {
                showAlert("Database Error", "An error occurred while saving: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        return layout;
    }

    private void setupBookTable(TableView<Book> table) {
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        TableColumn<Book, String> titleCol = new TableColumn<>("Title"); titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author"); authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, Integer> totalCol = new TableColumn<>("Total"); totalCol.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        TableColumn<Book, Integer> availCol = new TableColumn<>("Available"); availCol.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        table.getColumns().setAll(idCol, titleCol, authorCol, totalCol, availCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem up = new MenuItem("Update Inventory"); up.setOnAction(e -> showUpdateBookDialog(row.getItem(), table));
            MenuItem del = new MenuItem("Delete Book"); del.setStyle("-fx-text-fill: red;");
            del.setOnAction(e -> { bookService.deleteBook(row.getItem().getBookId()); refreshBookTable(table); });
            menu.getItems().addAll(up, del);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
            return row;
        });
    }

    private void showUpdateBookDialog(Book b, TableView<Book> t) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Book Details & Circulation History");
        ButtonType upBtn = new ButtonType("Save Copies", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(upBtn, ButtonType.CANCEL);

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setPrefWidth(550);

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10); editGrid.setVgap(10);
        editGrid.addRow(0, new Label("Title:"), new Label(b.getTitle()));
        editGrid.addRow(1, new Label("Author:"), new Label(b.getAuthor()));

        TextField copiesField = new TextField(String.valueOf(b.getTotalCopies()));
        editGrid.addRow(2, new Label("Update Total Copies:"), copiesField);

        Label historyTitle = new Label("📖 Book Circulation History:");
        historyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");

        TableView<Borrow> historyTable = new TableView<>();

        TableColumn<Borrow, String> studentCol = new TableColumn<>("Student Name");
        studentCol.setCellValueFactory(cd -> {
            Student s = cd.getValue().getStudent();
            return new SimpleStringProperty(s != null ? s.getFirstname() + " " + s.getLastname() : "N/A");
        });

        TableColumn<Borrow, String> dateCol = new TableColumn<>("Borrow Date");
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd").format(cd.getValue().getBorrowDate())));

        TableColumn<Borrow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));

        historyTable.getColumns().addAll(studentCol, dateCol, statusCol);

        if (b.getBorrowRecords() != null) {
            historyTable.getItems().setAll(b.getBorrowRecords());
        }
        historyTable.setPrefHeight(200);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainLayout.getChildren().addAll(new Label("Book Information:"), editGrid, new Separator(), historyTitle, historyTable);
        d.getDialogPane().setContent(mainLayout);

        d.showAndWait().ifPresent(r -> {
            if (r == upBtn) {
                try {
                    int newTotal = Integer.parseInt(copiesField.getText());
                    int diff = newTotal - b.getTotalCopies();
                    b.setTotalCopies(newTotal);
                    b.setAvailableCopies(b.getAvailableCopies() + diff);
                    bookService.updateBook(b.getBookId(), b);
                    refreshBookTable(t);
                } catch (Exception ex) {
                    showAlert("Error", "Enter a valid number for copies", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void refreshBookTable(TableView<Book> t) { if(bookService != null) t.getItems().setAll(bookService.getAllBooks()); }

    private VBox createBorrowView() {
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(20));
        HBox topSection = new HBox(20);

        VBox issueCard = createCard("📥 Issue Book Operations");
        issueCard.setSpacing(20);
        HBox.setHgrow(issueCard, Priority.ALWAYS);

        borrowStudentCombo = new ComboBox<>();
        borrowBookCombo = new ComboBox<>();
        borrowLibCombo = new ComboBox<>();

        setupSearchableComboBox(borrowStudentCombo, studentService.getAllStudents(), "student", "Select Student...");
        setupSearchableComboBox(borrowBookCombo, bookService.getAllBooks(), "book", "Select Book...");
        setupSearchableComboBox(borrowLibCombo, librarianService.getAllLibrarians(), "librarian", "Select Librarian...");

        borrowStudentCombo.setMaxWidth(Double.MAX_VALUE);
        borrowBookCombo.setMaxWidth(Double.MAX_VALUE);
        borrowLibCombo.setMaxWidth(Double.MAX_VALUE);

        setupBorrowConverters();

        Button issueBtn = createActionBtn("Confirm & Issue Book", ACCENT_COLOR);
        issueBtn.setMaxWidth(Double.MAX_VALUE);
        issueCard.getChildren().addAll(borrowStudentCombo, borrowBookCombo, borrowLibCombo, issueBtn);

        VBox returnCard = createCard("📤 Return Book System");
        returnCard.setSpacing(20);
        HBox.setHgrow(returnCard, Priority.ALWAYS);
        TextField borrowIdField = new TextField();
        borrowIdField.setPromptText("Enter Transaction ID");
        borrowIdField.setPrefHeight(45);
        Button returnBtn = createActionBtn("Confirm Return", SUCCESS_COLOR);
        returnBtn.setMaxWidth(Double.MAX_VALUE);
        returnCard.getChildren().addAll(borrowIdField, returnBtn);

        topSection.getChildren().addAll(issueCard, returnCard);


        VBox tableCard = createCard("Library Transaction Logs");
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search History (Student, Book, or Librarian)...");
        searchField.setPrefHeight(35);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 0 15;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button overdueBtn = new Button("Overdue");
        Button activeBtn = new Button("🔄 Active");
        Button viewAllBtn = new Button("All");
        HBox filterHeader = new HBox(15, searchField, overdueBtn, activeBtn, viewAllBtn);
        filterHeader.setPadding(new Insets(0, 0, 15, 0));

        TableView<Borrow> table = new TableView<>();
        setupBorrowTable(table);
        refreshBorrowTable(table);

        searchField.textProperty().addListener((obs, old, newVal) -> {
            table.getItems().setAll(borrowService.searchTransactions(newVal));
        });

        overdueBtn.setOnAction(e -> table.getItems().setAll(borrowService.getOverdueLoans()));
        activeBtn.setOnAction(e -> table.getItems().setAll(borrowService.getBorrowsByStatus("BORROWED")));
        viewAllBtn.setOnAction(e -> refreshBorrowTable(table));

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(filterHeader, table);


        issueBtn.setOnAction(e -> {
            if (borrowStudentCombo.getValue() == null || borrowBookCombo.getValue() == null || borrowLibCombo.getValue() == null) {
                showAlert("Input Missing", "Please select valid entries from the lists!", Alert.AlertType.WARNING);
                return;
            }
            try {
                String res = borrowService.borrowBook(
                        borrowStudentCombo.getValue().getStudentId(),
                        borrowBookCombo.getValue().getBookId(),
                        borrowLibCombo.getValue().getLibrarianId()
                );
                showAlert("Transaction Status", res, Alert.AlertType.INFORMATION);
                if (res.toLowerCase().contains("successfully")) {
                    borrowStudentCombo.setValue(null);
                    borrowBookCombo.setValue(null);
                    borrowLibCombo.setValue(null);
                    layout.requestFocus();
                }
                refreshBorrowTable(table);
            } catch (Exception ex) { showAlert("Error", "Action failed", Alert.AlertType.ERROR); }
        });

        returnBtn.setOnAction(e -> {
            try {
                String res = borrowService.returnBook(Integer.parseInt(borrowIdField.getText()));
                showAlert("Status", res, Alert.AlertType.INFORMATION);
                borrowIdField.clear();
                refreshBorrowTable(table);
            } catch (Exception ex) { showAlert("Error", "Invalid ID", Alert.AlertType.ERROR); }
        });

        layout.getChildren().addAll(topSection, tableCard);
        return layout;
    }

    private <T> void setupSearchableComboBox(ComboBox<T> comboBox, List<T> items, String type, String placeholder) {
        if (items == null) return;


        ObservableList<T> originalList = FXCollections.observableArrayList(new ArrayList<>(items));

        comboBox.setItems(originalList);
        comboBox.setEditable(false);
        comboBox.setPromptText(placeholder);

        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getDisplayName(item, type));
                }
            }
        });

        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(placeholder);
                } else {
                    setText(getDisplayName(item, type));
                }
            }
        });
    }

    private String getDisplayName(Object item, String type) {
        if (item == null) return "";
        return switch (type) {
            case "student" -> ((Student) item).getFirstname() + " " + ((Student) item).getLastname();
            case "book" -> ((Book) item).getTitle();
            case "librarian" -> ((Librarian) item).getFirstname() + " " + ((Librarian) item).getLastname();
            default -> item.toString();
        };
    }

    private void setupBorrowConverters() {
        borrowStudentCombo.setConverter(new javafx.util.StringConverter<Student>() {
            @Override public String toString(Student s) { return s == null ? "" : s.getFirstname() + " " + s.getLastname(); }
            @Override public Student fromString(String s) { return null; }
        });
        borrowBookCombo.setConverter(new javafx.util.StringConverter<Book>() {
            @Override public String toString(Book b) { return b == null ? "" : b.getTitle(); }
            @Override public Book fromString(String s) { return null; }
        });
        borrowLibCombo.setConverter(new javafx.util.StringConverter<Librarian>() {
            @Override public String toString(Librarian l) { return l == null ? "" : l.getFirstname(); }
            @Override public Librarian fromString(String s) { return null; }
        });
    }

    private void setupBorrowTable(TableView<Borrow> t) {
        TableColumn<Borrow, String> idCol = new TableColumn<>("Txn ID");
        idCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getBorrowId())));

        TableColumn<Borrow, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(cd -> {
            Student s = cd.getValue().getStudent();
            return new SimpleStringProperty(s != null ? s.getFirstname() + " " + s.getLastname() : "N/A");
        });

        TableColumn<Borrow, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(cd -> {
            Book b = cd.getValue().getBook();
            return new SimpleStringProperty(b != null ? b.getTitle() : "N/A");
        });

        TableColumn<Borrow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cd -> {
            Borrow b = cd.getValue();
            String currentStatus = b.getStatus();

            if ("BORROWED".equalsIgnoreCase(currentStatus)) {
                if (b.getDueDate() != null && b.getDueDate().before(new java.util.Date())) {
                    return new SimpleStringProperty("⚠️ OVERDUE");
                }
                return new SimpleStringProperty("🔄 Borrowed");
            }
            return new SimpleStringProperty("✅ Returned");
        });


        statusCol.setCellFactory(column -> new TableCell<Borrow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Returned")) {
                        setTextFill(Color.web("#10B981"));
                        setStyle("-fx-font-weight: bold;");
                    } else if (item.contains("OVERDUE")) {
                        setTextFill(Color.web("#EF4444"));
                        setStyle("-fx-font-weight: bold;");
                    } else if (item.contains("Borrowed")) {
                        setTextFill(Color.web("#3B82F6"));
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });

        t.getColumns().setAll(idCol, studentCol, bookCol, statusCol);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshBorrowTable(TableView<Borrow> table) {
        try {
            List<Borrow> allBorrows = borrowService.getAllBorrows();
            if (allBorrows == null) allBorrows = new ArrayList<>();

            table.getItems().setAll(new ArrayList<>(allBorrows));
        } catch (Exception e) {
            System.err.println("Error refreshing table: " + e.getMessage());
        }
    }


    private VBox createDepartmentView() {
        VBox layout = new VBox(25);

        VBox formCard = createCard("Manage Departments");
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);

        TextField deptNameField = new TextField();
        deptNameField.setPromptText("e.g., Computer Science, Engineering");
        deptNameField.setPrefWidth(300);

        deptNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                deptNameField.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });

        grid.addRow(0, new Label("Department Name:"), deptNameField);

        Button addBtn = createActionBtn("Create Department", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Existing Departments");

        TableView<Department> table = new TableView<>();

        TableColumn<Department, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("deptId"));
        idCol.setPrefWidth(100);

        TableColumn<Department, String> nameCol = new TableColumn<>("Department Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("deptName"));

        table.getColumns().setAll(idCol, nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<Department> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem update = new MenuItem("Update Name");
            update.setOnAction(e -> showUpdateDepartmentDialog(row.getItem(), table));

            MenuItem delete = new MenuItem("Delete Department");
            delete.setStyle("-fx-text-fill: red;");
            delete.setOnAction(e -> {
                try {
                    departmentService.deleteDepartment(row.getItem().getDeptId());
                    refreshDepartmentTable(table);
                } catch (Exception ex) {
                    showAlert("Cannot Delete", "This department might be assigned to active students.", Alert.AlertType.ERROR);
                }
            });

            menu.getItems().addAll(update, delete);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
            return row;
        });

        refreshDepartmentTable(table);

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().add(table);

        addBtn.setOnAction(e -> {
            String name = deptNameField.getText().trim();

            if (name.isEmpty()) {
                showAlert("Input Error", "Department name cannot be empty!", Alert.AlertType.WARNING);
                return;
            }

            if (name.length() < 2) {
                showAlert("Input Error", "Department name is too short!", Alert.AlertType.WARNING);
                return;
            }

            try {
                Department d = new Department();
                d.setDeptName(name);

                departmentService.addDepartment(d);
                refreshDepartmentTable(table);

                deptNameField.clear();
                showAlert("Success", "Department '" + name + "' added successfully!", Alert.AlertType.INFORMATION);

            } catch (Exception ex) {
                showAlert("Database Error", "This department might already exist or there's a connection issue.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }

    private void showUpdateDepartmentDialog(Department d, TableView<Department> t) {
        TextInputDialog dia = new TextInputDialog(d.getDeptName());
        dia.setTitle("Update Department");
        dia.setHeaderText("Enter new name for department:");
        dia.showAndWait().ifPresent(val -> {
            try {
                if(!val.trim().isEmpty()) {
                    d.setDeptName(val.trim());
                    departmentService.updateDepartment(d.getDeptId(), d);
                    refreshDepartmentTable(t);
                }
            } catch (Exception ex) {
                showAlert("Error", "Could not update department.", Alert.AlertType.ERROR);
            }
        });
    }

    private void refreshDepartmentTable(TableView<Department> t) { if(departmentService!=null) t.getItems().setAll(departmentService.getAllDepartments()); }

    private VBox createLibrarianView() {
        VBox layout = new VBox(25);

        VBox formCard = createCard("Register New Librarian");
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);

        TextField fn = new TextField(); fn.setPromptText("First Name");
        TextField ln = new TextField(); ln.setPromptText("Last Name");

        ComboBox<String> shift = new ComboBox<>(FXCollections.observableArrayList("MORNING", "EVENING", "NIGHT"));
        shift.setPromptText("Select Work Shift");
        shift.setMaxWidth(Double.MAX_VALUE);

        fn.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                fn.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });
        ln.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                ln.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });

        grid.addRow(0, new Label("First Name:"), fn, new Label("Last Name:"), ln);
        grid.addRow(1, new Label("Shift:"), shift);

        Button addBtn = createActionBtn("Add Librarian", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Staff Directory");

        ComboBox<String> filterCombo = new ComboBox<>(FXCollections.observableArrayList("ALL", "MORNING", "EVENING", "NIGHT"));
        filterCombo.setPromptText("Filter by Shift");

        TableView<Librarian> table = new TableView<>();

        TableColumn<Librarian, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("librarianId"));

        TableColumn<Librarian, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstname() + " " + cellData.getValue().getLastname()));

        TableColumn<Librarian, String> shiftCol = new TableColumn<>("Shift");
        shiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));

        table.getColumns().setAll(idCol, nameCol, shiftCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<Librarian> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem update = new MenuItem("Update Details");
            update.setOnAction(e -> showUpdateLibrarianDialog(row.getItem(), table));

            MenuItem delete = new MenuItem("Delete Librarian");
            delete.setStyle("-fx-text-fill: red;");
            delete.setOnAction(e -> {
                try {
                    librarianService.deleteLibrarian(row.getItem().getLibrarianId());
                    refreshLibrarianTable(table);
                } catch (Exception ex) {
                    showAlert("Cannot Delete", "This librarian is linked to existing borrow records.", Alert.AlertType.ERROR);
                }
            });

            menu.getItems().addAll(update, delete);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
            return row;
        });

        filterCombo.setOnAction(e -> {
            if ("ALL".equals(filterCombo.getValue())) {
                refreshLibrarianTable(table);
            } else {
                table.getItems().setAll(librarianService.getLibrariansByShift(filterCombo.getValue()));
            }
        });

        refreshLibrarianTable(table);

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(new Label("Quick Filter:"), filterCombo, table);

        addBtn.setOnAction(e -> {
            String firstName = fn.getText().trim();
            String lastName = ln.getText().trim();
            String selectedShift = shift.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || selectedShift == null) {
                showAlert("Missing Data", "Please enter names and select a shift.", Alert.AlertType.WARNING);
                return;
            }

            try {
                Librarian l = new Librarian();
                l.setFirstname(firstName);
                l.setLastname(lastName);
                l.setShift(selectedShift);

                librarianService.addLibrarian(l);
                refreshLibrarianTable(table);

                fn.clear(); ln.clear(); shift.getSelectionModel().clearSelection();
                showAlert("Success", "Librarian added to the system!", Alert.AlertType.INFORMATION);

            } catch (Exception ex) {
                showAlert("Error", "Could not save librarian: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }


    private void showUpdateLibrarianDialog(Librarian l, TableView<Librarian> t) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Update Librarian");
        ButtonType upBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(upBtn, ButtonType.CANCEL);

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));

        TextField fn = new TextField(l.getFirstname());
        TextField ln = new TextField(l.getLastname());
        ComboBox<String> shiftCombo = new ComboBox<>(FXCollections.observableArrayList("MORNING", "EVENING", "NIGHT"));
        shiftCombo.setValue(l.getShift());

        g.addRow(0, new Label("First Name:"), fn);
        g.addRow(1, new Label("Last Name:"), ln);
        g.addRow(2, new Label("Shift:"), shiftCombo);

        d.getDialogPane().setContent(g);
        d.showAndWait().ifPresent(r -> {
            if (r == upBtn) {
                try {
                    l.setFirstname(fn.getText().trim());
                    l.setLastname(ln.getText().trim());
                    l.setShift(shiftCombo.getValue());
                    librarianService.updateLibrarian(l.getLibrarianId(), l);
                    refreshLibrarianTable(t);
                } catch (Exception ex) {
                    showAlert("Error", "Could not update librarian.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void refreshLibrarianTable(TableView<Librarian> t) { if(librarianService!=null) t.getItems().setAll(librarianService.getAllLibrarians()); }


    private void showAlert(String t, String c, Alert.AlertType type) {
        Alert a = new Alert(type); a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }

    private <T> void makeSearchable(ComboBox<T> comboBox) {
        comboBox.setEditable(true);
        comboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                comboBox.hide();
            } else {
                comboBox.show();
            }
        });
    }

    public static void main(String[] args) { launch(args); }
}