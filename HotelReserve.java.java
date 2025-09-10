package mypackage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotelReserve {

    private static final Map<Integer, Room> availableRooms = new HashMap<>();
    private static final Map<Integer, Reservation> reservations = new HashMap<>();
    private static int reservationCounter = 1;
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JTable roomTable;
    private static DefaultTableModel roomTableModel;
    private static JTable reservationTable;
    private static DefaultTableModel reservationTableModel;

    public static void main(String[] args) {
        initializeRooms();
        SwingUtilities.invokeLater(HotelReserve::createAndShowGUI);

    }

    private static void createAndShowGUI() {
        frame = new JFrame("Hotel Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        mainPanel = new JPanel(new CardLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        createMenuPanel();
        createRoomPanel();
        createReservationPanel();
        createReserveRoomPanel();
        createUpdateReservationPanel();
        createDeleteReservationPanel();

        frame.setVisible(true);
    }

    private static void createMenuPanel() {
        // Create the main menu panel
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Create a gradient background for a more interesting look
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(70, 70, 70);  // Dark grey color
                Color color2 = new Color(40, 40, 40);  // Even darker grey
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };

        menuPanel.setLayout(new GridBagLayout());  // GridBagLayout for proportional resizing

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);  // Adds padding around buttons

        // Create title label with custom RGB background and text color
        JLabel titleLabel = new JLabel("Hotel Reservation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Palatino Linotype", Font.BOLD, 24));
        titleLabel.setOpaque(true);  // Make background visible
        titleLabel.setBackground(new Color(224, 224, 224));  // Set background color using RGB (Black)
        titleLabel.setForeground(new Color(0, 0, 229));  // Set text color using RGB (White)
        titleLabel.setPreferredSize(new Dimension(300, 50));

        // No border around the title label
        menuPanel.add(titleLabel, gbc);

        // Create buttons with colors
        JButton viewRoomsButton = new JButton("View Available Rooms");
        JButton reserveRoomButton = new JButton("Reserve a Room");
        JButton viewReservationsButton = new JButton("View Reservations");
        JButton updateReservationButton = new JButton("Update Reservation");
        JButton deleteReservationButton = new JButton("Delete Reservation");
        JButton exitButton = new JButton("Exit");

        // Set custom colors for the buttons
        viewRoomsButton.setBackground(Color.CYAN);
        reserveRoomButton.setBackground(Color.ORANGE);
        viewReservationsButton.setBackground(Color.GREEN);
        updateReservationButton.setBackground(Color.MAGENTA);
        deleteReservationButton.setBackground(Color.PINK);
        exitButton.setBackground(Color.RED);

        // Add buttons to the panel
        menuPanel.add(viewRoomsButton, gbc);
        menuPanel.add(reserveRoomButton, gbc);
        menuPanel.add(viewReservationsButton, gbc);
        menuPanel.add(updateReservationButton, gbc);
        menuPanel.add(deleteReservationButton, gbc);
        menuPanel.add(exitButton, gbc);

        // Add button actions
        viewRoomsButton.addActionListener(e -> switchPanel("Rooms"));
        reserveRoomButton.addActionListener(e -> switchPanel("ReserveRoom"));
        viewReservationsButton.addActionListener(e -> switchPanel("Reservations"));
        updateReservationButton.addActionListener(e -> switchPanel("UpdateReservation"));
        deleteReservationButton.addActionListener(e -> switchPanel("DeleteReservation"));
        exitButton.addActionListener(e -> System.exit(0));

        mainPanel.add(menuPanel, "Menu");
    }


    // Helper method to create styled buttons
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(70, 130, 180)); // Steel blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2)); // Grey border
        return button;
    }


    private static void createRoomPanel() {
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BorderLayout());

        roomTableModel = new DefaultTableModel(new String[]{"Room Number", "Type", "WiFi", "Aircon", "Cost"}, 0);
        roomTable = new JTable(roomTableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        roomPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> switchPanel("Menu"));
        roomPanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(roomPanel, "Rooms");
    }

    private static void createReservationPanel() {
        JPanel reservationPanel = new JPanel();
        reservationPanel.setLayout(new BorderLayout());

        reservationTableModel = new DefaultTableModel(new String[]{
                "Reservation ID", "Guest Name", "Room Number", "Contact Number", "Check-in Date", "Check-out Date"
        }, 0);
        reservationTable = new JTable(reservationTableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        reservationPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> switchPanel("Menu"));
        reservationPanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(reservationPanel, "Reservations");
    }

    private static void createReserveRoomPanel() {
        JPanel reservePanel = new JPanel();
        reservePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        reservePanel.add(new JLabel("Guest Name:"), gbc);

        gbc.gridx = 1;
        JTextField guestNameField = new JTextField(20);
        reservePanel.add(guestNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        reservePanel.add(new JLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        JTextField contactNumberField = new JTextField(20);
        reservePanel.add(contactNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        reservePanel.add(new JLabel("Room Number:"), gbc);

        gbc.gridx = 1;
        JTextField roomNumberField = new JTextField(20);
        reservePanel.add(roomNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        reservePanel.add(new JLabel("Check-in Date:"), gbc);

        gbc.gridx = 1;
        JSpinner checkInDateSpinner = createDateSpinner();
        reservePanel.add(checkInDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        reservePanel.add(new JLabel("Check-out Date:"), gbc);

        gbc.gridx = 1;
        JSpinner checkOutDateSpinner = createDateSpinner();
        reservePanel.add(checkOutDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton reserveButton = createStyledButton("Reserve Room");
        JButton backButton = createStyledButton("Back");

        reservePanel.add(reserveButton, gbc);

        gbc.gridy = 6;
        reservePanel.add(backButton, gbc);

        reserveButton.addActionListener(e -> reserveRoom(
                guestNameField,
                contactNumberField,
                roomNumberField,
                checkInDateSpinner,
                checkOutDateSpinner
        ));
        backButton.addActionListener(e -> switchPanel("Menu"));

        mainPanel.add(reservePanel, "ReserveRoom");
    }

    private static void createUpdateReservationPanel() {
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        updatePanel.add(new JLabel("Reservation ID:"), gbc);

        gbc.gridx = 1;
        JTextField reservationIdField = new JTextField(20);
        updatePanel.add(reservationIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        updatePanel.add(new JLabel("New Guest Name:"), gbc);

        gbc.gridx = 1;
        JTextField newGuestNameField = new JTextField(20);
        updatePanel.add(newGuestNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        updatePanel.add(new JLabel("New Contact Number:"), gbc);

        gbc.gridx = 1;
        JTextField newContactNumberField = new JTextField(20);
        updatePanel.add(newContactNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton updateButton = createStyledButton("Update Reservation");
        JButton backButton = createStyledButton("Back");

        updatePanel.add(updateButton, gbc);

        gbc.gridy = 4;
        updatePanel.add(backButton, gbc);

        updateButton.addActionListener(e -> updateReservation(
                reservationIdField,
                newGuestNameField,
                newContactNumberField
        ));
        backButton.addActionListener(e -> switchPanel("Menu"));

        mainPanel.add(updatePanel, "UpdateReservation");
    }


    private static void createDeleteReservationPanel() {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        deletePanel.add(new JLabel("Reservation ID:"), gbc);

        gbc.gridx = 1;
        JTextField reservationIdField = new JTextField(20);
        deletePanel.add(reservationIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton deleteButton = createStyledButton("Delete Reservation");
        JButton backButton = createStyledButton("Back");

        deletePanel.add(deleteButton, gbc);

        gbc.gridy = 2;
        deletePanel.add(backButton, gbc);

        deleteButton.addActionListener(e -> deleteReservation(reservationIdField));
        backButton.addActionListener(e -> switchPanel("Menu"));

        mainPanel.add(deletePanel, "DeleteReservation");
    }

    private static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Create a gradient background similar to the menu panel
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(70, 70, 70);  // Dark grey color
                Color color2 = new Color(40, 40, 40);  // Even darker grey
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
    }


    private static JSpinner createDateSpinner() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.SEPTEMBER, 1, 0, 0, 0);
        Date minDate = calendar.getTime();
        calendar.set(2025, Calendar.DECEMBER, 31, 23, 59, 59);
        Date maxDate = calendar.getTime();

        SpinnerDateModel model = new SpinnerDateModel(minDate, minDate, maxDate, Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(model);

        // Create a custom editor for the spinner that hides the arrows
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(editor);

        // Remove the arrow buttons
        JComponent editorComponent = dateSpinner.getEditor();
        if (editorComponent instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editorComponent;
            JTextField textField = dateEditor.getTextField();
            textField.setBorder(BorderFactory.createEmptyBorder()); // Remove border for better appearance
            textField.setFocusable(true); // Ensure it can receive focus
        }

        // Disable the spinner's arrow buttons by making the spinner model uneditable
        dateSpinner.setModel(new SpinnerDateModel(minDate, minDate, maxDate, Calendar.DAY_OF_MONTH) {
            @Override
            public void setValue(Object value) {
                super.setValue(value);
                // Update the editor text field with the new value
                JComponent editorComponent = dateSpinner.getEditor();
                if (editorComponent instanceof JSpinner.DateEditor) {
                    JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editorComponent;
                    JTextField textField = dateEditor.getTextField();
                    textField.setText(new SimpleDateFormat("MM/dd/yyyy").format(value));
                }
            }
        });

        return dateSpinner;
    }

    private static void reserveRoom(JTextField guestNameField, JTextField contactNumberField, JTextField roomNumberField, JSpinner checkInDateSpinner, JSpinner checkOutDateSpinner) {
        Pattern namePattern = Pattern.compile("^[a-zA-Z ]+$"); // Name validation pattern
        Pattern phonePattern = Pattern.compile("^\\d{9,11}$");  // Phone number validation pattern

        String guestName = guestNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String roomNumberStr = roomNumberField.getText().trim();
        Date checkInDate = (Date) checkInDateSpinner.getValue();
        Date checkOutDate = (Date) checkOutDateSpinner.getValue();

        Matcher nameMatcher = namePattern.matcher(guestName);
        Matcher phoneMatcher = phonePattern.matcher(contactNumber);

        try {
            int roomNumber = Integer.parseInt(roomNumberStr);
            Room room = availableRooms.get(roomNumber);

            // Validate that the room exists and dates are valid
            if (nameMatcher.matches() && phoneMatcher.matches() && room != null) {
                // Check if the check-in date is before the check-out date
                if (checkInDate.after(checkOutDate)) {
                    JOptionPane.showMessageDialog(frame, "Check-in date must be before check-out date.");
                    return;
                }

                // Create the reservation
                Reservation reservation = new Reservation(reservationCounter++, guestName, roomNumber, contactNumber, checkInDate, checkOutDate);
                reservations.put(reservation.getReservationId(), reservation);
                availableRooms.remove(roomNumber); // Remove room from availability list

                JOptionPane.showMessageDialog(frame, "Reservation successful!");
                switchPanel("Menu");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid input. Ensure all fields are correctly filled and the room number is valid.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number format. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage()); // Use the exception message
        }
    }



    private static void updateReservation(JTextField reservationIdField, JTextField newGuestNameField, JTextField newContactNumberField) {
        String reservationIdStr = reservationIdField.getText().trim();
        String newGuestName = newGuestNameField.getText().trim();
        String newContactNumber = newContactNumberField.getText().trim();

        Pattern namePattern = Pattern.compile("^[a-zA-Z ]+$");
        Pattern phonePattern = Pattern.compile("^\\d{9,11}$");

        Matcher nameMatcher = namePattern.matcher(newGuestName);
        Matcher phoneMatcher = phonePattern.matcher(newContactNumber);

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            Reservation reservation = reservations.get(reservationId);

            if (reservation != null && nameMatcher.matches() && phoneMatcher.matches()) {
                reservation.setGuestName(newGuestName);
                reservation.setContactNumber(newContactNumber);

                JOptionPane.showMessageDialog(frame, "Reservation updated successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid reservation ID or input data.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid reservation ID format.");
        }
    }


    private static void deleteReservation(JTextField reservationIdField) {
        String reservationIdStr = reservationIdField.getText().trim();

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            Reservation reservation = reservations.remove(reservationId);

            if (reservation != null) {
                Room room = new Room(reservation.getRoomNumber(), "Unknown", false, false, 0);
                availableRooms.put(reservation.getRoomNumber(), room);

                JOptionPane.showMessageDialog(frame, "Reservation deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Reservation not found for the given ID.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid reservation ID format. Please enter a valid number.");
        }
    }


    private static void updateRoomTable() {
        roomTableModel.setRowCount(0);
        for (Room room : availableRooms.values()) {
            roomTableModel.addRow(new Object[]{
                    room.getRoomNumber(),
                    room.getType(),
                    room.hasWifi() ? "Yes" : "No",
                    room.hasAircon() ? "Yes" : "No",
                    room.getCost()
            });
        }
    }

    private static void updateReservationTable() {
        reservationTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        for (Reservation reservation : reservations.values()) {
            reservationTableModel.addRow(new Object[]{
                    reservation.getReservationId(),
                    reservation.getGuestName(),
                    reservation.getRoomNumber(),
                    reservation.getContactNumber(),
                    dateFormat.format(reservation.getCheckInDate()),
                    dateFormat.format(reservation.getCheckOutDate())
            });
        }
    }


    private static void switchPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, panelName);
        if ("Rooms".equals(panelName)) {
            updateRoomTable();
        } else if ("Reservations".equals(panelName)) {
            updateReservationTable();
        }
    }

    private static void initializeRooms() {
        availableRooms.put(101, new Room(101, "Single", true, false, 100));
        availableRooms.put(102, new Room(102, "Single", false, true, 120));
        availableRooms.put(103, new Room(103, "Single", true, true, 150));
        availableRooms.put(104, new Room(104, "Single", true, false, 100));
        availableRooms.put(105, new Room(105, "Single", false, true, 120));
        availableRooms.put(106, new Room(106, "Single", true, true, 150));
        availableRooms.put(107, new Room(107, "Single", true, false, 100));
        availableRooms.put(108, new Room(108, "Single", false, true, 120));
        availableRooms.put(109, new Room(109, "Medium", true, true, 250));
        availableRooms.put(110, new Room(110, "Medium", true, false, 200));
        availableRooms.put(111, new Room(111, "Medium", false, true, 220));
        availableRooms.put(112, new Room(112, "Medium", true, true, 250));
        availableRooms.put(113, new Room(113, "Medium", true, false, 200));
        availableRooms.put(114, new Room(114, "Medium", false, true, 220));
        availableRooms.put(115, new Room(115, "Medium", true, true, 250));
        availableRooms.put(116, new Room(116, "Deluxe", true, false, 300));
        availableRooms.put(117, new Room(117, "Deluxe", false, true, 320));
        availableRooms.put(118, new Room(118, "Deluxe", true, true, 350));
        availableRooms.put(119, new Room(119, "Deluxe", true, false, 300));
        availableRooms.put(120, new Room(120, "Deluxe", false, true, 320));
    }

    static class Room {
        private final int roomNumber;
        private final String type;
        private final boolean hasWifi;
        private final boolean hasAircon;
        private final int cost;

        public Room(int roomNumber, String type, boolean hasWifi, boolean hasAircon, int cost) {
            this.roomNumber = roomNumber;
            this.type = type;
            this.hasWifi = hasWifi;
            this.hasAircon = hasAircon;
            this.cost = cost;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        public String getType() {
            return type;
        }

        public boolean hasWifi() {
            return hasWifi;
        }

        public boolean hasAircon() {
            return hasAircon;
        }

        public int getCost() {
            return cost;
        }
    }


   static class Reservation {
        private final int reservationId;
        private String guestName;
        private int roomNumber;
        private String contactNumber;
        private Date checkInDate;
        private Date checkOutDate;

        public Reservation(int reservationId, String guestName, int roomNumber, String contactNumber, Date checkInDate, Date checkOutDate) {
            if (checkInDate == null || checkOutDate == null) {
                throw new IllegalArgumentException("Check-in and check-out dates cannot be null.");
            }
            if (checkInDate.after(checkOutDate)) {
                throw new IllegalArgumentException("Check-in date must be before check-out date.");
            }
            this.reservationId = reservationId;
            this.guestName = guestName;
            this.roomNumber = roomNumber;
            this.contactNumber = contactNumber;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
        }

        public int getReservationId() {
            return reservationId;
        }

        public String getGuestName() {
            return guestName;
        }

        public void setGuestName(String guestName) {
            this.guestName = guestName;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(int roomNumber) {
            this.roomNumber = roomNumber;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public Date getCheckInDate() {
            return checkInDate;
        }

        public void setCheckInDate(Date checkInDate) {
            if (checkInDate.after(this.checkOutDate)) {
                throw new IllegalArgumentException("Check-in date must be before check-out date.");
            }
            this.checkInDate = checkInDate;
        }

        public Date getCheckOutDate() {
            return checkOutDate;
        }

        public void setCheckOutDate(Date checkOutDate) {
            if (this.checkInDate != null && checkInDate.after(checkOutDate)) {
                throw new IllegalArgumentException("Check-in date must be before check-out date.");
            }
            this.checkOutDate = checkOutDate;
        }
    }
}