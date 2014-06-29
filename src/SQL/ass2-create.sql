DROP TABLE Room;
DROP TABLE Booking;
DROP TABLE Discount;
DROP TABLE Consumer;
DROP TABLE City;
DROP TABLE RoomType;

CREATE TABLE RoomType (
	room_type_id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	name VARCHAR(30) NOT NULL,
	price INTEGER NOT NULL,
	num_beds INTEGER NOT NULL,
	total_available INTEGER NOT NULL,
	PRIMARY KEY (room_type_id)
	);

CREATE TABLE City (
	city_id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	name VARCHAR(40) NOT NULL,
	PRIMARY KEY (city_id)
	);

CREATE TABLE Consumer (
	consumer_id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	email VARCHAR(100) NOT NULL,
	pin INTEGER NOT NULL,
	url VARCHAR(50) NOT NULL,
	PRIMARY KEY (consumer_id)
	);

CREATE TABLE Discount (
	discount_id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	room_type_id INTEGER NOT NULL,
	start_date DATE NOT NULL,
	end_date DATE NOT NULL, 
	city_id INTEGER NOT NULL,
	reduced_rate FLOAT NOT NULL,
	PRIMARY KEY (discount_id),
   	FOREIGN KEY (room_type_id) references RoomType (room_type_id),
   	FOREIGN KEY (city_id) references City (city_id)
	);

CREATE TABLE Booking (
	booking_id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	start_date DATE NOT NULL,
	end_date DATE NOT NULL,
	room_type_id INTEGER NOT NULL,
	city_id INTEGER NOT NULL,
	extra_bed INTEGER NOT NULL,
	num_rooms INTEGER NOT NULL,
	consumer_id INTEGER NOT NULL,
	discount_id INTEGER,
	PRIMARY KEY (booking_id),
   	FOREIGN KEY (room_type_id) references RoomType (room_type_id),
   	FOREIGN KEY (city_id) references City (city_id),
   	FOREIGN KEY (consumer_id) references Consumer (consumer_id),
   	FOREIGN KEY (discount_id) references Discount (discount_id)
	);

CREATE TABLE Room (
	room_id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	room_type_id INTEGER NOT NULL,
	booking_id INTEGER,
	status VARCHAR(100) NOT NULL,
	city_id INTEGER NOT NULL,
	PRIMARY KEY (room_id),
   	FOREIGN KEY (room_type_id) references RoomType (room_type_id),
   	FOREIGN KEY (booking_id) references Booking (booking_id),
   	FOREIGN KEY (city_id) references City (city_id)
	);
	