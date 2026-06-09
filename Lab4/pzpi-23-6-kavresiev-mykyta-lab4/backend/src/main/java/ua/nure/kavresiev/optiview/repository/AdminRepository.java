package ua.nure.kavresiev.optiview.repository;

import ua.nure.kavresiev.optiview.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}