import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.studentsubjectapi.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByMatricula(String matricula);
    List<Student> findByNomeContaining(String nome);
}