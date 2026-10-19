package utils;

import model.Room;

import java.util.List;

public class Utility {
    public static void printRooms(List<Room> rooms) {
        for (Room room : rooms) {
            System.out.printf("Room Number :   %s%n", room.getRoomNumber());
            System.out.printf("Room Type   :   %s%n", room.getRoomType());
            System.out.printf("Price/night :   %.2f%n", room.getPrice());
            System.out.println();
        }
    }
}
