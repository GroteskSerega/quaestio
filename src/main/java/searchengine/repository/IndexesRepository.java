package searchengine.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.entity.Index;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexesRepository extends JpaRepository<Index, Integer> {

    @Modifying
    @Query("DELETE FROM Index i WHERE i.page.id = :pageId")
    void deleteByPageId(@Param("pageId") Integer pageId);

    @Modifying
    @Query("DELETE FROM Index i WHERE i.page.id IN :pageIds")
    void deleteByPageIdIn(@Param("pageIds") List<Integer> pageIds);

    @Modifying
    @Transactional
    void deleteFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId);

    @Query("SELECT i.id FROM Index i JOIN i.page p WHERE p.site.id = :siteId")
    List<Integer> findAllIdsBySiteId(@Param("siteId") Integer siteId);

    Iterable<Index> findAllByPageId(Integer pageId);

    Optional<Index> findByLemmaIdAndPageId(Integer lemmaId, Integer pageId);

    Integer countAllByLemmaIdIn(List<Integer> lemmaId);

    @Query("SELECT p.id FROM Index i JOIN i.page p WHERE p.site.id IN (:siteId) and i.lemma.id = :lemmaId")
    List<Integer> findAllPageIdsBySiteIdInAndLemmaId(@Param("siteId") List<Integer> siteId, @Param("lemmaId") Integer lemmaId);

    @Query("SELECT p.id FROM Index i JOIN i.page p JOIN i.lemma l WHERE p.site.id IN (:siteId) and l.lemma = :lemma")
    List<Integer> findAllPageIdsBySiteIdInAndLemma(@Param("siteId") List<Integer> siteId, @Param("lemma") String lemma);

    Index findFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId);
}
