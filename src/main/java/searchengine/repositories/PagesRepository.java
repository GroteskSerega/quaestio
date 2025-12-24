package searchengine.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;

@Repository
public interface PagesRepository extends CrudRepository<Page, Integer> {
    @Modifying
    @Transactional
    void deleteAllBySiteId(Integer siteId);

//    @Transactional
    Page findFirstBySiteIdAndPath(Integer siteId, String path);

    Page findFirstBySiteId(Integer siteId);
}
