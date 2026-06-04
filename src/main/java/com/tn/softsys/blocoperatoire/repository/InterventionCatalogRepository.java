package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.InterventionCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface InterventionCatalogRepository extends JpaRepository<InterventionCatalog, UUID> {

    Page<InterventionCatalog> findByActiveTrueOrderByDesignationAsc(Pageable pageable);

    @Query("""
            select c
            from InterventionCatalog c
            where c.active = true
              and (
                    lower(coalesce(c.designation, '')) like lower(concat('%', :designation, '%'))
                 or lower(coalesce(c.designationEn, '')) like lower(concat('%', :designation, '%'))
                 or lower(coalesce(c.designationAr, '')) like lower(concat('%', :designation, '%'))
              )
            order by c.designation asc
            """)
    Page<InterventionCatalog> searchActiveCatalog(String designation, Pageable pageable);
}
