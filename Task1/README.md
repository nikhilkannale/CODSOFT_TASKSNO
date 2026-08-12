# Number Guessing Game

A console-based Number Guessing Game built in Java for the **CodSoft Java
Development Internship – Task 1**. The program picks a secret number
between 1 and 100, and the player has up to 10 attempts per round to
guess it correctly. The game tracks scores and statistics across as
many rounds as the player wants to play.

## Features

- **Random number generation** between 1 and 100 using `java.util.Random`.
- **Input validation** — non-numeric input is rejected with a friendly
  message (handled via `InputMismatchException`), and out-of-range
  numbers are also caught, without costing the player an attempt.
- **Guess evaluation** — after every guess, the game reports whether the
  guess was correct, too high, or too low.
- **Attempt limitation** — a maximum of 10 attempts per round, with the
  remaining attempts shown after every incorrect guess. If the player
  runs out of attempts, the correct number is revealed.
- **Multiple rounds** — after each round the player is asked whether
  they'd like to play again (`Y`/`Yes` or `N`/`No`, case-insensitive).
- **Scoring system**
  - Each round starts at 100 points.
  - 10 points are deducted for every incorrect guess.
  - Because the deduction scales with attempts used, guessing in fewer
    tries naturally rewards the player with a higher score (the round's
    built-in "speed bonus").
  - A minimum of 10 points is guaranteed for any round that is won; an
    unsolved round scores 0.
- **Statistics tracked across the whole session**: total rounds played,
  total rounds won, highest score, and overall (cumulative) score.
- **Game summary** printed when the player chooses to stop, showing
  rounds played/won/lost, highest score, overall score, and a
  thank-you message.

## Project Structure

```
NumberGame/
│
├── src/
│   └── NumberGame.java     # Full game source code
│
├── README.md                # This file
└── screenshots/
    └── sample-run.txt       # Example console transcript
```

## Code Design

The game is implemented as a single well-structured class,
`NumberGame`, with state (statistics, `Random`, `Scanner`) held as
instance fields and behavior broken into small, single-purpose methods:

| Method                 | Responsibility                                             |
|-------------------------|-------------------------------------------------------------|
| `generateRandomNumber()`| Produces the secret number for a round                     |
| `playRound()`           | Runs one full round: guessing loop, feedback, score update  |
| `evaluateGuess()`       | Compares a guess to the target (correct/too high/too low)   |
| `readValidGuess()`      | Prompts and validates user input, handling bad input        |
| `calculateScore()`      | Computes the score earned for a round                       |
| `askPlayAgain()`        | Prompts for and validates the play-again choice             |
| `displaySummary()`      | Prints the end-of-session statistics                        |
| `printBanner()`         | Prints the game title banner                                |

Naming follows standard Java conventions (camelCase for methods and
variables, PascalCase for the class), and comments explain the purpose
of each section and any non-obvious logic (particularly the scoring
formula).

## How to Run

### Requirements
A Java Development Kit (JDK), version 8 or later, is required to
compile and run the program.

### Compile
```bash
cd NumberGame/src
javac NumberGame.java
```

### Run
```bash
java NumberGame
```

### Using an IDE
The project can also be opened and run directly from IntelliJ IDEA,
Eclipse, NetBeans, or VS Code (with the Java Extension Pack):
1. Open the `NumberGame` folder as a project (or import `src/NumberGame.java`
   into a new Java project).
2. Run the `main` method in `NumberGame.java`.

## Sample Gameplay

```
==================================
        NUMBER GUESSING GAME
==================================

Guess a number between 1 and 100.

Attempt 1 of 10
Enter your guess: 45

Too Low!

Attempts Remaining: 9

Attempt 2 of 10
Enter your guess: 72

Too High!

Attempt 3 of 10
Enter your guess: 61

Congratulations!
You guessed the correct number.

Attempts Used : 3
Round Score   : 80

Would you like to play again? (Y/N): Y
```

A longer example transcript, including invalid input handling and the
end-of-session summary, is available in `screenshots/sample-run.txt`.

## Notes on Scoring

The scoring formula is intentionally simple and predictable:

```
roundScore = 100 - (10 * incorrectGuesses)   [minimum 10 if the round is won]
roundScore = 0                                [if the round is not won]
```

Because `incorrectGuesses = attemptsUsed - 1`, guessing correctly on the
first attempt yields the maximum 100 points, while guessing on the last
possible attempt (10th) yields the minimum 10 points — directly
rewarding players who find the number in fewer tries.
