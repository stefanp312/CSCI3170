import java.util.Set;

public class Console {
    String user = "";
    String[] options = {};

    public void printMenu() {
        System.out.println();
        System.out.println("-----Operations for "+user+"-----");
        for(String option : options) {
            System.out.println(option);
        }
        System.out.println("0. Return to the main menu");
    }

    public int getAnswer(Set<Integer> validOptions) {
        int option;
        try {
            option = Integer.parseInt(main.input.nextLine());
        } catch(Exception e) {
            option = -1;
        }
        while (!(validOptions.contains(option))) {
            System.out.println("[Error]: Wrong Input, Type in again!!!");
            try {
                option = Integer.parseInt(main.input.nextLine());
            } catch(Exception e) {
                option = -1;
            }
        }
        return option;
    }

    public String getAnswer(String question) {
        System.out.println(question);
        String result = main.input.nextLine();
        return result;
    }
}