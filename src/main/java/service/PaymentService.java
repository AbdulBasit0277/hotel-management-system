package service;

import configuration.DbConfig;
import model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentService {

    private static Connection con(){
        return DbConfig.getConnection();
    }
    public static Reservation payForReservation(long reservationId, String method) throws SQLException {
        try (Connection connection = con()) {
            double total;

            // read the reservation and check it can be paid
            try (PreparedStatement pst = connection.prepareStatement("select total_amount, status from reservation where id = ?")) {
                pst.setLong(1, reservationId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("No reservation found.");
                    }
                    String status = rs.getString("status");
                    if (status.equals("PAID")) {
                        throw new IllegalArgumentException("This reservation is already paid.");
                    }
                    if (status.equals("CANCELLED")) {
                        throw new IllegalArgumentException("This reservation was cancelled and cannot be paid.");
                    }
                    total = rs.getDouble("total_amount");
                }
            }

            String reference = "PAY" + System.currentTimeMillis();

            try (PreparedStatement pst = connection.prepareStatement("update reservation set status = 'PAID', payment_method = ?, payment_reference = ? where id = ?")) {
                pst.setString(1, method);
                pst.setString(2, reference);
                pst.setLong(3, reservationId);
                pst.executeUpdate();
            }

            Reservation paid = new Reservation();
            paid.setId(reservationId);
            paid.setTotalAmount(total);
            paid.setStatus("PAID");
            paid.setPaymentMethod(method);
            paid.setPaymentReference(reference);
            return paid;
        }

    }
}
