import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * StudentGradeCalculator.java
 *
 * CodSoft Java Development Internship - Task 2
 *
 * A console-based Student Grade Calculator that:
 *  - Reads marks for a configurable number of subjects
 *  - Validates all user input (numeric, range, subject count)
 *  - Calculates total marks, average percentage and letter grade
 *  - Determines pass/fail status
 *  - Displays a well-formatted report with motivational feedback
 *  - Supports multiple students in one run (bonus)
 *  - Optionally exports the report to a text file (bonus)
 *
 * The heavy lifting for a single student's data/calculations lives in
 * the Student class; this class is responsible for interacting with the
 * user (input/output) and orchestrating the overall program flow.
 */
public class StudentGradeCalculator {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static final String DIVIDER = "=========================================";

    public static void main(String[] args) {
        printWelcomeBanner();

        boolean calculateAnother = true;

        while (calculateAnother) {
            Student student = inputStudentDetails();
            inputMarks(student);

            displayReport(student);
            offerFileExport(student);

            calculateAnother = askYesNo("\nCalculate result for another student? (Y/N): ");
        }

        System.out.println(DIVIDER);
        System.out.println("Thank you for using Student Grade Calculator!");
        System.out.println(DIVIDER);

        scanner.close();
    }

    /** Prints the initial welcome banner. */
    private static void printWelcomeBanner() {
        System.out.println(DIVIDER);
        System.out.println("      STUDENT GRADE CALCULATOR");
        System.out.println(DIVIDER);
    }

    /** Collects the student's name and roll number (bonus feature). */
    private static Student inputStudentDetails() {
        System.out.println();
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "N/A";
        }

        System.out.print("Enter roll number: ");
        String rollNumber = scanner.nextLine().trim();
        if (rollNumber.isEmpty()) {
            rollNumber = "N/A";
        }

        return new Student(name, rollNumber);
    }

    /**
     * Prompts for the number of subjects and then the marks for each
     * subject, validating every value entered by the user.
     */
    private static void inputMarks(Student student) {
        int numberOfSubjects = readNumberOfSubjects();

        System.out.println();
        for (int i = 1; i <= numberOfSubjects; i++) {
            double mark = readSubjectMark(i);
            student.addMark(mark);
        }
    }

    /** Reads and validates the number of subjects (must be greater than zero). */
    private static int readNumberOfSubjects() {
        int numberOfSubjects = -1;

        while (numberOfSubjects <= 0) {
            System.out.print("\nEnter number of subjects: ");
            try {
                numberOfSubjects = scanner.nextInt();
                scanner.nextLine(); // consume leftover newline

                if (numberOfSubjects <= 0) {
                    System.out.println("Error: Number of subjects must be greater than zero. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid whole number.");
                scanner.nextLine(); // discard invalid token
            }
        }

        return numberOfSubjects;
    }

    /** Reads and validates a single subject's mark (must be between 0 and 100). */
    private static double readSubjectMark(int subjectIndex) {
        double mark = -1;
        boolean valid = false;

        while (!valid) {
            System.out.print("Enter marks for Subject " + subjectIndex + " (out of 100): ");
            try {
                mark = scanner.nextDouble();
                scanner.nextLine(); // consume leftover newline

                if (mark < 0 || mark > 100) {
                    System.out.println("Error: Marks must be between 0 and 100. Please try again.");
                } else {
                    valid = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid numeric value.");
                scanner.nextLine(); // discard invalid token
            }
        }

        return mark;
    }

    /** Displays the full, formatted result report for a student. */
    private static void displayReport(Student student) {
        StringBuilder report = buildReport(student);
        System.out.println(report);
    }

    /**
     * Builds the formatted report as a String so it can be reused for
     * both console display and file export.
     */
    private static StringBuilder buildReport(Student student) {
        StringBuilder sb = new StringBuilder();

        double total = student.calculateTotalMarks();
        double maxMarks = student.getMaxPossibleMarks();
        double average = student.calculateAveragePercentage();
        String grade = student.calculateGrade();
        String result = student.getResultStatus();

        sb.append("\n").append(DIVIDER).append("\n");
        sb.append("             RESULT\n");
        sb.append(DIVIDER).append("\n\n");

        sb.append("Student Name        : ").append(student.getName()).append("\n");
        sb.append("Roll Number         : ").append(student.getRollNumber()).append("\n");
        sb.append("Subjects            : ").append(student.getNumberOfSubjects()).append("\n\n");

        sb.append("Subject-wise Marks:\n");
        sb.append(String.format("%-15s | %-10s%n", "Subject", "Marks"));
        sb.append("--------------------------------\n");
        int subjectNumber = 1;
        for (double mark : student.getSubjectMarks()) {
            sb.append(String.format("%-15s | %-10s%n", "Subject " + subjectNumber, decimalFormat.format(mark)));
            subjectNumber++;
        }
        sb.append("\n");

        sb.append("Total Marks         : ").append(decimalFormat.format(total))
                .append(" / ").append(decimalFormat.format(maxMarks)).append("\n");
        sb.append("Average Percentage  : ").append(decimalFormat.format(average)).append("%\n");
        sb.append("Grade               : ").append(grade).append("\n");
        sb.append("Result              : ").append(result).append("\n");
        sb.append("Highest Subject Mark: ").append(decimalFormat.format(student.getHighestMark())).append("\n");
        sb.append("Lowest Subject Mark : ").append(decimalFormat.format(student.getLowestMark())).append("\n");
        sb.append("Class Distinction   : ").append(student.getClassDistinction()).append("\n\n");

        sb.append("Performance         : ").append(student.getPerformanceFeedback()).append("\n");
        sb.append(DIVIDER);

        return sb;
    }

    /** Bonus: offers to export the report to a text file. */
    private static void offerFileExport(Student student) {
        boolean export = askYesNo("\nExport this result to a text file? (Y/N): ");
        if (!export) {
            return;
        }

        String fileName = "result_" + sanitizeFileName(student.getRollNumber()) + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(buildReport(student).toString());
            System.out.println("Result exported successfully to \"" + fileName + "\".");
        } catch (IOException e) {
            System.out.println("Error: Could not export result to file. " + e.getMessage());
        }
    }

    /** Removes characters that are unsafe for file names. */
    private static String sanitizeFileName(String rawName) {
        String sanitized = rawName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return sanitized.isEmpty() ? "student" : sanitized;
    }

    /** Reads a Y/N response from the user, re-prompting on invalid input. */
    private static boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String response = scanner.nextLine().trim().toUpperCase();
            if (response.equals("Y") || response.equals("YES")) {
                return true;
            } else if (response.equals("N") || response.equals("NO")) {
                return false;
            } else {
                System.out.println("Please enter Y or N.");
            }
        }
    }
}
