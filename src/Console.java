import java.util.Scanner;
import java.util.Set;

public class Console {
    public void printMenu(String user, String[] options) {
        System.out.println();
        System.out.println("-----Operations for "+user+"-----");
        for(String option : options) {
            System.out.println(option);
        }
        System.out.println("0. Return to the main menu");
    }

    public int getAnswer(Set<Integer> validOptions) {
        Scanner input = new Scanner(System.in);
        int option = Integer.valueOf(input.nextLine());
        while (!(validOptions.contains(option))) {
            System.out.println("[Error]: Wrong Input, Type in again!!!");
            option = Integer.valueOf(input.nextLine());
        }
        input.close();
        return option;
    }

    public String getAnswer(String question) {
        System.out.println(question);
        Scanner input = new Scanner(System.in);
        String result = input.nextLine();
        input.close();
        return result;
    }
}