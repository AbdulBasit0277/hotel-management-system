-- Hotel Reservation System: database schema
-- Run this file against an empty PostgreSQL database named hotelDb.

drop table if exists reservation;
drop table if exists guest;
drop table if exists room;

create table room(
    id serial primary key,
    room_number varchar(10) not null unique,
    room_type varchar(50) not null
    check (room_type in ('Standard', 'Deluxe', 'Suite')),
    price decimal(10,2) not null,
    is_available boolean not null default true
);

create table guest(
    id serial primary key,
    name varchar(100) not null,
    phone_number varchar(20) not null,
    email varchar(100)
);

create table reservation(
    id serial primary key,
    room_id int not null references room(id),
    guest_id int not null references guest(id),
    check_in_date date not null,
    check_out_date date not null,
    total_amount decimal(10,2) not null,
    status varchar(20) not null default 'BOOKED'
    check (status in ('BOOKED', 'PAID', 'CANCELLED')),
    created_at timestamp not null default current_timestamp,
    payment_method varchar(20),
    payment_reference varchar(50),
    check (check_out_date > check_in_date)
);

-- Sample rooms
insert into room(room_number, room_type, price, is_available) values
     ('HT-001', 'Standard', 20000, true),
     ('HT-002', 'Standard', 20000, true),
     ('HT-003', 'Standard', 20000, true),
     ('HT-004', 'Deluxe', 45000, true),
     ('HT-005', 'Deluxe', 45000, true),
     ('HT-006', 'Deluxe', 45000, true),
     ('HT-007', 'Suite', 75000, true),
     ('HT-008', 'Suite', 75000, true),
     ('HT-009', 'Suite', 75000, true);