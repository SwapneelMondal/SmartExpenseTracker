package com.tracker;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

public class MainApp extends Application {

    private TableView<Expense> table;
    private ObservableList<Expense> expenses;
    private PieChart pieChart;
    private ExpenseDAO dao;
    private BudgetManager budgetManager;
    private Label totalLabel;

    @Override
    public void start(Stage stage) {
        dao = new ExpenseDAO();
        dao.createTable();
        budgetManager = new BudgetManager();
        expenses = FXCollections.observableArrayList(dao.getAllExpenses());

        // === Table ===
        table = new TableView<>();
        table.setItems(expenses);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Expense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Expense, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        table.getColumns().addAll(descCol, catCol, amtCol);

        // === Chart ===
        pieChart = new PieChart();
        pieChart.setTitle("Category Breakdown");
        updateChart();

        VBox leftPanel = new VBox(15,
                new Label("📊 Category Breakdown"),
                pieChart
        );
        leftPanel.setPadding(new Insets(15));
        leftPanel.setPrefWidth(280);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // === Header ===
        Label headerTitle = new Label("💰 Smart Expense Tracker");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        totalLabel = new Label();
        totalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00e676;");
        updateTotal();

        TextField searchField = new TextField();
        searchField.setPromptText("Search by description...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterExpenses(newVal));

        HBox header = new HBox(20, headerTitle, new Region(), searchField, totalLabel);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        // === Buttons ===
        Button addBtn = new Button("➕ Add Expense");
        Button delBtn = new Button("🗑 Delete Selected");
        Button exportBtn = new Button("📤 Export CSV");
        Button refreshBtn = new Button("🔄 Refresh");
        Button budgetBtn = new Button("💵 Set Budget");

        addBtn.setOnAction(e -> showAddDialog());
        delBtn.setOnAction(e -> deleteSelected());
        exportBtn.setOnAction(e -> exportCSV());
        refreshBtn.setOnAction(e -> refreshData());
        budgetBtn.setOnAction(e -> showBudgetDialog());

        HBox buttons = new HBox(12, addBtn, delBtn, exportBtn, refreshBtn, budgetBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(15));

        // === Layout ===
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(table);
        root.setLeft(leftPanel);
        root.setBottom(buttons);

        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Smart Expense Tracker");
        stage.setScene(scene);
        stage.show();
    }

    private void showAddDialog() {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");

        Label descLabel = new Label("Description:");
        TextField descField = new TextField();
        Label amtLabel = new Label("Amount:");
        TextField amtField = new TextField();
        Label catLabel = new Label("Category:");
        TextField catField = new TextField();

        Button suggestBtn = new Button("Auto Suggest");
        suggestBtn.setOnAction(e -> catField.setText(AutoCategorizer.suggestCategory(descField.getText())));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, descLabel, descField);
        grid.addRow(1, amtLabel, amtField);
        grid.addRow(2, catLabel, catField, suggestBtn);
        grid.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(grid);

        ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == addType) {
                try {
                    return new Expense(0, descField.getText(), catField.getText(),
                            Double.parseDouble(amtField.getText()));
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid input!").show();
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(exp -> {
            dao.insertExpense(exp);
            AutoCategorizer.learn(exp.getDescription(), exp.getCategory(), exp.getAmount());
            refreshData();
            checkBudgetAlert(exp.getCategory());
        });
    }

    private void deleteSelected() {
        Expense selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteExpense(selected.getId());
            refreshData();
        }
    }

    private void refreshData() {
        expenses.setAll(dao.getAllExpenses());
        updateChart();
        updateTotal();
    }

    private void exportCSV() {
        try (FileWriter writer = new FileWriter("expenses_export.csv")) {
            writer.write("ID,Description,Category,Amount\n");
            for (Expense e : expenses) {
                writer.write(e.getId() + "," + e.getDescription() + "," +
                        e.getCategory() + "," + e.getAmount() + "\n");
            }
            new Alert(Alert.AlertType.INFORMATION, "Exported to expenses_export.csv!").show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Export failed!").show();
        }
    }

    private void filterExpenses(String query) {
        if (query == null || query.isEmpty()) {
            expenses.setAll(dao.getAllExpenses());
        } else {
            expenses.setAll(dao.getAllExpenses().stream()
                    .filter(e -> e.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .toList());
        }
        updateChart();
        updateTotal();
    }

    private void updateChart() {
        pieChart.getData().clear();
        dao.getCategoryTotals().forEach((cat, total) ->
                pieChart.getData().add(new PieChart.Data(cat, total))
        );
    }

    private void updateTotal() {
        double sum = expenses.stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText("Total: ₹" + String.format("%.2f", sum));
    }

    private void showBudgetDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Category Budgets");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label infoLabel = new Label("Set monthly budget limits for each category:");
        infoLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(infoLabel);

        String[] categories = {"Food", "Transport", "Utilities", "Entertainment", "Health", "Shopping", "Education", "Other"};

        for (String cat : categories) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label catLabel = new Label(cat + ":");
            catLabel.setPrefWidth(120);

            TextField budgetField = new TextField();
            budgetField.setPromptText("Enter budget");
            double currentBudget = budgetManager.getBudget(cat);
            if (currentBudget > 0) {
                budgetField.setText(String.valueOf(currentBudget));
            }

            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> {
                try {
                    double budget = Double.parseDouble(budgetField.getText());
                    budgetManager.setBudget(cat, budget);
                    new Alert(Alert.AlertType.INFORMATION, cat + " budget set to ₹" + budget).show();
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid amount!").show();
                }
            });

            row.getChildren().addAll(catLabel, budgetField, saveBtn);
            content.getChildren().add(row);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    private void checkBudgetAlert(String category) {
        double budget = budgetManager.getBudget(category);
        if (budget <= 0) return;

        double spent = dao.getCategoryTotals().getOrDefault(category, 0.0);
        double percentage = (spent / budget) * 100;

        if (percentage >= 100) {
            new Alert(Alert.AlertType.WARNING,
                    "⚠️ Budget Exceeded!\n" +
                            category + " budget: ₹" + budget + "\n" +
                            "Spent: ₹" + String.format("%.2f", spent) + " (" + String.format("%.0f", percentage) + "%)").show();
        } else if (percentage >= 80) {
            new Alert(Alert.AlertType.WARNING,
                    "⚠️ Budget Alert!\n" +
                            category + " is at " + String.format("%.0f", percentage) + "% of budget\n" +
                            "Budget: ₹" + budget + " | Spent: ₹" + String.format("%.2f", spent)).show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}