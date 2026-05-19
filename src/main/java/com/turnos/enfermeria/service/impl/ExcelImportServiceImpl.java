package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.request.ProgramacionDiariaRequest;
import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.ExcelImportService;
import com.turnos.enfermeria.service.ProgramacionDiariaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ExcelImportServiceImpl implements ExcelImportService {

    private final ProgramacionDiariaService programacionDiariaService;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final PersonaRepository personaRepository;
    private final TipoJornadaRepository tipoJornadaRepository;

    @Override
    public MatrizMensualDTO importarExcel(MultipartFile file, Long idCuadroTurno) throws Exception {
        CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado: " + idCuadroTurno));

        List<Persona> miembrosEquipo = new ArrayList<>();
        if (cuadro.getEquipos() != null) {
            miembrosEquipo = personaRepository.findPersonasByEquipos_IdEquipo(cuadro.getEquipos().getIdEquipo());
            log.info("Equipo '{}' tiene {} miembros", cuadro.getNombreEquipo(), miembrosEquipo.size());
        }

        Map<String, TipoJornada> codigosJornada = new HashMap<>();
        for (TipoJornada tj : tipoJornadaRepository.findAll()) {
            codigosJornada.put(tj.getCodigo().toUpperCase(), tj);
        }

        List<ProgramacionDiariaRequest.CeldaMatriz> celdas = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) {
                throw new IllegalArgumentException("El archivo Excel no tiene datos (mínimo 2 filas)");
            }

            Row headerRow = sheet.getRow(0);
            String firstHeader = getCellValueAsString(headerRow.getCell(0));
            boolean isSimpleFormat = firstHeader != null &&
                    (firstHeader.toUpperCase().contains("NOMBRE") || firstHeader.toUpperCase().contains("TERAPEUTA"));

            int nameCol, cedulaCol, perfilCol, servicioCol, firstDayCol;
            if (isSimpleFormat) {
                nameCol = 0; cedulaCol = -1; perfilCol = -1; servicioCol = -1; firstDayCol = 1;
            } else {
                nameCol = 1; cedulaCol = 2; perfilCol = 3; servicioCol = 0; firstDayCol = 4;
            }

            int diasDelMes = 0;
            int ultimaColumna = headerRow.getLastCellNum();
            for (int i = firstDayCol; i < ultimaColumna; i++) {
                String cellValue = getCellValueAsString(headerRow.getCell(i));
                if (cellValue != null && !cellValue.isEmpty()) {
                    try {
                        Integer.parseInt(cellValue.trim());
                        diasDelMes++;
                    } catch (NumberFormatException e) {
                        if (!cellValue.toUpperCase().contains("HORA")) break;
                    }
                }
            }

            if (diasDelMes == 0) {
                throw new IllegalArgumentException("No se detectaron columnas de días (1-31) en el encabezado");
            }

            log.info("Formato detectado: {} | Col nombre: {} | Primer día col: {} | Días: {}",
                    isSimpleFormat ? "SIMPLIFICADO" : "ESTÁNDAR", nameCol, firstDayCol, diasDelMes);

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                String nombreEmpleado = getCellValueAsString(row.getCell(nameCol));
                if (nombreEmpleado == null || nombreEmpleado.trim().isEmpty()) continue;
                nombreEmpleado = nombreEmpleado.trim();

                String documento = cedulaCol >= 0 ? getCellValueAsString(row.getCell(cedulaCol)) : null;

                Persona persona = buscarPersona(documento, nombreEmpleado, miembrosEquipo);
                if (persona == null) {
                    log.warn("Persona '{}' no encontrada en el equipo '{}'. Se omitirá.", nombreEmpleado, cuadro.getNombreEquipo());
                    continue;
                }

                for (int dia = 0; dia < diasDelMes; dia++) {
                    int colIdx = firstDayCol + dia;
                    if (colIdx >= ultimaColumna) break;

                    String codigo = getCellValueAsString(row.getCell(colIdx));
                    if (codigo == null || codigo.trim().isEmpty()) continue;

                    codigo = codigo.trim().toUpperCase();
                    if (codigosJornada.containsKey(codigo)) {
                        celdas.add(new ProgramacionDiariaRequest.CeldaMatriz(
                                persona.getIdPersona(), dia + 1, codigo, null));
                    } else {
                        log.warn("Código no reconocido: '{}' para {} día {}. Se asigna L (Libre).", codigo, nombreEmpleado, dia + 1);
                        TipoJornada libre = codigosJornada.get("L");
                        if (libre != null) {
                            celdas.add(new ProgramacionDiariaRequest.CeldaMatriz(
                                    persona.getIdPersona(), dia + 1, "L", "Original: " + codigo));
                        }
                    }
                }
            }
        }

        if (celdas.isEmpty()) {
            throw new IllegalArgumentException("No se pudieron importar datos. Verifica que los empleados existan en el sistema y los códigos sean válidos (M,T,N,L,C,D,OFF).");
        }

        ProgramacionDiariaRequest request = new ProgramacionDiariaRequest();
        request.setIdCuadroTurno(idCuadroTurno);
        request.setCeldas(celdas);

        log.info("Importadas {} celdas desde Excel para cuadro {}", celdas.size(), idCuadroTurno);
        return programacionDiariaService.guardarMatrizCompleta(request);
    }

    @Override
    public String validarEstructuraExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return "El archivo no tiene fila de encabezados";

            String firstHeader = getCellValueAsString(headerRow.getCell(0));
            boolean isSimple = firstHeader != null &&
                    (firstHeader.toUpperCase().contains("NOMBRE") || firstHeader.toUpperCase().contains("TERAPEUTA"));

            int firstDayCol = isSimple ? 1 : 4;

            StringBuilder reporte = new StringBuilder();
            reporte.append("Formato: ").append(isSimple ? "SIMPLIFICADO" : "ESTÁNDAR").append(". ");
            reporte.append(sheet.getPhysicalNumberOfRows() - 1).append(" filas de datos. ");

            int cols = 0;
            for (int i = firstDayCol; i < headerRow.getLastCellNum(); i++) {
                String val = getCellValueAsString(headerRow.getCell(i));
                if (val != null && !val.isEmpty()) {
                    try {
                        Integer.parseInt(val.trim());
                        cols++;
                    } catch (NumberFormatException e) {
                        if (!val.toUpperCase().contains("HORA")) break;
                    }
                }
            }
            reporte.append(cols).append(" días detectados.");
            return reporte.toString();
        }
    }

    private Persona buscarPersona(String documento, String nombre, List<Persona> miembrosEquipo) {
        if (miembrosEquipo.isEmpty()) return null;

        if (documento != null && !documento.trim().isEmpty()) {
            Optional<Persona> porDoc = miembrosEquipo.stream()
                    .filter(p -> documento.trim().equals(p.getDocumento()))
                    .findFirst();
            if (porDoc.isPresent()) return porDoc.get();
        }

        return miembrosEquipo.stream()
                .filter(p -> p.getNombreCompleto() != null &&
                        p.getNombreCompleto().toLowerCase().contains(nombre.toLowerCase().trim()))
                .findFirst()
                .orElse(null);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
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
