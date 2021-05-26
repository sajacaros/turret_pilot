package com.bnpinnovation.turret.service;

import com.bnpinnovation.turret.domain.Third;
import com.bnpinnovation.turret.dto.ThirdForm;
import com.bnpinnovation.turret.repository.ThirdRepository;
import com.bnpinnovation.turret.security.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface ThirdService {
    ThirdForm.ThirdDetails newThird(ThirdForm.New requestForm);

    Third getThird(String symbol);

    List<ThirdForm.ThirdDetails> allThirds();

    ThirdForm.ThirdDetails refreshThird(Long thirdId);

    void disableThird(Long thirdId);

    void enableThird(Long thirdId);

    void removeAll();

    @Service
    @Slf4j
    @Transactional
    class Default implements ThirdService {

        @Autowired
        private JWTUtil jwtUtil;

        @Autowired
        private ThirdRepository thirdRepository;

        @Override
        public ThirdForm.ThirdDetails newThird(ThirdForm.New requestForm) {
            if(thirdRepository.existsBySymbol(requestForm.getSymbol())) {
                throw new EntityExistsException(requestForm.getSymbol() + " exist");
            }
            String accessToken = jwtUtil.generate(requestForm.getSymbol(), JWTUtil.TokenType.THIRD, requestForm.getLifeTime());
            Third third = Third.builder()
                    .symbol(requestForm.getSymbol())
                    .name(requestForm.getName())
                    .accessToken(accessToken)
                    .lifeTime(requestForm.getLifeTime())
                    .expiredDate(LocalDateTime.now().plusSeconds(requestForm.getLifeTime()))
                    .build();
            Third savedThird = thirdRepository.save(third);
            return savedThird.constructDetailsDto();
        }

        @Override
        public Third getThird(String symbol) {
            return thirdRepository.findBySymbol(symbol)
                    .orElseThrow(()->new EntityNotFoundException(symbol+" not exist"));
        }

        @Override
        public List<ThirdForm.ThirdDetails> allThirds() {
            return thirdRepository.findAll().stream()
                    .map(Third::constructDetailsDto)
                    .collect(Collectors.toList());
        }

        @Override
        public ThirdForm.ThirdDetails refreshThird(Long thirdId) {
            Third third = getThird(thirdId);
            String accessToken = jwtUtil.generate(third.symbol(), JWTUtil.TokenType.THIRD, third.lifeTime());
            third.refresh(accessToken, LocalDateTime.now().plusSeconds(third.lifeTime()));
            return third.constructDetailsDto();
        }

        private Third getThird(Long thirdId) {
            Third third = thirdRepository.findById(thirdId)
                    .orElseThrow(()->new EntityNotFoundException("third("+ thirdId +") not exist."));
            return third;
        }

        @Override
        public void disableThird(Long thirdId) {
            getThird(thirdId).disable();
        }

        @Override
        public void enableThird(Long thirdId) {
            getThird(thirdId).enable();
        }

        @Override
        public void removeAll() {
            thirdRepository.deleteAll();
        }
    }
}
