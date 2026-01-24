package br.com.jorgefigueredoo.agendamento_barbearia_api.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_trabalho")
public class HorarioTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // por barbeiro (se quiser global, pode deixar nullable)
    @ManyToOne(optional = false)
    @JoinColumn(name = "barber_id")
    private Barber barber;

    // 1=Seg ... 7=Dom (vamos usar ISO)
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Boolean active = true;

    public HorarioTrabalho() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Barber getBarber() { return barber; }
    public void setBarber(Barber barber) { this.barber = barber; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
