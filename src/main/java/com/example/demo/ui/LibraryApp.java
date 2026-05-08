package com.example.demo.ui;

import com.example.demo.DemoApplication;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.application.Application;
import javafx.application.Platform;
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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LibraryApp extends Application {

    // Spring Context and Services
    private ConfigurableApplicationContext springContext;
    private StudentService studentService;
    private BookService bookService;
    private BorrowService borrowService;
    private LibrarianService librarianService;
    private DepartmentService departmentService;

    // Modern UI Color Palette
    private static final String SIDEBAR_COLOR = "#1E293B"; // Slate 800
    private static final String BG_COLOR = "#F8FAFC";      // Slate 50
    private static final String CARD_COLOR = "#FFFFFF";    // White
    private static final String ACCENT_COLOR = "#3B82F6";  // Blue 500
    private static final String SUCCESS_COLOR = "#10B981"; // Emerald 500
    private static final String TEXT_DARK = "#334155";     // Slate 700

    private BorderPane root;
    private StackPane contentArea;

    // View References
    private VBox studentView;
    private VBox bookView;
    private VBox borrowView;
    private VBox departmentView;
    private VBox librarianView;

    // Dynamic Dropdowns (Need to be accessible for refreshing)
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
        primaryStage.setTitle("Library Management System");

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));

        // Initialize Views
        studentView = createStudentView();
        bookView = createBookView();
        borrowView = createBorrowView();
        departmentView = createDepartmentView();
        librarianView = createLibrarianView();

        // Setup Layout
        root.setLeft(createSidebar());
        root.setCenter(contentArea);

        // Set default view
        switchView(borrowView, "Borrow");

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    // ==================== SIDEBAR NAVIGATION ====================
    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_COLOR + ";");
        sidebar.setPadding(new Insets(20, 0, 20, 0));

        // Logo Area
        Label logo = new Label("📚 LibraryOS");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        logo.setTextFill(Color.WHITE);
        logo.setPadding(new Insets(0, 0, 30, 20));

        // Navigation Buttons
        sidebar.getChildren().addAll(
                logo,
                createNavButton("🔄 Borrow Operations", borrowView, "Borrow"),
                createNavButton("📖 Books Database", bookView, "Books"),
                createNavButton("👨‍🎓 Students List", studentView, "Students"),
                createNavButton("🏛️ Departments", departmentView, "Departments"),
                createNavButton("👤 Librarians", librarianView, "Librarians")
        );

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

        // Refresh dropdowns based on view context
        if (viewName.equals("Students") && departmentService != null) {
            studentDeptCombo.getItems().setAll(departmentService.getAllDepartments());
        } else if (viewName.equals("Borrow")) {
            if (studentService != null) borrowStudentCombo.getItems().setAll(studentService.getAllStudents());
            if (bookService != null) borrowBookCombo.getItems().setAll(bookService.getAllBooks());
            if (librarianService != null) borrowLibCombo.getItems().setAll(librarianService.getAllLibrarians());
        }
    }

    // ==================== REUSABLE UI COMPONENTS ====================
    private VBox createCard(String titleText) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 10; -fx-padding: 20;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(10);
        shadow.setOffsetY(2);
        card.setEffect(shadow);

        if (titleText != null) {
            Label title = new Label(titleText);
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            title.setTextFill(Color.web(TEXT_DARK));
            card.getChildren().add(title);
            card.getChildren().add(new Separator());
        }
        return card;
    }

    private Button createActionBtn(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        String baseStyle = "-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: derive(" + colorHex + ", -10%); -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    // ==================== STUDENT VIEW ====================
    private VBox createStudentView() {
        VBox layout = new VBox(20);

        VBox formCard = createCard("Add New Student");
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);

        TextField fNameField = new TextField(); fNameField.setPromptText("First Name");
        TextField lNameField = new TextField(); lNameField.setPromptText("Last Name");
        TextField emailField = new TextField(); emailField.setPromptText("Email Address");
        TextField limitField = new TextField(); limitField.setPromptText("Borrow Limit (e.g., 3)");

        studentDeptCombo = new ComboBox<>();
        studentDeptCombo.setPromptText("Select Department");
        studentDeptCombo.setConverter(new javafx.util.StringConverter<Department>() {
            @Override public String toString(Department d) { return d == null ? "" : d.getDeptName(); }
            @Override public Department fromString(String string) { return null; }
        });

        grid.addRow(0, new Label("First Name:"), fNameField, new Label("Last Name:"), lNameField);
        grid.addRow(1, new Label("Email:"), emailField, new Label("Borrow Limit:"), limitField);
        grid.addRow(2, new Label("Department:"), studentDeptCombo);

        Button addBtn = createActionBtn("Add Student", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Student Directory");
        TableView<Student> table = new TableView<>();
        setupStudentTable(table);
        refreshStudentTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().add(table);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        addBtn.setOnAction(e -> {
            try {
                Student s = new Student();
                s.setFirstname(fNameField.getText());
                s.setLastname(lNameField.getText());
                s.setEmail(emailField.getText());
                s.setMaxBorrowLimit(Integer.parseInt(limitField.getText()));
                s.setDepartment(studentDeptCombo.getValue());
                studentService.addStudent(s);
                refreshStudentTable(table);
                fNameField.clear(); lNameField.clear(); emailField.clear(); limitField.clear(); studentDeptCombo.getSelectionModel().clearSelection();
                showAlert("Success", "Student added successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Error", "Invalid input. Please check fields.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }

    private void setupStudentTable(TableView<Student> table) {
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        TableColumn<Student, String> fNameCol = new TableColumn<>("First Name"); fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        TableColumn<Student, String> lNameCol = new TableColumn<>("Last Name"); lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        TableColumn<Student, String> emailCol = new TableColumn<>("Email"); emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Student, Integer> limitCol = new TableColumn<>("Limit"); limitCol.setCellValueFactory(new PropertyValueFactory<>("maxBorrowLimit"));
        table.getColumns().addAll(idCol, fNameCol, lNameCol, emailCol, limitCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    private void refreshStudentTable(TableView<Student> table) { if(studentService != null) table.getItems().setAll(studentService.getAllStudents()); table.refresh(); }

    // ==================== BOOK VIEW ====================
    private VBox createBookView() {
        VBox layout = new VBox(20);
        VBox formCard = createCard("Catalog New Book");
        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(15);

        TextField titleField = new TextField(); titleField.setPromptText("Book Title");
        TextField authorField = new TextField(); authorField.setPromptText("Author Name");
        TextField catField = new TextField(); catField.setPromptText("Category / Genre");
        TextField copiesField = new TextField(); copiesField.setPromptText("Total Copies");

        grid.addRow(0, new Label("Title:"), titleField, new Label("Author:"), authorField);
        grid.addRow(1, new Label("Category:"), catField, new Label("Total Copies:"), copiesField);

        Button addBtn = createActionBtn("Add Book", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Library Catalog");
        TableView<Book> table = new TableView<>();
        setupBookTable(table); refreshBookTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().add(table);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        addBtn.setOnAction(e -> {
            try {
                Book b = new Book(); b.setTitle(titleField.getText()); b.setAuthor(authorField.getText());
                b.setCategory(catField.getText()); int c = Integer.parseInt(copiesField.getText());
                b.setTotalCopies(c); b.setAvailableCopies(c);
                bookService.addBook(b); refreshBookTable(table);
                titleField.clear(); authorField.clear(); catField.clear(); copiesField.clear();
                showAlert("Success", "Book cataloged successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) { showAlert("Error", "Invalid input for copies.", Alert.AlertType.ERROR); }
        });

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }

    private void setupBookTable(TableView<Book> table) {
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        TableColumn<Book, String> titleCol = new TableColumn<>("Title"); titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author"); authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, Integer> availCol = new TableColumn<>("Available Copies"); availCol.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        table.getColumns().addAll(idCol, titleCol, authorCol, availCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    private void refreshBookTable(TableView<Book> table) { if(bookService != null) table.getItems().setAll(bookService.getAllBooks()); table.refresh(); }

    // ==================== BORROW VIEW ====================
    private VBox createBorrowView() {
        VBox layout = new VBox(20);

        HBox topSection = new HBox(20);

        // Issue Form
        VBox issueCard = createCard("📥 Issue Book");
        HBox.setHgrow(issueCard, Priority.ALWAYS);
        borrowStudentCombo = new ComboBox<>(); borrowStudentCombo.setPromptText("Select Student"); borrowStudentCombo.setMaxWidth(Double.MAX_VALUE);
        borrowBookCombo = new ComboBox<>(); borrowBookCombo.setPromptText("Select Book"); borrowBookCombo.setMaxWidth(Double.MAX_VALUE);
        borrowLibCombo = new ComboBox<>(); borrowLibCombo.setPromptText("Select Librarian"); borrowLibCombo.setMaxWidth(Double.MAX_VALUE);

        setupBorrowConverters();

        Button issueBtn = createActionBtn("Process Borrow", ACCENT_COLOR); issueBtn.setMaxWidth(Double.MAX_VALUE);
        issueCard.getChildren().addAll(borrowStudentCombo, borrowBookCombo, borrowLibCombo, issueBtn);

        // Return Form
        VBox returnCard = createCard("📤 Return Book");
        HBox.setHgrow(returnCard, Priority.ALWAYS);
        TextField borrowIdField = new TextField(); borrowIdField.setPromptText("Enter Transaction ID");
        Button returnBtn = createActionBtn("Confirm Return", SUCCESS_COLOR); returnBtn.setMaxWidth(Double.MAX_VALUE);
        returnCard.getChildren().addAll(new Label("Enter Txn ID from the table below:"), borrowIdField, returnBtn);

        topSection.getChildren().addAll(issueCard, returnCard);

        // Table
        VBox tableCard = createCard("Transaction History");
        TableView<Borrow> table = new TableView<>();
        setupBorrowTable(table); refreshBorrowTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().add(table);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // Actions
        issueBtn.setOnAction(e -> {
            try {
                String result = borrowService.borrowBook(borrowStudentCombo.getValue().getStudentId(), borrowBookCombo.getValue().getBookId(), borrowLibCombo.getValue().getLibrarianId());
                showAlert("Result", result, result.contains("Successfully") ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
                refreshBorrowTable(table);
            } catch (Exception ex) { showAlert("Error", "Please select all fields.", Alert.AlertType.ERROR); }
        });

        returnBtn.setOnAction(e -> {
            try {
                String result = borrowService.returnBook(Integer.parseInt(borrowIdField.getText()));
                showAlert("Result", result, Alert.AlertType.INFORMATION);
                borrowIdField.clear(); refreshBorrowTable(table);
            } catch (Exception ex) { showAlert("Error", "Invalid Transaction ID.", Alert.AlertType.ERROR); }
        });

        layout.getChildren().addAll(topSection, tableCard);
        return layout;
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

    private void setupBorrowTable(TableView<Borrow> table) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        TableColumn<Borrow, String> idCol = new TableColumn<>("Txn ID"); idCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getBorrowId())));
        TableColumn<Borrow, String> studentCol = new TableColumn<>("Student"); studentCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStudent() != null ? cd.getValue().getStudent().getFirstname() : ""));
        TableColumn<Borrow, String> bookCol = new TableColumn<>("Book"); bookCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBook() != null ? cd.getValue().getBook().getTitle() : ""));
        TableColumn<Borrow, String> dueCol = new TableColumn<>("Due Date"); dueCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDueDate() != null ? sdf.format(cd.getValue().getDueDate()) : ""));
        TableColumn<Borrow, String> statusCol = new TableColumn<>("Status"); statusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        table.getColumns().addAll(idCol, studentCol, bookCol, dueCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    private void refreshBorrowTable(TableView<Borrow> table) { if(borrowService != null) table.getItems().setAll(borrowService.getAllBorrows()); table.refresh(); }


    // ==================== DEPARTMENT VIEW ====================
    private VBox createDepartmentView() {
        VBox layout = new VBox(20);
        VBox formCard = createCard("Add Department");
        HBox inputRow = new HBox(15); inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField nameField = new TextField(); nameField.setPromptText("Department Name");
        Button addBtn = createActionBtn("Save", SUCCESS_COLOR);
        inputRow.getChildren().addAll(new Label("Name:"), nameField, addBtn);
        formCard.getChildren().add(inputRow);

        VBox tableCard = createCard("Department List");
        TableView<Department> table = new TableView<>();
        TableColumn<Department, Integer> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("deptId"));
        TableColumn<Department, String> nameCol = new TableColumn<>("Name"); nameCol.setCellValueFactory(new PropertyValueFactory<>("deptName"));
        table.getColumns().addAll(idCol, nameCol); table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshDepartmentTable(table);
        VBox.setVgrow(table, Priority.ALWAYS); tableCard.getChildren().add(table); VBox.setVgrow(tableCard, Priority.ALWAYS);

        addBtn.setOnAction(e -> {
            try { Department d = new Department(); d.setDeptName(nameField.getText()); departmentService.addDepartment(d); refreshDepartmentTable(table); nameField.clear(); }
            catch (Exception ex) { showAlert("Error", "Failed to add.", Alert.AlertType.ERROR); }
        });
        layout.getChildren().addAll(formCard, tableCard); return layout;
    }
    private void refreshDepartmentTable(TableView<Department> table) { if(departmentService!=null) table.getItems().setAll(departmentService.getAllDepartments()); table.refresh(); }

    // ==================== LIBRARIAN VIEW ====================
    private VBox createLibrarianView() {
        VBox layout = new VBox(20);
        VBox formCard = createCard("Register Librarian");
        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(15);
        TextField fName = new TextField(); TextField lName = new TextField(); TextField email = new TextField();
        ComboBox<String> shift = new ComboBox<>(FXCollections.observableArrayList("MORNING", "EVENING", "NIGHT"));
        grid.addRow(0, new Label("First Name:"), fName, new Label("Last Name:"), lName);
        grid.addRow(1, new Label("Email:"), email, new Label("Shift:"), shift);
        Button addBtn = createActionBtn("Add Librarian", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Librarian Roster");
        TableView<Librarian> table = new TableView<>();
        TableColumn<Librarian, Integer> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("librarianId"));
        TableColumn<Librarian, String> fNameCol = new TableColumn<>("First Name"); fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        TableColumn<Librarian, String> shiftCol = new TableColumn<>("Shift"); shiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));
        table.getColumns().addAll(idCol, fNameCol, shiftCol); table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshLibrarianTable(table);
        VBox.setVgrow(table, Priority.ALWAYS); tableCard.getChildren().add(table); VBox.setVgrow(tableCard, Priority.ALWAYS);

        addBtn.setOnAction(e -> {
            try { Librarian l = new Librarian(); l.setFirstname(fName.getText()); l.setLastname(lName.getText()); l.setEmail(email.getText()); l.setShift(shift.getValue()); librarianService.addLibrarian(l); refreshLibrarianTable(table); fName.clear(); lName.clear(); email.clear(); }
            catch (Exception ex) { showAlert("Error", "Failed to add.", Alert.AlertType.ERROR); }
        });
        layout.getChildren().addAll(formCard, tableCard); return layout;
    }
    private void refreshLibrarianTable(TableView<Librarian> table) { if(librarianService!=null) table.getItems().setAll(librarianService.getAllLibrarians()); table.refresh(); }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}