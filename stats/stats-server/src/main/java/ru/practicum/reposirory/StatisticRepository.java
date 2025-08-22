package ru.practicum.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

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
    List<ResponseStatsDto> getByUris(String start, String end, List<String> uris);

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
    List<ResponseStatsDto> getByAllUris(String start, String end);

    interface ResponseStatsDto {
        String getApp();

        String getUri();

        Long getHits();

        Long getUniqHits();
    }
}
