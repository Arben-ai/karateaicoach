package ch.zhaw.karateaicoach.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SportlerCreateDTO {
    private String name;
    private String email;
    private String guertelgrad;
    private double gewicht;
}