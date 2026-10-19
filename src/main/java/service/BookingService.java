package service;

import configuration.DbConfig;
import model.Guest;
import model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BookingService {
    private static Connection con() {
        return DbConfig.getConnection();
    }

    // method to createReservation
    public static Reservation createReservation(Guest guest, String roomNumber, int nights) throws SQLException {
        try (Connection connection = con()) {
            connection.setAutoCommit(false);
            try {
                // checking the available room
                long roomId;
                double price;
                try (PreparedStatement pst = connection.prepareStatement("select id, price from room where room_number = ? and is_available = true")) {
                    pst.setString(1, roomNumber);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Room " + roomNumber + " does not exist or is not available, please enter 1 to see the available rooms");
                        }
                        roomId = rs.getLong("id");
                        price = rs.getDouble("price");
                    }
                }

                LocalDate checkIn = LocalDate.now();
                LocalDate checkOut = checkIn.plusDays(nights);
                double total = nights * price;

                // adding users to guest
                long guestId;
                try (PreparedStatement pst = connection.prepareStatement("insert into guest(name, phone_number, email) values (?, ?, ?) returning id")) {
                    pst.setString(1, guest.getName());
                    pst.setString(2, guest.getPhoneNumber());
                    pst.setString(3, guest.getEmail());
                    try (ResultSet rs = pst.executeQuery()) {
                        rs.next();
                        guestId = rs.getLong(1);
                    }
                }

                // adding guest to reservation
                long reservationId;
                try (PreparedStatement pst = connection.prepareStatement(
                        "insert into reservation(room_id, guest_id, check_in_date, check_out_date, total_amount) values (?, ?, ?, ?, ?) returning id")) {
                    pst.setLong(1, roomId);
                    pst.setLong(2, guestId);
                    pst.setDate(3, java.sql.Date.valueOf(checkIn));
                    pst.setDate(4, java.sql.Date.valueOf(checkOut));
                    pst.setDouble(5, total);
                    try (ResultSet rs = pst.executeQuery()) {
                        rs.next();
                        reservationId = rs.getLong(1);
                    }
                }
                // setting the room to not available after reservation
                try (PreparedStatement pst = connection.prepareStatement("update room set is_available = false where id = ?")) {
                    pst.setLong(1, roomId);
                    pst.execute();
                }

                Reservation reservation = new Reservation(reservationId, roomId, guestId, checkIn, checkOut, total, "BOOKED", null);

                connection.commit();
                return reservation;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }

    }
    // method to cancelReservation
    public static String cancelReservation(long reservationId) throws SQLException {
        try (Connection connection = con()) {
            connection.setAutoCommit(false);
            try {
                long roomId;
                String previousStatus;

                try (PreparedStatement pst = connection.prepareStatement("select room_id, status from reservation where id = ?")) {
                    pst.setLong(1, reservationId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("No reservation found.");
                        }
                        roomId = rs.getLong("room_id");
                        previousStatus = rs.getString("status");
                    }
                }

                if (previousStatus.equals("CANCELLED")) {
                    throw new IllegalArgumentException("This reservation is already cancelled.");
                }

                try (PreparedStatement pst = connection.prepareStatement("update reservation set status = 'CANCELLED' where id = ?")) {
                    pst.setLong(1, reservationId);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = connection.prepareStatement("update room set is_available = true where id = ?")) {
                    pst.setLong(1, roomId);
                    pst.executeUpdate();
                }

                connection.commit();
                return previousStatus;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // method to viewBookingDetails
    public static String viewBookingDetails(long reservationId) throws SQLException {
        String historyQuery = "select r.id, g.name, g.phone_number, rm.room_number, rm.room_type, " +
                "r.check_in_date, r.check_out_date, r.total_amount, r.status, " +
                "r.payment_method, r.payment_reference from reservation r " +
                "join guest g on r.guest_id = g.id " +
                "join room rm on r.room_id = rm.id " +
                "where r.id = ?";
        try (Connection connection = con();
             PreparedStatement pst = connection.prepareStatement(historyQuery)) {
            pst.setLong(1, reservationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No reservation found.");
                }
                String method = rs.getString("payment_method");
                String reference = rs.getString("payment_reference");
                return "===== BOOKING DETAILS =====\n"
                        + "Reservation ID : " + rs.getLong("id") + "\n"
                        + "Guest          : " + rs.getString("name") + "\n"
                        + "Phone          : " + rs.getString("phone_number") + "\n"
                        + "Room           : " + rs.getString("room_number") + " (" + rs.getString("room_type") + ")\n"
                        + "Check-in       : " + rs.getDate("check_in_date") + "\n"
                        + "Check-out      : " + rs.getDate("check_out_date") + "\n"
                        + String.format("Total          : %.2f%n", rs.getDouble("total_amount"))
                        + "Status         : " + rs.getString("status") + "\n"
                        + "Payment method : " + (method == null ? "-" : method) + "\n"
                        + "Payment ref    : " + (reference == null ? "-" : reference);
            }
        }
    }
}
