import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CirculationPanel extends JInternalFrame implements ActionListener {

    // Left Column UI Components (Check-Out Terminal)
    private JTextField coPatronIdTx = new JTextField(15);
    private JTextField coBookIdsTx = new JTextField(15);
    private JButton checkoutBtn = new JButton("Process Check-Out");

    // Right Column UI Components (Check-In Terminal)
    private JTextField ciPatronIdTx = new JTextField(15);
    private JTextField ciBookIdsTx = new JTextField(15);
    private JButton checkinBtn = new JButton("Process Check-In");

    public CirculationPanel() {
        // Circulation Panel initialization
        super("Book Circulation Desk (Batch Processing)", true, true, true, true);
        this.setSize(SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);

        // Splits layout evenly into two functional bays with a 20px gap
        this.setLayout(new GridLayout(1, 2, 20, 0));

        // --- 1. ASSEMBLE LEFT PANEL: CHECK-OUT LOGIC BAY ---
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBorder(BorderFactory.createTitledBorder("Batch Book Check-Out"));

        // Form Fields Container (Stacked vertically)
        JPanel coForm = new JPanel(new GridLayout(4, 1, 5, 5));
        coForm.add(new JLabel("Patron ID:"));
        coForm.add(coPatronIdTx);
        coForm.add(new JLabel("Book IDs (Comma separated):"));
        coForm.add(coBookIdsTx);

        checkoutBtn.setBackground(new Color(46, 204, 113));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(this);

        checkoutPanel.add(coForm, BorderLayout.CENTER);
        checkoutPanel.add(checkoutBtn, BorderLayout.SOUTH);

        // --- 2. ASSEMBLE RIGHT PANEL: CHECK-IN LOGIC BAY ---
        JPanel checkinPanel = new JPanel(new BorderLayout());
        checkinPanel.setBorder(BorderFactory.createTitledBorder("🔄 Batch Book Check-In"));

        // Form panel layout settings matching check-out grid alignments
        JPanel ciForm = new JPanel(new GridLayout(4, 1, 5, 5));
        ciForm.add(new JLabel("Patron ID:"));
        ciForm.add(ciPatronIdTx);
        ciForm.add(new JLabel("Book IDs (Comma separated):"));
        ciForm.add(ciBookIdsTx);

        checkinBtn.setBackground(new Color(52, 152, 219));
        checkinBtn.setForeground(Color.WHITE);
        checkinBtn.addActionListener(this);

        checkinPanel.add(ciForm, BorderLayout.CENTER);
        checkinPanel.add(checkinBtn, BorderLayout.SOUTH);

        // Map split bays down into master container view
        this.add(checkoutPanel);
        this.add(checkinPanel);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == checkoutBtn) {
            handleBatchCheckOut();
        } else if (source == checkinBtn) {
            handleBatchCheckIn();
        }
    }

    // Method to handle batch check out
    private void handleBatchCheckOut() {
        String patronId = coPatronIdTx.getText().trim();
        String rawBookIds = coBookIdsTx.getText().trim();

        if (patronId.isEmpty() || rawBookIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both Patron and Book ID cannot be empty.", "Input Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LibraryUser user = LibraryDatabase.searchUserById(patronId);
        if (!(user instanceof Patron)) {
            JOptionPane.showMessageDialog(this, "Patron record not found matching ID: " + patronId, "Check-Out Cancelled", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Patron patron = (Patron) user;

        // --- ENFORCE STRICT TRANSACTION QUOTA (Max 5 active loans) ---
        if (patron.getActiveLoans().size() >= 5) {
            JOptionPane.showMessageDialog(this,
                    "Check-Out Denied: Patron \"" + patron.getName() + "\" has already reached the maximum allowance limit of 5 borrowed books.",
                    "Quota Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] bookIds = rawBookIds.split(",");
        StringBuilder summaryReport = new StringBuilder("Transaction summary overview for " + patron.getName() + ":\n\n");
        boolean atLeastOneSuccess = false;

        for (String idToken : bookIds) {
            String cleanId = idToken.trim();
            if (cleanId.isEmpty()) {
                continue;
            }

            if (patron.getActiveLoans().size() >= 5) {
                summaryReport.append("\nWarning: Remaining items skipped. Max limit of 5 books strictly reached.");
                break;
            }

            Book book = LibraryDatabase.searchBookById(cleanId);

            if (book == null) {
                summaryReport.append("ID [").append(cleanId).append("]: Book not found.\n");
                continue;
            }

            boolean alreadyBorrowed = false;
            for (Loan activeLoan : patron.getActiveLoans()) {
                if (activeLoan.getBook().getItemId().equals(book.getItemId())) {
                    alreadyBorrowed = true;
                    break;
                }
            }

            if (book.getAmount() <= 0) {
                summaryReport.append("\"").append(book.getTitle()).append("\": Out of stock.\n");
            } else if (alreadyBorrowed) {
                summaryReport.append("\"").append(book.getTitle()).append("\": Already borrowed by this user.\n");
            } else {
                book.setAmount(book.getAmount() - 1);

                String generatedLoanId = "L-" + System.currentTimeMillis() + "-" + cleanId;
                Loan newLoan = new Loan(generatedLoanId, book, patron);

                patron.borrowBook(newLoan);
                summaryReport.append("\"").append(book.getTitle()).append("\": Checked out successfully.\n");
                atLeastOneSuccess = true;
            }
        }

        JOptionPane.showMessageDialog(this, summaryReport.toString(), "Batch Check-Out Report", JOptionPane.INFORMATION_MESSAGE);

        if (atLeastOneSuccess) {
            coBookIdsTx.setText("");
        }
    }

    // Method to handle batch check in
    private void handleBatchCheckIn() {
        String patronId = ciPatronIdTx.getText().trim();
        String rawBookIds = ciBookIdsTx.getText().trim();

        if (patronId.isEmpty() || rawBookIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both Patron and Book ID cannot be empty.", "Input Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LibraryUser user = LibraryDatabase.searchUserById(patronId);
        if (!(user instanceof Patron)) {
            JOptionPane.showMessageDialog(this, "Patron record not found matching ID: " + patronId, "Check-In Cancelled", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Patron patron = (Patron) user;

        String[] bookIds = rawBookIds.split(",");
        StringBuilder summaryReport = new StringBuilder("Return Summary for " + patron.getName() + ":\n\n");
        boolean atLeastOneSuccess = false;

        for (String idToken : bookIds) {
            String cleanId = idToken.trim();
            if (cleanId.isEmpty()) {
                continue;
            }

            Loan matchingLoan = null;
            for (Loan activeLoan : patron.getActiveLoans()) {
                if (activeLoan.getBook().getItemId().equals(cleanId)) {
                    matchingLoan = activeLoan;
                    break;
                }
            }

            if (matchingLoan == null) {
                summaryReport.append("ID [").append(cleanId).append("]: No active borrow under this account.\n");
            } else {
                matchingLoan.processReturn();
                patron.returnBook(matchingLoan);

                Book book = matchingLoan.getBook();
                book.setAmount(book.getAmount() + 1);

                summaryReport.append("\"").append(book.getTitle()).append("\": Returned successful.\n");

                double fineAssigned = matchingLoan.calculateOverdueFine();
                if (fineAssigned > 0) {
                    summaryReport.append("Overdue fine applied: RM ").append(String.format("%.2f", fineAssigned)).append("\n");
                }

                atLeastOneSuccess = true;
            }
        }

        JOptionPane.showMessageDialog(this, summaryReport.toString(), "Batch Check-In Report", JOptionPane.INFORMATION_MESSAGE);

        if (atLeastOneSuccess) {
            ciBookIdsTx.setText("");
        }
    }
}