package com.example.microticket.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminChartsDto {

    private List<String> weekDates;
    private List<BigDecimal> weekBox;

    private List<NameValue> typeMovieCount;
    private TypeBoxOffice typeBoxOffice;

    @Data
    public static class NameValue {
        private String name;
        private Integer value;

        public NameValue(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }

    @Data
    public static class TypeBoxOffice {
        private List<String> types;
        private List<BigDecimal> values;

        public TypeBoxOffice(List<String> types, List<BigDecimal> values) {
            this.types = types;
            this.values = values;
        }
    }
}
