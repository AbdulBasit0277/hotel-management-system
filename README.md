# Hotel Reservation System

A console-based Java application for searching, booking, paying for and cancelling hotel rooms. Data is stored in a PostgreSQL database using JDBC. It was built as **Task 4** of the CodeAlpha Java Programming internship.

## Features

- View all available rooms
- Search available rooms by category (Standard, Deluxe, Suite)
- Book a room (guest details and number of nights, total cost calculated automatically)
- Simulated payment (Transfer, Card or Cash) with a generated payment reference
- Cancel a reservation (the room becomes available again)
- View full booking details by reservation ID
- Input validation: invalid menu choices, empty fields and bad numbers never crash the program

## Concepts Used

- Object-Oriented Programming (model, service and utility classes)
- JDBC with `PreparedStatement` (protects against SQL injection)
- Try-with-resources for connections, statements and result sets
- Database transactions (`commit` and `rollback`) so a booking or cancellation either fully succeeds or changes nothing
- SQL joins across `reservation`, `guest` and `room`
- Exception handling with user-friendly messages

## Project Structure

```
src/main/java/
├── com/Hotel/Menu.java                 # Entry point and console menu
├── configuration/DbConfig.java         # Database connection
├── model/                              # Room, Guest, Reservation
├── service/
│   ├── RoomService.java                # View and search rooms
│   ├── BookingService.java             # Book, cancel, booking details
│   └── PaymentService.java             # Payment simulation
└── utils/
    ├── InputCheck.java                 # Safe, validated input
    └── Utility.java                    # Display helpers
schema.sql                              # Database tables and sample rooms
```

## Database Design

| Table | Purpose |
|-------|---------|
| `room` | Room number, category, price per night, availability |
| `guest` | Name, phone number, email |
| `reservation` | Links a guest to a room, with dates, total, status and payment details |

Reservation status flow:

```
BOOKED --pay--> PAID
   \              /
    -- cancel --> CANCELLED
```

## Setup

**Requirements:** JDK 21, Maven, PostgreSQL.

1. Create a database named `hotelDb`:
   ```sql
   create database "hotelDb";
   ```
2. Connect to `hotelDb` and run `schema.sql`. It creates the tables and inserts 9 sample rooms.
3. Open `src/main/java/configuration/DbConfig.java` and replace `YOUR_PASSWORD` with your own PostgreSQL password.
4. If your PostgreSQL user or port differs from the defaults (`postgres`, `5432`), update `user` and `url` in the same file.
5. Run `Menu.java` from your IDE, or build with Maven and run the compiled `com.Hotel.Menu` class.

## Sample Run

```
Press 1 to view the available room
Press 2 to search room by type
Press 3 to book a room
Press 4 to pay for a reservation
Press 5 to cancel a reservation
Press 6 to view booking details
Press 0 to exit

Choose action: 3
Enter your name: Ada
Enter the room number you want: HT-001
...
Reservation created!
Reservation ID : 1
Room           : HT-001
Nights         : 2
Total          : 40000.00
Status         : BOOKED
```

## Known Limitations

- Bookings always start from today. The guest chooses the number of nights, not the dates.
- Payment is a simulation: no real transaction takes place and no card details are collected.
- Room availability is a simple available/unavailable flag, not date-based.
- Two guests booking the same room at the exact same moment is not handled.
- Each booking creates a new guest record, even for returning guests.

## Possible Improvements

- Date-based availability so rooms can be booked in advance
- Booking history by guest phone number
- Reuse existing guest records
- A GUI or web front end

## Author

Saadudeen Abdulbasit