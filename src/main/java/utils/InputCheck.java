package utils;

import java.util.Scanner;

public class InputCheck {

    public static int readInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public static String readText(Scanner sc, String message) {
        while (true){
            System.out.print(message);
            String input = sc.nextLine().trim();
            if (input.isEmpty()){
                System.out.println("This field can not be empty.");
                continue;
            }
            return input;
        }
    }

    public static int readPositiveInt(Scanner sc, String message) {
        while (true) {
            int value = readInt(sc, message);
            if (value >= 1) {
                return value;
            }
            System.out.println("Please enter a number greater than 0.");
        }
    }

    public static int readChoice(Scanner sc, String message, int min, int max) {
        while (true) {
            int value = readInt(sc, message);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }
}
