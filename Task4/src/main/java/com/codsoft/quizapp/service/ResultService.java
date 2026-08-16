package com.codsoft.quizapp.service;

import com.codsoft.quizapp.dto.AnswerSubmission;
import com.codsoft.quizapp.dto.QuizSubmission;
import com.codsoft.quizapp.dto.ResultResponse;
import com.codsoft.quizapp.entity.Question;
import com.codsoft.quizapp.entity.Quiz;
import com.codsoft.quizapp.entity.Result;
import com.codsoft.quizapp.entity.User;
import com.codsoft.quizapp.exception.BadRequestException;
import com.codsoft.quizapp.exception.ResourceNotFoundException;
import com.codsoft.quizapp.repository.QuestionRepository;
import com.codsoft.quizapp.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private static final double PASS_PERCENTAGE = 40.0;

    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final QuizService quizService;

    /**
     * Grades a submission server-side (client never sees correct answers ahead of time),
     * persists the Result, and returns a full breakdown.
     */
    @Transactional
    public ResultResponse submitQuiz(User user, QuizSubmission submission) {
        if (submission.getQuizId() == null) {
            throw new BadRequestException("quizId is required");
        }
        Quiz quiz = quizService.getQuizOrThrow(submission.getQuizId());

        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        if (questions.isEmpty()) {
            throw new BadRequestException("This quiz has no questions configured");
        }

        Map<Long, String> submittedAnswers = submission.getAnswers() == null
                ? Map.of()
                : submission.getAnswers().stream()
                    .filter(a -> a.getQuestionId() != null)
                    .collect(Collectors.toMap(
                            AnswerSubmission::getQuestionId,
                            a -> a.getSelectedOption() == null ? "" : a.getSelectedOption().toUpperCase(),
                            (a, b) -> b));

        Set<Long> validQuestionIds = questions.stream().map(Question::getId).collect(Collectors.toSet());

        int correct = 0, incorrect = 0, attempted = 0, score = 0;
        for (Question q : questions) {
            String given = submittedAnswers.get(q.getId());
            if (given == null || given.isBlank()) {
                continue; // unattempted
            }
            if (!Set.of("A", "B", "C", "D").contains(given)) {
                continue; // ignore garbage input rather than failing the whole submission
            }
            attempted++;
            if (given.equalsIgnoreCase(q.getCorrectOption())) {
                correct++;
                score += q.getMarks();
            } else {
                incorrect++;
            }
        }
        // Defensive check: reject question ids that don't belong to this quiz.
        if (submittedAnswers.keySet().stream().anyMatch(id -> !validQuestionIds.contains(id))) {
            throw new BadRequestException("Submission contains answers for questions outside this quiz");
        }

        int totalMarks = quiz.getTotalMarks();
        double percentage = totalMarks == 0 ? 0.0 : Math.round((score * 10000.0) / totalMarks) / 100.0;
        boolean passed = percentage >= PASS_PERCENTAGE;

        int timeTaken = submission.getTimeTakenInSeconds() == null ? 0 : submission.getTimeTakenInSeconds();
        // Clamp to the quiz duration so client-side tampering can't record a negative or oversized time.
        timeTaken = Math.max(0, Math.min(timeTaken, quiz.getDurationInSeconds()));

        Result result = Result.builder()
                .user(user)
                .quiz(quiz)
                .totalQuestions(questions.size())
                .attemptedQuestions(attempted)
                .correctAnswers(correct)
                .incorrectAnswers(incorrect)
                .score(score)
                .totalMarks(totalMarks)
                .percentage(percentage)
                .passed(passed)
                .timeTakenInSeconds(timeTaken)
                .build();

        result = resultRepository.save(result);
        return toResponse(result);
    }

    public List<ResultResponse> getResultsForUser(Long userId) {
        return resultRepository.findByUserIdOrderBySubmittedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<Result> getResultsForQuiz(Long quizId) {
        return resultRepository.findByQuizIdOrderByScoreDescTimeTakenInSecondsAsc(quizId);
    }

    public List<Result> getAllResults() {
        return resultRepository.findAllByOrderBySubmittedAtDesc();
    }

    public ResultResponse getResultOrThrow(Long resultId, Long requestingUserId, boolean isAdmin) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + resultId));
        if (!isAdmin && !result.getUser().getId().equals(requestingUserId)) {
            throw new BadRequestException("You are not allowed to view this result");
        }
        return toResponse(result);
    }

    private ResultResponse toResponse(Result r) {
        return ResultResponse.builder()
                .resultId(r.getId())
                .quizId(r.getQuiz().getId())
                .quizTitle(r.getQuiz().getTitle())
                .totalQuestions(r.getTotalQuestions())
                .attemptedQuestions(r.getAttemptedQuestions())
                .correctAnswers(r.getCorrectAnswers())
                .incorrectAnswers(r.getIncorrectAnswers())
                .score(r.getScore())
                .totalMarks(r.getTotalMarks())
                .percentage(r.getPercentage())
                .passed(r.getPassed())
                .timeTakenInSeconds(r.getTimeTakenInSeconds())
                .submittedAt(r.getSubmittedAt())
                .build();
    }
}
