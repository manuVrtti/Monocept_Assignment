import java.util.Scanner;
import java.util.Random;

public class NumberGuesserGame {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        while (true) {

            int randomNumber = random.nextInt(100) + 1;
            int attempts = 0;
            int maxAttempts = 5;
            boolean guessedCorrectly = false;

            System.out.println("\n===== NEW GAME STARTED =====");
            System.out.println("Guess a number between 1 and 100");
            System.out.println("Maximum attempts: " + maxAttempts);

            while (attempts < maxAttempts) {

                int guess = 0;

                // -------- Suyash_Gupta --------
                while (true) {

                    System.out.print("Enter your guess (1-100): ");

                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid input! Please enter a number.");
                        scanner.next();
                        continue;
                    }

                    guess = scanner.nextInt();

                    if (guess < 1 || guess > 100) {
                        System.out.println("Number must be between 1 and 100.");
                        continue;
                    }

                    break;
                }

                attempts++;

                if (guess < randomNumber) {
                    System.out.println("Too Low!");
                }
                else if (guess > randomNumber) {
                    System.out.println("Too High!");
                }
                else {
                    System.out.println("Correct! You guessed in " + attempts + " attempts.");
                    guessedCorrectly = true;
                    break;
                }

                System.out.println("Attempts left: " + (maxAttempts - attempts));
            }

            if (!guessedCorrectly) {
                System.out.println("Game Over! Correct number was: " + randomNumber);
            }

            
            while (true) {

                System.out.print("\nEnter choice (1 = Continue, 2 = Exit): ");

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
                    System.out.println("Thank you for playing!");
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
