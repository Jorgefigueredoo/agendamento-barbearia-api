package br.com.jorgefigueredoo.agendamento_barbearia_api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

public class SalvarHorarioTrabalhoRequest {

    @NotNull
    private Long barberId;

    @NotNull @Min(1) @Max(7)
    private Integer dayOfWeek; // 1=Seg ... 7=Dom (ISO)

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private Boolean active = true;

    public Long getBarberId() { return barberId; }
    public void setBarberId(Long barberId) { this.barberId = barberId; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
