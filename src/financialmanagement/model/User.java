package financialmanagement.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private String profileId;
    private String profileName;
    private LocalDate createdDate;
    private List<Transaction> transactions;

    public User(String profileName) {
        this.profileId = UUID.randomUUID().toString();
        this.profileName = profileName;
        this.createdDate = LocalDate.now();
        this.transactions = new ArrayList<>();
    }

    public String getProfileId() {
        return profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }
}
