package org.example.client_app;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Booking Status UI.
 * This version dynamically parses a simulated server response to populate the UI.
 * pattern [2025-09-13^, tester^, BCA^, testing^, slot2^, pending, .]
 */
public class BookingStatusController implements Initializable {

    // --- FXML Injected Fields ---
    @FXML
    private Label dateLabel; // This field is required to set the date
    @FXML
    private Label totalLabel;
    @FXML
    private Label approvedLabel;
    @FXML
    private Label pendingLabel;
    @FXML
    private Label deniedLabel;
    @FXML
    private VBox bookingsContainer;
    @FXML
    Button back_btn;
    @FXML
    Text anouncer;
    Conncetion_helper conncetion_helper = new Conncetion_helper();
    Connection_Manager connectionManager = conncetion_helper.get_instance();
    String username = Main_Booking_Application.username;
    String branch = Main_Booking_Application.branch;
    String respone;

    /**
     * This method is automatically called after the FXML file has been loaded.
     * It fetches and parses data, then populates the entire UI dynamically.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("loaded......");
        // --- 1. Set the Real-Time Date ---
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        // Ensure the dateLabel from FXML is available before setting it
        if (dateLabel != null) {
            dateLabel.setText(currentDate.format(formatter));
        }

       // sending req
        connectionManager.push_request("status?,"+username+","+branch);
        Task<String> task = new Task<String>() {
            String res = null;
            @Override
            protected String call() throws Exception {
                do{
                    try {
                        res = connectionManager.get_Response();
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        System.out.println("error at reading response..");
                        throw new RuntimeException(e);
                    }
                }while (res == null);
                System.out.println(respone);
                return res;
            }
        };
        task.setOnSucceeded(e ->{
            respone = task.getValue();
            parseAndDisplayBookings(respone);
            anouncer.setText("");
        });
        task.setOnFailed(e->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("some error occured");
            alert.show();
            anouncer.setText("No requests Yet");
        });

        new Thread(task).start();

        // back button

        back_btn.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("hall_booking_page.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Boolean fullscreen = stage.isFullScreen();
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setFullScreen(fullscreen);
                stage.show();
            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * Handles the click event on the back arrow.
     * This method is linked from the FXML file via onMouseClicked="#handleBack".
     * @param event The mouse click event.
     */
    @FXML
    private void handleBack(MouseEvent event) {
        System.out.println("Back arrow clicked!");
        // Get the current window (Stage) and close it.
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Simulates fetching a raw data string from a server.
     * @return A string containing booking data in a structured format.
     */
    private String fetchDataFromServer() {
        // This string mimics a response from a server API.
        // Format: [[date, ^course, ^year, ^user, ^status.], [next_record...]]
        return "[[2025-08-10,^BCA,^3,^Tejas S,^Approved.],[2025-08-12,^BBA,^2,^Alex Ray,^Pending.],[2025-08-15,^MBA,^1,^Priya Sharma,^Pending.],[2025-08-18,^BCA,^3,^John Doe,^Denied.],[2025-08-22,^MBA,^2,^Emily White,^Approved.]]";
    }

    /**
     * Parses the raw server data and updates the UI.
     * @param response The raw string response from the server.
     */
    public void parseAndDisplayBookings(String response) {
        // Initialize counters for the summary.
        int total = 0;
        int approved = 0;
        int pending = 0;
        int denied = 0;

        // Basic validation for the response string.
        if (response == null || response.length() < 4) {
            System.err.println("Server response is empty or invalid.");
            return;
        }

        // Clear any placeholder content.
        bookingsContainer.getChildren().clear();

        // Clean the string by removing the outer brackets "[[" and "]]".
        response = response.replace("[","");
        response = response.replace("]","");
        response = response.replace(",","");

        // Split the string into individual booking records. Example: "2025-08-10,^BCA,^3,^Tejas S,^Approved."
        String[] records = response.split("\\.");

        for (String record : records) {
            // Split each record into its fields.
            String[] fields = record.split("\\^");

            if (fields.length < 5) continue; // Skip malformed records.

            // Clean and extract data, removing extra characters.
            //[[2025-09-13^, tester^, BCA^, testing^, slot2^, pending, .]
            String date = fields[0].trim();
            String course = fields[2].replace("^", "").trim();
            String purpose = fields[3].replace("^", "").trim();
            String slot = fields[4].replace("^", "").trim();
            String status = fields[5].replace("^", "").replace(".", "").trim();

            // Create a styled UI card for the booking and add it to the list.
            HBox card = createBookingCard(slot, course, purpose,date, status);
            bookingsContainer.getChildren().add(card);

            // Update the summary counters based on the status.
            total++;
            switch (status.toLowerCase()) {
                case "approved" -> approved++;
                case "pending" -> pending++;
                case "denied" -> denied++;
            }
        }

        // Update the summary labels at the top of the screen with the final counts.
        totalLabel.setText(String.valueOf(total));
        approvedLabel.setText(String.valueOf(approved));
        pendingLabel.setText(String.valueOf(pending));
        deniedLabel.setText(String.valueOf(denied));
    }

    /**
     * Creates a styled HBox that serves as a visual card for a single booking.
     * This method now creates a more detailed layout.
     *
     * @return A styled HBox representing the booking card.
     */
    private HBox createBookingCard(String date, String course, String purpose, String slot, String status) {
        // Main card container
        HBox card = new HBox();
        card.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Status color bar on the left
        Region statusBar = new Region();
        statusBar.setPrefWidth(5.0);

        // Status label for the bottom right
        Label statusLabel = new Label(status);
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12.0));
        statusLabel.setPadding(new Insets(4, 10, 4, 10));
        statusLabel.setStyle("-fx-background-radius: 15;");

        // Set colors based on status
        switch (status.toLowerCase()) {
            case "approved" -> {
                statusBar.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 8 0 0 8;");
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #4CAF50;");
            }
            case "pending" -> {
                statusBar.setStyle("-fx-background-color: #FF9800; -fx-background-radius: 8 0 0 8;");
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #FF9800;");
            }
            case "rejected" -> {
                statusBar.setStyle("-fx-background-color: #F44336; -fx-background-radius: 8 0 0 8;");
                statusLabel.setStyle(statusLabel.getStyle() + " -fx-background-color: #F44336;");
            }
        }

        // VBox for the main content (to stack elements vertically)
        VBox contentBox = new VBox(8.0);
        contentBox.setPadding(new Insets(12.0));
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        // Top section: User Info and Date
        BorderPane topPane = new BorderPane();

        // Create a modern, circular user icon using SVGPath
        SVGPath userIcon = new SVGPath();
        userIcon.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z");
        userIcon.setFill(Color.GRAY); // Using a lighter, more neutral color

        // Create the user label
        Label userLabel = new Label(slot);
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 16.0));

        // HBox to hold the icon and the name together
        HBox userBox = new HBox(8.0, userIcon, userLabel);
        userBox.setAlignment(Pos.CENTER_LEFT);

        // Create the date label
        Label dateLabel = new Label(date);
        dateLabel.setTextFill(Color.GRAY);

        // Set the user info and date in the top pane
        topPane.setLeft(userBox);
        topPane.setRight(dateLabel);

        // Bottom section: Course/Year and Status
        BorderPane bottomPane = new BorderPane();
        Label courseYearLabel = new Label("Branch: " + course + " | Purpose: " + purpose);
        courseYearLabel.setTextFill(Color.DARKSLATEGRAY);
        bottomPane.setLeft(courseYearLabel);
        bottomPane.setRight(statusLabel);

        // Add all parts to the card
        contentBox.getChildren().addAll(topPane, bottomPane);
        card.getChildren().addAll(statusBar, contentBox);

        return card;
    }
}
