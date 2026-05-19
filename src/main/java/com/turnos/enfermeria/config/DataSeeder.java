package com.turnos.enfermeria.config;

import com.turnos.enfermeria.model.entity.Entidad;
import com.turnos.enfermeria.model.entity.TipoPersonal;
import com.turnos.enfermeria.repository.EntidadRepository;
import com.turnos.enfermeria.repository.TipoPersonalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final EntidadRepository entidadRepository;
    private final TipoPersonalRepository tipoPersonalRepository;

    @Override
    public void run(String... args) {
        if (entidadRepository.count() == 0) {
            log.info("Sembrando datos iniciales de entidades...");
            String[] entidades = {
                    "FUNCIONARIOS_PLANTA", "ASICA", "SIMED", "ASOCIRGE",
                    "ASOMI", "ASOTERAPEUTAS", "ASTRASALUD", "IMPORSALUD",
                    "SAIRENA", "SINTRAOEMPUH", "SITSALUD", "VHR",
                    "INSACON", "CLINIREUMA", "OTRA"
            };
            for (String nombre : entidades) {
                Entidad e = new Entidad();
                e.setNombre(nombre);
                e.setSigla(nombre.length() > 6 ? nombre.substring(0, 6) : nombre);
                e.setEstado(true);
                entidadRepository.save(e);
            }
            log.info("{} entidades sembradas.", entidades.length);
        }

        if (tipoPersonalRepository.count() == 0) {
            log.info("Sembrando datos iniciales de tipos de personal...");
            String[][] tipos = {
                    {"ENFERMERO", "ENF"},
                    {"AUXILIAR", "AUX"},
                    {"MEDICO", "MED"},
                    {"TERAPEUTA", "TER"},
                    {"OTRO", "OTR"}
            };
            for (String[] tp : tipos) {
                TipoPersonal t = new TipoPersonal();
                t.setNombre(tp[0]);
                t.setSigla(tp[1]);
                t.setEstado(true);
                tipoPersonalRepository.save(t);
            }
            log.info("{} tipos de personal sembrados.", tipos.length);
        }
    }
}
