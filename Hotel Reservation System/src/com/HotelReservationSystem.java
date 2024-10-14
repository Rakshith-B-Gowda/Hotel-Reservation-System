package com;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelReservationSystem {

	private static final String url = "your_database_url";
	private static final String user = "your_user_name";
	private static final String password = "Your_password";

	public static void main(String[] args) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			while (true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner scanner = new Scanner(System.in);
				System.out.println("1. Reserve a Room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservations");
				System.out.println("5. Delete Reservations");
				System.out.println("0. Exit");
				System.out.println("Choose an Option: ");
				int choice = scanner.nextInt();
				switch (choice) {
				case 1:
					reserveRoom(connection , scanner);
					break;
				case 2:
					viewReservations(connection);
					break;
				case 3:
					getRoomNumber(connection , scanner);
					break;
				case 4:
					updateReservations(connection , scanner);
					break;
				case 5:
					deleteReservation(connection , scanner);
					break;
				case 0:
					exit();
					scanner.close();
					return;
				default:
					System.out.println("Invalid choice. Try again.");
					break;
				}
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	
	
	static void reserveRoom(Connection connection, Scanner scanner) {
		System.out.println("Enter the Guest Name: ");
		String guestName = scanner.next();
		scanner.nextLine();
		System.out.println("Enter the Room Number: ");
		int roomNumber = scanner.nextInt();
		System.out.println("Enter the Contact Number: ");
		String contactNumber = scanner.next();

		String sql = "INSERT INTO reservations(guest_name, room_number, contact_number) " +
				"VALUES( '"+guestName+"', "+roomNumber+", '"+contactNumber+"' );";

		try(Statement statement = connection.createStatement()) 
		{
			int affectedRows = statement.executeUpdate(sql);
			if (affectedRows > 0) {
				System.out.println("Reservation Successful.");
			} 
			else {
				System.out.println("Reservation Failed!!");
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	static void viewReservations(Connection connection) throws SQLException{
		String sql = "SELECT * FROM reservations;";

		try(Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {


			System.out.println("Current Reservations:");
			System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
			System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date        |");
			System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

			while (resultSet.next()) {
				int reservationId = resultSet.getInt("reservation_id");
				String guestName = resultSet.getString("guest_name");
				int roomNumber = resultSet.getInt("room_number");
				String contactNumber = resultSet.getString("contact_number");
				String timeStamp = resultSet.getTimestamp("reservation_date").toString();

				System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
						reservationId, guestName, roomNumber, contactNumber, timeStamp);
			}

			System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

		} 
	}

	static void getRoomNumber(Connection connection, Scanner scanner) {
		System.out.println("Enter Reservation ID: ");
		int reservationId = scanner.nextInt();
		System.out.println("Enter the Guest Name: ");
		String guestName = scanner.next();
		
		String sql = "SELECT room_number FROM reservations WHERE reservation_id = " +
						reservationId+" AND guest_name = '"+guestName+"' ;";
		
		try(Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql)) 
		{
			if (resultSet.next()) {
				int roomNumber = resultSet.getInt("room_number");
				System.out.println("Room Number for Reservation ID: "+
						reservationId+" and Guest "+guestName+" is: "+roomNumber);
			} else {
				System.out.println("Reservation not found for the given ID and Guest Name.");
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	static void updateReservations(Connection connection, Scanner scanner) {
		System.out.println("Enter the Reservation ID to Update: ");
		int reservationId = scanner.nextInt();
		scanner.nextLine();
		
		if (!reservationExists(connection , reservationId)) {
			System.out.println("Reservation not Found for given ID.");
			return;
		}
		
		System.out.println("Enter new guest name: ");
		String newGuestName = scanner.nextLine();
		System.out.println("Enter new room number: ");
		int newRoomNumber = scanner.nextInt();
		System.out.println("Enter new contact number: ");
		String newContactNumber = scanner.next();
		
		String sql = "UPDATE reservations "
				+ "SET guest_name = '"+newGuestName+"' , room_number = '"+newRoomNumber+"' , contact_number = '"+newContactNumber+"' "
						+ "WHERE reservation_id = "+reservationId+" ;";
		
		try(Statement statement = connection.createStatement()) 
		{
			int affectedRows = statement.executeUpdate(sql);
			if (affectedRows > 0) {
				System.out.println("Reservation Updated Successsfully.");
			} 
			else {
				System.out.println("Reservation update failed.");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	static void deleteReservation(Connection connection, Scanner scanner) {
		System.out.println("Enter the reservation ID to delete: ");
		int reservationId = scanner.nextInt();
		
		if(!reservationExists(connection , reservationId)) {
			System.out.println("Reservation not found for the given ID.");
			return;
		}
		
		String sql = "DELETE FROM reservations WHERE reservation_id = "+reservationId+" ;";
		
		try(Statement statement = connection.createStatement()) 
		{
			int affectedRows = statement.executeUpdate(sql);
			if (affectedRows > 0) {
				System.out.println("Reservation deleted successfully!");
			} 
			else {
				System.out.println("Reservation deletion failed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean reservationExists(Connection connection, int reservationId) {
		String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "
				+reservationId+" ;";
		
		try(Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql))
		{
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	static void exit() throws InterruptedException{
		System.out.print("Exiting System");
		int i = 5;
		while (i>=0) {
			System.out.print(".");
			Thread.sleep(450);
			i--;
		}
		System.out.println();
		System.out.println("Thank You for using Hotel Reservation System!!!");	
	}
}
