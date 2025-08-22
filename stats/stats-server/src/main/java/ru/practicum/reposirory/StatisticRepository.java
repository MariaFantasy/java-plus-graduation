package ru.practicum.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Hit, Long> {
    @Query(value = """
            SELECT
                h.app,
                h.uri,
                COUNT(h) AS hits,
                COUNT(DISTINCT h.ip) AS uniqHits
            FROM Hit h
            WHERE h.timestamp >= ?1
                AND h.timestamp <= ?2
                AND h.uri IN ?3
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
        """
    )
    List<ResponseStatsDto> getByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = """
            SELECT
                h.app,
                h.uri,
                COUNT(h) AS hits,
                COUNT(DISTINCT h.ip) AS uniqHits
            FROM Hit h
            WHERE h.timestamp >= ?1
                AND h.timestamp <= ?2
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
        """
    )
    List<ResponseStatsDto> getByAllUris(LocalDateTime start, LocalDateTime end);

    interface ResponseStatsDto {
        String getApp();

        String getUri();

        Long getHits();

        Long getUniqHits();
    }
}
