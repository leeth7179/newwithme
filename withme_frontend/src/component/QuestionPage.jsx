import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const QuestionPage = () => {
  const [questions, setQuestions] = useState([]); // 문항 데이터
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0); // 현재 문항
  const [answers, setAnswers] = useState([]); // 사용자가 선택한 답변들
  const navigate = useNavigate();

  // 문항 불러오기
  useEffect(() => {
    // 백엔드에서 FREE 설문 문항 가져오기
    axios.get('/api/surveys/free-questions')
      .then(response => {
        setQuestions(response.data); // 문항 데이터를 상태에 저장
      })
      .catch(error => {
        console.error('문항을 불러오는 데 실패했습니다.', error);
      });
  }, []);

  // 라디오 버튼 선택 처리
  const handleAnswerChange = (questionId, selectedChoiceId) => {
    const newAnswers = [...answers];
    const existingAnswerIndex = newAnswers.findIndex(answer => answer.questionId === questionId);

    if (existingAnswerIndex !== -1) {
      newAnswers[existingAnswerIndex] = { questionId, choiceId: selectedChoiceId };
    } else {
      newAnswers.push({ questionId, choiceId: selectedChoiceId });
    }

    setAnswers(newAnswers); // 답변 업데이트
  };

  // "마침" 버튼 클릭 시
  const handleFinish = () => {
    // 답변 제출 API 요청
    axios.post('/api/submit-answers', { answers })
      .then(response => {
        alert('문진이 완료되었습니다!');
        navigate('/'); // 메인 페이지로 이동
      })
      .catch(error => {
        console.error('답변 제출 실패:', error);
      });
  };

  // 현재 문항
  const currentQuestion = questions[currentQuestionIndex];

  return (
    <div className="questionnaire-page">
      <h2>문진검사</h2>
      <div className="question">
        {currentQuestion && (
          <>
            <p>{currentQuestion.questionText}</p>
            <div className="choices">
              {currentQuestion.choices.map(choice => (
                <label key={choice.choiceId}>
                  <input
                    type="radio"
                    name={`question-${currentQuestion.questionId}`}
                    value={choice.choiceId}
                    checked={answers.find(answer => answer.questionId === currentQuestion.questionId)?.choiceId === choice.choiceId}
                    onChange={() => handleAnswerChange(currentQuestion.questionId, choice.choiceId)}
                  />
                  {choice.choiceText}
                </label>
              ))}
            </div>
          </>
        )}
      </div>

      <div className="navigation">
        <button
          disabled={currentQuestionIndex === 0}
          onClick={() => setCurrentQuestionIndex(currentQuestionIndex - 1)}
        >
          이전
        </button>

        {currentQuestionIndex < questions.length - 1 ? (
          <button onClick={() => setCurrentQuestionIndex(currentQuestionIndex + 1)}>
            다음
          </button>
        ) : (
          <button onClick={handleFinish}>
            마침
          </button>
        )}
      </div>
    </div>
  );
};

export default QuestionPage;
