package searchengine.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.entity.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    @Modifying
    @Query("DELETE FROM Page p WHERE p.site.id IN :siteIds")
    void deleteBySiteIds(@Param("siteIds") List<Integer> siteIds);

    @Modifying
    @Transactional
    void deleteFirstBySiteIdAndPath(Integer siteId, String path);

    Optional<Page> findFirstBySiteIdAndPath(Integer siteId, String path);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
//    @Query("SELECT p FROM Page p WHERE p.site.siteId = :siteId AND p.path = :path")
//    Optional<Page> findBySiteIdAndPathWithLock(@Param("siteId") Integer siteId,@Param("path") String path);
    Optional<Page> findBySiteIdAndPath(Integer siteId, String path);

    Page findFirstBySiteId(Integer siteId);

    Integer countAllBySiteId(Integer siteId);
    Integer countAllBySiteIdIn(List<Integer> sitesId);
}
