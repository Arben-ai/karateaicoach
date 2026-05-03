package ch.zhaw.karateaicoach.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Mail {
    private String to;
    private String subject;
    private String message;
}
