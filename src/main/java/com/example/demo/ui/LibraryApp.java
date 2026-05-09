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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.SimpleDateFormat;
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
                createNavButton("🔄 Borrow Operations", borrowView, "Borrow"),
                createNavButton("📖 Books Database", bookView, "Books"),
                createNavButton("👨‍🎓 Students List", studentView, "Students"),
                createNavButton("🏛️ Departments", departmentView, "Departments"),
                createNavButton("👤 Librarians", librarianView, "Librarians"));
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

    // ==================== STUDENT VIEW ====================
    private VBox createStudentView() {
        VBox layout = new VBox(25);
        VBox formCard = createCard("Add New Student");
        GridPane grid = new GridPane(); grid.setHgap(20); grid.setVgap(15);
        TextField fName = new TextField(); fName.setPromptText("First Name");
        TextField lName = new TextField(); lName.setPromptText("Last Name");
        TextField email = new TextField(); email.setPromptText("student@example.com");
        TextField limit = new TextField(); limit.setPromptText("Max Books (e.g. 3)");
        studentDeptCombo = new ComboBox<>(); studentDeptCombo.setPromptText("Select Department");
        studentDeptCombo.setConverter(new javafx.util.StringConverter<Department>() {
            @Override public String toString(Department d) { return d == null ? "" : d.getDeptName(); }
            @Override public Department fromString(String s) { return null; }
        });
        grid.addRow(0, new Label("First Name:"), fName, new Label("Last Name:"), lName);
        grid.addRow(1, new Label("Email:"), email, new Label("Limit:"), limit);
        grid.addRow(2, new Label("Department:"), studentDeptCombo);
        Button addBtn = createActionBtn("Add Student", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Student Directory");
        HBox filterBar = new HBox(15); filterBar.setAlignment(Pos.CENTER_LEFT);
        Button vipBtn = new Button("⭐ VIP Students (Limit > 5)");
        vipBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        Button allBtn = new Button("Refresh All");
        filterBar.getChildren().addAll(vipBtn, allBtn);

        TableView<Student> table = new TableView<>(); setupStudentTable(table);
        refreshStudentTable(table);

        vipBtn.setOnAction(e -> table.getItems().setAll(studentService.getVipStudents(5)));
        allBtn.setOnAction(e -> refreshStudentTable(table));

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(filterBar, table);
        layout.getChildren().addAll(formCard, tableCard);
        addBtn.setOnAction(e -> {
            try {
                Student s = new Student(); s.setFirstname(fName.getText()); s.setLastname(lName.getText());
                s.setEmail(email.getText()); s.setMaxBorrowLimit(Integer.parseInt(limit.getText()));
                s.setDepartment(studentDeptCombo.getValue()); studentService.addStudent(s);
                refreshStudentTable(table); fName.clear(); lName.clear(); email.clear(); limit.clear();
            } catch (Exception ex) { showAlert("Error", "Check all fields", Alert.AlertType.ERROR); }
        });
        return layout;
    }

    private void setupStudentTable(TableView<Student> table) {
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        TableColumn<Student, String> fNameCol = new TableColumn<>("First Name"); fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        TableColumn<Student, String> lNameCol = new TableColumn<>("Last Name"); lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        TableColumn<Student, String> emailCol = new TableColumn<>("Email"); emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        table.getColumns().setAll(idCol, fNameCol, lNameCol, emailCol);
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
        Dialog<ButtonType> d = new Dialog<>(); d.setTitle("Update Student");
        ButtonType upBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(upBtn, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(20));
        TextField fn = new TextField(s.getFirstname()); TextField ln = new TextField(s.getLastname());
        g.addRow(0, new Label("First:"), fn); g.addRow(1, new Label("Last:"), ln);
        d.getDialogPane().setContent(g);
        d.showAndWait().ifPresent(r -> {
            if (r == upBtn) { s.setFirstname(fn.getText()); s.setLastname(ln.getText());
                studentService.updateStudent(s.getStudentId(), s); refreshStudentTable(t); }
        });
    }

    private void refreshStudentTable(TableView<Student> t) { if(studentService != null) t.getItems().setAll(studentService.getAllStudents()); }

    // ==================== BOOK VIEW ====================
    private VBox createBookView() {
        VBox layout = new VBox(25);
        VBox formCard = createCard("Catalog New Book");
        GridPane grid = new GridPane(); grid.setHgap(20); grid.setVgap(15);
        TextField title = new TextField(); title.setPromptText("Book Title");
        TextField author = new TextField(); author.setPromptText("Author Name");
        TextField cat = new TextField(); cat.setPromptText("Genre");
        TextField copies = new TextField(); copies.setPromptText("Total Copies");
        grid.addRow(0, new Label("Title:"), title, new Label("Author:"), author);
        grid.addRow(1, new Label("Category:"), cat, new Label("Total Copies:"), copies);
        Button addBtn = createActionBtn("Add Book", SUCCESS_COLOR);
        formCard.getChildren().addAll(grid, addBtn);

        VBox tableCard = createCard("Inventory Management");
        CheckBox availableOnly = new CheckBox("Show In-Stock Only");
        availableOnly.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TableView<Book> table = new TableView<>(); setupBookTable(table);
        refreshBookTable(table);
        availableOnly.setOnAction(e -> {
            if(availableOnly.isSelected()) table.getItems().setAll(bookService.getAvailableBooksOnly());
            else refreshBookTable(table);
        });
        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(availableOnly, table);
        layout.getChildren().addAll(formCard, tableCard);

        addBtn.setOnAction(e -> {
            try { Book b = new Book(); b.setTitle(title.getText()); b.setAuthor(author.getText());
                b.setCategory(cat.getText()); int c = Integer.parseInt(copies.getText());
                b.setTotalCopies(c); b.setAvailableCopies(c); bookService.addBook(b);
                refreshBookTable(table); title.clear(); author.clear(); cat.clear(); copies.clear();
            } catch (Exception ex) { showAlert("Error", "Invalid inputs", Alert.AlertType.ERROR); }
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
        TextInputDialog dia = new TextInputDialog(String.valueOf(b.getTotalCopies()));
        dia.setTitle("Inventory Update"); dia.setHeaderText("Set total copies for: " + b.getTitle());
        dia.showAndWait().ifPresent(val -> {
            try { int nt = Integer.parseInt(val); int d = nt - b.getTotalCopies();
                b.setTotalCopies(nt); b.setAvailableCopies(b.getAvailableCopies() + d);
                bookService.updateBook(b.getBookId(), b); refreshBookTable(t);
            } catch (Exception ex) { showAlert("Error", "Enter a valid number", Alert.AlertType.ERROR); }
        });
    }

    private void refreshBookTable(TableView<Book> t) { if(bookService != null) t.getItems().setAll(bookService.getAllBooks()); }

    // ==================== BORROW VIEW ====================
    private VBox createBorrowView() {
        VBox layout = new VBox(25);
        HBox topSection = new HBox(20);

        VBox issueCard = createCard("📥 Issue Book");
        HBox.setHgrow(issueCard, Priority.ALWAYS);

        borrowStudentCombo = new ComboBox<>();
        borrowBookCombo = new ComboBox<>();
        borrowLibCombo = new ComboBox<>();

        borrowStudentCombo.setPromptText("-- Select Student Name --");
        borrowBookCombo.setPromptText("-- Select Book Title --");
        borrowLibCombo.setPromptText("-- Select Librarian --");

        borrowStudentCombo.setMaxWidth(Double.MAX_VALUE);
        borrowBookCombo.setMaxWidth(Double.MAX_VALUE);
        borrowLibCombo.setMaxWidth(Double.MAX_VALUE);

        setupBorrowConverters();

        Button issueBtn = createActionBtn("Process Transaction", ACCENT_COLOR);
        issueBtn.setMaxWidth(Double.MAX_VALUE);
        issueCard.getChildren().addAll(borrowStudentCombo, borrowBookCombo, borrowLibCombo, issueBtn);

        VBox returnCard = createCard("📤 Return Book");
        HBox.setHgrow(returnCard, Priority.ALWAYS);

        TextField borrowIdField = new TextField();
        borrowIdField.setPromptText("Enter Transaction ID (e.g., 101)");
        borrowIdField.setPrefHeight(40);

        Button returnBtn = createActionBtn("Confirm Return", SUCCESS_COLOR);
        returnBtn.setMaxWidth(Double.MAX_VALUE);

        Label returnLabel = new Label("Return using Transaction ID:");
        returnLabel.setTextFill(Color.web(TEXT_DARK));

        returnCard.getChildren().addAll(returnLabel, borrowIdField, returnBtn);

        topSection.getChildren().addAll(issueCard, returnCard);

        VBox tableCard = createCard("Transaction History");

        Button overdueBtn = new Button("⚠️ Overdue Only");
        overdueBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");

        Button activeBtn = new Button("🔄 Active Borrows");
        activeBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");

        Button viewAllBtn = new Button("View All History");
        viewAllBtn.setStyle("-fx-padding: 8 15; -fx-cursor: hand;");

        HBox filterBar = new HBox(15, overdueBtn, activeBtn, viewAllBtn);
        filterBar.setPadding(new Insets(0, 0, 15, 0));
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TableView<Borrow> table = new TableView<>();
        setupBorrowTable(table);
        refreshBorrowTable(table);

        overdueBtn.setOnAction(e -> table.getItems().setAll(borrowService.getOverdueLoans()));
        activeBtn.setOnAction(e -> table.getItems().setAll(borrowService.getBorrowsByStatus("BORROWED")));
        viewAllBtn.setOnAction(e -> refreshBorrowTable(table));

        VBox.setVgrow(table, Priority.ALWAYS);
        tableCard.getChildren().addAll(filterBar, table);

        issueBtn.setOnAction(e -> {
            try {
                String res = borrowService.borrowBook(
                        borrowStudentCombo.getValue().getStudentId(),
                        borrowBookCombo.getValue().getBookId(),
                        borrowLibCombo.getValue().getLibrarianId()
                );
                showAlert("System Message", res, Alert.AlertType.INFORMATION);
                refreshBorrowTable(table);
            } catch (Exception ex) {
                showAlert("Selection Error", "Please select a student, a book, and a librarian.", Alert.AlertType.ERROR);
            }
        });

        returnBtn.setOnAction(e -> {
            try {
                String res = borrowService.returnBook(Integer.parseInt(borrowIdField.getText()));
                showAlert("System Message", res, Alert.AlertType.INFORMATION);
                borrowIdField.clear();
                refreshBorrowTable(table);
            } catch (Exception ex) {
                showAlert("Input Error", "Please enter a valid Numeric Transaction ID.", Alert.AlertType.ERROR);
            }
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

    private void setupBorrowTable(TableView<Borrow> t) {
        TableColumn<Borrow, String> id = new TableColumn<>("Txn ID");
        id.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getBorrowId())));

        TableColumn<Borrow, String> st = new TableColumn<>("Student");
        st.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStudent() != null ? cd.getValue().getStudent().getFirstname() : ""));

        TableColumn<Borrow, String> bk = new TableColumn<>("Book");
        bk.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBook() != null ? cd.getValue().getBook().getTitle() : ""));

        TableColumn<Borrow, String> sts = new TableColumn<>("Status");
        sts.setCellValueFactory(cd -> {
            Borrow b = cd.getValue();
            if ("BORROWED".equalsIgnoreCase(b.getStatus())) {
                if (b.getDueDate() != null && b.getDueDate().before(new java.util.Date())) {
                    return new SimpleStringProperty("⚠️ Overdue");
                }
                return new SimpleStringProperty("Borrowed");
            }
            return new SimpleStringProperty("Returned");
        });

        t.getColumns().setAll(id, st, bk, sts);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    private void refreshBorrowTable(TableView<Borrow> t) { if(borrowService != null) t.getItems().setAll(borrowService.getAllBorrows()); }

    // ==================== DEPARTMENT & LIBRARIAN ====================
    private VBox createDepartmentView() {
        VBox layout = new VBox(25); VBox card = createCard("Add Department");
        TextField name = new TextField(); name.setPromptText("Enter Department Name"); Button save = createActionBtn("Save", SUCCESS_COLOR);
        card.getChildren().addAll(new Label("Department Name:"), name, save);
        VBox tableCard = createCard("Existing Departments");
        TableView<Department> t = new TableView<>();
        TableColumn<Department, Integer> idc = new TableColumn<>("ID"); idc.setCellValueFactory(new PropertyValueFactory<>("deptId"));
        TableColumn<Department, String> nc = new TableColumn<>("Name"); nc.setCellValueFactory(new PropertyValueFactory<>("deptName"));
        t.getColumns().addAll(idc, nc); refreshDepartmentTable(t); tableCard.getChildren().add(t);
        save.setOnAction(e -> { Department d = new Department(); d.setDeptName(name.getText()); departmentService.addDepartment(d); refreshDepartmentTable(t); name.clear(); });
        layout.getChildren().addAll(card, tableCard); return layout;
    }
    private void refreshDepartmentTable(TableView<Department> t) { if(departmentService!=null) t.getItems().setAll(departmentService.getAllDepartments()); }

    private VBox createLibrarianView() {
        VBox layout = new VBox(25); VBox card = createCard("Register Staff");
        TextField fn = new TextField(); TextField ln = new TextField();
        ComboBox<String> shift = new ComboBox<>(FXCollections.observableArrayList("MORNING", "EVENING", "NIGHT"));
        shift.setPromptText("Select Shift"); Button add = createActionBtn("Add", SUCCESS_COLOR);
        card.getChildren().addAll(new Label("Name:"), fn, ln, new Label("Work Shift:"), shift, add);

        VBox tableCard = createCard("Staff Directory");
        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList("ALL", "MORNING", "EVENING", "NIGHT"));
        filter.setPromptText("Quick Filter by Shift");
        TableView<Librarian> t = new TableView<>();
        TableColumn<Librarian, String> nc = new TableColumn<>("Name"); nc.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        TableColumn<Librarian, String> sc = new TableColumn<>("Shift"); sc.setCellValueFactory(new PropertyValueFactory<>("shift"));
        t.getColumns().setAll(nc, sc);
        filter.setOnAction(e -> { if("ALL".equals(filter.getValue())) refreshLibrarianTable(t); else t.getItems().setAll(librarianService.getLibrariansByShift(filter.getValue())); });
        refreshLibrarianTable(t); tableCard.getChildren().addAll(filter, t);
        add.setOnAction(e -> { Librarian l = new Librarian(); l.setFirstname(fn.getText()); l.setLastname(ln.getText()); l.setShift(shift.getValue()); librarianService.addLibrarian(l); refreshLibrarianTable(t); });
        layout.getChildren().addAll(card, tableCard); return layout;
    }
    private void refreshLibrarianTable(TableView<Librarian> t) { if(librarianService!=null) t.getItems().setAll(librarianService.getAllLibrarians()); }

    private void showAlert(String t, String c, Alert.AlertType type) {
        Alert a = new Alert(type); a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}