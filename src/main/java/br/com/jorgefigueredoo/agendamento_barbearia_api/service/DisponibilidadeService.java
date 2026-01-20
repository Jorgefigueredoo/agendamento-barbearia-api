package br.com.jorgefigueredoo.agendamento_barbearia_api.service;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.HorarioDisponivelResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.enums.StatusAgendamento;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Barber;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.ServiceEntity;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.AgendamentoRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.BarberRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadeService {

    private final ServiceRepository serviceRepository;
    private final BarberRepository barberRepository;
    private final AgendamentoRepository agendamentoRepository;

    private final LocalTime dayStart;
    private final LocalTime dayEnd;
    private final int slotMinutes;

    public DisponibilidadeService(ServiceRepository serviceRepository,
                                 BarberRepository barberRepository,
                                 AgendamentoRepository agendamentoRepository,
                                 @Value("${app.availability.day-start}") String dayStart,
                                 @Value("${app.availability.day-end}") String dayEnd,
                                 @Value("${app.availability.slot-minutes}") int slotMinutes) {
        this.serviceRepository = serviceRepository;
        this.barberRepository = barberRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.dayStart = LocalTime.parse(dayStart);
        this.dayEnd = LocalTime.parse(dayEnd);
        this.slotMinutes = slotMinutes;
    }

    public List<HorarioDisponivelResponse> listar(LocalDate date, Long serviceId, String barberIdOrAny) {
        ServiceEntity servico = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        int duracao = servico.getDurationMin();
        List<StatusAgendamento> ativos = List.of(StatusAgendamento.PENDENTE, StatusAgendamento.CONFIRMADO);

        // barbeiro específico
        if (!"any".equalsIgnoreCase(barberIdOrAny)) {
            Long barberId = Long.valueOf(barberIdOrAny);
            return slotsParaBarbeiro(date, barberId, duracao, ativos);
        }

        // qualquer barbeiro: para cada horário, pega o primeiro barbeiro livre
        List<Barber> barbers = barberRepository.findByActiveTrueOrderByNameAsc();
        List<HorarioDisponivelResponse> result = new ArrayList<>();

        LocalDateTime startDay = date.atTime(dayStart);

        for (LocalDateTime slotStart = startDay;
             !slotStart.plusMinutes(duracao).toLocalTime().isAfter(dayEnd);
             slotStart = slotStart.plusMinutes(slotMinutes)) {

            LocalDateTime slotEnd = slotStart.plusMinutes(duracao);

            for (Barber b : barbers) {
                boolean conflito = agendamentoRepository.existsOverlap(b.getId(), slotStart, slotEnd, ativos);
                if (!conflito) {
                    result.add(new HorarioDisponivelResponse(slotStart, slotEnd, b.getId()));
                    break;
                }
            }
        }

        return result;
    }

    private List<HorarioDisponivelResponse> slotsParaBarbeiro(LocalDate date,
                                                              Long barberId,
                                                              int duracao,
                                                              List<StatusAgendamento> ativos) {
        barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        List<HorarioDisponivelResponse> slots = new ArrayList<>();
        LocalDateTime startDay = date.atTime(dayStart);

        for (LocalDateTime slotStart = startDay;
             !slotStart.plusMinutes(duracao).toLocalTime().isAfter(dayEnd);
             slotStart = slotStart.plusMinutes(slotMinutes)) {

            LocalDateTime slotEnd = slotStart.plusMinutes(duracao);

            boolean conflito = agendamentoRepository.existsOverlap(barberId, slotStart, slotEnd, ativos);
            if (!conflito) {
                slots.add(new HorarioDisponivelResponse(slotStart, slotEnd, barberId));
            }
        }

        return slots;
    }
}
