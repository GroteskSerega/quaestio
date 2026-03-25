package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.entity.Site;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    @Modifying
    @Query("DELETE FROM Site s WHERE id IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);

    Site findByName(String name);
}
