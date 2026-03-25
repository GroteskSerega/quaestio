package searchengine.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.entity.Index;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    @Modifying
    @Query("DELETE FROM Index i WHERE i.page.id = :pageId")
    void deleteByPageId(@Param("pageId") Integer pageId);

    @EntityGraph(attributePaths = {"page", "lemma"})
    List<Index> findAllByPageId(Integer pageId);

    Integer countAllByLemmaIdIn(List<Integer> lemmaId);

    @Query("SELECT p.id FROM Index i JOIN i.page p JOIN i.lemma l WHERE p.site.id IN (:siteId) and l.lemma = :lemma")
    List<Integer> findAllPageIdsBySiteIdInAndLemma(@Param("siteId") List<Integer> siteId, @Param("lemma") String lemma);

    Index findFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId);

    @Modifying
    @Query("DELETE FROM Index i WHERE i.page.site.id IN :siteIds")
    void deleteBySiteIds(@Param("siteIds") List<Integer> siteIds);
}
