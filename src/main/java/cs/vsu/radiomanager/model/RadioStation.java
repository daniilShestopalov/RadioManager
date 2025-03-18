package cs.vsu.radiomanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "radio_station")
@Getter
@Setter
public class RadioStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, length = 100, nullable = false)
    private String name;

    @Digits(integer = 3, fraction = 1)
    @Column(name = "frequency", nullable = false, precision = 4, scale = 1)
    private Double frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "id" ,nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private City city;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User representative;

}
