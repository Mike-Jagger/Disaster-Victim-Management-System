package edu.ucalgary.oop;

import java.util.Scanner;

public class MainReliefWorkerInterface {

    public static void main(String[] args) {
        boolean programOnline = true;

        Scanner scanner = new Scanner(System.in);
        while(programOnline) {
            System.out.println("Select the system to access:");
            System.out.println("1. Location Based Worker");
            System.out.println("2. Central Worker");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            System.out.println();

            switch (choice) {
                case 1:
                    CommandLineDisasterVictimEntry.runEntrySystem(scanner);
                    break;
                case 2:
                    CommandLineDisasterVictimInquiry.runInquirySystem(scanner);
                    break;
                case 3:
                    System.out.println("Exiting the system.");
                    programOnline = false;
                    break;
                default:
                    System.out.println("Invalid choice, please enter 1, 2, or 3.");
                    break;
            }

        }
        System.out.println("\nThank you for using our system.");
        scanner.close(); // Close the resource

    }
}
