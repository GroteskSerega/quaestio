package searchengine.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;

import java.util.List;

@Repository
public interface IndexesRepository extends CrudRepository<Index, Integer> {
    @Modifying
    @Transactional
    void deleteAllByPageId(Integer pageId);

    @Modifying
    @Transactional
    void deleteAllByPageIdIn(List<Integer> pageId);

    @Modifying
    @Transactional
    void deleteFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId);

    @Query("SELECT i.id FROM Index i JOIN i.page p WHERE p.site.id = :siteId")
    List<Integer> findAllIdsBySiteId(@Param("siteId") Integer siteId);

    Iterable<Index> findAllByPageId(Integer pageId);
}
