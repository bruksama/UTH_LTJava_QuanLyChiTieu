package main.java.vn.edu.ut.expensemanager.util;

import main.java.vn.edu.ut.expensemanager.model.Transaction;
import main.java.vn.edu.ut.expensemanager.model.Category;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Kiểm tra xem số tiền có hợp lệ không (phải > 0)
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    // Kiểm tra ngày có đúng định dạng (yyyy-MM-dd)
    public static boolean isValidDate(String date) {
        String regex = "^(\\d{4})-(\\d{2})-(\\d{2})$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(date).matches();
    }

    // Kiểm tra mô tả giao dịch có hợp lệ không (không được null hoặc rỗng)
    public static boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty();
    }

    // Kiểm tra loại giao dịch có hợp lệ không (chỉ có thể là 'income' hoặc 'expense')
    public static boolean isValidType(String type) {
        return type != null && (type.equals("income") || type.equals("expense"));
    }

    // Kiểm tra thông tin của giao dịch có hợp lệ không
    public static boolean isValidTransaction(Transaction transaction) {
        return transaction != null &&
                isValidAmount(transaction.getAmount()) &&
                isValidDate(transaction.getDate()) &&
                isValidDescription(transaction.getDescription()) &&
                isValidType(transaction.getType());
    }

    // Kiểm tra tên danh mục có hợp lệ không (không được null hoặc rỗng)
    public static boolean isValidCategoryName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    // Kiểm tra loại danh mục có hợp lệ không (chỉ có thể là 'income' hoặc 'expense')
    public static boolean isValidCategoryType(String type) {
        return type != null && (type.equals("income") || type.equals("expense"));
    }

    // Kiểm tra thông tin của danh mục có hợp lệ không
    public static boolean isValidCategory(Category category) {
        return category != null &&
                isValidCategoryName(category.getName()) &&
                isValidCategoryType(category.getType());
    }
}
