package stanislav.danylenko.hibernate.entities.lifecycle;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.Instant;


@Entity
@Table(name = "persons")
@Getter
@Setter
@ToString
@NamedQuery(
        name="findById",
        query="SELECT p FROM Person p WHERE p.id = :idparam"
)
@FilterDef(name = "testFilter",
        parameters = @ParamDef(name = "testVal", type = Boolean.class),
        defaultCondition = "isActive = :testVal")
@Filter(name = "testFilter")
public class Person {

    // hibernate asks sequence for next id before inserting
    // for IDENTITY record is inserted without ID and then ID is inserted
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    private Instant birthDate;

    private boolean isActive;

    public Person() {}

    public Person(String name, Instant birthDate, boolean isActive) {
        this.name = name;
        this.birthDate = birthDate;
        this.isActive = isActive;
    }
}
