package financialmanagement.model;

import java.util.List;

public class Report {
    private double totalIncome;
    private double totalExpense;
    private double balance;

    public Report(List<Transaction> transactions) {
        this.totalIncome = 0;
        this.totalExpense = 0;
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("income")) {
                totalIncome += t.getAmount();
            } else if (t.getType().equalsIgnoreCase("expense")) {
                totalExpense += t.getAmount();
            }
        }
        this.balance = totalIncome - totalExpense;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getBalance() {
        return balance;
    }
}

