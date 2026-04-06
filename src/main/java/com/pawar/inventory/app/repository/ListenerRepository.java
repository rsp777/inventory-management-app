package com.pawar.inventory.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.Listener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListenerRepository extends JpaRepository<Listener, Long> {

    List<Listener> findAllByOrderByUpdatedAtDesc();
    
    // Find all listeners by status
    List<Listener> findByStatus(String status);
    
    // Find all active listeners
    default List<Listener> findAllActive() {
        return findByStatus("active");
    }
    
    // Find all inactive listeners
    default List<Listener> findAllInactive() {
        return findByStatus("inactive");
    }
    
    // Find listener by name
    Optional<Listener> findByListenerName(String listenerName);
    
    // Check if listener name exists
    boolean existsByListenerName(String listenerName);
    
    // Find listeners by type
    List<Listener> findByListenerType(String listenerType);
    
    // Find listeners by port/channel
    List<Listener> findByPortChannel(String portChannel);
    
    // Search listeners by name or description
    @Query("SELECT l FROM Listener l WHERE " +
           "LOWER(l.listenerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.listenerType) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Listener> searchListeners(@Param("searchTerm") String searchTerm);
    
    // Find listeners with recent activity
    @Query("SELECT l FROM Listener l WHERE l.lastActivity >= :since ORDER BY l.lastActivity DESC")
    List<Listener> findListenersWithRecentActivity(@Param("since") LocalDateTime since);
    
    // Count active listeners
    long countByStatus(String status);
    
    // Find listeners by multiple IDs
    List<Listener> findByIdIn(List<Long> ids);
    
    // Custom query to find duplicate listener names (excluding current ID)
    @Query("SELECT l FROM Listener l WHERE l.listenerName = :name AND l.id != :id")
    Optional<Listener> findByListenerNameAndIdNot(@Param("name") String name, @Param("id") Long id);
}
