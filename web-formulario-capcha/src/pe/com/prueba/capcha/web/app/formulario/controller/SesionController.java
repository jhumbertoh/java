package pe.com.visanet.spr.web.app.formularioafiliacion.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import pe.com.visanet.spr.web.framework.mvc.ClientIpAddress;

/**
 * Controlador de sesiones de usuarios
 */
@Controller
public class SesionController
{
    private static final Logger logger = LoggerFactory.getLogger(SesionController.class);
    private static final String PAG_INICIO = "inicio";
    private static final String PAG_SALIDA = "salida";

    /**
     * Maneja el acceso a la raiz de la aplicacion
     * 
     * @return vista lógica
     */
    @RequestMapping("/")
    public String manejarRaiz()
    {
        return "redirect:/" + PAG_INICIO;
    }

    @RequestMapping("/inicio")
    public String mostrarInicio(@ClientIpAddress String ip)
    {
        //logger.debug("numero de tarjeta: " + token.getTarjeta().getNumeroEnmascarado());
        logger.debug("ip: " + ip);        
        return PAG_INICIO;
    }

    @RequestMapping("/salida")
    public String mostrarSalida()
    {
        return PAG_SALIDA;
    }

}