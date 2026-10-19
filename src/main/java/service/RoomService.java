
package service;

import configuration.DbConfig;
import model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomService {

    private static Connection con() {
        return DbConfig.getConnection();
    }

    // method to view available rooms
    public static List<Room> viewAvailableRooms() throws SQLException {
        try (Connection connection = con()) {
            try (PreparedStatement pst = connection.prepareStatement("select * from room where is_available = true order by room_number asc");
                 ResultSet rs = pst.executeQuery()) {
                List<Room> rooms = new ArrayList<>();
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getLong("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setPrice(rs.getDouble("price"));
                    room.setAvailable(rs.getBoolean("is_available"));
                    rooms.add(room);
                }
                return rooms;
            }
        }
    }

    // method to search room by type
    public static List<Room> searchRoomByRoomType(String roomType) throws SQLException {
        try (Connection connection = con();
        PreparedStatement pst = connection.prepareStatement(" select * from room where lower(room_type) = lower(?) and is_available = true order by room_number")){

            pst.setString(1, roomType);
            try (ResultSet rs = pst.executeQuery()) {
                List<Room> rooms = new ArrayList<>();
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getLong("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setPrice(rs.getDouble("price"));
                    room.setAvailable(rs.getBoolean("is_available"));
                    rooms.add(room);
                }
                return rooms;
            }
        }
    }
}


