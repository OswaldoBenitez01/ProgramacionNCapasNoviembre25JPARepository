
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUsuarioJPA extends JpaRepository<Usuario, Integer>{ 
    List<Usuario> findAllByOrderByIdUsuarioAsc();
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:nombre IS NULL OR :nombre = '' OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:apellidoPaterno IS NULL OR :apellidoPaterno = '' OR LOWER(u.apellidoPaterno) LIKE LOWER(CONCAT('%', :apellidoPaterno, '%'))) AND " +
           "(:apellidoMaterno IS NULL OR :apellidoMaterno = '' OR LOWER(u.apellidoMaterno) LIKE LOWER(CONCAT('%', :apellidoMaterno, '%'))) AND " +
           "(:idRol IS NULL OR u.rol.idRol = :idRol)")
    List<Usuario> busquedaAbierta(@Param("nombre") String nombre,
                                   @Param("apellidoPaterno") String apellidoPaterno,
                                   @Param("apellidoMaterno") String apellidoMaterno,
                                   @Param("idRol") Integer idRol);
    Usuario findByUsername(String username);
}
