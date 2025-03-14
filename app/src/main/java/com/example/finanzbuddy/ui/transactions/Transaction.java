package com.example.finanzbuddy.ui.transactions;

/**
 * Modellklasse für eine Transaktion.
 * Speichert die Informationen einer finanziellen Transaktion.
 */
public class Transaction {
    private String id; // Eindeutige Transaktions-ID
    private String description; // Beschreibung der Transaktion
    private double amount; // Betrag der Transaktion
    private String date; // Datum der Transaktion (Format: "yyyy-MM-dd")

    /**
     * Standardkonstruktor - erforderlich für Firebase-Datenabruf.
     */
    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    /**
     * Konstruktor zum Erstellen einer neuen Transaktion.
     * @param id Eindeutige Transaktions-ID
     * @param description Beschreibung der Transaktion
     * @param amount Betrag der Transaktion
     * @param date Datum der Transaktion
     */
    public Transaction(String id, String description, double amount, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    /**
     * Gibt die Transaktions-ID zurück.
     */
    public String getId() {
        return id;
    }

    /**
     * Setzt eine neue Transaktions-ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gibt die Beschreibung der Transaktion zurück.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gibt den Transaktionsbetrag zurück.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gibt das Datum der Transaktion zurück.
     */
    public String getDate() {
        return date;
    }
}

