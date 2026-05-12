package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.*;
import com.turnos.enfermeria.model.entity.Contrato;

import java.util.List;
import java.util.Optional;

public interface ContratoService {

    Contrato guardarContratoCompleto(ContratoTotalDTO contratoDTO);

    Contrato actualizarContratoCompleto(Long contratoId, ContratoTotalDTO contratoDTO);

    ContratoTotalDTO obtenerContratoCompleto(Long contratoId);

    List<Contrato> obtenerTodosLosContratos();

    void eliminarContrato(Long contratoId);

    boolean existeNumeroContrato(String numContrato);

    ContratoDTO create(ContratoDTO contratoDTO);

    ContratoDTO update(ContratoDTO detalleContratoDTO, Long id);

    Optional<ContratoDTO> findById(Long idContrato);

    List<ContratoDTO> findAll();

    void delete(Long idContrato);

    List<TitulosFormacionAcademicaDTO> agregarTituloAContrato(Long idContrato, Long idTitulo);

    List<ProcesosDTO> agregarProcesoAContrato(Long idContrato, Long idProceso);

    List<TitulosFormacionAcademicaDTO> obtenerTitulosPorContrato(Long idContrato);

    List<ProcesosDTO> obtenerProcesosPorContrato(Long idContrato);

    TitulosFormacionAcademicaDTO actualizarTitulosDeContrato(Long idTitulo, List<Long> nuevosTitulosIds);

    ProcesosDTO actualizarProcesosDeContrato(Long idProceso, List<Long> nuevosProcesosIds);

    void eliminarTituloDeContrato(Long idContrato, Long idTitulo);

    void eliminarProcesoDeContrato(Long idContrato, Long idProceso);
}
