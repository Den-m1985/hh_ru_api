package com.example.dto.negotiation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SuperjobResponse<T> {
    private List<T> objects;
    private Integer total;
    private Boolean more;
}
