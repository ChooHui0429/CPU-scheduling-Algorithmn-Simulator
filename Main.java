import java.io.IOException;
import java.util.Scanner;

import util.NonPreSJF;
import util.PreSJF;
import util.RoundRobin;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void mainUI() {
        Boolean validInput = false;

        System.out.println("------------------------------------------");
        System.out.println("|   CPU SCHEDULING ALGORITHM SIMULATOR   |");
        System.out.println("|----------------------------------------|");
        System.out.println("|       SCHEDULING ALGORITHM TYPE        |");
        System.out.println("|       1. Round Robin                   |");
        System.out.println("|       2. Preemptive SJF                |");
        System.out.println("|       3. Non Preemptive SJF            |");
        System.out.println("|                                        |");
        System.out.println("|       0. Exit                          |");
        System.out.println("------------------------------------------");

        while (!validInput) {
            System.out.print("  Selection >> ");
            Integer selection = scanner.nextInt();
            if (selection >= 0 && selection <= 3) {
                switch (selection) {
                case 0:
                    System.exit(1);
                case 1:
                    System.out.println("\nEnter quantum number for Round Robin algorithm (eg: 3) : ");
                    int quantum = scanner.nextInt();
                    RoundRobin.runTerminal(quantum);
                    break;
                case 2:
                    PreSJF.display(scanner);
                    break;
                case 3:
                    NonPreSJF.display(scanner);
                    break;
                }
                validInput = true;
                checkForNewProcess();
            } else {
                System.out.println("  Please enter a valid selection (0-3).");
            }
        }
    }

    public static void checkForNewProcess() {
        Boolean validinput = false;
        do {
            System.out.print("Do you wish to continue ? (Y/N) : ");
            String continueControl = scanner.next().toUpperCase();
            if (continueControl.equals("Y")) {
                validinput = true;
                mainUI();
            } else if (continueControl.equals("N")) {
                validinput = true;
                scanner.close();
            } else {
                System.out.println("Please input a valid selection (Y/N).");
            }
        } while (!validinput);
    }

    public static void main(String[] args) throws IOException {
        mainUI();
    }
}