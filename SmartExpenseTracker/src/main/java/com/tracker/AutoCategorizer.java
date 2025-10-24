package com.tracker;

import java.util.*;

/**
 * AutoCategorizer:
 * Suggests a category (and optionally amount) based on expense description.
 * Learns from past user data to improve future suggestions.
 */
public class AutoCategorizer {

    // Stores learned patterns from user input
    private static final Map<String, String> learnedMap = new HashMap<>(); // desc -> category
    private static final Map<String, List<Double>> categoryAmounts = new HashMap<>(); // category -> past amounts

    // Predefined keyword map for default suggestions
    private static final Map<String, List<String>> KEYWORDS = Map.of(
            "Food", List.of("food", "restaurant", "pizza", "burger", "snack", "canteen", "grocery", "zomato", "swiggy"),
            "Transport", List.of("uber", "bus", "cab", "metro", "train", "fuel", "petrol", "diesel", "taxi"),
            "Utilities", List.of("bill", "rent", "wifi", "electricity", "water", "gas", "maintenance"),
            "Entertainment", List.of("movie", "game", "netflix", "prime", "spotify", "cinema", "recharge"),
            "Health", List.of("doctor", "medicine", "hospital", "pharmacy", "gym", "fitness"),
            "Shopping", List.of("amazon", "flipkart", "shopping", "myntra", "clothes", "electronics"),
            "Education", List.of("book", "tuition", "course", "school", "college", "exam", "fees")
    );

    /**
     * Suggests a category based on the expense description.
     * First checks learned data, then keyword matches, else returns "Other".
     */
    public static String suggestCategory(String desc) {
        if (desc == null || desc.trim().isEmpty()) return "Other";
        String lowerDesc = desc.toLowerCase().trim();

        // 1️⃣ Check learned user data
        for (String key : learnedMap.keySet()) {
            if (lowerDesc.contains(key)) {
                return learnedMap.get(key);
            }
        }

        // 2️⃣ Check keyword map
        for (var entry : KEYWORDS.entrySet()) {
            for (String kw : entry.getValue()) {
                if (lowerDesc.contains(kw)) {
                    return entry.getKey();
                }
            }
        }

        // 3️⃣ Default
        return "Other";
    }

    /**
     * Suggests an average amount based on past similar expenses.
     */
    public static double suggestAmount(String desc) {
        String category = suggestCategory(desc);
        List<Double> amounts = categoryAmounts.get(category);

        if (amounts == null || amounts.isEmpty()) {
            return 0;
        }

        // Return average of previous amounts in that category
        return amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * Learns a new description-category-amount pair.
     * Should be called when the user adds a new expense.
     */
    public static void learn(String desc, String category, double amount) {
        if (desc == null || desc.trim().isEmpty() || category == null) return;

        learnedMap.put(desc.toLowerCase().trim(), category);

        categoryAmounts
                .computeIfAbsent(category, k -> new ArrayList<>())
                .add(amount);
    }

    /**
     * Debug method to print learned data (optional for logging)
     */
    public static void printLearnedData() {
        System.out.println("=== Learned Expense Data ===");
        learnedMap.forEach((k, v) -> System.out.println(k + " → " + v));
        System.out.println("============================");
    }
}
