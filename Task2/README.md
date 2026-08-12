# Student Grade Calculator

A console-based Java application built for **CodSoft Java Development Internship – Task 2**.
It calculates a student's total marks, average percentage, and final grade from marks entered
for multiple subjects, and reports a pass/fail result with performance feedback.

## Features

### Core
- Interactive console input for number of subjects and marks per subject.
- Full input validation:
  - Number of subjects must be a positive whole number.
  - Marks must be numeric and between 0 and 100.
  - Invalid entries are rejected with a clear error message, and the user is re-prompted.
- Calculates:
  - **Total Marks** = sum of all subject marks
  - **Average Percentage** = Total Marks ÷ Number of Subjects
- Assigns a letter grade:

  | Percentage | Grade |
  |------------|-------|
  | 90 – 100   | A+    |
  | 80 – 89    | A     |
  | 70 – 79    | B     |
  | 60 – 69    | C     |
  | 50 – 59    | D     |
  | Below 50   | F     |

- Determines **Pass/Fail**: a student passes only if the average is 50% or above
  **and** no single subject mark is below 35.
- Displays a well-formatted result report (subjects, total, max marks, average, grade, result).
- Motivational performance feedback message tied to the grade (e.g. "Excellent Work!").

### Bonus
- Stores **student name and roll number**.
- Displays **subject-wise marks** in a formatted table.
- Shows the **highest** and **lowest** subject marks.
- Reports a **class of distinction** (Distinction / First Class / Second Class / Third Class).
- Supports **multiple students** in a single run (loops until the user says no).
- Optionally **exports the report to a text file** (`result_<rollNumber>.txt`).
- Built with basic **OOP**: a dedicated `Student` class encapsulates a student's data and
  all grade-related calculations, separate from the console I/O logic in the main class.

## Project Structure

```
StudentGradeCalculator/
│
├── src/
│   ├── Student.java                  # Encapsulates a student's data & calculations
│   └── StudentGradeCalculator.java   # Main class: handles I/O and program flow
│
├── README.md
└── screenshots/                      # Add sample execution screenshots here
```

## How to Compile and Run

From the `src/` directory:

```bash
javac Student.java StudentGradeCalculator.java
java StudentGradeCalculator
```

The program also compiles and runs without changes in IntelliJ IDEA, Eclipse, NetBeans,
or VS Code — just open the `StudentGradeCalculator` folder (or import `src/` as a source
root) and run `StudentGradeCalculator.java`.

## Sample Console Output

```
=========================================
      STUDENT GRADE CALCULATOR
=========================================

Enter student name: Ravi Kumar
Enter roll number: 101

Enter number of subjects: 5

Enter marks for Subject 1 (out of 100): 85
Enter marks for Subject 2 (out of 100): 92
Enter marks for Subject 3 (out of 100): 76
Enter marks for Subject 4 (out of 100): 88
Enter marks for Subject 5 (out of 100): 95

=========================================
             RESULT
=========================================

Student Name        : Ravi Kumar
Roll Number         : 101
Subjects            : 5

Subject-wise Marks:
Subject         | Marks
--------------------------------
Subject 1       | 85.00
Subject 2       | 92.00
Subject 3       | 76.00
Subject 4       | 88.00
Subject 5       | 95.00

Total Marks         : 436.00 / 500.00
Average Percentage  : 87.20%
Grade               : A
Result              : PASS
Highest Subject Mark: 95.00
Lowest Subject Mark : 76.00
Class Distinction   : Distinction

Performance         : Excellent Work!
=========================================

Export this result to a text file? (Y/N): N

Calculate result for another student? (Y/N): N
=========================================
Thank you for using Student Grade Calculator!
=========================================
```

## Technologies Used

- Java (Scanner, ArrayList, exception handling)
- `InputMismatchException` handling for robust input validation
- `DecimalFormat` for two-decimal-place formatting
- Basic object-oriented design (`Student` class)

## Notes

- Add your own execution screenshots to the `screenshots/` folder as deliverables for the
  internship submission.
- The exported text report (when the export option is used) is written to the current
  working directory as `result_<rollNumber>.txt`.
