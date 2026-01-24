package br.com.jorgefigueredoo.agendamento_barbearia_api.service;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.HorarioDisponivelResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.enums.StatusAgendamento;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Barber;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.HorarioTrabalho;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.ServiceEntity;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.AgendamentoRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.BarberRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.HorarioTrabalhoRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadeService {

    private final ServiceRepository serviceRepository;
    private final BarberRepository barberRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final HorarioTrabalhoRepository horarioTrabalhoRepository;

    private final int slotMinutes;

    public DisponibilidadeService(ServiceRepository serviceRepository,
                                 BarberRepository barberRepository,
                                 AgendamentoRepository agendamentoRepository,
                                 HorarioTrabalhoRepository horarioTrabalhoRepository,
                                 @Value("${app.availability.slot-minutes}") int slotMinutes) {
        this.serviceRepository = serviceRepository;
        this.barberRepository = barberRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.horarioTrabalhoRepository = horarioTrabalhoRepository;
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

        // qualquer barbeiro: para cada horário, pega o primeiro barbeiro livre (respeitando horário de trabalho)
        List<Barber> barbers = barberRepository.findByActiveTrueOrderByNameAsc();
        List<HorarioDisponivelResponse> result = new ArrayList<>();

        // dia da semana ISO: 1=Seg ... 7=Dom
        int dayOfWeek = date.getDayOfWeek().getValue();

        // Vamos gerar um “grid base” usando o intervalo de cada barbeiro.
        // Estratégia simples: percorre os barbeiros e tenta montar slots; para cada slot retornamos o primeiro disponível.
        // (Se preferir, dá pra ordenar por hora e mesclar melhor depois.)

        // Gera uma lista de candidatos de slots por horário: vamos usar o horário mínimo e máximo do dia entre barbeiros ativos.
        // Se ninguém trabalha no dia, retorna vazio.
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;

        for (Barber b : barbers) {
            HorarioTrabalho ht = horarioTrabalhoRepository.findByBarberIdAndDayOfWeek(b.getId(), dayOfWeek)
                    .orElse(null);

            if (ht == null || !Boolean.TRUE.equals(ht.getActive())) continue;

            LocalDateTime s = date.atTime(ht.getStartTime());
            LocalDateTime e = date.atTime(ht.getEndTime());

            if (minStart == null || s.isBefore(minStart)) minStart = s;
            if (maxEnd == null || e.isAfter(maxEnd)) maxEnd = e;
        }

        if (minStart == null || maxEnd == null) return result;

        // Percorre os slots do intervalo “geral” e tenta achar um barbeiro que trabalhe nesse horário e não tenha conflito
        for (LocalDateTime slotStart = minStart;
             !slotStart.plusMinutes(duracao).isAfter(maxEnd);
             slotStart = slotStart.plusMinutes(slotMinutes)) {

            LocalDateTime slotEnd = slotStart.plusMinutes(duracao);

            for (Barber b : barbers) {
                HorarioTrabalho ht = horarioTrabalhoRepository.findByBarberIdAndDayOfWeek(b.getId(), dayOfWeek)
                        .orElse(null);

                if (ht == null || !Boolean.TRUE.equals(ht.getActive())) continue;

                LocalDateTime workStart = date.atTime(ht.getStartTime());
                LocalDateTime workEnd = date.atTime(ht.getEndTime());

                // slot precisa caber dentro do horário do barbeiro
                if (slotStart.isBefore(workStart) || slotEnd.isAfter(workEnd)) continue;

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
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        if (!Boolean.TRUE.equals(barber.getActive())) {
            return List.of();
        }

        int dayOfWeek = date.getDayOfWeek().getValue();

        HorarioTrabalho ht = horarioTrabalhoRepository.findByBarberIdAndDayOfWeek(barberId, dayOfWeek)
                .orElse(null);

        // se não tem horário configurado ou está inativo, não tem disponibilidade
        if (ht == null || !Boolean.TRUE.equals(ht.getActive())) {
            return List.of();
        }

        LocalDateTime startDay = date.atTime(ht.getStartTime());
        LocalDateTime endDay = date.atTime(ht.getEndTime());

        List<HorarioDisponivelResponse> slots = new ArrayList<>();

        for (LocalDateTime slotStart = startDay;
             !slotStart.plusMinutes(duracao).isAfter(endDay);
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
