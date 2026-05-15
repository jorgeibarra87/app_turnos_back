package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.request.CuadroTurnoRequest;
import com.turnos.enfermeria.model.dto.request.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.request.ProgramacionDiariaRequest;
import com.turnos.enfermeria.model.dto.response.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.CuadroTurnoService;
import com.turnos.enfermeria.service.EquipoService;
import com.turnos.enfermeria.service.FullImportService;
import com.turnos.enfermeria.service.ProgramacionDiariaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FullImportServiceImpl implements FullImportService {

    private final PersonaRepository personaRepository;
    private final TitulosFormacionAcademicaRepository titulosRepository;
    private final TipoFormacionAcademicaRepository tipoFormacionRepository;
    private final EquipoRepository equipoRepository;
    private final EquipoService equipoService;
    private final CuadroTurnoService cuadroTurnoService;
    private final ProgramacionDiariaService programacionDiariaService;
    private final TipoJornadaRepository tipoJornadaRepository;
    private final ServicioRepository servicioRepository;
    private final ProcesosRepository procesosRepository;
    private final SeccionesServicioRepository seccionesRepository;
    private final SubseccionesServicioRepository subseccionesRepository;
    private final MacroprocesosRepository macroprocesosRepository;

    @Override
    public Map<String, Object> importarCompleto(
            MultipartFile file, String anio, String mes, String entidad, String tipoPersonal,
            Long idEquipo, String categoria, Long idMacroproceso, Long idProceso,
            Long idServicio, Long idSeccionServicio, Long idSubseccionServicio,
            String observaciones) throws Exception {

        Map<String, Object> resultado = new HashMap<>();
        List<String> creadas = new ArrayList<>();
        List<String> existentes = new ArrayList<>();

        Map<String, TipoJornada> codigosJornada = new HashMap<>();
        for (TipoJornada tj : tipoJornadaRepository.findAll()) {
            codigosJornada.put(tj.getCodigo().toUpperCase(), tj);
        }
        log.info("Códigos de jornada disponibles: {}", codigosJornada.keySet());

        TipoFormacionAcademica tipoDefault = tipoFormacionRepository.findByTipo("PROFESIONAL")
                .orElseGet(() -> tipoFormacionRepository.findAll().stream().findFirst().orElse(null));

        List<ProgramacionDiariaRequest.CeldaMatriz> celdas = new ArrayList<>();
        List<Long> personaIds = new ArrayList<>();
        String servicioDesdeExcel = null;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) {
                throw new IllegalArgumentException("El archivo Excel no tiene datos");
            }

            Row headerRow = sheet.getRow(0);
            String firstHeader = getCellValueAsString(headerRow.getCell(0));
            boolean isSimpleFormat = firstHeader != null &&
                    (firstHeader.toUpperCase().contains("NOMBRE") || firstHeader.toUpperCase().contains("TERAPEUTA"));

            int nameCol, docCol, perfilCol, servicioCol, firstDayCol;
            if (isSimpleFormat) {
                nameCol = 0; docCol = 1; perfilCol = 2; servicioCol = -1; firstDayCol = 3;
            } else {
                nameCol = 1; docCol = 2; perfilCol = 3; servicioCol = 0; firstDayCol = 4;
            }

            int diasDelMes = 0;
            for (int i = firstDayCol; i < headerRow.getLastCellNum(); i++) {
                String cv = getCellValueAsString(headerRow.getCell(i));
                if (cv != null && !cv.isEmpty()) {
                    try { Integer.parseInt(cv.trim()); diasDelMes++; }
                    catch (NumberFormatException e) {
                        if (!cv.toUpperCase().contains("HORA")) break;
                    }
                }
            }

            if (diasDelMes == 0) {
                throw new IllegalArgumentException("No se detectaron columnas de días en el encabezado");
            }

            log.info("Importación completa: {} filas, {} días, formato: {}, col nombre: {}, primer día col: {}",
                    sheet.getPhysicalNumberOfRows() - 1, diasDelMes,
                    isSimpleFormat ? "SIMPLIFICADO" : "ESTÁNDAR", nameCol, firstDayCol);

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                String nombre = getCellValueAsString(row.getCell(nameCol));
                if (nombre == null || nombre.trim().isEmpty()) continue;
                nombre = nombre.trim();

                if (nombre.toUpperCase().startsWith("CÓDIGO") || nombre.toUpperCase().startsWith("CODIGO")) {
                    continue;
                }

                String documento = docCol >= 0 ? getCellValueAsString(row.getCell(docCol)) : null;
                String perfil = perfilCol >= 0 ? getCellValueAsString(row.getCell(perfilCol)) : null;
                String servicio = servicioCol >= 0 ? getCellValueAsString(row.getCell(servicioCol)) : null;

                if (servicioDesdeExcel == null && servicio != null && !servicio.trim().isEmpty()) {
                    servicioDesdeExcel = servicio.trim();
                }

                if (documento == null || documento.trim().isEmpty()) {
                    documento = "TEMP_" + UUID.randomUUID().toString().substring(0, 8);
                }
                documento = documento.trim();

                Persona persona = personaRepository.findByDocumento(documento).orElse(null);
                if (persona == null) {
                    Persona nueva = new Persona();
                    nueva.setDocumento(documento);
                    nueva.setNombreCompleto(nombre);
                    String[] parts = nombre.split(" ", 2);
                    nueva.setNombres(parts[0]);
                    nueva.setApellidos(parts.length > 1 ? parts[1] : "");
                    nueva.setEstado(true);
                    persona = personaRepository.save(nueva);
                    creadas.add(nombre);

                    if (perfil != null && !perfil.trim().isEmpty()) {
                        String perfilLimpio = perfil.trim().toUpperCase();
                        Optional<TitulosFormacionAcademica> tituloOpt = titulosRepository
                                .findByTituloContainingIgnoreCase(perfilLimpio)
                                .stream().findFirst();

                        TitulosFormacionAcademica titulo;
                        if (tituloOpt.isPresent()) {
                            titulo = tituloOpt.get();
                        } else {
                            titulo = new TitulosFormacionAcademica();
                            titulo.setTitulo(perfilLimpio);
                            titulo.setTipoFormacionAcademica(tipoDefault);
                            titulo.setEstado(true);
                            titulo = titulosRepository.save(titulo);
                        }

                        if (persona.getTitulosFormacionAcademica() == null) {
                            persona.setTitulosFormacionAcademica(new HashSet<>());
                        }
                        persona.getTitulosFormacionAcademica().add(titulo);
                        personaRepository.save(persona);
                    }
                } else {
                    existentes.add(nombre);
                }

                personaIds.add(persona.getIdPersona());

                for (int dia = 0; dia < diasDelMes; dia++) {
                    int colIdx = firstDayCol + dia;
                    if (colIdx >= row.getLastCellNum()) break;

                    String codigo = getCellValueAsString(row.getCell(colIdx));
                    if (codigo == null || codigo.trim().isEmpty()) continue;

                    codigo = codigo.trim().toUpperCase();
                    if (codigosJornada.containsKey(codigo)) {
                        celdas.add(new ProgramacionDiariaRequest.CeldaMatriz(
                                persona.getIdPersona(), dia + 1, codigo, null));
                    } else {
                        log.warn("Código no reconocido: '{}' para {} día {}. Se asigna L (Libre).", codigo, nombre, dia + 1);
                        TipoJornada libre = codigosJornada.get("L");
                        if (libre != null) {
                            celdas.add(new ProgramacionDiariaRequest.CeldaMatriz(
                                    persona.getIdPersona(), dia + 1, "L", "Código original: " + codigo));
                        }
                    }
                }
            }
        }

        if (personaIds.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron personas en el archivo");
        }

        Long equipoFinalId = idEquipo;
        if (equipoFinalId == null) {
            String subcategoria = servicioDesdeExcel != null ? servicioDesdeExcel : "GENERAL";
            String categoriaEq = categoria != null && !categoria.isBlank()
                    ? categoria.substring(0, 1).toUpperCase() + categoria.substring(1).toLowerCase()
                    : "Servicio";

            EquipoSelectionDTO selection = new EquipoSelectionDTO();
            selection.setCategoria(categoriaEq);
            selection.setSubcategoria(subcategoria);
            selection.setEntidad(entidad);
            selection.setObservaciones(observaciones);

            try {
                EquipoDTO equipoCreado = equipoService.createWithGeneratedName(selection);
                equipoFinalId = equipoCreado.getIdEquipo();

                for (Long pid : personaIds) {
                    Persona p = personaRepository.findById(pid).orElse(null);
                    if (p != null) {
                        if (p.getEquipos() == null) p.setEquipos(new HashSet<>());
                        Equipo eq = equipoRepository.findById(equipoFinalId).orElse(null);
                        if (eq != null) {
                            p.getEquipos().add(eq);
                            personaRepository.save(p);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error al crear equipo con nombre generado: {}", e.getMessage());
                String fallbackName = "EQUIPO_IMPORTACION_" + mes + anio + "_" + String.format("%02d",
                        equipoRepository.countByNombreStartingWith("EQUIPO_IMPORTACION") + 1);
                Equipo eqFallback = new Equipo();
                eqFallback.setNombre(fallbackName);
                eqFallback.setEstado(true);
                eqFallback = equipoRepository.save(eqFallback);
                equipoFinalId = eqFallback.getIdEquipo();
                for (Long pid : personaIds) {
                    Persona p = personaRepository.findById(pid).orElse(null);
                    if (p != null) {
                        if (p.getEquipos() == null) p.setEquipos(new HashSet<>());
                        p.getEquipos().add(eqFallback);
                        personaRepository.save(p);
                    }
                }
            }
        } else {
            for (Long pid : personaIds) {
                Persona p = personaRepository.findById(pid).orElse(null);
                if (p != null) {
                    if (p.getEquipos() == null) p.setEquipos(new HashSet<>());
                    Equipo eq = equipoRepository.findById(equipoFinalId).orElse(null);
                    if (eq != null) {
                        p.getEquipos().add(eq);
                        personaRepository.save(p);
                    }
                }
            }
        }

        String categoriaCuadro = categoria != null ? categoria.toLowerCase() : "servicio";
        CuadroTurnoRequest ctRequest = new CuadroTurnoRequest();
        ctRequest.setAnio(anio);
        ctRequest.setMes(mes);
        ctRequest.setEntidad(entidad);
        ctRequest.setTipoPersonal(tipoPersonal);
        ctRequest.setCategoria(categoriaCuadro);
        ctRequest.setIdEquipo(equipoFinalId);
        ctRequest.setObservaciones(observaciones);
        ctRequest.setEstado(true);

        if (idMacroproceso != null) ctRequest.setIdMacroproceso(idMacroproceso);
        if (idProceso != null) ctRequest.setIdProceso(idProceso);
        if (idServicio != null) ctRequest.setIdServicio(idServicio);
        if (idSeccionServicio != null) ctRequest.setIdSeccionServicio(idSeccionServicio);
        if (idSubseccionServicio != null) ctRequest.setIdSubseccionServicio(idSubseccionServicio);

        CuadroTurnoDTO cuadroCreado = cuadroTurnoService.crearCuadroTurnoTotal(ctRequest);

        if (!celdas.isEmpty()) {
            ProgramacionDiariaRequest progRequest = new ProgramacionDiariaRequest();
            progRequest.setIdCuadroTurno(cuadroCreado.getIdCuadroTurno());
            progRequest.setCeldas(celdas);
            try {
                programacionDiariaService.guardarMatrizCompleta(progRequest);
                log.info("Matriz guardada con {} celdas para cuadro {}", celdas.size(), cuadroCreado.getIdCuadroTurno());
            } catch (Exception e) {
                log.error("Error al guardar matriz: {}", e.getMessage(), e);
                throw new RuntimeException("Error al guardar la matriz de turnos: " + e.getMessage());
            }
        } else {
            log.warn("No se generaron celdas para importar. Códigos disponibles: {}", codigosJornada.keySet());
        }

        resultado.put("exitoso", true);
        resultado.put("idCuadro", cuadroCreado.getIdCuadroTurno());
        resultado.put("nombreCuadro", cuadroCreado.getNombre());
        resultado.put("idEquipo", equipoFinalId);
        resultado.put("personasCreadas", creadas.size());
        resultado.put("personasExistentes", existentes.size());
        resultado.put("totalCeldas", celdas.size());
        resultado.put("personasCreadasLista", creadas);
        resultado.put("personasExistentesLista", existentes);
        resultado.put("servicioDetectado", servicioDesdeExcel);

        log.info("Importación completa: cuadro={}, equipo={}, personas creadas={}, existentes={}, celdas={}",
                cuadroCreado.getNombre(), equipoFinalId, creadas.size(), existentes.size(), celdas.size());

        return resultado;
    }

    private String limpiarTexto(String texto) {
        if (texto == null) return "";
        return java.text.Normalizer.normalize(texto.toUpperCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val))
                    yield String.valueOf((long) val);
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try { yield String.valueOf((long) cell.getNumericCellValue()); }
                catch (Exception e) {
                    try { yield cell.getStringCellValue(); }
                    catch (Exception e2) { yield null; }
                }
            }
            default -> null;
        };
    }
}
