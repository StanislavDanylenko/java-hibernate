package stanislav.danylenko.hibernate.entities.lifecycle;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "persons_seq_key")
@Getter
@Setter
@ToString
public class PersonSequenceKey {

    // hibernate asks sequence for next id before inserting
    // for IDENTITY record is inserted without ID and then ID is inserted
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String name;


    public PersonSequenceKey() {}

    public PersonSequenceKey(String name) {
        this.name = name;
    }
}
