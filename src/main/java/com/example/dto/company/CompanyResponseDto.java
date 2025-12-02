package com.example.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyResponseDto {

    Integer id;

    String createdAt;

    String updatedAt;

    List<Integer> category;

    String name;

    @JsonProperty("company_url")
    String companyUrl;

    @JsonProperty("career_url")
    String careerUrl;

    @JsonProperty("logo_url")
    String logoUrl;

    @JsonProperty("category_virtual_map")
    private Integer categoryVirtualMap;

    @JsonProperty("present_in_virtual_map")
    private Boolean presentInVirtualMap;

    List<Integer> recruiters;
}
