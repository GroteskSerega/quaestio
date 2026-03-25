package searchengine.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.entity.Lemma;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    @Modifying
    @Query("DELETE FROM Lemma l WHERE l.site.id IN :siteIds")
    void deleteBySiteIds(@Param("siteIds") List<Integer> siteIds);

    Integer countAllBySiteId(Integer siteId);

    @Query("SELECT l.id FROM Lemma l WHERE l.site.id IN (:siteId) AND l.lemma = :lemma")
    List<Integer> findAllIdBySiteIdInAndLemma(@Param("siteId") List<Integer> siteId, @Param("lemma") String lemma);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    @Query("SELECT l FROM Lemma l WHERE l.site.id = :siteId AND l.lemma = :lemma")
    Optional<Lemma> findBySiteIdAndLemmaWithLock(@Param("siteId") Integer siteId, @Param("lemma") String lemma);

    List<Lemma> findAllBySiteIdInAndLemmaIn(List<Integer> siteId, List<String> lemma);
}
