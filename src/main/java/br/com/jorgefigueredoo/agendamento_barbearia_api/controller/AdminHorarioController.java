package br.com.jorgefigueredoo.agendamento_barbearia_api.controller;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.SalvarHorarioTrabalhoRequest;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Barber;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.HorarioTrabalho;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.BarberRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.HorarioTrabalhoRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/horarios")
public class AdminHorarioController {

    private final HorarioTrabalhoRepository horarioRepo;
    private final BarberRepository barberRepo;

    public AdminHorarioController(HorarioTrabalhoRepository horarioRepo, BarberRepository barberRepo) {
        this.horarioRepo = horarioRepo;
        this.barberRepo = barberRepo;
    }

    // GET /admin/horarios?barberId=1
    @GetMapping
    public List<HorarioTrabalho> listar(@RequestParam Long barberId) {
        return horarioRepo.findByBarberIdOrderByDayOfWeekAsc(barberId);
    }

    // POST /admin/horarios (upsert por barberId + dayOfWeek)
    @PostMapping
    public HorarioTrabalho salvar(@RequestBody @Valid SalvarHorarioTrabalhoRequest req) {
        Barber barber = barberRepo.findById(req.getBarberId())
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new RuntimeException("startTime precisa ser antes de endTime");
        }

        HorarioTrabalho ht = horarioRepo.findByBarberIdAndDayOfWeek(req.getBarberId(), req.getDayOfWeek())
                .orElseGet(HorarioTrabalho::new);

        ht.setBarber(barber);
        ht.setDayOfWeek(req.getDayOfWeek());
        ht.setStartTime(req.getStartTime());
        ht.setEndTime(req.getEndTime());
        ht.setActive(req.getActive() != null ? req.getActive() : true);

        return horarioRepo.save(ht);
    }
}
