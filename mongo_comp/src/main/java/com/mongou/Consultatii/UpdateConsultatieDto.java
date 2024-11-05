package com.mongou.Consultatii;


import com.mongou.enums.Diagnostic;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class UpdateConsultatieDto {
    private Diagnostic diagnostic;

}
