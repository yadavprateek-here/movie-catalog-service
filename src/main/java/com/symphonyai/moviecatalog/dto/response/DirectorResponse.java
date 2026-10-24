package com.symphonyai.moviecatalog.dto.response;

public class DirectorResponse {

    private Long id;
    private String name;
    private String nationality;
    private Integer birthYear;

    public DirectorResponse() {
    }

    public DirectorResponse(Long id, String name, String nationality, Integer birthYear) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.birthYear = birthYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }
}
