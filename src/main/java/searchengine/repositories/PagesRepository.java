package searchengine.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagesRepository extends CrudRepository<Page, Integer> {
    @Modifying
    @Transactional
    void deleteAllBySiteId(Integer siteId);

    @Modifying
    @Transactional
    void deleteFirstBySiteIdAndPath(Integer siteId, String path);

//    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Page> findFirstBySiteIdAndPath(Integer siteId, String path);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Page> findBySiteIdAndPath(Integer siteId, String path);

    Page findFirstBySiteId(Integer siteId);

    Integer countAllBySiteId(Integer siteId);

    List<Page> getAllBySiteId(Integer siteId);

    @Query("SELECT p.id FROM Page p WHERE p.site.id = :siteId")
    List<Integer> findAllIdsBySiteId(@Param("siteId") Integer siteId);
}
