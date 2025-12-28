package searchengine.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmasRepository extends CrudRepository<Lemma, Integer> {

    @Modifying
    @Transactional
    void deleteFirstBySiteId(Integer siteId);

    @Modifying
    @Transactional
    void deleteAllBySiteId(Integer siteId);

    @Modifying
    @Transactional
    void deleteAllByIdIn(List<Integer> id);

    Integer countAllBySiteId(Integer siteId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Lemma> findBySiteIdAndLemma(Integer siteId, String lemma);
}
