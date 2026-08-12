import java.util.ArrayList;
import java.util.List;

/**
 * Student.java
 *
 * Represents a single student and encapsulates all data and calculations
 * related to that student: name, roll number, subject marks, total marks,
 * average percentage, grade, pass/fail status and class distinction.
 *
 * Keeping this logic inside a dedicated class (instead of static methods
 * in the main class) demonstrates basic OOP principles and makes it easy
 * to support multiple students in a single run of the program.
 */
public class Student {

    private static final double MAX_MARKS_PER_SUBJECT = 100.0;
    private static final double PASS_THRESHOLD_PER_SUBJECT = 35.0;
    private static final double PASS_THRESHOLD_AVERAGE = 50.0;

    private final String name;
    private final String rollNumber;
    private final List<Double> subjectMarks;

    public Student(String name, String rollNumber) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.subjectMarks = new ArrayList<>();
    }

    /** Adds one subject's marks to this student's record. */
    public void addMark(double mark) {
        subjectMarks.add(mark);
    }

    public String getName() {
        return name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public List<Double> getSubjectMarks() {
        return subjectMarks;
    }

    public int getNumberOfSubjects() {
        return subjectMarks.size();
    }

    public double getMaxPossibleMarks() {
        return getNumberOfSubjects() * MAX_MARKS_PER_SUBJECT;
    }

    /** Total Marks = Sum of Marks in All Subjects. */
    public double calculateTotalMarks() {
        double total = 0.0;
        for (double mark : subjectMarks) {
            total += mark;
        }
        return total;
    }

    /** Average Percentage = Total Marks / Number of Subjects. */
    public double calculateAveragePercentage() {
        if (getNumberOfSubjects() == 0) {
            return 0.0;
        }
        return calculateTotalMarks() / getNumberOfSubjects();
    }

    /** Assigns a letter grade based on the average percentage. */
    public String calculateGrade() {
        double average = calculateAveragePercentage();

        if (average >= 90) {
            return "A+";
        } else if (average >= 80) {
            return "A";
        } else if (average >= 70) {
            return "B";
        } else if (average >= 60) {
            return "C";
        } else if (average >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Determines pass/fail status.
     * A student passes if the average percentage is 50% or above
     * AND no individual subject mark is below 35.
     */
    public boolean hasPassed() {
        if (calculateAveragePercentage() < PASS_THRESHOLD_AVERAGE) {
            return false;
        }
        for (double mark : subjectMarks) {
            if (mark < PASS_THRESHOLD_PER_SUBJECT) {
                return false;
            }
        }
        return true;
    }

    public String getResultStatus() {
        return hasPassed() ? "PASS" : "FAIL";
    }

    /** Bonus: highest mark scored among all subjects. */
    public double getHighestMark() {
        double highest = Double.MIN_VALUE;
        for (double mark : subjectMarks) {
            if (mark > highest) {
                highest = mark;
            }
        }
        return highest;
    }

    /** Bonus: lowest mark scored among all subjects. */
    public double getLowestMark() {
        double lowest = Double.MAX_VALUE;
        for (double mark : subjectMarks) {
            if (mark < lowest) {
                lowest = mark;
            }
        }
        return lowest;
    }

    /**
     * Bonus: class of distinction based on average percentage.
     * Only meaningful when the student has passed; failing students
     * are simply reported as "Not Applicable".
     */
    public String getClassDistinction() {
        if (!hasPassed()) {
            return "Not Applicable";
        }
        double average = calculateAveragePercentage();
        if (average >= 75) {
            return "Distinction";
        } else if (average >= 60) {
            return "First Class";
        } else if (average >= 50) {
            return "Second Class";
        } else {
            return "Third Class";
        }
    }

    /** Bonus: motivational feedback message based on the final grade. */
    public String getPerformanceFeedback() {
        switch (calculateGrade()) {
            case "A+":
                return "Outstanding Performance!";
            case "A":
                return "Excellent Work!";
            case "B":
                return "Good Job!";
            case "C":
                return "Keep Improving!";
            case "D":
                return "You Passed, Practice More!";
            default:
                return "Better Luck Next Time!";
        }
    }
}
