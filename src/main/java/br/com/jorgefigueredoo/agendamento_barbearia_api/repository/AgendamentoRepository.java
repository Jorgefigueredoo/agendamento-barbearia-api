package br.com.jorgefigueredoo.agendamento_barbearia_api.repository;

import br.com.jorgefigueredoo.agendamento_barbearia_api.enums.StatusAgendamento;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientPhoneOrderByStartTimeDesc(String clientPhone);

    @Query("""
                select count(a) > 0 from Appointment a
                where a.barber.id = :barberId
                  and a.status in :statuses
                  and a.startTime < :endTime
                  and a.endTime > :startTime
            """)
    boolean existsOverlap(
            @Param("barberId") Long barberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") Collection<StatusAgendamento> statuses);

    @Query("""
              select a from Appointment a
              where a.startTime >= :from
                and a.startTime < :to
                and (:barberId is null or a.barber.id = :barberId)
              order by a.startTime asc
            """)
    java.util.List<Appointment> findAgendaDoDia(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("barberId") Long barberId);
}
