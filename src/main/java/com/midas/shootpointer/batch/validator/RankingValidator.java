package com.midas.shootpointer.batch.validator;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.mongodb.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Component
@Slf4j
public class RankingValidator implements JobParametersValidator {
    @Override
    public void validate(@Nullable JobParameters parameters) throws JobParametersInvalidException {
        String type = parameters.getString("rankingType");
        if (type == null || type.isEmpty()) {
            log.error("ranking parameter is null parameter value = {}",type);
            throw new JobParametersInvalidException("ranking parameter의 값이 Null 입니다.");
        }

        /**
         * ======= RankingType 검증 =======
         */
        try {
            RankingType.valueOf(type);

        }catch (IllegalArgumentException e){
            log.error("ranking parameter is invalid parameter value= {}",type);
            throw new JobParametersInvalidException("유효하지 않은 파라미터 값입니다.");
        }

        /**
         * ======= DateTime 검증 =======
         */
        String endStr=parameters.getString("end");
        if (endStr == null || endStr.isBlank()){
            log.error("ranking parameter의 값이 Null 입니다. value = {}",endStr);
            throw new JobParametersInvalidException("ranking parameter의 값이 Null 입니다.");
        }

        try {
            LocalDateTime end=LocalDateTime.parse(endStr);

            //미래 시간
            if (end.isAfter(LocalDateTime.now().plusSeconds(1))){
                log.error("end 파라미터는 현재 시각 이후일 수 없습니다. value = {}",endStr);
                throw new JobParametersInvalidException("end 파라미터는 현재 시각 이후일 수 없습니다.");
            }
        }catch (DateTimeParseException e){
            log.error("end parameter is invalid value");
            throw new JobParametersInvalidException("end 파라미터 형식이 잘못되었습니다.");
        }
    }
}
