package io.github.ronaldobertolucci.mygames.model.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findCompaniesByNameContaining(String name);

    Page<Company> findCompaniesByNameContaining(String name, Pageable pageable);
}
