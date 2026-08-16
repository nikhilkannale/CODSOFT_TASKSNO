package com.codsoft.quizapp.service;

import com.codsoft.quizapp.dto.AnswerSubmission;
import com.codsoft.quizapp.dto.QuizSubmission;
import com.codsoft.quizapp.dto.ResultResponse;
import com.codsoft.quizapp.entity.Question;
import com.codsoft.quizapp.entity.Quiz;
import com.codsoft.quizapp.entity.Result;
import com.codsoft.quizapp.entity.User;
import com.codsoft.quizapp.exception.BadRequestException;
import com.codsoft.quizapp.repository.QuestionRepository;
import com.codsoft.quizapp.repository.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the server-side grading logic in ResultService.
 * Grading is the most important piece of business logic in the app -- it must
 * never trust the client for the correct answers, and must be resilient to
 * partial/garbage/out-of-range submissions.
 */
@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock private ResultRepository resultRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private QuizService quizService;

    @InjectMocks
    private ResultService resultService;

    private Quiz quiz;
    private User user;
    private Question q1, q2, q3;

    @BeforeEach
    void setUp() {
        quiz = Quiz.builder().id(1L).title("Sample Quiz").durationInSeconds(300).active(true).build();
        user = User.builder().id(1L).fullName("Test Student").email("student@test.com").build();

        q1 = Question.builder().id(101L).quiz(quiz).questionText("Q1")
                .optionA("a").optionB("b").optionC("c").optionD("d")
                .correctOption("A").marks(1).build();
        q2 = Question.builder().id(102L).quiz(quiz).questionText("Q2")
                .optionA("a").optionB("b").optionC("c").optionD("d")
                .correctOption("B").marks(2).build();
        q3 = Question.builder().id(103L).quiz(quiz).questionText("Q3")
                .optionA("a").optionB("b").optionC("c").optionD("d")
                .correctOption("C").marks(1).build();

        // total marks across the quiz = 1 + 2 + 1 = 4
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> {
            Result r = invocation.getArgument(0);
            r.setId(999L);
            return r;
        });
    }

    @Test
    void scoresAllCorrectAnswersAsFullMarks() {
        when(quizService.getQuizOrThrow(1L)).thenReturn(quiz);
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1, q2, q3));

        QuizSubmission submission = submissionOf(
                answer(101L, "A"), answer(102L, "B"), answer(103L, "C"));

        ResultResponse result = resultService.submitQuiz(user, submission);

        assertThat(result.getScore()).isEqualTo(4);
        assertThat(result.getTotalMarks()).isEqualTo(4);
        assertThat(result.getCorrectAnswers()).isEqualTo(3);
        assertThat(result.getIncorrectAnswers()).isEqualTo(0);
        assertThat(result.getPercentage()).isEqualTo(100.0);
        assertThat(result.getPassed()).isTrue();
    }

    @Test
    void partiallyCorrectSubmissionIsGradedAccurately() {
        when(quizService.getQuizOrThrow(1L)).thenReturn(quiz);
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1, q2, q3));

        // q1 correct (1 mark), q2 wrong, q3 unattempted
        QuizSubmission submission = submissionOf(answer(101L, "A"), answer(102L, "D"));

        ResultResponse result = resultService.submitQuiz(user, submission);

        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getCorrectAnswers()).isEqualTo(1);
        assertThat(result.getIncorrectAnswers()).isEqualTo(1);
        assertThat(result.getAttemptedQuestions()).isEqualTo(2);
        assertThat(result.getTotalQuestions()).isEqualTo(3);
        assertThat(result.getPercentage()).isEqualTo(25.0); // 1/4
        assertThat(result.getPassed()).isFalse(); // below 40% pass threshold
    }

    @Test
    void blankAndGarbageAnswersAreTreatedAsUnattemptedNotIncorrect() {
        when(quizService.getQuizOrThrow(1L)).thenReturn(quiz);
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1, q2, q3));

        QuizSubmission submission = submissionOf(
                answer(101L, ""), answer(102L, "Z"), answer(103L, "C"));

        ResultResponse result = resultService.submitQuiz(user, submission);

        // q1 blank -> unattempted, q2 "Z" (invalid) -> ignored/unattempted, q3 correct
        assertThat(result.getAttemptedQuestions()).isEqualTo(1);
        assertThat(result.getCorrectAnswers()).isEqualTo(1);
        assertThat(result.getIncorrectAnswers()).isEqualTo(0);
        assertThat(result.getScore()).isEqualTo(1);
    }

    @Test
    void rejectsAnswersForQuestionsOutsideTheQuiz() {
        when(quizService.getQuizOrThrow(1L)).thenReturn(quiz);
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1, q2, q3));

        QuizSubmission submission = submissionOf(answer(9999L, "A")); // question id not in this quiz

        assertThatThrownBy(() -> resultService.submitQuiz(user, submission))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void clampsTimeTakenToTheQuizDuration() {
        when(quizService.getQuizOrThrow(1L)).thenReturn(quiz);
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(q1, q2, q3));

        QuizSubmission submission = submissionOf(answer(101L, "A"));
        submission.setTimeTakenInSeconds(999999); // client tampering / clock skew

        ResultResponse result = resultService.submitQuiz(user, submission);

        assertThat(result.getTimeTakenInSeconds()).isEqualTo(quiz.getDurationInSeconds());
    }

    @Test
    void rejectsSubmissionForQuizWithNoQuestions() {
        when(quizService.getQuizOrThrow(1L)).thenReturn(quiz);
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of());

        QuizSubmission submission = submissionOf(answer(101L, "A"));

        assertThatThrownBy(() -> resultService.submitQuiz(user, submission))
                .isInstanceOf(BadRequestException.class);
    }

    // ---------- helpers ----------

    private QuizSubmission submissionOf(AnswerSubmission... answers) {
        QuizSubmission submission = new QuizSubmission();
        submission.setQuizId(1L);
        submission.setTimeTakenInSeconds(120);
        submission.setAnswers(List.of(answers));
        return submission;
    }

    private AnswerSubmission answer(Long questionId, String selected) {
        AnswerSubmission a = new AnswerSubmission();
        a.setQuestionId(questionId);
        a.setSelectedOption(selected);
        return a;
    }
}
