# 💰 Smart Expense Tracker

A **desktop-based personal finance management app** built using **JavaFX** and **SQLite**.
This app helps you record, categorize, and analyze your expenses — with smart suggestions, budget alerts, and beautiful visualizations.

---

## 🧠 Overview

The **Smart Expense Tracker** is designed to simplify expense tracking by:
- Automatically categorizing expenses using keyword-based learning.
- Allowing users to set and monitor category-wise budgets.
- Displaying a clear visual breakdown of spending via charts.
- Storing all data locally using SQLite.
- Providing CSV export for backup and analysis.

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 17+ |
| **UI Framework** | JavaFX |
| **Database** | SQLite (via JDBC) |
| **Data Visualization** | JavaFX Charts API (PieChart) |
| **Build Tool** | Maven |
| **Architecture** | MVC (Model-View-Controller) |
| **Styling** | JavaFX CSS |
| **Persistence** | SQLite file (`expenses.db`) |

---

## 🧩 Features

✅ **Add / Delete / View Expenses**  
Easily manage all your expense entries in one place.

✅ **Smart Auto-Categorization**  
Automatically suggests categories (like Food, Transport, etc.) using learned data and predefined keywords.

✅ **Budget Management System**  
Set monthly budget limits for categories and get alerts when you exceed 80% or 100% of your budget.

✅ **Data Visualization**  
Interactive **Pie Chart** showing spending distribution by category.

✅ **CSV Export**  
Export all expenses to a `.csv` file for Excel or Google Sheets.

✅ **Real-Time Filtering**  
Search and filter expenses dynamically using the search bar.

✅ **Local Database (Offline Mode)**
All data is stored locally in `expenses.db` — works fully offline.

---

## 🏗️ Project Structure

com.tracker/<br>
├── MainApp.java # JavaFX main application; handles UI, event logic, and charts<br>
├── AutoCategorizer.java # Smart categorization logic that learns from past expenses<br>
├── BudgetManager.java # Manages category-wise budgets and budget alerts<br>
├── Expense.java # Expense model (POJO) representing each expense record<br>
├── ExpenseDAO.java # Handles all database CRUD operations for expenses<br>
├── DBHelper.java # Provides SQLite database connection<br>
├── styles.css # CSS file for UI styling (colors, fonts, themes)<br>
└── expenses.db # SQLite database file (auto-created on first run)<br>


**Notes:**
- Dialog windows (add expense, budget settings) are handled in `MainApp.java`.
- Learned category data is managed by `AutoCategorizer` in memory.
- CSV export generates `expenses_export.csv` in the project root.
- Follows **MVC architecture**:
  - **Model:** `Expense`, `BudgetManager`, `ExpenseDAO`
  - **View:** JavaFX UI elements in `MainApp`
  - **Controller:** `MainApp` manages user interactions and updates views.

---

