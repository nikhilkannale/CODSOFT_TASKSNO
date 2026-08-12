import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/**
 * NumberGame
 * ----------
 * A console-based Number Guessing Game built for the
 * CodSoft Java Development Internship - Task 1.
 *
 * The game generates a random number between 1 and 100 and asks the
 * player to guess it within a limited number of attempts. Points are
 * awarded based on how quickly the player guesses correctly, and
 * overall statistics are tracked across multiple rounds.
 *
 * Core concepts demonstrated:
 *  - Random number generation (java.util.Random)
 *  - User input handling (java.util.Scanner)
 *  - Loops (while / do-while)
 *  - Conditional statements (if-else)
 *  - Modular design using separate methods
 *  - Exception handling (InputMismatchException)
 *  - Basic object-oriented programming (state kept as instance fields)
 */
public class NumberGame {

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 100;
    private static final int MAX_ATTEMPTS = 10;
    private static final int STARTING_SCORE = 100;
    private static final int PENALTY_PER_WRONG_GUESS = 10;

    // ---------------------------------------------------------------
    // Instance fields (game state / statistics)
    // ---------------------------------------------------------------
    private final Random random;
    private final Scanner scanner;

    private int totalRoundsPlayed;
    private int totalRoundsWon;
    private int highestScore;
    private int overallScore;

    /**
     * Constructor - initializes the random generator, scanner, and
     * all statistics to their default starting values.
     */
    public NumberGame() {
        this.random = new Random();
        this.scanner = new Scanner(System.in);
        this.totalRoundsPlayed = 0;
        this.totalRoundsWon = 0;
        this.highestScore = 0;
        this.overallScore = 0;
    }

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        NumberGame game = new NumberGame();
        game.run();
    }

    /**
     * Controls the overall game flow: prints the banner, plays rounds
     * in a loop until the user chooses to stop, then shows a summary.
     */
    public void run() {
        printBanner();

        boolean playAgain = true;
        while (playAgain) {
            playRound();
            playAgain = askPlayAgain();
        }

        displaySummary();
        scanner.close();
    }

    // ---------------------------------------------------------------
    // Core game logic
    // ---------------------------------------------------------------

    /**
     * Generates a random integer within the game's range (inclusive).
     *
     * @return a random number between MIN_RANGE and MAX_RANGE
     */
    private int generateRandomNumber() {
        return random.nextInt(MAX_RANGE - MIN_RANGE + 1) + MIN_RANGE;
    }

    /**
     * Plays a single round of the game: generates a secret number,
     * repeatedly prompts the user for guesses (validating input and
     * evaluating each guess) until the player either guesses correctly
     * or runs out of attempts. Updates the running statistics at the
     * end of the round.
     */
    private void playRound() {
        int targetNumber = generateRandomNumber();
        int attemptsUsed = 0;
        boolean guessedCorrectly = false;

        System.out.println("\nGuess a number between " + MIN_RANGE + " and " + MAX_RANGE + ".\n");

        while (attemptsUsed < MAX_ATTEMPTS && !guessedCorrectly) {
            System.out.println("Attempt " + (attemptsUsed + 1) + " of " + MAX_ATTEMPTS);
            int guess = readValidGuess();
            attemptsUsed++;

            int comparison = evaluateGuess(guess, targetNumber);

            if (comparison == 0) {
                guessedCorrectly = true;
                System.out.println("\nCongratulations!");
                System.out.println("You guessed the correct number.\n");
            } else if (comparison > 0) {
                System.out.println("\nToo High!\n");
            } else {
                System.out.println("\nToo Low!\n");
            }

            if (!guessedCorrectly) {
                int attemptsRemaining = MAX_ATTEMPTS - attemptsUsed;
                if (attemptsRemaining > 0) {
                    System.out.println("Attempts Remaining: " + attemptsRemaining + "\n");
                } else {
                    System.out.println("You've used all your attempts!");
                    System.out.println("The correct number was: " + targetNumber + "\n");
                }
            }
        }

        totalRoundsPlayed++;
        int roundScore = calculateScore(attemptsUsed, guessedCorrectly);
        overallScore += roundScore;

        if (guessedCorrectly) {
            totalRoundsWon++;
            System.out.println("Attempts Used : " + attemptsUsed);
        }
        System.out.println("Round Score   : " + roundScore);

        if (roundScore > highestScore) {
            highestScore = roundScore;
        }
    }

    /**
     * Compares the player's guess against the target number.
     *
     * @param guess  the number entered by the player
     * @param target the secret number to guess
     * @return 0 if equal, a positive value if the guess is too high,
     *         a negative value if the guess is too low
     */
    private int evaluateGuess(int guess, int target) {
        return Integer.compare(guess, target);
    }

    /**
     * Calculates the score earned for a round.
     *
     * Scoring rules:
     *  - Each round starts with a base of {@link #STARTING_SCORE} points.
     *  - {@link #PENALTY_PER_WRONG_GUESS} points are deducted for every
     *    incorrect guess made before the correct answer (or before the
     *    attempts run out).
     *  - Because the penalty scales with the number of guesses used,
     *    guessing the number in fewer attempts naturally results in a
     *    higher score - this is the round's built-in "bonus" for speed.
     *  - A minimum score of 10 is guaranteed for any round that is won.
     *  - A round that is not won (out of attempts) scores 0.
     *
     * @param attemptsUsed     the number of guesses the player made
     * @param guessedCorrectly whether the player guessed correctly
     * @return the score earned for the round
     */
    private int calculateScore(int attemptsUsed, boolean guessedCorrectly) {
        if (!guessedCorrectly) {
            return 0;
        }
        int incorrectGuesses = attemptsUsed - 1;
        int score = STARTING_SCORE - (incorrectGuesses * PENALTY_PER_WRONG_GUESS);
        return Math.max(score, 10);
    }

    // ---------------------------------------------------------------
    // Input handling
    // ---------------------------------------------------------------

    /**
     * Prompts the user for a guess and validates that it is an integer
     * within the allowed range. Keeps prompting on invalid input
     * without consuming an attempt.
     *
     * @return a validated integer guess
     */
    private int readValidGuess() {
        while (true) {
            System.out.print("Enter your guess: ");
            try {
                int guess = scanner.nextInt();
                if (guess < MIN_RANGE || guess > MAX_RANGE) {
                    System.out.println("Please enter a number between "
                            + MIN_RANGE + " and " + MAX_RANGE + ".\n");
                    continue;
                }
                return guess;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a whole number.\n");
                scanner.next(); // discard the invalid token
            }
        }
    }

    /**
     * Asks the user whether they would like to play another round.
     * Accepts Y/Yes or N/No (case-insensitive) and re-prompts on any
     * other input.
     *
     * @return true if the user wants to play again, false otherwise
     */
    private boolean askPlayAgain() {
        while (true) {
            System.out.print("\nWould you like to play again? (Y/N): ");
            String response = scanner.next().trim().toLowerCase();

            if (response.equals("y") || response.equals("yes")) {
                printBanner();
                return true;
            } else if (response.equals("n") || response.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid choice. Please enter Y or N.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Display helpers
    // ---------------------------------------------------------------

    /**
     * Prints the game's title banner.
     */
    private void printBanner() {
        System.out.println("==================================");
        System.out.println("        NUMBER GUESSING GAME");
        System.out.println("==================================");
    }

    /**
     * Displays the final game summary once the user chooses to stop
     * playing, including totals for rounds played/won/lost, the
     * highest score, the overall score, and a thank-you message.
     */
    private void displaySummary() {
        int totalRoundsLost = totalRoundsPlayed - totalRoundsWon;

        System.out.println("\n==================================");
        System.out.println("            GAME SUMMARY");
        System.out.println("==================================");
        System.out.println("Total Rounds Played : " + totalRoundsPlayed);
        System.out.println("Total Rounds Won     : " + totalRoundsWon);
        System.out.println("Total Rounds Lost    : " + totalRoundsLost);
        System.out.println("Highest Score         : " + highestScore);
        System.out.println("Overall Score         : " + overallScore);
        System.out.println("==================================");
        System.out.println("Thank you for playing the Number Guessing Game!");
        System.out.println("==================================");
    }
}
