package com.Hotel;

import model.Guest;
import model.Reservation;
import model.Room;
import service.BookingService;
import service.PaymentService;
import service.RoomService;
import utils.InputCheck;
import utils.Utility;

import java.util.List;
import java.util.Scanner;

public  class Menu {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        boolean running = true;
        while (running){

            System.out.println("Enter the action you want to take");
            System.out.println("Press 1 to view the available room");
            System.out.println("Press 2 to search room by type");
            System.out.println("Press 3 to book a room");
            System.out.println("Press 4 to pay for a reservation");
            System.out.println("Press 5 to cancel a reservation");
            System.out.println("Press 6 to view booking details");
            System.out.println("Press 0 to exit");
            System.out.println();


            int action = InputCheck.readInt(sc, "Choose action: ");

            switch (action){
                case 1:
                    try {
                        System.out.println("===== AVAILABLE ROOMS =====");
                        List<Room> availableRooms = RoomService.viewAvailableRooms();

                        if (availableRooms.isEmpty()){
                            System.out.println("No available rooms");
                        }else {
                            Utility.printRooms(availableRooms);
                        }

                    }catch (Exception e){
                        System.out.println("A database error occur while viewing the rooms " + e.getMessage());
                    }

                    break;

                case 2:
                    try {
                        String roomType = InputCheck.readText(sc, "Search the room type(Standard, Deluxe, Suite): ");
                        List<Room> searchRoom  = RoomService.searchRoomByRoomType(roomType);

                        if (searchRoom.isEmpty()){
                            System.out.println("no available room for type " + roomType);
                        }
                        else {

                            Utility.printRooms(searchRoom);
                        }

                    }catch (Exception e){
                        System.out.println("A database error occur while searching: " + e.getMessage());
                    }

                    break;

                case 3:
                    String guestName = InputCheck.readText(sc, "Enter your name: ");
                    String roomNumber = InputCheck.readText(sc, "Enter the room number you want: ");
                    String phoneNumber = InputCheck.readText(sc, "Enter your phone number: ");
                    String email = InputCheck.readText(sc, "Enter your email: ");
                    int numberOfNights = InputCheck.readPositiveInt(sc, "How many nights do you want to spend: ");

                    try {
                        Guest guest = new Guest(0, guestName, phoneNumber, email);
                        Reservation reserve = BookingService.createReservation(guest, roomNumber, numberOfNights);

                        System.out.println();
                        System.out.println("Reservation created!");
                        System.out.println("Reservation ID : " + reserve.getId());
                        System.out.println("Room           : " + roomNumber);
                        System.out.println("Nights         : " + numberOfNights);
                        System.out.printf("Total          : %.2f%n", reserve.getTotalAmount());
                        System.out.println("Status         : " + reserve.getStatus());
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("A database error occurred while booking: " + e.getMessage());
                    }
                    break;

                case 4: {
                    int reservationId = InputCheck.readPositiveInt(sc, "Enter your reservation ID: ");

                    System.out.println("Choose payment method:");
                    System.out.println("1. TRANSFER");
                    System.out.println("2. CARD");
                    System.out.println("3. CASH");
                    int choice = InputCheck.readChoice(sc, "Enter choice: ", 1, 3);

                    String paymentMethod = switch (choice) {
                        case 1 -> "TRANSFER";
                        case 2 -> "CARD";
                        default -> "CASH";
                    };

                    try {
                        Reservation paid = PaymentService.payForReservation(reservationId, paymentMethod);
                        System.out.println();
                        System.out.println("Payment successful!");
                        System.out.println("Reservation ID : " + paid.getId());
                        System.out.printf("Amount paid    : %.2f%n", paid.getTotalAmount());
                        System.out.println("Method         : " + paid.getPaymentMethod());
                        System.out.println("Reference      : " + paid.getPaymentReference());
                        System.out.println("Status         : " + paid.getStatus());
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("A database error occurred while processing payment: " + e.getMessage());
                    }
                    break;
                }

                case 5: {
                    int cancelId = InputCheck.readPositiveInt(sc, "Enter the reservation ID to cancel your reservation: ");
                    try {
                        String previousStatus = BookingService.cancelReservation(cancelId);
                        System.out.println("Reservation " + cancelId + " cancelled successfully. The room is available again.");
                        if (previousStatus.equals("PAID")) {
                            System.out.println("Your payment will be refunded.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("A database error occurred while cancelling: " + e.getMessage());
                    }
                    break;
                }

                case 6: {
                    int reservationId = InputCheck.readPositiveInt(sc, "Enter the reservation ID: ");
                    try {
                        System.out.println(BookingService.viewBookingDetails(reservationId));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("A database error occurred while loading details: " + e.getMessage());
                    }
                    break;
                }
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input");
            }
        }

    }
}
