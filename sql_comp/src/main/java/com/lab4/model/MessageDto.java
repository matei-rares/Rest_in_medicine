package com.lab4.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MessageDto {
    private String message;

    public MessageDto(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
