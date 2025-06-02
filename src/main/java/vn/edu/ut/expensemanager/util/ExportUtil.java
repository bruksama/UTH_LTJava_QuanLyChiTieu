package main.java.vn.edu.ut.expensemanager.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import main.java.vn.edu.ut.expensemanager.model.Transaction;

public class ExportUtil {

    // Hàm xuất dữ liệu giao dịch ra tệp CSV
    public static void exportTransactionsToCSV(List<Transaction> transactions, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Viết tiêu đề cột
            writer.append("Date,Type,Amount,Description\n");

            // Viết từng giao dịch vào tệp CSV
            for (Transaction transaction : transactions) {
                writer.append(transaction.getDate())
                        .append(",")
                        .append(transaction.getType())
                        .append(",")
                        .append(String.valueOf(transaction.getAmount()))
                        .append(",")
                        .append(transaction.getDescription())
                        .append("\n");
            }

            System.out.println("Dữ liệu đã được xuất ra tệp: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Có lỗi khi xuất tệp!");
        }
    }
}
