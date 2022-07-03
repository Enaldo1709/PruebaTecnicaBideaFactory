package com.bideafactory.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookModel {
    private String id;
    private String name;
    private String lastname;
    private int age;
    private String phoneNumber;
    private Date startDate;
    private Date endDate;
    private String houseId;
    private String discountCode;
}
