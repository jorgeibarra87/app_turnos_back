INSERT INTO bloque_servicio (id_bloque_servicio, nombre, estado) VALUES (1, 'INTERNACIÓN GENERAL ADULTOS', true);
INSERT INTO bloque_servicio (id_bloque_servicio, nombre, estado) VALUES (2, 'UNIDAD DE CUIDADOS INTERMEDIOS ADULTOS', true);
INSERT INTO bloque_servicio (id_bloque_servicio, nombre, estado) VALUES (3, 'INTERNACIÓN GENERAL PEDIATRIA', true);
INSERT INTO bloque_servicio (id_bloque_servicio, nombre, estado) VALUES (4, 'GINECOLOGIA', true);

INSERT INTO public.configuracion_correos (id_configuracion, activo, correo, fecha_actualizacion, fecha_creacion, tipo_correo) VALUES (4, true, 'jorgeibarraing@gmail.com', '2025-10-03 09:38:22.475991', '2025-10-03 09:38:22.475991', 'SELECCIONABLE');

INSERT INTO public.macroproceso (id_macroproceso, nombre, estado) VALUES (1, 'URGENCIAS INTERNACIÓN Y CRÍTICOS', true);

INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (1, 'URGENCIAS ADULTOS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (2, 'URGENCIAS PEDIATRIA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (3, 'URGENCIAS GINECOBSTETRICIA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (4, 'MEDICINA INTERNA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (5, 'GINECOBSTETRICIA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (6, 'MEDICOQUIRURGICAS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (7, 'QUIRURGICAS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (8, 'TRAUMATOLOGIA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (9, 'SALUD MENTAL', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (10, 'PEDIATRIA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (11, 'UNIDAD DE CUIDADOS INTENSIVOS ADULTOS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (12, 'UNIDAD DE CUIDADOS INTERMEDIOS ADULTOS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (13, 'UNIDAD DE CUIDADOS INTENSIVOS PEDIATRICO', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (14, 'UNIDAD DE CUIDADOS INTERMEDIOS PEDIATRICO', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (15, 'UNIDAD DE CUIDADOS INTERMEDIOS PEDIATRICO QUEMADOS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (16, 'NEONATOS', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (17, 'BANCO DE LECHE', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (18, 'CIRUGÍA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (19, 'ENDOSCOPIA DIGESTIVA', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (20, 'PROCESO ESTERILIZACIÓN', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (21, 'CONSULTA EXTERNA', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (22, 'LABORATORIO CLINICO', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (23, 'BANCO DE SANGRE Y SERVICIO TRANSFUSIONAL', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (24, 'DIAGNOSTICO CARDIOVASCULAR', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (25, 'NEFROLOGIA', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (26, 'SALA ADMINISTRACION DE MEDICAMENTOS ESPECIALES', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (27, 'NUTRICION Y DIETETICA', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (28, 'REHABILITACION Y TERAPIAS', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (29, 'SERVICIO FARMACEUTICO', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (30, 'PROGRAMA MADRE CANGURO', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (31, 'PROGRAMA DE PROMOCION Y MANTENMIENTO DE LA SALUD - PPMS RIAS', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (32, 'RADIOLOGÍA E IMÁGENES DIAGNOSTICAS', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (33, 'LABORATORIO DE PATOLOGÍA', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (34, 'HEMODINAMIA - ANGIOGRAFÍA', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (35, 'PREVENCION Y CONTROL DE INFECCIONES ASOCIADAS A LA ATENCION EN SALUD', 1, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (36, 'APOYO PSICOSOCIAL', NULL, true);
INSERT INTO public.procesos (id_proceso, nombre, id_macroproceso, estado) VALUES (37, 'REFERENCIA Y CONTRAREFERENCIA', NULL, true);

INSERT INTO public.servicio (id_servicio, nombre, tipo, id_bloque_servicio, id_proceso, estado) VALUES (1, 'URGENCIAS ADULTOS', NULL, NULL, 1, true);
INSERT INTO public.servicio (id_servicio, nombre, tipo, id_bloque_servicio, id_proceso, estado) VALUES (2, 'URGENCIAS PEDIATRIA', NULL, NULL, 2, true);
INSERT INTO public.servicio (id_servicio, nombre, tipo, id_bloque_servicio, id_proceso, estado) VALUES (3, 'URGENCIAS GINECOBSTETRICIA', NULL, NULL, 3, true);
INSERT INTO public.servicio (id_servicio, nombre, tipo, id_bloque_servicio, id_proceso, estado) VALUES (4, 'MEDICINA INTERNA 1', NULL, NULL, 4, true);
INSERT INTO public.servicio (id_servicio, nombre, tipo, id_bloque_servicio, id_proceso, estado) VALUES (5, 'MEDICINA INTERNA 2', NULL, NULL, 4, true);


INSERT INTO public.secciones_servicio (id_seccion_servicio, nombre, id_servicio, estado) VALUES (1, 'TRIAGE', 1, true);
INSERT INTO public.secciones_servicio (id_seccion_servicio, nombre, id_servicio, estado) VALUES (2, 'CONSULTORIOS', 1, true);
INSERT INTO public.secciones_servicio (id_seccion_servicio, nombre, id_servicio, estado) VALUES (3, 'CUBICULO', 1, true);

INSERT INTO public.subsecciones_servicio (id_subseccion_servicio, nombre, id_seccion_servicio, estado) VALUES (1, 'CONSULTORIO 1', 2, true);
INSERT INTO public.subsecciones_servicio (id_subseccion_servicio, nombre, id_seccion_servicio, estado) VALUES (2, 'CONSULTORIO 2', 2, true);
INSERT INTO public.subsecciones_servicio (id_subseccion_servicio, nombre, id_seccion_servicio, estado) VALUES (3, 'CONSULTORIO 3', 2, true);

INSERT INTO public.tipo_formacion_academica (id_tipo_formacion_academica, tipo, estado) VALUES (1, 'ESPECIALISTA', true);
INSERT INTO public.tipo_formacion_academica (id_tipo_formacion_academica, tipo, estado) VALUES (2, 'PROFESIONAL', true);
INSERT INTO public.tipo_formacion_academica (id_tipo_formacion_academica, tipo, estado) VALUES (3, 'NO PROFESIONAL', true);

INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (1, 'ANESTESIOLOGÍA', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (2, 'ALGESIOLOGIA', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (3, 'ANGIOGRAFIA', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (4, 'ARTROSCOPÍA', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (5, 'CIRUGÍA DE MAMA', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (6, 'CIRUGÍA DE TÓRAX', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (7, 'CIRUGÍA GENERAL', 1, true);
INSERT INTO public.titulos_formacion_academica (id_titulo, titulo, id_tipo_formacion_academica, estado) VALUES (8, 'CIRUGÍA LAPAROSCÓPICA', 1, true);




