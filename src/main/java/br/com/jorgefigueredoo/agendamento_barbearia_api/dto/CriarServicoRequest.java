package br.com.jorgefigueredoo.agendamento_barbearia_api.dto;

import jakarta.validation.constraints.*;

public class CriarServicoRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    @Min(5)
    private Integer durationMin;

    @NotNull
    @Min(0)
    private Integer priceCents;

    private Boolean active = true;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
