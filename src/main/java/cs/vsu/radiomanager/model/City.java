package cs.vsu.radiomanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "city", uniqueConstraints = {
        @UniqueConstraint(
                name = "UNIQ_NAME_REGION",
                columnNames = {
                        "name", "region"
                })
})
@Getter
@Setter
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "region", length = 100, nullable = false)
    private String region;

}
