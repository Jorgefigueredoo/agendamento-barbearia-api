package br.com.jorgefigueredoo.agendamento_barbearia_api.repository;

import br.com.jorgefigueredoo.agendamento_barbearia_api.model.HorarioTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioTrabalhoRepository extends JpaRepository<HorarioTrabalho, Long> {
    List<HorarioTrabalho> findByBarberIdOrderByDayOfWeekAsc(Long barberId);
    Optional<HorarioTrabalho> findByBarberIdAndDayOfWeek(Long barberId, Integer dayOfWeek);
}
