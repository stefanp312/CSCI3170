import java.sql.Connection;
import java.util.Scanner;

//package com.tutorialspoint;

public class main {
    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        Connection mySQLDB = Database.connectToOracle();
        Admin admin = new Admin(mySQLDB);
        Company company = new Company(mySQLDB);
        Staff staff = new Staff(mySQLDB);
        System.out.println("Welcome to NEAs Exploration Mission Design System!");

        while (true) {
            System.out.println();
            System.out.println("-----Main menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Operations for administrator");
            System.out.println("2. Operations for exploration companies (rental customers)");
            System.out.println("3. Operations for spacecraft rental staff");
            System.out.println("0. Exit this program");
            System.out.print("Enter Your Choice: ");

            String answer = input.nextLine();

            if (answer.equals("1")) {
                admin.menu();
            } else if (answer.equals("2")) {
                company.menu();
            } else if (answer.equals("3")) {
                staff.menu();
            } else if (answer.equals("0")) {
                break;
            } else {
                System.out.println("[Error]: Wrong Input, Type in again!!!");
            }
        }
        input.close();
        System.exit(0);
    }
}
