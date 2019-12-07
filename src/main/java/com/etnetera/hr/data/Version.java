package com.etnetera.hr.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(max = 20)
    @Column(nullable = false, length = 20, unique = true)
    private String name;

    @Column
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate releaseDate;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private JavaScriptFramework javaScriptFramework;

    public Version(){
    }

    public Version(String name, LocalDate releaseDate) {
        setName(name);
        setReleaseDate(releaseDate);
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
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return "Version{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public JavaScriptFramework getJavaScriptFramework() {
        return javaScriptFramework;
    }

    public void setJavaScriptFramework(JavaScriptFramework javaScriptFramework) {
        this.javaScriptFramework = javaScriptFramework;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
