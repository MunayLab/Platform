package org.munaylab

import org.munaylab.components.*
import org.munaylab.direccion.Domicilio
import org.munaylab.osc.Organizacion
import org.munaylab.planificacion.Actividad
import org.munaylab.planificacion.ActividadCommand
import org.munaylab.planificacion.Evento
import org.munaylab.planificacion.EventoCommand
import org.munaylab.planificacion.Programa
import org.munaylab.planificacion.ProgramaCommand
import org.munaylab.planificacion.Proyecto
import org.munaylab.planificacion.ProyectoCommand
import org.munaylab.planificacion.PlanificacionCommand

import grails.gorm.transactions.Transactional

@Transactional
class PlanificacionService {

    def actualizarPlanificacion(PlanificacionCommand command, Organizacion org) {
        if (command && command.validate && org) {
            if (command in ProgramaCommand) return actualizarPrograma(command, org)
            if (command in ProyectoCommand) return actualizarProyecto(command, org)
            if (command in ActividadCommand) return actualizarActividad(command)
        }
        return null
    }

    Programa actualizarPrograma(ProgramaCommand command, Organizacion org) {
        Programa programa = command.id ? Programa.get(command.id) : null
        if (programa) {
            programa.actualizarDatos(command)
        } else {
            programa = new Programa(command.properties)
            org.addToProgramas(programa)
            org.save()
        }
        return programa
    }

    void eliminarPrograma(Programa programa) {
        if (!programa || !programa.organizacion) return

        Organizacion org = programa.organizacion
        org.removeFromProgramas(programa)
        programa.delete()
        org.programas.clear()
    }

    Proyecto actualizarProyecto(ProyectoCommand command, Organizacion org) {
        Proyecto proyecto = command.id ? Proyecto.get(command.id) : null
        if (proyecto) {
            proyecto.actualizarDatos(command)
        } else {
            proyecto = new Proyecto(command.properties)
            Programa programa = org.programas.find { it.id == command.programaId }
            programa.addToProyectos(proyecto)
            org.save()
        }
        return proyecto
    }

    void eliminarProyecto(Proyecto proyecto) {
        if (!proyecto || !proyecto.programa) return

        Programa programa = proyecto.programa
        programa.removeFromProyectos(proyecto)
        proyecto.delete()
        programa.proyectos.clear()
    }

    Actividad actualizarActividad(ActividadCommand command) {
        Proyecto proyecto = Proyecto.get(command.proyectoId)
        if (!proyecto) return null

        Actividad actividad = command.id ? Actividad.get(command.id) : null
        if (actividad) {
            actividad.actualizarDatos(command)
        } else {
            actividad = new Actividad(command.properties)
            proyecto.addToActividades(actividad)
            proyecto.save()
        }
        return actividad
    }

    void eliminarActividad(Actividad actividad) {
        if (!actividad || !actividad.proyecto) return

        Proyecto proyecto = actividad.proyecto
        proyecto.removeFromActividades(actividad)
        actividad.delete()
        proyecto.actividades.clear()
    }

    Evento actualizarEvento(EventoCommand command) {
        if (!command || !command.validate()) return null

        Organizacion org = Organizacion.get(command.orgId)
        if (!org) return null

        Evento evento = command.id ? Evento.get(command.id) : null
        if (evento) {
            evento.actualizarDatos(command)
            if (command.direccion) {
                if (!evento.direccion) evento.direccion = new Domicilio()
                evento.direccion.actualizarDatos(command.direccion)
            }
        } else {
            evento = new Evento(command.properties)
            org.addToEventos(evento)
            org.save()
        }
        return evento
    }

    void cancelarEvento(Evento evento) {
        if (!evento || !evento.organizacion) return

        Organizacion org = evento.organizacion
        org.removeFromEventos(evento)
        evento.delete()
        org.eventos.clear()
    }

    def getProgramas(Organizacion org) {
        Programa.findAllPublicadoByOrganizacion(org)
    }

    def getProyectos(Organizacion org) {
        def proyectos = []
        def programas = getProgramas(org)

        programas.each { proyectos << it.proyectos }

        proyectos
    }

    def getResumen(Organizacion org) {
        def panels = []
        int totalProgramas = getTotalProgramas(org)
        int totalProyectos = getTotalProyectos(org)
        int totalActividades = getTotalActividades(org)

        panels << new PanelProgramas(name: 'Programas', value: totalProgramas, link: '#')
        panels << new PanelProyectos(name: 'Proyectos', value: totalProyectos, link: '#')
        panels << new PanelActividades(name: 'Actividades', value: totalActividades, link: '#')

        panels << new PanelEventos(name: 'Eventos', value: '300', link: '#')

        return panels
    }
    private int getTotalProgramas(Organizacion org) {
        Programa.createCriteria().get {
            eq 'publicado', true
            eq 'organizacion', org
            projections {
                rowCount()
            }
        }
    }
    private int getTotalProyectos(Organizacion org) {
        Proyecto.createCriteria().get {
            eq 'publicado', true
            programa {
                eq 'organizacion', org
            }
            projections {
                rowCount()
            }
        }
    }
    private int getTotalActividades(Organizacion org) {
        Actividad.createCriteria().get {
            eq 'publicado', true
            proyecto {
                programa {
                    eq 'organizacion', org
                }
            }
            projections {
                rowCount()
            }
        }
    }
}
