package com.qmate.domain.pet.repository;


import com.qmate.domain.match.Match;
import java.util.Optional;
import com.qmate.domain.pet.entity.Pet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

  Optional<Pet> findByMatch(Match match);

  Optional<Pet> findByMatch_Id(Long matchId);
}