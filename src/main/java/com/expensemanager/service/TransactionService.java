package main.java.com.expensemanager.service;

import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.dao.TransactionDAO;
import main.java.com.expensemanager.model.Transaction;

import java.util.List;

public class TransactionService {
    private final TransactionDAO transactionDAO;

    public TransactionService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction.getProfileId() <= 0 || transaction.getType() == null || transaction.getDate() == null) {
            System.out.println("Thông tin giao dịch không đầy đủ!");
            return false;
        }

        if (!isProfileExist(transaction.getProfileId())) {
            System.out.println("Hồ sơ không tồn tại. Vui lòng tạo hồ sơ trước khi thêm giao dịch.");
            return false;
        }

        System.out.println("Đã thêm giao dịch thành công.");
        return transactionDAO.addTransaction(transaction);
    }

    public List<Transaction> getTransactionsByProfile(int profileId) {
        if (profileId <= 0) {
            System.out.println("ID hồ sơ không hợp lệ.");
            return null;
        }

        return transactionDAO.getTransactionsByProfile(profileId);
    }

    public boolean deleteTransaction(int id) {
        if (id <= 0) {
            System.out.println("ID giao dịch không hợp lệ.");
            return false;
        }

        return transactionDAO.deleteTransaction(id);
    }

    public boolean updateTransaction(Transaction transaction) {
        if (transaction.getId() <= 0 || transaction.getType() == null || transaction.getDate() == null) {
            System.out.println("Dữ liệu giao dịch không hợp lệ để cập nhật.");
            return false;
        }

        return transactionDAO.updateTransaction(transaction);
    }

    private boolean isProfileExist(int profileId) {
        ProfileDAO profileDAO = new ProfileDAO();
        return profileDAO.isProfileExist(profileId);
    }
}
