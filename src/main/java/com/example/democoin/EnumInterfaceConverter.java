package com.example.democoin;

import com.example.democoin.configuration.enums.EnumInterface;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter
public abstract class EnumInterfaceConverter<T extends EnumInterface> implements AttributeConverter<T, String> {

    private final Class<T> clazz;

    public EnumInterfaceConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.getType();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        T[] enumConstants = clazz.getEnumConstants();
        for (T constant : enumConstants) {
            if (StringUtils.equals(constant.getType(), dbData)) {
                return constant;
            }
        }

        throw new UnsupportedOperationException(String.format("%s 지원하지 않는 enum 형식입니다.", dbData));
    }



}
