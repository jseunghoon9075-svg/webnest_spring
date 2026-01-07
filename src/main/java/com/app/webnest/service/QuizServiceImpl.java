package com.app.webnest.service;

import com.app.webnest.domain.dto.QuizPersonalDTO;
import com.app.webnest.domain.dto.QuizPersonalResponseDTO;
import com.app.webnest.domain.dto.QuizResponseDTO;
import com.app.webnest.domain.vo.QuizPersonalVO;
import com.app.webnest.domain.vo.QuizSubmitVO;
import com.app.webnest.domain.vo.QuizVO;
import com.app.webnest.exception.QuizException;
import com.app.webnest.repository.QuizDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {


    private final QuizDAO quizDAO;

    @Override
    public List<QuizVO> getQuizDirection(HashMap<String, Object> params) {
        if (params == null) params = new HashMap<>();
        return quizDAO.findQuizAll(params);
    }
    @Override
    public List<QuizPersonalDTO> getQuizPersonal(HashMap<String, Object> params) {
        return quizDAO.findQuizWithPersonal(params);
    }

    @Override
    public List<QuizVO> getQuizList() { return quizDAO.findQuizAll(); }

    @Override
    public Long getQuizCount(HashMap<String, Object> filters) { return quizDAO.findAllCount(filters); }

    @Override
    public QuizVO getQuizById(Long id) {
        QuizVO quizId = quizDAO.findById(id);
        if(quizId == null){
            throw new QuizException("해당 문제 삭제");
        } else {
            return quizId;
        }
    }

    @Override
    public QuizPersonalDTO getQuizPersonalByAll(){
        return  quizDAO.findQuizPersonalAll();
    }

    @Override
    public String getQuizExpectationById(Long id) {
        return quizDAO.findExpectationById(id);
    }


    @Override
    public Integer modifyIsBookmarked(QuizResponseDTO quizResponseDTO) {
         return quizDAO.updateIsBookmark(quizResponseDTO);
        }
    @Override
    public boolean modifyIsSolved(QuizResponseDTO quizResponseDTO) {
        return quizDAO.updateIsSolve(quizResponseDTO);
    }
    @Override
    public Long getQuizPersonalById(QuizResponseDTO quizResponseDTO) { return quizDAO.findQuizPersonalById(quizResponseDTO); }

    @Override
    public QuizPersonalVO getAllQuizPersonalById(Long id) { return quizDAO.findAllQuizPersonalById(id); }

    @Override
    public List<QuizPersonalResponseDTO> getByIsBookmarkIsSolve(Long userId) { return quizDAO.findByBookmarkIsSolve(userId); }

    @Override
    public void saveQuizPersonal(QuizPersonalVO quizPersonalVO) { quizDAO.insertQuizPersonal(quizPersonalVO); }

    @Override
    public void deleteQuizPersonal(Long id){}

    @Override
    public void saveQuizSubmit(QuizResponseDTO quizResponseDTO) { quizDAO.insertByQuizSubmit(quizResponseDTO); }

    @Override
    public QuizSubmitVO getQuizSubmitByIds(QuizResponseDTO quizResponseDTO) { return quizDAO.findByQuizSubmit(quizResponseDTO); }

    @Override
    public List<QuizSubmitVO> getAllQuizSubmitByIds(QuizResponseDTO quizResponseDTO) { return quizDAO.findByQuizSubmitAll(quizResponseDTO); }

    @Override
    public void modifySubmitResult(QuizResponseDTO quizResponseDTO) { quizDAO.updateBySubmitResult(quizResponseDTO); }



}
