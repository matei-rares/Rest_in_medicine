package com.lab4.Programari;


import com.lab4.enums.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProgramareUpdateDto {
    private Status status;

}
