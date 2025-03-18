package cs.vsu.radiomanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "radio_station")
@Getter
@Setter
public class RadioStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Digits(integer = 3, fraction = 1)
    @Positive
    @Column(name = "frequency", nullable = false, precision = 4, scale = 1)
    private BigDecimal frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "id" ,nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private City city;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User representative;

}
