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
        if (transaction.getProfileId() <= 0 || transaction.getType() == null) {
            System.out.println("Thông tin giao dịch không đầy đủ!");
            return false;
        }

        if (!isProfileExist(transaction.getProfileId())) {
            System.out.println("Hồ sơ không tồn tại. Vui lòng tạo hồ sơ trước khi thêm giao dịch.");
            return false;
        }

        boolean success = transactionDAO.addTransaction(transaction);
        if (success) {
            System.out.println("Thêm giao dịch thành công.");
        }
        return success;
    }

    public List<Transaction> getTransactionsByProfile(int profileId) {
        if (profileId <= 0) {
            System.out.println("ID hồ sơ không hợp lệ.");
            return null;
        }

        List<Transaction> list = transactionDAO.getTransactionsByProfile(profileId);
        if (list != null && !list.isEmpty()) {
            System.out.println("Lấy danh sách giao dịch thành công.");
        } else {
            System.out.println("Không có giao dịch nào được tìm thấy.");
        }
        return list;
    }

    public boolean deleteTransaction(int id) {
        if (id <= 0) {
            System.out.println("ID giao dịch không hợp lệ.");
            return false;
        }

        boolean success = transactionDAO.deleteTransaction(id);
        if (success) {
            System.out.println("Xóa giao dịch thành công.");
        }
        return success;
    }

    public boolean updateTransaction(Transaction transaction) {
        if (transaction.getId() <= 0 || transaction.getType() == null) {
            System.out.println("Dữ liệu giao dịch không hợp lệ để cập nhật.");
            return false;
        }

        boolean success = transactionDAO.updateTransaction(transaction);
        if (success) {
            System.out.println("Cập nhật giao dịch thành công.");
        }
        return success;
    }

    private boolean isProfileExist(int profileId) {
        ProfileDAO profileDAO = new ProfileDAO();
        return profileDAO.isProfileExist(profileId);
    }
}
