package searchengine.entity;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "lemma", indexes = {
        @Index(name = "idx_lemma_lemma", columnList = "lemma", unique = false),
        @Index(name = "idx_lemma_site_id_lemma", columnList = "site_id, lemma", unique = true)
})
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String lemma;

    @Column(nullable = false)
    private Integer frequency;
}
