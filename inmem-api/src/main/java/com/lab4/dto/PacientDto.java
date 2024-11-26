package com.lab4.dto;

import lombok.Data;



@Data
public class PacientDto {

    private long id;
    private String firstname;
    private String lastname;
    private int age;

    public PacientDto(long id, String firstname, String lastname, int age){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public void setId(long id) {this.id = id;}

    @Override
    public String toString() {
        return "PacientDto{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", age=" + age +
                '}';
    }

}
