package com.midas.shootpointer.global.util.encrypt;

import com.midas.shootpointer.global.util.ApplicationContextProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;

@Convert
public class EncryptConverter implements AttributeConverter<String, String> {

    private AesUtil getAesUtil() {
        return ApplicationContextProvider.getBean(AesUtil.class);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return attribute == null ? null : getAesUtil().encrypt(attribute);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : getAesUtil().decrypt(dbData);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
