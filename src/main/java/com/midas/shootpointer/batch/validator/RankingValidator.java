package com.midas.shootpointer.batch.validator;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.mongodb.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RankingValidator implements JobParametersValidator {
    @Override
    public void validate(@Nullable JobParameters parameters) throws JobParametersInvalidException {
        if (parameters == null || parameters.isEmpty()) {
            log.error("ranking parameter is null parameter value = {}",parameters);
            throw new JobParametersInvalidException("ranking parameter의 값이 Null 입니다.");
        }
        String type = parameters.getString("rankingType");

        try {
            RankingType.valueOf(type);
        }catch (IllegalArgumentException e){
            log.error("ranking parameter is invalid parameter = {}",type);
            throw new JobParametersInvalidException("유효하지 않은 파라미터 값입니다.");
        }
    }
}
