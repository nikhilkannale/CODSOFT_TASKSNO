package com.codsoft.quizapp.service;

import com.codsoft.quizapp.dto.QuestionPublicDto;
import com.codsoft.quizapp.dto.QuestionRequest;
import com.codsoft.quizapp.dto.QuizRequest;
import com.codsoft.quizapp.entity.Question;
import com.codsoft.quizapp.entity.Quiz;
import com.codsoft.quizapp.exception.BadRequestException;
import com.codsoft.quizapp.exception.ResourceNotFoundException;
import com.codsoft.quizapp.repository.QuestionRepository;
import com.codsoft.quizapp.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    // ---------- Student-facing ----------

    public List<Quiz> getActiveQuizzes() {
        return quizRepository.findByActiveTrue();
    }

    /**
     * Loads a quiz together with its questions (fetch-joined) so the transient
     * getTotalMarks()/getQuestionCount() getters used during JSON serialization
     * can safely read the collection even with spring.jpa.open-in-view=false.
     */
    public Quiz getQuizOrThrow(Long id) {
        return quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<QuestionPublicDto> getPublicQuestions(Long quizId) {
        Quiz quiz = getQuizOrThrow(quizId);
        if (!quiz.isActive()) {
            throw new BadRequestException("This quiz is not currently available");
        }
        return questionRepository.findByQuizId(quizId).stream()
                .map(q -> QuestionPublicDto.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .marks(q.getMarks())
                        .build())
                .toList();
    }

    // ---------- Admin ----------

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAllWithQuestions();
    }

    public Quiz createQuiz(QuizRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationInSeconds(request.getDurationInSeconds())
                .active(true)
                .build();
        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = getQuizOrThrow(id);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setDurationInSeconds(request.getDurationInSeconds());
        return quizRepository.save(quiz);
    }

    public void toggleActive(Long id, boolean active) {
        Quiz quiz = getQuizOrThrow(id);
        quiz.setActive(active);
        quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        Quiz quiz = getQuizOrThrow(id);
        quizRepository.delete(quiz);
    }

    @Transactional
    public Question addQuestion(Long quizId, QuestionRequest request) {
        Quiz quiz = getQuizOrThrow(quizId);
        Question question = Question.builder()
                .quiz(quiz)
                .questionText(request.getQuestionText())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctOption(request.getCorrectOption().toUpperCase())
                .marks(request.getMarks() == null ? 1 : request.getMarks())
                .build();
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption().toUpperCase());
        question.setMarks(request.getMarks() == null ? 1 : request.getMarks());
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        questionRepository.delete(question);
    }

    public List<Question> getQuestionsForAdmin(Long quizId) {
        getQuizOrThrow(quizId);
        return questionRepository.findByQuizId(quizId);
    }
}
