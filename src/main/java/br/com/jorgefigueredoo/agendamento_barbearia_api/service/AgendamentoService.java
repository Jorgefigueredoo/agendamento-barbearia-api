package br.com.jorgefigueredoo.agendamento_barbearia_api.service;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.AgendamentoResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.CriarAgendamentoRequest;
import br.com.jorgefigueredoo.agendamento_barbearia_api.enums.StatusAgendamento;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Appointment;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Barber;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.ServiceEntity;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.AgendamentoRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.BarberRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ServiceRepository serviceRepository;
    private final BarberRepository barberRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ServiceRepository serviceRepository,
                              BarberRepository barberRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.serviceRepository = serviceRepository;
        this.barberRepository = barberRepository;
    }

    @Transactional
    public AgendamentoResponse criar(CriarAgendamentoRequest req) {
        ServiceEntity servico = serviceRepository.findById(req.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Barber barbeiro = barberRepository.findById(req.getBarbeiroId())
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        LocalDateTime start = req.getStartTime();
        LocalDateTime end = start.plusMinutes(servico.getDurationMin());

        boolean conflito = agendamentoRepository.existsOverlap(
                barbeiro.getId(),
                start,
                end,
                List.of(StatusAgendamento.PENDENTE, StatusAgendamento.CONFIRMADO)
        );

        if (conflito) {
            throw new RuntimeException("Horário indisponível para esse barbeiro");
        }

        Appointment ag = new Appointment();
        ag.setClientName(req.getClientName());
        ag.setClientPhone(req.getClientPhone());
        ag.setNotes(req.getNotes());
        ag.setStatus(StatusAgendamento.PENDENTE);
        ag.setService(servico);
        ag.setBarber(barbeiro);
        ag.setStartTime(start);
        ag.setEndTime(end);
        ag.setCreatedAt(LocalDateTime.now());

        Appointment salvo = agendamentoRepository.save(ag);
        return toResponse(salvo);
    }

    public List<AgendamentoResponse> listarPorTelefone(String phone) {
        return agendamentoRepository.findByClientPhoneOrderByStartTimeDesc(phone)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id) {
        Appointment ag = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        ag.setStatus(StatusAgendamento.CANCELADO);
        return toResponse(ag);
    }

    @Transactional
    public AgendamentoResponse confirmar(Long id) {
        Appointment ag = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        ag.setStatus(StatusAgendamento.CONFIRMADO);
        return toResponse(ag);
    }

    public List<AgendamentoResponse> listarAgendaDoDia(LocalDate date, Long barberId) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        return agendamentoRepository.findAgendaDoDia(from, to, barberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * range:
     * - today  -> só hoje
     * - 7d     -> próximos 7 dias (inclui o 7º dia inteiro)
     * - 30d    -> próximos 30 dias (inclui o 30º dia inteiro)
     * - all    -> todos
     */
    public List<AgendamentoResponse> listarPorRange(String range, Long barberId) {
        if (range == null || range.isBlank()) range = "today";

        if (range.equalsIgnoreCase("all")) {
            return agendamentoRepository.findAllByOrderByStartTimeAsc()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to;

        switch (range.toLowerCase()) {
            case "7d":
                to = today.plusDays(7).plusDays(1).atStartOfDay();
                break;

            case "30d":
                to = today.plusDays(30).plusDays(1).atStartOfDay();
                break;

            case "today":
            default:
                to = today.plusDays(1).atStartOfDay();
                break;
        }

        return agendamentoRepository.findByRange(from, to, barberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AgendamentoResponse toResponse(Appointment ag) {
        AgendamentoResponse r = new AgendamentoResponse();
        r.setId(ag.getId());
        r.setStatus(ag.getStatus());

        r.setClientName(ag.getClientName());
        r.setClientPhone(ag.getClientPhone());
        r.setNotes(ag.getNotes());

        r.setServicoId(ag.getService().getId());
        r.setServicoNome(ag.getService().getName());
        r.setServicoDuracaoMin(ag.getService().getDurationMin());

        r.setBarbeiroId(ag.getBarber().getId());
        r.setBarbeiroNome(ag.getBarber().getName());

        r.setStartTime(ag.getStartTime());
        r.setEndTime(ag.getEndTime());
        return r;
    }
}
