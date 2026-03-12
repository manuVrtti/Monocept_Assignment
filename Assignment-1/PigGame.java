import java.util.Scanner;
import java.util.Random;

public class PigGame {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        while (true) {

            int totalScore = 0;
            int turns = 0;

            System.out.println("\n===== LET'S PLAY PIG =====");
            System.out.println("* Reach total score of 20.");
            System.out.println("* Turn ends when you hold or roll a 1.");
            System.out.println("* Roll 1 → lose turn points.");
            System.out.println("* Hold → save turn points.");

            while (totalScore < 20) {

                turns++;
                int turnScore = 0;
                boolean turnOver = false;

                System.out.println("\nTURN " + turns);

                while (!turnOver) {

                    String input = "";

                    // ---------- INPUT VALIDATION ----------
                    while (true) {

                        System.out.print("Roll or Hold? (r/h): ");
                        input = scanner.next();

                        if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("h")) {
                            break;
                        } 
                        else {
                            System.out.println("Invalid input! Enter 'r' to roll or 'h' to hold.");
                        }
                    }

                    // ---------- HOLD ----------
                    if (input.equalsIgnoreCase("h")) {

                        totalScore += turnScore;

                        System.out.println("Score for turn: " + turnScore);
                        System.out.println("Total score: " + totalScore);

                        turnOver = true;
                    }

                    // ---------- ROLL ----------
                    else {

                        int die = random.nextInt(6) + 1;
                        System.out.println("Die rolled: " + die);

                        if (die == 1) {

                            System.out.println("Turn over! No score added.");
                            turnScore = 0;
                            turnOver = true;

                        } 
                        else {

                            turnScore += die;
                            System.out.println("Turn score: " + turnScore);
                        }
                    }
                }
            }

            System.out.println("\nYou reached 20 points!");
            System.out.println("You finished in " + turns + " turns!");

            // ---------- CONTINUE / EXIT ----------
            while (true) {

                System.out.print("\nEnter choice (1 = Play Again, 2 = Exit): ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input! Enter 1 or 2.");
                    scanner.next();
                    continue;
                }

                int choice = scanner.nextInt();

                if (choice == 1) {
                    break;
                } 
                else if (choice == 2) {

                    System.out.println("Game Over! Thanks for playing.");
                    scanner.close();
                    return;
                } 
                else {
                    System.out.println("Invalid choice! Enter 1 or 2.");
                }
            }
        }
    }
}