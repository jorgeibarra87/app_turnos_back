package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.events.CambioEquipoEvent;
import com.turnos.enfermeria.model.dto.response.EquipoDTO;
import com.turnos.enfermeria.model.dto.request.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.response.MiembroPerfilDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.CambiosEquipoService;
import com.turnos.enfermeria.service.EquipoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final PersonaRepository personaRepository;
    private final ModelMapper modelMapper;
    private final ServicioRepository servicioRepository;
    private final MacroprocesosRepository macroprocesoRepository;
    private final ProcesosRepository procesoRepository;
    private final SeccionesServicioRepository seccionRepository;
    private final SubseccionesServicioRepository subseccionRepository;
    private final CambiosEquipoService cambiosEquipoService;
    private final ApplicationEventPublisher eventPublisher;

    private String generateOrValidateName(String providedName, EquipoSelectionDTO selection) {
        if (providedName == null || providedName.trim().isEmpty() || selection != null) {
            return generateEquipoName(selection);
        }
        return providedName.trim();
    }

    public EquipoDTO create(EquipoDTO equipoDTO) {
        return create(equipoDTO, null);
    }

    public EquipoDTO create(EquipoDTO equipoDTO, EquipoSelectionDTO selection) {
        Equipo equipo = modelMapper.map(equipoDTO, Equipo.class);

        String nombre = generateOrValidateName(equipoDTO.getNombre(), selection);
        equipo.setNombre(nombre);
        equipo.setEstado(true);
        equipo.setObservaciones(equipoDTO.getObservaciones());

        Equipo equipoGuardado = equipoRepository.save(equipo);
        cambiosEquipoService.registrarCambioEquipo(null, equipoGuardado, "CREACION");

        eventPublisher.publishEvent(new CambioEquipoEvent(
                equipoGuardado.getIdEquipo(),
                "CREACIÓN DE EQUIPO",
                "Se ha creado un nuevo equipo: " + equipoGuardado.getNombre() +
                        ". Categoría: " + (selection != null ? selection.getCategoria() : "Individual") +
                        ". Subcategoría: " + (selection != null ? selection.getSubcategoria() : "No especificada")
        ));
        return modelMapper.map(equipoGuardado, EquipoDTO.class);
    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id) {
        return update(detalleEquipoDTO, id, null);
    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id, EquipoSelectionDTO selection) {
        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        String nombreAnterior = equipoExistente.getNombre();

        if (detalleEquipoDTO.getNombre() != null || selection != null) {
            String nuevoNombre = generateOrValidateName(detalleEquipoDTO.getNombre(), selection);
            equipoExistente.setNombre(nuevoNombre);
        }

        if (detalleEquipoDTO.getIdEquipo() != null) {
            equipoExistente.setIdEquipo(detalleEquipoDTO.getIdEquipo());
        }
        if (detalleEquipoDTO.getObservaciones() != null) {
            equipoExistente.setObservaciones(detalleEquipoDTO.getObservaciones());
        }

        Equipo equipoActualizado = equipoRepository.save(equipoExistente);
        cambiosEquipoService.registrarCambioEquipo(equipoActualizado, equipoActualizado, "MODIFICACION");

        eventPublisher.publishEvent(new CambioEquipoEvent(
                equipoActualizado.getIdEquipo(),
                "MODIFICACIÓN DE EQUIPO",
                "Se ha modificado el equipo ID: " + id +
                        ". Nombre anterior: " + nombreAnterior +
                        ". Nuevo nombre: " + equipoActualizado.getNombre()
        ));
        return modelMapper.map(equipoActualizado, EquipoDTO.class);
    }

    public Optional<EquipoDTO> findById(Long idEquipo) {
        return equipoRepository.findById(idEquipo)
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class));
    }

    public List<EquipoDTO> findAll() {
        return equipoRepository.findAllByOrderByIdEquipoAsc()
                .stream()
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));

        String nombreEquipo = equipo.getNombre();

        List<Persona> personasDelEquipo = personaRepository.findDistinctByEquipos_IdEquipo(idEquipo);

        for (Persona persona : personasDelEquipo) {
            cambiosEquipoService.registrarCambioPersonaEquipo(
                    persona,
                    equipo,
                    null,
                    "DESVINCULACION"
            );
            persona.getEquipos().remove(equipo);
            personaRepository.save(persona);
        }

        cambiosEquipoService.registrarCambioEquipo(equipo, null, "ELIMINACION");

        eventPublisher.publishEvent(new CambioEquipoEvent(
                idEquipo,
                "ELIMINACIÓN DE EQUIPO",
                "Se ha eliminado el equipo: " + nombreEquipo +
                        ". Se desvincularon " + personasDelEquipo.size() + " personas del equipo."
        ));
        equipoRepository.deleteById(idEquipo);
    }

    public EquipoDTO createWithGeneratedName(EquipoSelectionDTO selection) {
        EquipoDTO equipoDTO = new EquipoDTO();
        equipoDTO.setEstado(true);
        equipoDTO.setObservaciones(selection.getObservaciones());
        return create(equipoDTO, selection);
    }

    public EquipoDTO updateWithGeneratedName(Long idEquipo, EquipoSelectionDTO selection) {
        EquipoDTO equipoDTO = new EquipoDTO();
        equipoDTO.setObservaciones(selection.getObservaciones());
        return update(equipoDTO, idEquipo, selection);
    }

    public String generateEquipoName(EquipoSelectionDTO selection) {
        if (selection == null || selection.getCategoria() == null || selection.getSubcategoria() == null) {
            throw new IllegalArgumentException("Categoría y subcategoría no pueden ser nulos");
        }

        String categoria = selection.getCategoria();
        String subcategoria = Normalizer.normalize(selection.getSubcategoria(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");

        String baseNombre = "EQUIPO_" + categoria + "_" + subcategoria;
        Long conteo = equipoRepository.countByNombreStartingWith(baseNombre);
        String consecutivo = String.format("_%02d", conteo + 1);

        return baseNombre + consecutivo;
    }

    public Equipo createEquipoWithGeneratedName(EquipoSelectionDTO selection) {
        EquipoDTO resultado = createWithGeneratedName(selection);
        return modelMapper.map(resultado, Equipo.class);
    }

    public List<MiembroPerfilDTO> obtenerMiembrosConPerfil(Long equipoId) {
        List<Object[]> resultados = equipoRepository.findMiembrosConPerfilRaw(equipoId);

        Map<Long, MiembroPerfilDTO> map = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            Long idPersona = ((Number) fila[0]).longValue();
            String nombre = (String) fila[1];
            String titulo = (String) fila[2];
            String documento = (String) fila[3];

            map.computeIfAbsent(idPersona, id -> new MiembroPerfilDTO(id, nombre, new ArrayList<>(), documento));
            if (titulo != null) {
                map.get(idPersona).getTitulos().add(titulo);
            }
        }

        return new ArrayList<>(map.values());
    }
}
